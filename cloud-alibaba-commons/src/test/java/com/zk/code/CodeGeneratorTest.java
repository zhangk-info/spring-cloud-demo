package com.zk.code;

public class CodeGeneratorTest {


    //代码生成
//    public static void main(String[] args) {
//        CodeGenerator c = new CodeGenerator();
//        c.generate(null);
//
//    }

    public static void main(String[] args) {
        Long[] ids = {1l, 2l};
        StringBuffer idsStr = new StringBuffer();
        for (Long id : ids) {
            idsStr.append(String.format("'%s',", id));
        }
        idsStr.deleteCharAt(idsStr.length() - 1);
        String sql = "update line_design set jlsp='通过' where id in (${ids})";
        sql = sql.replaceAll("\\$\\{ids}", idsStr.toString());
        System.out.println(sql);
    }
}
