package com.zk.es.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author
 * @since 2020-09-08
 */
@Data
@Accessors(chain = true)
@Document(indexName = "documents")
public class Documents {

    /**
     * 内部使用的逻辑主键。
     */
    @Id
    // 这里的id是否可以用自定义的分布式id 不行 id属性必须是类型String。
    private String id;

    /**
     * 创建时间
     */
    @CreatedDate // 这个没用
    @Field(name = "created_date_time", type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateTime;

    /**
     * 更新时间
     */
    @LastModifiedDate // 这个没用
    @Field(name = "update_date_time", type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDateTime;

    /**
     * 该条记录的版本号，乐观锁机制。
     */
    @Version
    private Long version;

    /**
     * 合并类型
     */
    @Field(name = "category")
    private String category;

    /**
     * 来自同步表的主键或逻辑主键
     */
    @Field(name = "data_id")
    private String dataId;

    /**
     * 同步表记录的“原始数据”，没有经过任何处理，仅仅是经过“从通过过来的文件读取”这个动作得到的数据，以Json的形式存储。
     */
    @Field(name = "data")
    private String data;

    /**
     * 上一次同步的“老原始数据”
     */
    @Field(name = "old_data")
    private String oldData;

    /**
     * 排序号
     */
    @Field(name = "order_number")
    private Long orderNumber;

    /**
     * 同步表记录的“处理状态”。
     */
    @Field(name = "status")
    private String status;

    /**
     * 对应的“清洗文件记录信息”。
     */
    @Field(name = "file_data_id")
    private Long fileDataId;

    /**
     * 合并失败原因
     */
    @Field(name = "reason")
    private String reason;
}
