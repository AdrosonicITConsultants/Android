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
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.UserDatabase
import com.adrosonic.craftexchangemarketing.database.predicates.ClusterPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.UserDatabasePredicates
import com.adrosonic.craftexchangemarketing.database.predicates.UserPredicates
import com.adrosonic.craftexchangemarketing.databinding.FragmentUserdbArtisanBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.User
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.MyTableAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.MyTableViewListener
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.MyTableViewListener.TableListenrs
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.productTemplate.yarnFrgamnets.WarpFragment
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.DatabaseViewModel
import io.realm.RealmResults
import io.realm.Sort


private const val ARG_PARAM1 = "roleId"

class ArtisanDatabaseFragment() :Fragment(),
    DatabaseViewModel.DbInterface,
    TableListenrs{

    private var roleId: Int = 1
    var mBinding : FragmentUserdbArtisanBinding?= null
    val mViewModel: DatabaseViewModel by viewModels()
    var userList= ArrayList<UserDatabase>()
    private var mTableAdapter: MyTableAdapter? = null
    var clusterList=ArrayList<String>()
    var clusterDetailsList:RealmResults<ClusterList>? = null
    var ratingList=ArrayList<String>()
    var nameOrder=Sort.DESCENDING
    var clusterOrder=Sort.DESCENDING
    var ratingOrder=Sort.DESCENDING
    var dategOrder=Sort.DESCENDING
    var brandOrder=Sort.DESCENDING
    var isSearch=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            roleId = it.getInt(ARG_PARAM1)
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
        mBinding?.btnApply?.setOnClickListener {
            val searchStr= if(mBinding?.searchArtisan?.text.toString().isNullOrEmpty()) "" else  mBinding?.searchArtisan?.text.toString()
            var cluster=mBinding?.spCluster?.selectedItem.toString()
            val rating= when(mBinding?.spRating?.selectedItemPosition){
                1->  3.0f
                2->  6.0f
                3->  8.0f
                else->  0.0f
            }
            apiCall(false,-1,1,-1,roleId,null,"desc","date",false)
            initializeTableView(UserDatabasePredicates.getUsers(true,searchStr,cluster,rating,"",Sort.DESCENDING))
        }
        initializeTableView(UserDatabasePredicates.getUsers(true,"","",0.0f,"",Sort.DESCENDING))
    }
    private fun initializeTableView(userList:List<UserDatabase>?) {
        // Create TableView Adapter
        Log.e("ArtisanDatabaseFragment", "initializeTableView ${userList?.size}")
        mTableAdapter = MyTableAdapter(roleId)
        mBinding?.tableview?.setAdapter(mTableAdapter)
        mTableAdapter?.setUserList(userList)
        Log.e("ArtisanDatabaseFragment", "initializeTableView 1111111111111")
        val tableLister=MyTableViewListener(mBinding?.tableview,userList,roleId)
        tableLister.tableListenrs=this
        mBinding?.tableview?.tableViewListener = tableLister
        Log.e("ArtisanDatabaseFragment", "initializeTableView 222222222")
    }

    override fun onSuccess(userList: List<User>) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("ArtisanDatabaseFragment", "onSuccess")
                mBinding?.pbLoader?.visibility=View.GONE
                val searchStr= if(mBinding?.searchArtisan?.text.toString().isNullOrEmpty()) "" else  mBinding?.searchArtisan?.text.toString()
                var cluster=mBinding?.spCluster?.selectedItem.toString()
                val rating= when(mBinding?.spRating?.selectedItemPosition){
                    1->  3.0f
                    2->  6.0f
                    3->  8.0f
                    else->  0.0f
                }
                initializeTableView(UserDatabasePredicates.getUsers(true,searchStr,cluster,rating,"",Sort.ASCENDING))
            }
            )
        } catch (e: Exception) {
            Log.e("ArtisanDatabaseFragment", "Exception " + e.message)
        }
    }
    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("ArtisanDatabaseFragment", "onFailure")
                mBinding?.pbLoader?.visibility=View.GONE
            }
            )
        } catch (e: Exception) {
            Log.e("ArtisanDatabaseFragment", "onFailure " + e.message)
        }
    }
    override fun onCountSuccess(count: Int) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("ArtisanDatabaseFragment", "onCountSuccess")
                setCount(count.toString())
                apiCall(false,-1,1,-1,roleId,null,"desc","date",true)
            }
            )
        } catch (e: Exception) {
            Log.e("ArtisanDatabaseFragment", "Exception onFailure " + e.message)
        }

    }
    override fun onCountFailure() { }
    private fun apiCall(isFilter:Boolean,clusterId : Int, pageNo:Int, rating:Int, roleId:Int,searchStr:String?, sortBy : String,sortType : String,showPbLoader:Boolean){
        if(Utility.checkIfInternetConnected(requireContext())){
           if(showPbLoader) mBinding?.pbLoader?.visibility=View.VISIBLE
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
        Log.e("ArtisanDatabaseFragment","onColumnClick : $columnIndex RoleId: $roleId")
        val searchStr= if(mBinding?.searchArtisan?.text.toString().isNullOrEmpty()) "" else  mBinding?.searchArtisan?.text.toString()
        var cluster=mBinding?.spCluster?.selectedItem.toString()
        val rating= when(mBinding?.spRating?.selectedItemPosition){
            1->  3.0f
            2->  6.0f
            3->  8.0f
            else->  0.0f
        }
        if(roleId.equals(1)) {
            when (columnIndex) {
                1 -> {
                    initializeTableView(UserDatabasePredicates.getUsers(false,searchStr,cluster,rating,"firstName",nameOrder))
                    if (nameOrder.equals(Sort.ASCENDING)) nameOrder = Sort.DESCENDING
                    else nameOrder = Sort.ASCENDING
                }
                3 -> {
                    initializeTableView(UserDatabasePredicates.getUsers(false,searchStr,cluster,rating,"cluster",clusterOrder))
                    if (clusterOrder.equals(Sort.ASCENDING)) clusterOrder = Sort.DESCENDING
                    else clusterOrder = Sort.ASCENDING
                }
                4 -> {
                    initializeTableView(UserDatabasePredicates.getUsers(false,searchStr,cluster,rating,"rating",ratingOrder))
                    if (ratingOrder.equals(Sort.ASCENDING)) ratingOrder = Sort.DESCENDING
                    else ratingOrder =Sort.ASCENDING
                }
                5 -> {
                    initializeTableView(UserDatabasePredicates.getUsers(false,searchStr,cluster,rating,"dateAdded",dategOrder))
                    if (dategOrder.equals(Sort.ASCENDING)) dategOrder = Sort.DESCENDING
                    else dategOrder = Sort.ASCENDING
                }
            }
        }
    }
    fun setCount(count:String){
        when(roleId){
            1->{
                mBinding?.totalUserCount?.text="Total artisans: $count"
            }
            2->{
                mBinding?.totalUserCount?.text="Total buyers: $count"
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        val searchStr= if(mBinding?.searchArtisan?.text.toString().isNullOrEmpty()) null else  mBinding?.searchArtisan?.text.toString()
//        var clusterId=-1
//        clusterDetailsList?.
//        forEach {
//            if(it?.cluster.equals(mBinding?.spCluster?.selectedItem.toString())){
//                clusterId=it.clusterid!!.toInt()
//            }
//        }
//        val rating= when(mBinding?.spRating?.selectedItemPosition){
//            1->  3
//            2->  6
//            3->  8
//            else->  -1
//        }
//        isSearch=true
//        if(clusterId.equals(-1) && rating.equals(-1) && searchStr.isNullOrEmpty()) apiCall(false,-1,1,-1,roleId,null,"desc","date")
//        else apiCall(true,clusterId,1,rating,roleId,searchStr,"asc","date")
        apiCall(false,-1,1,-1,roleId,null,"desc","date",true)
    }
    companion object {

        @JvmStatic
        fun newInstance(roleId: Int) =
            ArtisanDatabaseFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, roleId)
                }
            }
    }
}