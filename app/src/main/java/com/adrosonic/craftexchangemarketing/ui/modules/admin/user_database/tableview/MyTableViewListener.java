package com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.User;
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.holder.ColumnHeaderViewHolder;
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.popup.ColumnHeaderLongPressPopup;
import com.adrosonic.craftexchangemarketing.utils.Utility;
import com.evrencoskun.tableview.ITableView;
import com.evrencoskun.tableview.listener.ITableViewListener;

import java.util.List;
//import com.evrencoskun.tableviewsample2.ui.tableview.holder.ColumnHeaderViewHolder;
//import com.evrencoskun.tableviewsample2.ui.tableview.popup.ColumnHeaderLongPressPopup;

/**
 * Created by evrencoskun on 2.12.2017.
 */

public class MyTableViewListener implements ITableViewListener {
    private static final String LOG_TAG = MyTableViewListener.class.getSimpleName();
    public interface TableListenrs{
        void onColumnClick(int columnIndes);
    }
    private ITableView mTableView;
    private List<User> mUserList;
    public static TableListenrs tableListenrs;
    public MyTableViewListener(ITableView pTableView, List<User> userList) {
        this.mTableView = pTableView;
        this.mUserList=userList;
    }

    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
//        Log.e(LOG_TAG, "onCellClicked has been clicked for x= " + column + " y= " + row);
//        Utility.Companion.displayMessage("onCellClicked",mTableView.getContext());
        //tod cell clicked
    }

    @Override
    public void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
    }

    @Override
    public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
    }

    @Override
    public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {
        Log.e(LOG_TAG, "column has been clicked for " + column);
        if(tableListenrs!=null)tableListenrs.onColumnClick(column);
    }

    @Override
    public void onColumnHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {

    }

    @Override
    public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int  column) {
        if (columnHeaderView != null && columnHeaderView instanceof ColumnHeaderViewHolder) {

            // Create Long Press Popup
            ColumnHeaderLongPressPopup popup = new ColumnHeaderLongPressPopup( (ColumnHeaderViewHolder) columnHeaderView, mTableView);

            // Show
            popup.show();
        }
    }

    @Override
    public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
        Log.e(LOG_TAG, "onRowHeaderClicked has been clicked for " + row);
        Utility.Companion.displayMessage("onRowHeaderClicked ",mTableView.getContext());
        User user=mUserList.get(row);
        //todo call intent here
    }

    @Override
    public void onRowHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {

    }

    @Override
    public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder owHeaderView, int row) {
//        Log.d(LOG_TAG, "onRowHeaderLongPressed has been clicked for " + row);
    }
}
