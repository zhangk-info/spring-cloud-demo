package com.zk.code;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.BeetlTemplateEngine;

import java.util.*;

// 演示例子，执行 main 方法控制台输入模块表名回车自动生成对应项目目录中
public class CodeGenerator {

    //代码生成
    public static void main(String[] args) {
        CodeGenerator c = new CodeGenerator();
        c.generate(null);
    }

    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StrUtil.isNotEmpty(ipt)) {
                return ipt;
            } else {
                return "";
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    /**
     * 设置自定义文件
     *
     * @param projectPath
     * @param pc
     * @return
     */
    public List<FileOutConfig> getInjectConfigs(String projectPath, PackageConfig pc) {
        // 如果模板引擎是
//        String templatePath = "/templates/mapper.xml.btl";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
//        focList.add(new FileOutConfig(templatePath) {
//            @Override
//            public String outputFile(TableInfo tableInfo) {
//                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
//                return projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
//                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
//            }
//        });

        return focList;
    }

    /**
     * 设置模版
     *
     * @return
     */
    public TemplateConfig getTemplateConfig() {
        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        //todo 模版优化
        String entity = "template/demo/entity.java";
//        String entityKt = "template/entity.kt";
        String service = "template/demo/service.java";
        String serviceImpl = "template/demo/serviceImpl.java";
        String mapper = "template/demo/mapper.java";
        String xml = "template/demo/mapper.xml";
        String controller = "template/demo/controller.java";

        templateConfig.setEntity(entity);
        templateConfig.setService(service);
        templateConfig.setServiceImpl(serviceImpl);
        templateConfig.setMapper(mapper);
        templateConfig.setXml(xml);
        templateConfig.setController(controller);

        templateConfig.setXml(null);
        return templateConfig;
    }


    /**
     * 生成
     */
    public void generate(DataSourceConfig dsc2) {

        // 代码生成器
        AutoGenerator generator = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        String serverName = scanner("maven项目module名称,如:order-server");
        gc.setOutputDir(projectPath + (serverName.equals("") ? "" : "/" + serverName) + "/src/main/java");
        gc.setAuthor("");
        gc.setOpen(false);
        gc.setSwagger2(true); //实体属性 Swagger2 注解
        generator.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        if (dsc2 != null) {
            dsc = dsc2;
        } else {
            dsc.setUrl("jdbc:mysql://139.155.72.177:31001/seata-at-test?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai");
            // dsc.setSchemaName("public");
            dsc.setDriverName("com.mysql.cj.jdbc.Driver");
            dsc.setUsername("seata");
            dsc.setPassword("seata..123");
            generator.setDataSource(dsc);
        }

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(scanner("包名,如:com.zk.order"));
        pc.setModuleName(scanner("模块名"));
        generator.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
                //自定义属性注入:abc
                //在.ftl(或者是.vm)模板中，通过${cfg.abc}获取属性
                Map<String, Object> map = new HashMap<>();
                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                this.setMap(map);
            }
        };

        //自定义文件生成
        List<FileOutConfig> focList = getInjectConfigs(projectPath, pc);

        /*
        cfg.setFileCreate(new IFileCreate() {
            @Override
            public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                // 判断自定义文件夹是否需要创建
                checkDir("调用默认方法创建的目录，自定义目录用");
                if (fileType == FileType.MAPPER) {
                    // 已经生成 mapper 文件判断存在，不想重新生成返回 false
                    return !new File(filePath).exists();
                }
                // 允许生成模板文件
                return true;
            }
        });
        */
        cfg.setFileOutConfigList(focList);
        generator.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = getTemplateConfig();

        generator.setTemplate(templateConfig);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
//        strategy.setSuperEntityClass("BaseEntity"); //这个我直接模版写死了
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        // 公共父类
//        strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
        // 写于父类中的公共字段 todo 去掉创建时间 删除状态等公共字段
        strategy.setSuperEntityColumns("id");
        strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(pc.getModuleName() + "_");
        //设置名称转换器 重要!
        INameConvert nameConvert = new INameConvert() {
            @Override
            public String entityNameConvert(TableInfo tableInfo) {
                //下划线转大写 并首字母大写
                return NamingStrategy.capitalFirst(NamingStrategy.underlineToCamel(tableInfo.getName()));
            }

            @Override
            public String propertyNameConvert(TableField field) {
                //下划线转大写
                return NamingStrategy.underlineToCamel(field.getName());
            }
        };
        strategy.setNameConvert(nameConvert);
        generator.setStrategy(strategy);
        //指定模版引擎
        generator.setTemplateEngine(new BeetlTemplateEngine());
        generator.execute();
    }

}