package com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.model;

/**
 * Created by evrencoskun on 27.11.2017.
 */

public class RowHeaderModel {
    private String mData;
    private Integer status;

    public RowHeaderModel(String mData,Integer status) {
        this.mData = mData;
        this.status = status;
    }

    public String getData() {
        return mData;
    }
    public Integer getStatus() {
        return status;
    }
}
