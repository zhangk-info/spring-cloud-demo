package com.zk.elasticsearch;

import com.zk.ControllerTest;
import com.zk.ESApplication;
import com.zk.RequestTypeEnum;
import com.zk.es.entity.Documents;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashMap;
import java.util.Map;

@ContextConfiguration(classes = ESApplication.class)
public class ElasticSearchControllerTest extends ControllerTest {

    private final String URI = "/api/v1/";
    @Autowired
    private ElasticsearchOperations operations;

    /**
     * 测试保存
     *
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
     *
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
     *
     * @throws Exception
     */
    @Test
    public void getLastCleanDataByDataIdAndCategory() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("category", "category");
        map.put("dataId", "dataId");
        this.execute(RequestTypeEnum.GET, URI + "es/last-clean", map);
    }

    /**
     * 测试 QueryBuilder
     *
     * @throws Exception
     */
    @Test
    public void getLastCleanDataByDataIdAndCategory2() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("category", "category");
        map.put("dataId", "dataId");
        this.execute(RequestTypeEnum.GET, URI + "es/last-clean2", map);
    }

    // 踩坑 字段类型对于es来说很重要 不能随便变更 比如从字符串到时间，从float到double，都是不能再有数据之后再转换的，此时必须使用 elasticsearch-dump
    @Test
    public void deleteIndex() throws Exception {
        operations.indexOps(Documents.class).delete();
    }

//    @Test 默认自动创建的
//    public void createIndex() throws Exception {
//        operations.indexOps(Documents.class).create();
//    }

    // 更改了实体之后某些属性不会生效，调用 putMapping或借助客户端手动修改
    @Test
    public void putIndexMapping() throws Exception {
        // 更改配置映射关系
        IndexOperations indexOperations = operations.indexOps(Documents.class);
        indexOperations.putMapping(indexOperations.createMapping(Documents.class));
    }


}