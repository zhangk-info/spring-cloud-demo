package com.zk.elasticsearch;

import com.zk.ControllerTest;
import com.zk.ESApplication;
import com.zk.RequestTypeEnum;
import com.zk.es.entity.Documents;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = ESApplication.class)
public class ElasticSearchControllerTest extends ControllerTest {

    private final String URI = "/api/v1/";

    @Test
    public void save() throws Exception {
        Documents documents = new Documents();
        documents.setCategory("category");
        documents.setDataId("dataId");
        documents.setFileDataId(1L);
        documents.setStatus("WAITTING");
        this.execute(RequestTypeEnum.POST, URI + "save", documents);
    }

}