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
import androidx.lifecycle.lifecycleScope
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


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArtisanDatabaseFragment :Fragment(),
    DatabaseViewModel.DbInterface{

    private var param1: String? = null
    private var param2: String? = null

    var mBinding : FragmentUserdbArtisanBinding?= null
    val mViewModel: DatabaseViewModel by viewModels()
    var userList= ArrayList<User>()
    private var mTableAdapter: MyTableAdapter? = null
    var clusterList=ArrayList<String>()
    var clusterDetailsList:RealmResults<ClusterList>? = null
    var ratingList=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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
        getClusters()
        mBinding?.pbLoader?.visibility=View.VISIBLE

        mViewModel.getDatabaseCountForAdmin(-1,1,-1,1,null,"desc","date")

        val spClusterAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item,clusterList)
        spClusterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding?.spCluster?.adapter = spClusterAdapter

        ratingList.clear()
        ratingList.add("Select Rating")
        ratingList.add("Greater than 3")
        ratingList.add("Greater than 6")
        ratingList.add("Greater than 8")
        val spRatingAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item,ratingList)
        spRatingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding?.spRating?.adapter = spRatingAdapter
        mBinding?.btnApply?.setOnClickListener {
        val searchStr= if(mBinding?.searchArtisan?.text.toString().isNullOrEmpty()) null else  mBinding?.searchArtisan?.text.toString()
        var clusterId=-1
            clusterDetailsList?.forEach {
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
         if(clusterId.equals(-1) && rating.equals(-1) && searchStr.isNullOrEmpty()) apiCall(false,-1,1,-1,1,null,"desc","date")
         else apiCall(true,clusterId,1,rating,1,searchStr,"desc","date")
        }
    }
    private fun initializeTableView() {
        // Create TableView Adapter
        mTableAdapter = MyTableAdapter(context)
        mBinding?.tableview?.setAdapter(mTableAdapter)
        if (userList != null && userList.size > 0) {
            mTableAdapter?.setUserList(userList)
        }
        // Create listener
        mBinding?.tableview?.tableViewListener = MyTableViewListener(mBinding?.tableview,userList)
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
            Log.e(CommonUserFragment.TAG, "Exception " + e.message)
        }
    }
    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e(CommonUserFragment.TAG, "onFailure")
                mBinding?.pbLoader?.visibility=View.GONE
            }
            )
        } catch (e: Exception) {
            Log.e(CommonUserFragment.TAG, "onFailure " + e.message)
        }
    }
    override fun onCountSuccess(count: Int) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e(CommonUserFragment.TAG, "onCountSuccess")
                mBinding?.totalUserCount?.text="Total artisans: $count"
                apiCall(false,-1,1,-1,1,null,"desc","date")
            }
            )
        } catch (e: Exception) {
            Log.e(CommonUserFragment.TAG, "Exception onFailure " + e.message)
        }

    }
    override fun onCountFailure() {
    }
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
}