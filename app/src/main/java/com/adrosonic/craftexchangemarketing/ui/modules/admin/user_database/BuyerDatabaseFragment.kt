package com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchangemarketing.database.predicates.ClusterPredicates
import com.adrosonic.craftexchangemarketing.databinding.FragmentUserdbArtisanBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.User
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.MyTableAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.MyTableViewListener
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.DatabaseViewModel
import io.realm.RealmResults


private const val ARG_PARAM1 = "roleId"

class BuyerDatabaseFragment :Fragment(),
    DatabaseViewModel.DbInterface,
    MyTableViewListener.TableListenrs {
        private var roleId: Int = 2
        var mBinding : FragmentUserdbArtisanBinding?= null
        val mViewModel: DatabaseViewModel by viewModels()
        var userList= ArrayList<User>()
        private var mTableAdapter: MyTableAdapter? = null
        var clusterList=ArrayList<String>()
        var clusterDetailsList: RealmResults<ClusterList>? = null
        var ratingList=ArrayList<String>()
        var nameOrder="asc"
        var clusterOrder="asc"
        var ratingOrder="asc"
        var dategOrder="asc"
        var brandOrder="asc"
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            arguments?.let {
//                roleId = it.getInt(ARG_PARAM1)
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_userdb_artisan, container, false)
            return mBinding?.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            mViewModel?.listener=this
            setCount("NA")
            getClusters()
            mBinding?.pbLoader?.visibility=View.VISIBLE
            mBinding?.clusterParent?.visibility=View.GONE
            mViewModel.getDatabaseCountForAdmin(-1,1,-1,roleId,null,"desc","date")

            val spClusterAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item,clusterList)
            spClusterAdapter.setDropDownViewResource(R.layout.spinner_item)
            mBinding?.spCluster?.adapter = spClusterAdapter

            ratingList.clear()
            ratingList.add("Select Rating")
            ratingList.add("Greater than 3")
            ratingList.add("Greater than 6")
            ratingList.add("Greater than 8")
            val spRatingAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item,ratingList)
            spRatingAdapter.setDropDownViewResource(R.layout.spinner_item)
            mBinding?.spRating?.adapter = spRatingAdapter
            initializeTableView()
            mBinding?.btnApply?.setOnClickListener {
                val searchStr= if(mBinding?.searchArtisan?.text.toString().isNullOrEmpty()) null else  mBinding?.searchArtisan?.text.toString()
                var clusterId=-1
                clusterDetailsList?.
                forEach {
                    if(it?.cluster.equals(mBinding?.spCluster?.selectedItem.toString())){
                        clusterId=it.clusterid!!.toInt()
                    }
                }
                val rating= when(mBinding?.spRating?.selectedItemPosition){
                    1->  3
                    2->  6
                    3->  8
                    else->  -1
                }
                if(clusterId.equals(-1) && rating.equals(-1) && searchStr.isNullOrEmpty()) apiCall(false,-1,1,-1,roleId,null,"desc","date")
                else apiCall(true,clusterId,1,rating,roleId,searchStr,"asc","date")
            }
        }
        private fun initializeTableView() {
            // Create TableView Adapter
            mTableAdapter = MyTableAdapter(roleId)
            mBinding?.tableview?.setAdapter(mTableAdapter)
//            if (userList != null && userList.size > 0) {
                mTableAdapter?.setUserList(userList)
//            }
//            MyTableViewListener.tableListenrs=this
            // Create listener
            val tableLister=MyTableViewListener(mBinding?.tableview,userList,roleId)
            tableLister.tableListenrs=this
            mBinding?.tableview?.tableViewListener = tableLister
        }
        override fun onSuccess(userList: List<User>) {
            try {
                Handler(Looper.getMainLooper()).post(Runnable {
                    Log.e(CommonUserFragment.TAG, "onSuccess")
                    this.userList.clear()
                    this.userList.addAll(userList)
                    mBinding?.pbLoader?.visibility=View.GONE
                    initializeTableView()

                }
                )
            } catch (e: Exception) {
                Log.e("BuyerDatabaseFragment", "Exception " + e.message)
            }
        }
        override fun onFailure() {
            try {
                Handler(Looper.getMainLooper()).post(Runnable {
                    Log.e("BuyerDatabaseFragment", "onFailure")
                    mBinding?.pbLoader?.visibility=View.GONE
                }
                )
            } catch (e: Exception) {
                Log.e("BuyerDatabaseFragment", "onFailure " + e.message)
            }
        }
        override fun onCountSuccess(count: Int) {
            try {
                Handler(Looper.getMainLooper()).post(Runnable {
                    Log.e("BuyerDatabaseFragment", "onCountSuccess")
                    setCount(count.toString())
                    apiCall(false,-1,1,-1,roleId,null,"desc","date")
                }
                )
            } catch (e: Exception) {
                Log.e("BuyerDatabaseFragment", "Exception onFailure " + e.message)
            }

        }
        override fun onCountFailure() { }
        private fun apiCall(isFilter:Boolean,clusterId : Int, pageNo:Int, rating:Int, roleId:Int,searchStr:String?, sortBy : String,sortType : String){
            if(Utility.checkIfInternetConnected(requireContext())){
                mBinding?.pbLoader?.visibility=View.VISIBLE
                mViewModel.getDatabaseForAdmin(isFilter,clusterId,pageNo,rating,roleId,searchStr,sortBy,sortType)
            } else Utility.displayMessage(requireContext().getString(R.string.no_internet_connection),requireContext())
        }
        private fun getClusters(){
            clusterList.clear()
            clusterList.add("Select Cluster")
            clusterDetailsList=ClusterPredicates.getAllClusters()
            clusterDetailsList?.forEach {
                clusterList.add(it?.cluster?:"")
            }

        }
        override fun onColumnClick(columnIndex: Int) {
            Log.e("BuyerDatabaseFragment","onColumnClick : $columnIndex RoleId: $roleId")
                when (columnIndex) {
                    1 -> {
                        apiCall(true, -1, 1, -1, roleId, null, "name", nameOrder)
                        if (nameOrder.equals("asc")) nameOrder = "desc"
                        else nameOrder = "asc"
                    }
                    3 -> {
                        apiCall(true, -1, 1, -1, roleId, null, "mobile", clusterOrder)
                        if (clusterOrder.equals("asc")) clusterOrder = "desc"
                        else clusterOrder = "asc"
                    }
                    4 -> {
                        apiCall(true, -1, 1, -1, roleId, null, "rating", ratingOrder)
                        if (ratingOrder.equals("asc")) ratingOrder = "desc"
                        else ratingOrder = "asc"
                    }
                    5 -> {
                        apiCall(true, -1, 1, -1, roleId, null, "date", dategOrder)
                        if (dategOrder.equals("asc")) dategOrder = "desc"
                        else dategOrder = "asc"
                    }
//            0->{
//                apiCall(false,-1,1,-1,1,null,"brand","date")}
                }
        }
        fun setCount(count:String){
                    mBinding?.totalUserCount?.text="Total buyers: $count"

        }
        companion object {

            @JvmStatic
            fun newInstance(roleId: Int) =
                BuyerDatabaseFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_PARAM1, roleId)
                    }
                }
        }
}