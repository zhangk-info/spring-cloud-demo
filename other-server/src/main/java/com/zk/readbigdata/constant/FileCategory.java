package com.zk.readbigdata.constant;

import com.zk.commons.exception.ServiceException;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public enum FileCategory {

    /**
     * 测试
     */
    TEST("test", "test", "txt", "4", "UTF-8", "|@|"), // todo remove
    ;

    // 表名
    String tableCode;
    // CRM发送过来的唯一文件名，去掉变化的日期
    String fileName;
    // 文件类型，文件处理时需要
    String fileType;
    // 逻辑主键所在位置，下标，数据清洗需要
    String idIndex;
    // TXT文件的字符编码
    String charSet;
    // 每个字段值的分隔符
    String separator;

    FileCategory(String tableCode, String fileName, String fileType, String idIndex, String charSet, String separator) {
        this.tableCode = tableCode;
        this.fileName = fileName;
        this.fileType = fileType;
        this.idIndex = idIndex;
        this.charSet = charSet;
        this.separator = separator;
    }

    public static FileCategory getByFileName(String fileName) {
        if (fileName == null) {
            return null;
        }
        List<FileCategory> list = Arrays.stream(FileCategory.values()).filter(t -> fileName.contains(t.fileName)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(list)) {
            if (list.size() > 1) {
                throw new ServiceException("不能拿到唯一的文件类型，请检查文件名：" + fileName);
            } else {
                return list.get(0);
            }
        } else {
            throw new ServiceException("没有匹配的文件类型，请检查文件名：" + fileName);
        }
    }

    public static FileCategory getByTableCode(String tableCode) {
        if (tableCode == null) {
            return null;
        }
        List<FileCategory> list = Arrays.stream(FileCategory.values()).filter(t -> tableCode.equals(t.tableCode)).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public String getTableCode() {
        return tableCode;
    }

    public String getFileType() {
        return fileType;
    }

    public String getIdIndex() {
        return idIndex;
    }

    public String getSeparator() {
        return separator;
    }

    public String getCharSet() {
        return charSet;
    }
}