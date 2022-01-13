package com.zk.importbigdata.imports;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zk.commons.exception.ServiceException;
import com.zk.importbigdata.config.Header;
import com.zk.importbigdata.config.MergeConfig;
import com.zk.importbigdata.db.DataInsertService;
import com.zk.importbigdata.db.FileDataRecords;
import com.zk.importbigdata.db.FileStatus;
import com.zk.importbigdata.format.RowFormat;
import com.zk.importbigdata.msg.*;
import com.zk.importbigdata.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 合并工具类
 *
 * <pre>
 * 实现逻辑：
 * startImport 循环遍历readRow转换成instance
 * readRow 数据校验
 * saveOrUpdate 校验逻辑主键 并 分页分批插入更新
 * </pre>
 *
 * @param <T>
 */
@Slf4j
public class DataMergeService<T> {

    private static final List<String> TRUE_VALUES = Arrays.asList("1", "是", "正确", "对", "OK", "");
    // 用一个原子类来判断是否所有线程都执行完成
    public AtomicInteger runnableNum = new AtomicInteger(0);
    private Logger LOG = LoggerFactory.getLogger(LoggerFactory.class);
    // 本页数据
    private List<FileDataRecords> details;
    // 合并处理接口
    private DataInsertService<T> dataInsertService;
    // 整行数据格式化
    private RowFormat<T> rowFormat;
    // 合并配置(错误停止，允许更新，逻辑主键，表头)
    private MergeConfig config;
    // 合并文件的importId
    private Long importId;
    // 表头
    private List<Header> header;
    // 逻辑主键
    private Map<String, Header> logicPk;
    // 逻辑主键值Map
    private Map<String, String> valueMap;
    // 待检查列表
    private CopyOnWriteArrayList<WaitCheckData<T>> waitCheckList;
    // 待插入数据列表 解析一行后得到的数据
    private CopyOnWriteArrayList insertData;
    // 待更新数据列表 解析一行后得到的数据
    private CopyOnWriteArrayList updateData;
    // 待插入数据表数据列表
    private CopyOnWriteArrayList<FileDataRecords> insertFileDataRecordss;
    // 待更新数据表数据列表
    private CopyOnWriteArrayList<FileDataRecords> updateFileDataRecordss;
    // 导入消息 并未使用
    private ImportMsg msg;
    // 消息推送
    private IMsgSender iMsgSender;
    private ThreadPoolExecutor threadPoolExecutor = null;
    private ObjectMapper objectMapper;
    // 当前页开始行号
    private Integer startRow = 0;

