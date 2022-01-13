package com.zk.importbigdata.msg;

import java.util.ArrayList;
import java.util.List;

public class RowDataErrorMsg {

    private int rowIndex;
    private boolean repeat = false;
    private boolean blankRow = false;
    private List<ColDataErrorMsg> colError;

    public RowDataErrorMsg(int rowIndex) {
        this.rowIndex = rowIndex;
        this.colError = new ArrayList();
    }

    public RowDataErrorMsg addError(ColDataErrorMsg msg) {
        this.colError.add(msg);
        return this;
    }

    public Integer getRowIndex() {
        return this.rowIndex;
    }

    public List<ColDataErrorMsg> getColError() {
        return this.colError;
    }

    public void setColError(List<ColDataErrorMsg> colError) {
        this.colError = colError;
    }

    public boolean isRepeat() {
        return this.repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isBlankRow() {
        return this.blankRow;
    }

    public void setBlankRow(boolean blankRow) {
        this.blankRow = blankRow;
    }
}
