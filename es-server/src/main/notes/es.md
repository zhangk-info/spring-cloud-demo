
## spring-data-elasticsearch 注解解释
### @Document
```
这个主键对应的ElasticsearchCase#createIndex()方法

indexName 索引名称
type 类型
useServerConfiguration 是否使用系统配置
shards 集群模式下分片存储，默认分5片
replicas 数据复制几份，默认一份
refreshInterval 多久刷新数据 默认:1s
indexStoreType 索引存储模式 默认:fs，为深入研究
createIndex 是否创建索引，默认:true
```

### @Id


### @Field
这个主键对应的ElasticsearchCase#setMappings()方法
```
type 字段类型 默认根据java类型推断,可选类型：Text,Integer,Long,Date,Float,Double,Boolean,Object,Auto,Nested,Ip,Attachment,Keyword,新的数据类型请参考官网
index 应该是建索引
format 数据格式，可以理解为一个正则拦截可存储的数据格式
pattern 使用场景：format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss:SSS"
searchAnalyzer 搜索的分词，新的ik分词只有ik_smart和ik_max_word两个模式
store 是否单独存储，应该是存储在_source
analyzer 分词模式
ignoreFields 
includeInParent 
fielddata 
```

```
作者：zhaoyunxing
链接：https://www.jianshu.com/p/7019d93219f5
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
```
