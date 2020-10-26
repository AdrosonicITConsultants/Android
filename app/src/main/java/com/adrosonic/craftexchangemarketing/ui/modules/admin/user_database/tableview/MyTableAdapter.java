package com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.adrosonic.craftexchangemarketing.R;
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.User;
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.holder.CellViewHolder;
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.holder.ColumnHeaderViewHolder;
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.holder.MoneyCellViewHolder;
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.holder.RowHeaderViewHolder;
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.model.CellModel;
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.model.ColumnHeaderModel;
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.model.RowHeaderModel;
import com.evrencoskun.tableview.adapter.AbstractTableAdapter;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder;
import com.evrencoskun.tableview.adapter.recyclerview.holder.AbstractViewHolder;

import java.util.List;

/**
 * Created by evrencoskun on 27.11.2017.
 */

public class MyTableAdapter extends AbstractTableAdapter<ColumnHeaderModel, RowHeaderModel, CellModel> {

    private MyTableViewModel myTableViewModel;

    public MyTableAdapter(Context p_jContext) {
        super();
        this.myTableViewModel = new MyTableViewModel();
    }


    @Override
    public AbstractViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
        View layout;

        switch (viewType) {
//            case MyTableViewModel.GENDER_TYPE:
//                // Get gender cell xml Layout
//                layout = LayoutInflater.from(mContext).inflate(R.layout
//                        .tableview_gender_cell_layout, parent, false);
//
//                return new GenderCellViewHolder(layout);


//            case MyTableViewModel.MONEY_TYPE:
//                // Get money cell xml Layout
//                layout = LayoutInflater.from(mContext).inflate(R.layout
//                        .tableview_money_cell_layout, parent, false);
//
//                // Create the relevant view holder
//                return new MoneyCellViewHolder(layout);
            default:
                // Get default Cell xml Layout
                layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.tableview_cell_layout,
                        parent, false);

                // Create a Cell ViewHolder
                return new CellViewHolder(layout);
        }
    }

    @Override
    public void onBindCellViewHolder(@NonNull AbstractViewHolder holder, @Nullable CellModel cellItemModel, int columnPosition, int rowPosition) {
        CellModel cell = cellItemModel;
        ((CellViewHolder) holder).setCellModel(cell, columnPosition);
    }


    @Override
    public AbstractSorterViewHolder onCreateColumnHeaderViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.tableview_column_header_layout, parent, false);

        return new ColumnHeaderViewHolder(layout, getTableView());
    }

    @Override
    public void onBindColumnHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable ColumnHeaderModel columnHeaderItemModel, int columnPosition) {
        ColumnHeaderModel columnHeader = columnHeaderItemModel;
        // Get the holder to update cell item text
        ColumnHeaderViewHolder columnHeaderViewHolder = (ColumnHeaderViewHolder) holder;

        columnHeaderViewHolder.setColumnHeaderModel(columnHeader, columnPosition);
    }

    @Override
    public AbstractViewHolder onCreateRowHeaderViewHolder(ViewGroup parent, int viewType) {

        // Get Row Header xml Layout
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.tableview_row_header_layout,parent,false);

        // Create a Row Header ViewHolder
        return new RowHeaderViewHolder(layout);
    }

    @Override
    public void onBindRowHeaderViewHolder(@NonNull AbstractViewHolder holder, @Nullable RowHeaderModel rowHeaderItemModel, int rowPosition) {
        RowHeaderModel rowHeaderModel = rowHeaderItemModel;

        RowHeaderViewHolder rowHeaderViewHolder = (RowHeaderViewHolder) holder;
        rowHeaderViewHolder.row_header_textview.setText(rowHeaderModel.getData());
        if(rowHeaderModel.getStatus().equals(2)) rowHeaderViewHolder.txt_status.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.red_logo));
        else if(rowHeaderModel.getStatus().equals(1)) rowHeaderViewHolder.txt_status.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.dark_green));

    }

    @NonNull
    @Override
    public View onCreateCornerView(@NonNull ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.tableview_corner_layout, null, false);
    }

    @Override
    public int getColumnHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getRowHeaderItemViewType(int position) {
        return 0;
    }

    @Override
    public int getCellItemViewType(int position) {
        return myTableViewModel.getCellItemViewType(position);
    }


    /**
     * This method is not a generic Adapter method. It helps to generate lists from single user
     * list for this adapter.
     */
    public void setUserList(List<User> userList) {
        // Generate the lists that are used to TableViewAdapter
        myTableViewModel.generateListForTableView(userList);

        // Now we got what we need to show on TableView.
        setAllItems(myTableViewModel.getColumHeaderModeList(), myTableViewModel.getRowHeaderModelList(), myTableViewModel.getCellModelList());
    }

}
