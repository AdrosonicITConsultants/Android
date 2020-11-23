package com.adrosonic.craftexchangemarketing.ui.modules.admin.selectArtisan

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.predicates.ClusterPredicates
import com.adrosonic.craftexchangemarketing.databinding.ActivityArtisanAddProductTemplateBinding
import com.adrosonic.craftexchangemarketing.databinding.ActivitySelectArtisanBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.productCatalogue.FilteredArtisans
import com.adrosonic.craftexchangemarketing.ui.modules.admin.productCatalog.adapter.ProductCatalogueListAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.admin.productCatalog.addProduct.AdminAddProductTemplateActivity
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.productTemplate.WeaveSelectionAdapter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.ProductCatViewModal
import kotlinx.android.synthetic.main.dialog_are_you_sure.*
import kotlinx.android.synthetic.main.fragment_notifcation.*

fun Context.selectArtisanActivityIntent(): Intent {
    return Intent(this, SelectArtisanActivity::class.java).apply {
    }
}

fun Context.selectArtisanActivityIntent(template: String,imagePath:String): Intent {
    val intent = Intent(this, SelectArtisanActivity::class.java)
    intent.putExtra("productData", template)
    intent.putExtra("imagePath", imagePath)
    return intent.apply { }
//    return Intent(this, ArtisanAddProductTemplateActivity::class.java).apply {
}
class SelectArtisanActivity : AppCompatActivity(),
ArtisanSelectionAdapter.selectionListener,
ProductCatViewModal.FilteredArtisanInterface,
ProductCatViewModal.UploadProdInterface{
    private var mBinding:ActivitySelectArtisanBinding?=null
    val mViewModel: ProductCatViewModal by viewModels()
    var filteredList= ArrayList<Pair<FilteredArtisans,Boolean>>()
    private lateinit var artisanSelectionAdapter: ArtisanSelectionAdapter
    var clusterList=ArrayList<String>()
    var imagePath=ArrayList<String>()
    var productData=""
    var artisanId=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySelectArtisanBinding.inflate(layoutInflater)
        val view = mBinding?.root
        mViewModel.filteredListener=this
        if (intent.extras != null) {
            productData = intent.getStringExtra("productData")
            intent.getStringExtra("imagePath").split(";").forEach { imagePath.add(it) }
        }
        setContentView(view)
        setRecyclerList()
        getClusters()
        val spClusterAdapter = ArrayAdapter<String>(this, R.layout.spinner_item,clusterList)
        spClusterAdapter.setDropDownViewResource(R.layout.spinner_item)
        mBinding?.spCluster?.adapter = spClusterAdapter
        mBinding?.btnBack?.setOnClickListener { finish() }
        mBinding?.btnApply?.setOnClickListener {
            if(Utility.checkIfInternetConnected(this)) {
                val clusterId=ClusterPredicates.getClusterId(mBinding?.spCluster?.selectedItem?.toString()?:"")
                mBinding?.pbLoader?.visibility=View.VISIBLE
                mViewModel.getFilteredArtisans(clusterId.toInt(), mBinding?.searchArtisan?.text.toString())
            }else Utility.displayMessage(getString(R.string.no_internet_connection),this)
        }
        mBinding?.btnSave?.setOnClickListener {
            if(Utility.checkIfInternetConnected(this)) {
                if(artisanId>0) {
                    mBinding?.pbLoader?.visibility = View.VISIBLE
                    mViewModel?.uploadProdListener = this
                    mViewModel.uploadProduct(productData, imagePath, artisanId)
                }else Utility.displayMessage("Please select artisan",this)
            }else Utility.displayMessage(getString(R.string.no_internet_connection),this)
        }

    }

    override fun onArtisanItemSelected(pairList: ArrayList<Pair<FilteredArtisans, Boolean>>)
    {
        pairList.forEach {
           if(it.second) artisanId=it.first.id.toInt()
        }
        filteredList=pairList

//        filteredList.clear()
//        pairList?.forEach {
//           if(it.first.id.equals(artisanId)) filteredList.add(Pair(it.first,true))
//            else  filteredList.add(Pair(it.first,false))
//        }
//        artisanSelectionAdapter.updateProducts(filteredList)
//        setRecyclerList()
//        var dialog = Dialog(this)
//        dialog.setContentView(R.layout.dialog_are_you_sure)
//        dialog.create()
//        dialog.show()
//        dialog.txt_dscrp.text="Are you sure that you want to select artisan $artisanId"
//        dialog.btn_no?.setOnClickListener {
//            dialog.cancel()
//        }
//
//        dialog.btn_yes?.setOnClickListener {
//            if(Utility.checkIfInternetConnected(this)) {
//                if(artisanId>0) {
//                    mBinding?.pbLoader?.visibility = View.VISIBLE
//                    mViewModel?.uploadProdListener = this
//                    mViewModel.uploadProduct(productData, imagePath, artisanId)
//                }else Utility.displayMessage("Please select artisan",this)
//            }else Utility.displayMessage(getString(R.string.no_internet_connection),this)
//        }
    }

    override fun onSuccess(list: List<FilteredArtisans>?) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                mBinding?.pbLoader?.visibility=View.GONE
                filteredList.clear()
                list?.forEach { filteredList.add(Pair(it,false)) }
                setRecyclerList()
            }
            )
        } catch (e: Exception) {
            Log.e("Notifications", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Notifications", "Onsucces")
                mBinding?.pbLoader?.visibility=View.GONE
            }
            )
        } catch (e: Exception) {
            Log.e("Notifications", "Exception onSuccess " + e.message)
        }
    }
    fun setRecyclerList()
    {
        mBinding?.artisanBrandList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        artisanSelectionAdapter = ArtisanSelectionAdapter(this,filteredList)
        mBinding?.artisanBrandList?.adapter = artisanSelectionAdapter
        artisanSelectionAdapter.listener=this
    }
    private fun getClusters(){
        clusterList.clear()
        clusterList.add("Select Cluster")
        var clusterDetailsList=ClusterPredicates.getAllClusters()
        clusterDetailsList?.forEach {
            clusterList.add(it?.cluster?:"")
        }

    }

    override fun onUploadSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
               finish()
                //todo onactivityresult
            }
            )
        } catch (e: Exception) {
        }
    }

    override fun onUploadFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("offline", "onUploadFailure")
                mBinding?.pbLoader?.visibility=View.GONE
                Utility.displayMessage("Unable to upload product",this)
            }
            )
        } catch (e: Exception) {
            Log.e("Notifications", "Exception onSuccess " + e.message)
        }
    }

}