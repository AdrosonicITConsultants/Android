package com.adrosonic.craftexchangemarketing.ui.modules.admin.productCatalog

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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.AdminProductCatalogue
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.Notifications
import com.adrosonic.craftexchangemarketing.database.predicates.ClusterPredicates
import com.adrosonic.craftexchangemarketing.databinding.FragmentProdArtisanBinding
import com.adrosonic.craftexchangemarketing.databinding.FragmentUserdbArtisanBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase.User
import com.adrosonic.craftexchangemarketing.ui.modules.Notification.NotificationAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.productCatalog.adapter.ProductCatalogueListAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.MyTableAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.MyTableViewListener
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.tableview.MyTableViewListener.TableListenrs
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.productTemplate.yarnFrgamnets.WarpFragment
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.DatabaseViewModel
import com.adrosonic.craftexchangemarketing.viewModels.ProductCatViewModal
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_notifcation.*


private const val ARG_PARAM1 = "roleId"

class AntaranProductFragment() :Fragment(),
    ProductCatViewModal.ProdInterface
    {
    private var roleId = 1L
    private lateinit var prodAdapter: ProductCatalogueListAdapter
    var mBinding : FragmentProdArtisanBinding?= null
    val mViewModel: ProductCatViewModal by viewModels()
    var clusterList=ArrayList<String>()
    var clusterDetailsList:RealmResults<ClusterList>? = null
    var availabilityList=ArrayList<String>()
    var cluster=ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_prod_artisan, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel?.listener=this
        getClusters()
        setCount()
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mBinding?.swipeRefreshLayout?.isRefreshing=true
            mViewModel.getArtisanProducts()
        }
        mBinding?.productList?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        prodAdapter = ProductCatalogueListAdapter(requireContext(), mViewModel.getProductsMutableData(roleId,"","","").value,true)
        mBinding?.productList?.adapter = prodAdapter
        Log.e("Wishlist", "Size :" + mViewModel.getProductsMutableData(roleId,"","","").value?.size)
        mBinding?.swipeRefreshLayout?.setOnRefreshListener {
            mBinding?.swipeRefreshLayout?.isRefreshing=true
            mViewModel.getArtisanProducts()
        }
        mViewModel.getProductsMutableData(roleId,"","","").observe(viewLifecycleOwner, Observer<RealmResults<AdminProductCatalogue>> {
            prodAdapter.updateProducts(it)
        })

        val spClusterAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item,clusterList)
        spClusterAdapter.setDropDownViewResource(R.layout.spinner_item)
        mBinding?.spCluster?.adapter = spClusterAdapter

        availabilityList.clear()
        availabilityList.add("All")
        availabilityList.add("Made to order")
        availabilityList.add("Available in stock")
        val spAvailabilitygAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item,availabilityList)
        spAvailabilitygAdapter.setDropDownViewResource(R.layout.spinner_item)
        mBinding?.spAvailability?.adapter = spAvailabilitygAdapter
        mBinding?.btnApply?.setOnClickListener {
        val searchStr= if(mBinding?.searchArtisan?.text.toString().isNullOrEmpty()) "" else  mBinding?.searchArtisan?.text.toString()
         mViewModel?.getProductsMutableData(roleId,searchStr,mBinding?.spCluster?.selectedItem.toString(),mBinding?.spAvailability?.selectedItem.toString())
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                mBinding?.swipeRefreshLayout?.isRefreshing=false
                mViewModel.getProductsMutableData(roleId,"","","")
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
                mBinding?.swipeRefreshLayout?.isRefreshing=false
            }
            )
        } catch (e: Exception) {
            Log.e("ArtisanDatabaseFragment", "onFailure " + e.message)
        }
    }
    override fun onCountSuccess(count: Long) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("ArtisanDatabaseFragment", "onCountSuccess")
                setCount()
            }
            )
        } catch (e: Exception) {
            Log.e("ArtisanDatabaseFragment", "Exception onFailure " + e.message)
        }

    }
    override fun onCountFailure() { }
    private fun apiCall(availability: Long, clusterID: Long, madeWithAntaran: Long, pageNo: Long,
                        searchStr: String,sortBy: String,sortType: String){
        if(Utility.checkIfInternetConnected(requireContext())){
            mBinding?.swipeRefreshLayout?.isRefreshing=false
            mViewModel.getArtisanProducts()
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
        fun setCount(){
            mBinding?.totalProdCount?.text="Total Products: ${mViewModel.getProductsMutableData(roleId,"","","").value?.size}"
        }

    companion object {
        @JvmStatic
        fun newInstance(roleId: Int) =
            AntaranProductFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, roleId)
                }
            }
    }
}