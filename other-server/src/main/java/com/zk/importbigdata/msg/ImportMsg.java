package com.zk.importbigdata.msg;

import com.zk.importbigdata.config.Header;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImportMsg {

    private ImportStatus status;
    private Long importId;
    private String message;
    private List<RowDataErrorMsg> rowError;
    private List<Header> header;
    private Integer total;
    private Integer errorTotal;
    private Integer insertTotal;
    private Integer updateTotal;
    private Boolean allowUpdate = false;
    private Integer currentRow;
    private Date startDate;
    private Date endDate;
    private Long userId;

    public ImportMsg(Long importId, List<Header> header) {
        this.importId = importId;
        this.header = header;
        this.rowError = new ArrayList();
    }

    public void setError(String msg) {
        this.status = ImportStatus.ERROR;
        this.message = msg;
    }

    public ImportMsg addError(RowDataErrorMsg error) {
        this.rowError.add(error);
        return this;
    }

    public List<Header> getHeader() {
        return this.header;
    }

    public void setHeader(List<Header> header) {
        this.header = header;
    }

    public Integer getTotal() {
        return this.total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getErrorTotal() {
        return this.errorTotal;
    }

    public void setErrorTotal(Integer errorTotal) {
        this.errorTotal = errorTotal;
    }

    public Integer getInsertTotal() {
        return this.insertTotal;
    }

    public void setInsertTotal(Integer insertTotal) {
        this.insertTotal = insertTotal;
    }

    public Integer getUpdateTotal() {
        return this.updateTotal;
    }

    public void setUpdateTotal(Integer updateTotal) {
        this.updateTotal = updateTotal;
    }

    public ImportStatus getStatus() {
        return this.status;
    }

    public void setStatus(ImportStatus status) {
        this.status = status;
    }

    public List<RowDataErrorMsg> getRowError() {
        return this.rowError;
    }

    public void setRowError(List<RowDataErrorMsg> rowError) {
        this.rowError = rowError;
    }

    public Long getImportId() {
        return this.importId;
    }

    public void setImportId(Long importId) {
        this.importId = importId;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCurrentRow() {
        return this.currentRow;
    }

    public void setCurrentRow(Integer currentRow) {
        this.currentRow = currentRow;
    }

    public Boolean getAllowUpdate() {
        return this.allowUpdate;
    }

    public void setAllowUpdate(Boolean allowUpdate) {
        this.allowUpdate = allowUpdate;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
