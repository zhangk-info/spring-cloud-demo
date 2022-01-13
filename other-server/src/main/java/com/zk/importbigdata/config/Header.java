package com.zk.importbigdata.config;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zk.importbigdata.format.ColFormat;
import com.zk.importbigdata.validate.ExcelValueValidate;

// 一列的配置
public class Header {

    // 表头或者下标
    private String cnNameOrSort;
    // 最大长度
    private Integer length;
    // 实体参数名称
    private String name;
    // 是否逻辑主键
    private boolean logicPk;
    // 是否必须
    private boolean mustInput;
    // 当前列的format
    @JsonIgnore
    private ColFormat format;
    // 当前列的 validate
    @JsonIgnore
    private ExcelValueValidate validate;

    private Class type;

    public Header(String name, String cnNameOrSort) {
        this.logicPk = false;
        this.mustInput = false;
        this.name = name;
        this.cnNameOrSort = cnNameOrSort;
    }

    public Header(String name, String cnNameOrSort, boolean mustInput) {
        this.logicPk = false;
        this.mustInput = false;
        this.name = name;
        this.cnNameOrSort = cnNameOrSort;
        this.mustInput = mustInput;
    }

    public Header(String name, String cnNameOrSort, boolean mustInput, ExcelValueValidate validate) {
        this.logicPk = false;
        this.mustInput = false;
        this.name = name;
        this.cnNameOrSort = cnNameOrSort;
        this.mustInput = mustInput;
        this.validate = validate;
    }

    public Header(String name, String cnNameOrSort, ExcelValueValidate validate) {
        this(name, cnNameOrSort, false, validate);
    }

    public Header(String name, String cnNameOrSort, Integer length) {
        this.logicPk = false;
        this.mustInput = false;
        this.name = name;
        this.cnNameOrSort = cnNameOrSort;
        this.length = length;
    }

    public Header(String name, String cnNameOrSort, ColFormat format) {
        this.logicPk = false;
        this.mustInput = false;
        this.name = name;
        this.cnNameOrSort = cnNameOrSort;
        this.format = format;
    }

    public Header(String name, String cnNameOrSort, Integer length, ColFormat format) {
        this.logicPk = false;
        this.mustInput = false;
        this.name = name;
        this.cnNameOrSort = cnNameOrSort;
        this.length = length;
        this.format = format;
    }

    public Header(String name, String cnNameOrSort, boolean mustInput, ColFormat format) {
        this.logicPk = false;
        this.mustInput = false;
        this.name = name;
        this.cnNameOrSort = cnNameOrSort;
        this.mustInput = mustInput;
        this.format = format;
    }

    public static Header createLogicPk(String name, String cnName) {
        Header header = new Header(name, cnName);
        header.setLogicPk(true);
        header.setMustInput(true);
        return header;
    }

    public static Header createLogicPk(String name, String cnName, ColFormat format) {
        Header header = new Header(name, cnName);
        header.setLogicPk(true);
        header.setMustInput(true);
        header.setFormat(format);
        return header;
    }

    public static Header createLogicPk(String name, String cnName, ExcelValueValidate validate) {
        Header header = new Header(name, cnName);
        header.setLogicPk(true);
        header.setMustInput(true);
        header.setValidate(validate);
        return header;
    }

    public String getCnNameOrSort() {
        return this.cnNameOrSort;
    }

    public void setCnNameOrSort(String cnNameOrSort) {
        this.cnNameOrSort = cnNameOrSort;
    }

    public Integer getLength() {
        return this.length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColFormat getFormat() {
        return this.format;
    }

    public void setFormat(ColFormat format) {
        this.format = format;
    }

    public Class getType() {
        return this.type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public boolean isLogicPk() {
        return this.logicPk;
    }

    public void setLogicPk(boolean logicPk) {
        this.logicPk = logicPk;
    }

    public boolean isMustInput() {
        return this.mustInput;
    }

    public void setMustInput(boolean mustInput) {
        this.mustInput = mustInput;
    }

    public ExcelValueValidate getValidate() {
        return this.validate;
    }

    public void setValidate(ExcelValueValidate validate) {
        this.validate = validate;
    }
}
