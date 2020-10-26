package com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview;

import android.util.Log;
import android.view.Gravity;


import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.User;
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.model.CellModel;
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.model.ColumnHeaderModel;
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.model.RowHeaderModel;
import com.adrosonic.craftexchangemarketing.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evrencoskun on 4.02.2018.
 */

public class MyTableViewModel {
    // View Types
    public static final int GENDER_TYPE = 1;
    public static final int MONEY_TYPE = 2;

    private List<ColumnHeaderModel> mColumnHeaderModelList;
    private List<RowHeaderModel> mRowHeaderModelList;
    private List<List<CellModel>> mCellModelList;

    public int getCellItemViewType(int column) {

        switch (column) {
            case 5:
                // 5. column header is gender.
                return GENDER_TYPE;
            case 8:
                // 8. column header is Salary.
                return MONEY_TYPE;
            default:
                return 0;
        }
    }

     /*
       - Each of Column Header -
            "Id"
            "Name"
            "Nickname"
            "Email"
            "Birthday"
            "Gender"
            "Age"
            "Job"
            "Salary"
            "CreatedAt"
            "UpdatedAt"
            "Address"
            "Zip Code"
            "Phone"
            "Fax"
     */

    public int getColumnTextAlign(int column) {
        switch (column) {
            // Id
            case 0:
                return Gravity.CENTER;
            // Name
            case 1:
                return Gravity.LEFT;
            // Nickname
            case 2:
                return Gravity.LEFT;
            // Email
            case 3:
                return Gravity.LEFT;
            // BirthDay
            case 4:
                return Gravity.CENTER;
            // Gender (Sex)
            case 5:
                return Gravity.CENTER;
            // Age
            case 6:
                return Gravity.CENTER;
            // Job
            case 7:
                return Gravity.LEFT;
            // Salary
            case 8:
                return Gravity.CENTER;
            // CreatedAt
            case 9:
                return Gravity.CENTER;
            // UpdatedAt
            case 10:
                return Gravity.CENTER;
            // Address
            case 11:
                return Gravity.LEFT;
            // Zip Code
            case 12:
                return Gravity.RIGHT;
            // Phone
            case 13:
                return Gravity.RIGHT;
            // Fax
            case 14:
                return Gravity.RIGHT;
            default:
                return Gravity.CENTER;
        }

    }

    private List<ColumnHeaderModel> createColumnHeaderModelList() {
        List<ColumnHeaderModel> list = new ArrayList<>();

        // Create Column Headers
        list.add(new ColumnHeaderModel("Artisan Id"));
        list.add(new ColumnHeaderModel("Name"));
        list.add(new ColumnHeaderModel("Email"));
        list.add(new ColumnHeaderModel("Cluster"));
        list.add(new ColumnHeaderModel("Rating"));
        list.add(new ColumnHeaderModel("Date"));
//        list.add(new ColumnHeaderModel("Status"));
        return list;
    }

    private List<List<CellModel>> createCellModelList(List<User> userList) {
        List<List<CellModel>> lists = new ArrayList<>();

        // Creating cell model list from User list for Cell Items
        // In this example, User list is populated from web service

        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            List<CellModel> list = new ArrayList<>();

            // The order should be same with column header list;

            list.add(new CellModel("1-" + i, user.getId()));          // "Id"
            list.add(new CellModel("2-" + i, user.getFirstName()+" "+ user.getLastName()));        // "Name"
            list.add(new CellModel("3-" + i, user.getEmail()));    // "Nickname"
            list.add(new CellModel("4-" + i, user.getCluster()));       // "Email"
            list.add(new CellModel("5-" + i, user.getRating()));   // "BirthDay"
            list.add(new CellModel("6-" + i, Utility.Companion.returnDisplayDate(user.getDateAdded())));      // "Gender"
//            list.add(new CellModel("7-" + i, user.getStatus()));         // "Age"
            lists.add(list);
        }

        return lists;
    }

    private List<RowHeaderModel> createRowHeaderList(List<User> userList) {
        List<RowHeaderModel> list = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            // In this example, Row headers just shows the index of the TableView List.
            User user=userList.get(i);
            Log.e("RowHeaderModel",""+user.getBrandName());
            if(user.getBrandName()==null)list.add(new RowHeaderModel("NA",user.getStatus()));
            else list.add(new RowHeaderModel(""+user.getBrandName(),user.getStatus()));
        }
        return list;
    }


    public List<ColumnHeaderModel> getColumHeaderModeList() {
        return mColumnHeaderModelList;
    }

    public List<RowHeaderModel> getRowHeaderModelList() {
        return mRowHeaderModelList;
    }

    public List<List<CellModel>> getCellModelList() {
        return mCellModelList;
    }


    public void generateListForTableView(List<User> users) {
        mColumnHeaderModelList = createColumnHeaderModelList();
        mCellModelList = createCellModelList(users);
        mRowHeaderModelList = createRowHeaderList(users);
    }

}



