package com.adrosonic.craftexchangemarketing.ui.modules.admin.redirectEnquiries

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.predicates.ClusterPredicates
import com.adrosonic.craftexchangemarketing.databinding.ActivitySelectLessthan8ArtisanBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.redirectedEnquiries.ArtisanData
import com.adrosonic.craftexchangemarketing.ui.modules.admin.redirectEnquiries.adapter.ArtisanMultiSelectionAdapter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.RedirectedEnquiryViewModel


fun Context.selectLessThan8ArtisanActivityIntent(enquiryId:Long): Intent {
    val intent = Intent(this, SelectLessThan8ArtisanActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    return intent.apply { }
//    return Intent(this, ArtisanAddProductTemplateActivity::class.java).apply {
}
class SelectLessThan8ArtisanActivity : AppCompatActivity(),
    ArtisanMultiSelectionAdapter.selectionListener,
    RedirectedEnquiryViewModel.ProductDetailsInterface,
    RedirectedEnquiryViewModel.FetchArtisanInterface {
    private var mBinding:ActivitySelectLessthan8ArtisanBinding?=null
    val mViewModel: RedirectedEnquiryViewModel by viewModels()
    var filteredList= ArrayList<Pair<ArtisanData,Boolean>>()
    private lateinit var artisanSelectionAdapter: ArtisanMultiSelectionAdapter
    var clusterList=ArrayList<String>()
    var imagePath=ArrayList<String>()
    var artisanId=0
    var productId=0L
    var enquiryId=0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySelectLessthan8ArtisanBinding.inflate(layoutInflater)
        val view = mBinding?.root
//        mViewModel.filteredListener=this
        if (intent.extras != null) {
            productId = intent.getLongExtra("productId",0L)
            enquiryId = intent.getLongExtra("enquiryId",0L)
        }
        if(Utility.checkIfInternetConnected(this)) {
            mBinding?.pbLoader?.visibility=View.VISIBLE
            mViewModel?.artisanListener = this
            mViewModel.getArtisansLessThan8Rating(0, "",enquiryId.toInt())
        }else Utility.displayMessage(getString(R.string.no_internet_connection),this)

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
                mViewModel?.artisanListener = this
                mViewModel.getArtisansLessThan8Rating(clusterId.toInt(), mBinding?.searchArtisan?.text.toString(),enquiryId.toInt())
            }else Utility.displayMessage(getString(R.string.no_internet_connection),this)
        }
        mBinding?.btnSave?.setOnClickListener {
            if(Utility.checkIfInternetConnected(this)) {
                var userIds=""
                filteredList.forEach {
                    if(it.second) userIds=userIds+it.first.artisan.id+","
                }
                if (userIds.endsWith(",")) {
                    userIds = userIds.substring(0, userIds.length- 1);
                }
                Log.e("SendCutEnq","userIds: $userIds")
                if(userIds.isNotBlank()) {
                    mBinding?.pbLoader?.visibility = View.VISIBLE
                    mViewModel?.prodListener=this
                    mViewModel.sendCustomEnquiry(userIds, enquiryId.toInt())
                }else Utility.displayMessage("Please select artisan",this)
            }else Utility.displayMessage(getString(R.string.no_internet_connection),this)
        }
        mBinding?.chbSelectAll?.setOnCheckedChangeListener { compoundButton, b ->

                var i=0
                while (i<filteredList.size){
                    val obj=filteredList.get(i).first

                    if(b) filteredList?.set(i, Pair(obj,true))
                    else filteredList?.set(i, Pair(obj,false))
//                    pairList?.set(position, Pair(dscrp, true))
//                    notifyItemRangeChanged(position, pairList?.size?:0)
                    i++
                }
            artisanSelectionAdapter?.notifyDataSetChanged()
        }


    }


    fun setRecyclerList()
    {
        mBinding?.artisanBrandList?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        artisanSelectionAdapter = ArtisanMultiSelectionAdapter(this,filteredList)
        mBinding?.artisanBrandList?.adapter = artisanSelectionAdapter
        artisanSelectionAdapter.listener=this
        mBinding?.txtArtisanBeandCount?.text="Found ${filteredList.size} artisan brands"
    }
    private fun getClusters(){
        clusterList.clear()
        clusterList.add("Select Cluster")
        var clusterDetailsList=ClusterPredicates.getAllClusters()
        clusterDetailsList?.forEach {
            clusterList.add(it?.cluster?:"")
        }

    }


    override fun onFetchArtisanFailure() {
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

    override fun onFetchArtisanSuccess(list: List<ArtisanData>?) {
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

    override fun onArtisanItemSelected(list: ArrayList<Pair<ArtisanData, Boolean>>) {
        filteredList=list
    }

    override fun onSuccess() {
       finish()
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Notifications", "Onsucces")
                mBinding?.pbLoader?.visibility=View.GONE
                Utility.displayMessage("Unable to send enquiry",this)
            }
            )
        } catch (e: Exception) {
            Log.e("Notifications", "Exception onSuccess " + e.message)
        }
    }

}