    public DataMergeService(MergeConfig config, IMsgSender iMsgSender, List<FileDataRecords> details, RestTemplate restTemplate, ObjectMapper objectMapper, Integer startRow, Integer threadCount) {
        this.config = config;
        this.importId = 0L;
        this.header = config.getHeader();
        this.logicPk = config.getLoginPk();
        this.insertData = new CopyOnWriteArrayList();
        this.updateData = new CopyOnWriteArrayList();
        this.insertFileDataRecordss = new CopyOnWriteArrayList();
        this.updateFileDataRecordss = new CopyOnWriteArrayList();
        this.valueMap = new HashMap();
        this.waitCheckList = new CopyOnWriteArrayList();
        this.msg = new ImportMsg(this.importId, this.header);
        this.msg.setAllowUpdate(config.isAllowUpdate());
        this.msg.setStartDate(new Date());
        this.iMsgSender = iMsgSender;
        this.details = details;
        this.objectMapper = objectMapper;
        this.startRow = startRow;

        // ThreadPoolExecutor的7个参数:
        // 1 corePoolSize 核心线程数
        // 2 maximumPoolSize 最大线程数 //cpu密集型：设置最大线程数为电脑内核数 +1 //io密集型：电脑内核数/阻塞系数
        // 3、4 keepAliveTime 空闲线程存活时间
        // 5 BlockingQueue<Runnable> 任务队列，等待中的任务（提交了尚未执行的）
        // 6 ThreadFactory 线程工厂 //重写线程工厂 目的是调优的时候需要使用线程名字
        // 7 RejectedExecutionHandler 拒绝策略
        int maximumPoolSize = threadCount;//Runtime.getRuntime().availableProcessors() + 1;
        int queueSize = 200; // Runtime.getRuntime().availableProcessors() - 1;
        //初始化一个线程池
        this.threadPoolExecutor = new ThreadPoolExecutor(maximumPoolSize, maximumPoolSize, 10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueSize), new DateMergeThreadFactory(), new ThreadPoolExecutor.DiscardPolicy());

    }

    /**
     * The default thread factory.
     */


    public void registerDataInsert(DataInsertService dataInsert) {
        this.dataInsertService = dataInsert;
    }

    public Long getImportId() {
        return this.importId;
    }

    public void setImportId(Long importId) {
        this.importId = importId;
        this.msg.setImportId(importId);
    }

    public void start() {
        log.error("DataMergeService.start()");
        this.startImport(details);
    }

    private void startImport(List<FileDataRecords> datas) {
        this.msg.setStatus(ImportStatus.READING);
        int rows = datas.size();
        this.msg.setTotal(startRow + rows);
        this.iMsgSender.send(this.msg);

        if (CollectionUtils.isEmpty(datas)) {
            log.error(" - 155 无数据failFinish");
            this.failFinish(ImportErrorConst.NO_DATA);
            return;
        }

        LocalDateTime readRowStartDateTime = LocalDateTimeUtil.now();

        //readRow 读取行 todo 这里也用多个线程执行
        for (int i = 0; i <= rows - 1; ++i) {
            // 得到FileData的数据
            FileDataRecords fileDataRecords = datas.get(i);
            if (Objects.isNull(fileDataRecords)) {
                log.error("datas共 " + rows + " 行,第 " + i + "行未获取到数据");
                continue;
            }
            String row = null;
            try {
                if (fileDataRecords.getData() == null) {
                    row = "";
                } else {
                    row = objectMapper.readValue(fileDataRecords.getData(), Map.class).get("String").toString();
                }
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
            }
//            String row =  JSONUtil.parseObj(FileDataRecords.getData()).getStr("String");
            // this.// log.errorror("开始读取第" + i + "行：" + row);
            Object instance;

            try {
                instance = this.config.getDistClazz().getDeclaredConstructor().newInstance();
            } catch (Exception var7) {
                var7.printStackTrace();
                log.error(" 180 配置出错 failFinish");
                this.failFinish(ImportErrorConst.CONFIG_ERROR);
                return;
            }

            try {
                this.readRow(row, (T) instance, i, fileDataRecords);
                // this.// log.errorror("第" + i + "行读取成功");
            } catch (Exception e) {
                log.error("readRow 出错 " + fileDataRecords.getId() + "--" + e.getMessage());
                // 某一行失败了 标记该行就行了 不处理
                // 如果是有单独的处理逻辑 请抓异常并单独处理FileDataRecords
                fileDataRecords.setStatus(FileStatus.FAIL);
                //todo saveRecord(fileDataRecords);

            }

            // 得到已耗时 毫秒
            Long costTime = LocalDateTimeUtil.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() - readRowStartDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            // 如果已耗时 大于10分钟 10分钟还没有解析完成数据 进行锁续费
            if (costTime > 10 * 60 * 1000) {
                // 锁续费
                log.error("已耗时 大于10分钟 10分钟还没有解析完成数据 进行锁续费");
                this.iMsgSender.send(this.msg);
                readRowStartDateTime = LocalDateTimeUtil.now();
            }

//            if (i % 100 == 0) { // 这里原本是每100条更新一下msg msg中包含已读取行数等信息
//                this.iMsgSender.send(this.msg);
//            }
        }

        // readRow完成之后开始保存
        if (this.saveOrUpdate()) {
            this.successFinish();
        } else {
            log.error(" 227 failFinish");
            this.failFinish("保存执行判断未通过");
        }

    }

    // 分批插入更新
    private boolean saveOrUpdate() {
        this.msg.setStatus(ImportStatus.INSERTING);
        try {
            this.iMsgSender.send(this.msg);
        } catch (Exception ex) {
            log.error("this.iMsgSender.send(this.msg); -- " + ex.getMessage());
        }

        log.error("readRow完成 开始分页分线程执行更新 - " + this.waitCheckList.size());
        if (this.waitCheckList.size() > 0) {
            // 将数据分页后
            PageManager<WaitCheckData<T>> page = new PageManager(5, this.waitCheckList);
            AtomicInteger pageNum = new AtomicInteger(1);


            // 循环检查并分批插入
            while (true) {
                Integer currentRow = startRow + (this.details.size() - this.waitCheckList.size());
                List<WaitCheckData<T>> pageData = page.getData(pageNum.get());
                if (pageData == null || pageData.size() == 0) {
                    break;
                }
                // todo 这里要不要把赋值放到每个子线程完成之后再加pageSize
                this.msg.setCurrentRow(startRow + (this.details.size() - this.waitCheckList.size()) + page.getEndRow());

                if (this.logicPk != null && this.logicPk.size() > 0) {
                    pageData.forEach((ele) -> {
                        // 逻辑主键重复复
                        if (ele.getMsg().isRepeat()) {
                            if (this.config.isAllowUpdate()) {
                                this.updateData.add(ele.getData());
                                this.updateFileDataRecordss.add(ele.getFileDataRecords());
                            }
                        } else {
                            this.insertData.add(ele.getData());
                            this.insertFileDataRecordss.add(ele.getFileDataRecords());
                        }

                    });
                } else {
                    pageData.forEach((ele) -> {
                        this.insertData.add(ele.getData());
                        this.insertFileDataRecordss.add(ele.getFileDataRecords());
                    });
                }

                // 开始执行合并
                try {

                    // 队列大小
                    int queueSize = 200;
                    // 如果总任务数 - 已完成的任务数量 - 执行中的线程数 >= 队列大小 即 队列满了！ 就 等会儿再插
                    Long waitCount = threadPoolExecutor.getTaskCount() - threadPoolExecutor.getCompletedTaskCount() - threadPoolExecutor.getActiveCount();
                    while (waitCount >= queueSize) {
                        // 等0.5秒重新获取队列等待数量
                        TimeUnit.MILLISECONDS.sleep(500L);
                        waitCount = threadPoolExecutor.getTaskCount() - threadPoolExecutor.getCompletedTaskCount() - threadPoolExecutor.getActiveCount();
                    }

                    // 线程池执行 默认AbortPolicy超过拒绝并报错 这里的 不同线程参数传递的时候一定要copy 用他的toArray会getArray().clone();
                    DataMergeRunnable runnable = new DataMergeRunnable(Arrays.stream(insertData.toArray()).collect(Collectors.toList()),
                            Arrays.stream(updateData.toArray()).collect(Collectors.toList()),
                            Arrays.stream(insertFileDataRecordss.toArray(new FileDataRecords[0])).collect(Collectors.toList()),
                            Arrays.stream(updateFileDataRecordss.toArray(new FileDataRecords[0])).collect(Collectors.toList()),
                            this.config.isAllowUpdate(), dataInsertService, this);
                    runnableNum.incrementAndGet();
                    threadPoolExecutor.execute(runnable);

                    // 多线程下这两个数据是有问题的
//                    this.msg.setInsertTotal(this.insertData.size());
//                    this.msg.setUpdateTotal(this.updateData.size());

                    // 清空分页数据
                    this.insertData.clear();
                    this.updateData.clear();
                    this.insertFileDataRecordss.clear();
                    this.updateFileDataRecordss.clear();

                } catch (Exception var4) {
                    log.error("DataMergeService : 315: -- " + var4.getMessage());
                    log.error(" 316 failFinish");
                    this.failFinish(var4.getMessage());
                }

                pageNum.incrementAndGet();
            }

            while (runnableNum.get() > 0) {
                log.error(runnableNum.get() + "-- 遍历等待线程池处理结束  等 1秒钟 重新获取 池活线程数量");
                try {
                    // 等 10秒钟 重新获取 池活线程数量
                    TimeUnit.MILLISECONDS.sleep(1000L);
                } catch (Exception e) {
                    log.error(" 329 failFinish");
                    this.failFinish("睡死了！");
                }
            }

            // 所有合并线程跑完之后设置错误行数
            this.msg.setErrorTotal(this.msg.getRowError().size());
            // 重要！
            waitCheckList.clear();

            return true;
        } else {
            return false;
        }
    }

    public void closeThreadPool() {
        //遍历完成后关闭线程池
        threadPoolExecutor.shutdown();
        log.error(runnableNum.get() + "-------------------- 关闭线程池");
    }

    /**
     * 保存错误并更新FileDataRecords
     *
     * @param errorMsg
     * @param detail
     */
    private void addError(RowDataErrorMsg errorMsg, FileDataRecords detail) {
//        this.msg.addError(errorMsg); //不存这个 这个存到了单个的行数据里面 原本是所有行错误也保存在msg中
        // 更新：FileDataRecords为失败
        detail.setStatus(FileStatus.FAIL);
        try {
            detail.setReason(objectMapper.writeValueAsString(errorMsg));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        log.error("一行出现错误，错误原因" + JSONUtil.toJsonStr(errorMsg));
        //todo saveRecord(detail);
    }

    public boolean setFieldValueByFieldName(String fieldName, Object object, Object value) {
        try {
            // 获取obj类的字节文件对象
            Class c = object.getClass();
            // 获取该类的成员变量
            Field f = c.getDeclaredField(fieldName);
            // 取消语言访问检查
            f.setAccessible(true);
            // 给变量赋值
            f.set(object, value);
            return true;
        } catch (Exception e) {
            log.error("反射取值异常", e);
            return false;
        }
    }

    public Object getFieldValueByFieldName(String fieldName, Object object) {
        try {
            // 获取obj类的字节文件对象
            Class c = object.getClass();
            // 获取该类的成员变量
            Field f = c.getDeclaredField(fieldName);
            // 取消语言访问检查
            f.setAccessible(true);
            // 给变量赋值
            return f.get(object);
        } catch (Exception e) {
            log.error("反射取值异常", e);
            return null;
        }
    }

    /**
     * 读取一行数据
     * 数据format
     * 数据validate
     * 数据logicpk检查
     *
     * @param row
     * @param instance
     * @param rowIndex
     * @param FileDataRecords
     * @throws Exception
     */
    private void readRow(String row, T instance, int rowIndex, FileDataRecords FileDataRecords) throws Exception {
        RowDataErrorMsg rowMsg = new RowDataErrorMsg(rowIndex);
        if (row != null) {

            String[] strArray = {};
            Map<String, Object> map = new HashMap<>();

            // excel读取出来后存储是一个List<Map<String,Object>>
            if (row.startsWith("{") && row.endsWith("}")) {
                try {
                    map = objectMapper.readValue(row, Map.class);//JSONUtil.parseObj(row);
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage(), e);
                    throw e;
                }
            } else {
                // todo 这里没有动态 设置分隔符
                strArray = row.split("\\|@\\|");
            }
            for (int colIndex = 0; colIndex < this.header.size(); ++colIndex) {
                // 得到header
                Header oneHeader = this.header.get(colIndex);
                // 从row中得到该cell的值
                String cell;
                if (row.startsWith("{") && row.endsWith("}")) {
                    // 这里到底要不要判null 其实可以让异常出去，整个结束的！
                    cell = map.get(oneHeader.getCnNameOrSort()) == null ? "" : map.get(oneHeader.getCnNameOrSort()).toString();
                } else {
                    cell = strArray[Integer.valueOf(oneHeader.getCnNameOrSort())];
                }
                Object value = null;
                // 列是不是有format
                if (oneHeader.getFormat() == null) {
                    try {
                        value = this.readValue(cell, oneHeader);
                        // 列值是否必须
                        if (!oneHeader.isMustInput() || value != null && !StringUtils.isBlank(cell)) {
                            setFieldValueByFieldName(oneHeader.getName(), instance, value);
//                            bean.set(h.getName(), value);
                        } else {
                            // 列值必须但无值
                            rowMsg.addError(new ColDataErrorMsg(Integer.valueOf(colIndex), oneHeader, "", ImportErrorConst.REQUIRE));
                        }
                    } catch (Exception var15) {
                        // this.// log.errorror(String.format("数据读取失败:第%d行 [%s]列", rowIndex, h.getName()), var15);
                        rowMsg.addError(new ColDataErrorMsg(Integer.valueOf(colIndex), oneHeader, cell, ImportErrorConst.DATA_TYPE));
                        throw var15;
                    }
                } else {
                    // 列有format
                    try {
//                        value = this.readValue(cell, h);
//                        Object o = (Object) Double.parseDouble("123");
//                        System.out.println(o.toString()); // 123.0
                        // 这里直接传递读到的字符串到format中去 原因如上：字符串转数字再转回字符串会被加.0
                        value = oneHeader.getFormat().format(oneHeader, (cell == null || StringUtils.isBlank(cell)) ? null : cell, instance);
                        if (oneHeader.isMustInput() && (value == null || StringUtils.isBlank(cell))) {
                            rowMsg.addError(new ColDataErrorMsg(Integer.valueOf(colIndex), oneHeader, null, ImportErrorConst.REQUIRE));
                        } else if (value != null) {
                            if (oneHeader.getType().isInstance(value)) {
//                                bean.set(h.getName(), value);
                                setFieldValueByFieldName(oneHeader.getName(), instance, value);
                            } else {
                                rowMsg.addError(new ColDataErrorMsg(Integer.valueOf(colIndex), oneHeader, cell, ImportErrorConst.READ));
                            }
                        }
                    } catch (Exception excelValueFormatException) {
                        // this.// log.errorror("format error", var13);
                        if (excelValueFormatException instanceof ServiceException) {
                            rowMsg.addError(new ColDataErrorMsg(Integer.valueOf(colIndex), oneHeader, cell, ImportErrorConst.READ, String.valueOf(((ServiceException) excelValueFormatException).getCode())));
                        } else {
                            rowMsg.addError(new ColDataErrorMsg(Integer.valueOf(colIndex), oneHeader, cell, ImportErrorConst.READ, excelValueFormatException.getMessage()));
                        }
                        throw excelValueFormatException;
                    }
                }

                if (oneHeader.getLength() != null && oneHeader.getLength() > 0 && oneHeader.getType() == String.class && value != null && (cell).length() > oneHeader.getLength()) {
                    rowMsg.addError(new ColDataErrorMsg(Integer.valueOf(colIndex), oneHeader, cell, ImportErrorConst.MORE_LENGTH));
                }

                if (oneHeader.getValidate() != null && value != null && StringUtils.isNotBlank(cell)) {
                    try {
                        if (!oneHeader.getValidate().validate(value)) {
                            rowMsg.addError(new ColDataErrorMsg(Integer.valueOf(colIndex), oneHeader, cell, ImportErrorConst.VALIDATE));
                        }
                    } catch (Exception excelValueValidateException) {
                        if (excelValueValidateException instanceof ServiceException) {
                            rowMsg.addError(new ColDataErrorMsg(Integer.valueOf(colIndex), oneHeader, cell, ImportErrorConst.READ, String.valueOf(((ServiceException) excelValueValidateException).getCode())));
                        } else {
                            rowMsg.addError(new ColDataErrorMsg(Integer.valueOf(colIndex), oneHeader, cell, ImportErrorConst.READ, excelValueValidateException.getMessage()));
                        }
                        throw excelValueValidateException;
                    }
                }
            }
            if (this.rowFormat != null) {
                this.rowFormat.format(instance);
            }
            // 逻辑主键重复的
            if (this.logicPk != null && this.logicPk.size() > 0 && rowMsg.getColError().size() == 0) {
                String lv = this.getLogicValue(instance);
                if (this.valueMap.containsKey(lv)) {
                    rowMsg.setRepeat(true);
                } else {
                    // k-v 设置v为空节省内存
                    this.valueMap.put(lv, "");
                }
            }
            // 如果行逻辑主键没有重复且没有列读取错误
            if (!rowMsg.isRepeat() && rowMsg.getColError().size() == 0) {
                this.waitCheckList.add(new WaitCheckData(instance, rowMsg, FileDataRecords));
            }
            if (rowMsg.isRepeat() || rowMsg.getColError().size() > 0) {
                this.addError(rowMsg, FileDataRecords);
            }

        } else {
            rowMsg.setBlankRow(true);
            this.addError(rowMsg, FileDataRecords);
            throw new ServiceException("row is black");
        }
    }

    /**
     * 读取单个cell的数据
     *
     * @param cell
     * @param header
     * @return
     */
    private Object readValue(String cell, Header header) {
        if (StringUtils.isEmpty(cell)) {
            return null;
        } else if (header.getType() == String.class) {
            return cell;
        } else if (header.getType() == Date.class) {
            try {
                return DateUtils.smartFormat(cell);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }
        } else {
            String value = cell;
            if (header.getType() == Integer.class) {
                return StringUtils.isNotBlank(value) ? Integer.parseInt(value) : null;
            } else if (header.getType() == Long.class) {
                return StringUtils.isNotBlank(value) ? Long.parseLong(value) : null;
            } else if (header.getType() == Short.class) {
                return StringUtils.isNotBlank(value) ? Short.parseShort(value) : null;
            } else if (header.getType() == Float.class) {
                return StringUtils.isNotBlank(value) ? Float.parseFloat(value) : null;
            } else if (header.getType() == Double.class) {
                return StringUtils.isNotBlank(value) ? Double.parseDouble(value) : null;
            } else if (header.getType() == Boolean.class) {
                return TRUE_VALUES.contains(value.trim());
            } else {
                throw new RuntimeException("不支持的数据类型");
            }
        }
    }

    /**
     * 成功结束
     */
    private void successFinish() {
        this.msg.setCurrentRow(startRow + this.details.size());
        this.msg.setStatus(ImportStatus.SUCCESS);
        this.msg.setEndDate(new Date());
        this.iMsgSender.send(this.msg);
    }

    /**
     * 失败结束
     * 情况1 errorStop true
     * 情况2 配置出错
     * 情况3 没有数据
     * 情况4 合并过程出错（msgSendError等）
     *
     * @param msg
     */
    private void failFinish(String msg) {
        this.msg.setError(msg);
        this.msg.setEndDate(new Date());
        this.msg.setCurrentRow(startRow + this.details.size());
        this.iMsgSender.send(this.msg);

        //将所有details改成失败
        details = details.stream().filter(t -> t.getStatus().equals(FileStatus.WAITTING)).collect(Collectors.toList());
        details.forEach(detail -> {
            detail.setStatus(FileStatus.FAIL);
            // todo saveRecord(detail);
        });
    }

    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    public void setRowFormat(RowFormat rowFormat) {
        this.rowFormat = rowFormat;
    }

    private String getLogicValue(Object bean) {
        StringBuffer buffer = new StringBuffer();
        this.logicPk.values().forEach((ele) -> {
            Object v = getFieldValueByFieldName(ele.getName(), bean);
            buffer.append(v == null ? "null" : v.toString()).append("_@_");
        });
        return buffer.toString();
    }

    public void setDetails(List<FileDataRecords> fileDataRecords) {
        this.details = fileDataRecords;
    }
}
