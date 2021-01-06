package com.zk.elasticsearch;

import com.zk.ControllerTest;
import com.zk.ESApplication;
import com.zk.RequestTypeEnum;
import com.zk.es.entity.Documents;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

@ContextConfiguration(classes = ESApplication.class)
public class ElasticSearchControllerTest extends ControllerTest {

    private final String URI = "/api/v1/";

    /**
     * 测试保存
     * @throws Exception
     */
    @Test
    public void save() throws Exception {
        Documents documents = new Documents();
        documents.setCategory("category");
        documents.setDataId("dataId");
        documents.setFileDataId(1L);
        documents.setStatus("WAITTING");
        this.execute(RequestTypeEnum.POST, URI + "es/save", documents);
        this.execute(RequestTypeEnum.POST, URI + "es/save", documents);
        this.execute(RequestTypeEnum.POST, URI + "es/save", documents);
        this.execute(RequestTypeEnum.POST, URI + "es/save", documents);
        this.execute(RequestTypeEnum.POST, URI + "es/save", documents);
        this.execute(RequestTypeEnum.POST, URI + "es/save", documents);
        this.execute(RequestTypeEnum.POST, URI + "es/save", documents);
    }

    /**
     * 测试分页读取
     * @throws Exception
     */
    @Test
    public void getByFileDataIdAndStatus() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("fileDataId", "1");
        map.put("status", "WAITTING");
        map.put("page", "0");
        map.put("size", "100");
        this.execute(RequestTypeEnum.GET, URI + "es", map);
    }

    /**
     * 测试 @Query 读取
     * @throws Exception
     */
    @Test
    public void getLastCleanDataByDataIdAndCategory() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("category", "category");
        map.put("dataId", "dataId");
        this.execute(RequestTypeEnum.GET, URI + "es/last-clean", map);
    }


}