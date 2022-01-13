package com.zk.importbigdata;

import cn.hutool.core.util.IdcardUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zk.importbigdata.config.DataMergeMethod;
import com.zk.importbigdata.config.Header;
import com.zk.importbigdata.config.MergeConfig;
import com.zk.importbigdata.validate.ExcelValueValidate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class DataMergeTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        MergeConfig config = new MergeConfig(Test.class,
                // 逻辑主键示例
                Header.createLogicPk("id", "0"),// 客户编号
                // format示例
                new Header("name", "1", true, (header, value, row) -> {
                    value = StringUtils.replace(value.toString(), "_", "");
                    // 这里可以做一些操作，如把编码替换成id等
                    return value;
                }),
                // calidate示例
                new Header("idCard", "2", true, new ExcelValueValidate() {
                    @Override
                    public boolean validate(Object v) {
                        return IdcardUtil.isValidCard(v.toString());
                    }
                })
        );

        DataMergeMethod method = new DataMergeMethod<Test>() {
            @Override
            public void format(Object row) {

                // 这里处理整行的format 如加创建人等固定值信息
            }

            @Override
            public void saveFromRecords(Test baseInfo) {
                // todo
                // 保存数据
                log.info("保存一条数据：id - " + baseInfo.getId());
                try {
                    // 测试用 设置每次保存耗时200毫秒
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        };


        //执行
        new DataMergeExecutor().execute(config, method, new RestTemplate(), new ObjectMapper(), 10);
    }


}

