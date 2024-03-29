package com.zk.readbigdata.read;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.zk.configuration.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 模板的读取类
 *
 * 
 */
// 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
@Slf4j
public class FileDataExcelReadListener extends AnalysisEventListener<Map<Integer, Object>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDataExcelReadListener.class);
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 3000;
    List<String> list;
    Integer size;
    String key;
    RedisService redisService;
    Map<Integer, String> headMap = new HashMap<>();

    public FileDataExcelReadListener(Integer size, String key, RedisService redisService) {
        this.list = new ArrayList<>();
        this.size = size;
        this.key = key;
        this.redisService = redisService;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(Map<Integer, Object> data, AnalysisContext context) {
        Map<String, Object> returnData = new HashMap<>();
        data.keySet().forEach(t -> {
            returnData.put(headMap.get(t), data.get(t));
        });
        list.add(JSONUtil.toJsonStr(returnData));
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (list.size() >= BATCH_COUNT) {
            // 存储
            saveData();
            // 存储完成清理 list
            list.clear();
            // 等半秒 减小服务器压力
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void saveData() {
        size += BATCH_COUNT;
        if (list.size() > 0) {
            redisService.lAdd(key, list, 2 * 24 * 60 * 60);
        }
    }

    /**
     * 这里会一行行的返回头
     *
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        LOGGER.info("解析到一条头数据:{}", JSONUtil.toJsonStr(headMap));
        this.headMap = headMap;
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        LOGGER.info(" 所有数据解析完成！");
    }
}