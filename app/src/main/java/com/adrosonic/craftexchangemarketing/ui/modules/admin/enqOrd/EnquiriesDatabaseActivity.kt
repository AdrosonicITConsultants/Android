package com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.CategoryProducts
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ClusterList
import com.adrosonic.craftexchangemarketing.database.predicates.ClusterPredicates
import com.adrosonic.craftexchangemarketing.databinding.EnquiryDatabaseActivityBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnqiuiryListResponse
import com.adrosonic.craftexchangemarketing.repository.data.response.enquiryOrderDatabase.EnquiryData
import com.adrosonic.craftexchangemarketing.ui.modules.admin.enqOrd.adapter.ELAdapter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.EnquiryOrderViewModel
import io.realm.RealmResults
import java.text.SimpleDateFormat
import java.util.*


fun Context.enquiriesDatabaseIntent(enquiryCount: Long, type: Long): Intent {
    val intent = Intent(this, EnquiriesDatabaseActivity::class.java)
    intent.putExtra("enquiryCount", enquiryCount)
    intent.putExtra("type", type)
    return intent.apply { flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        Intent.FLAG_ACTIVITY_NEW_TASK  }
}
class EnquiriesDatabaseActivity: AppCompatActivity(),
EnquiryOrderViewModel.EnquiryListInterface{
    var scrollcall = 0
    var type : Long?=1
    var availability : Int?=null
    var buyerBrand : String??=null
    var clusterId : Int?=null
    var enquiryId : String??=null
    var fromDate : String?=null
    var madeWithAntaran : Int?=null
    var pageNo : Long?=null
    var productCategory : Int?=null
    var statusId : Int?=null
    var toDate : String?=null
    var weaverIdOrBrand :String?=null
    var Availibility=ArrayList<String>()
    var clusterList=ArrayList<String>()
    var categoryList=ArrayList<String>()
    var clusterDetailsList: RealmResults<ClusterList>? = null
    var CategoryDetailsList:RealmResults<CategoryProducts>? = null

    private lateinit var selectedRadioButton: RadioButton
    private val TAG = "EnquiriesDatabase"
    var mEnquiryListAdapter : ELAdapter?= null
    var enquiryList: ArrayList<EnquiryData> = arrayListOf()
    var enquiryCount : Long?=null
    private var mBinding : EnquiryDatabaseActivityBinding?= null
    val mEOVM : EnquiryOrderViewModel by viewModels()
    var filterString : String?=null
    var showString = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRecyclerList(enquiryList)
        if(intent.extras!=null){
            enquiryCount = intent.getLongExtra("enquiryCount", 0)
            type = intent.getLongExtra("type", 1)
            Log.d(TAG, "onCreateType:  type:" + type)
            Log.d(TAG, "id after assign $enquiryCount")
        }
        mBinding?.pbLoader?.visibility=View.VISIBLE

        mBinding = EnquiryDatabaseActivityBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        getClusters()
//        mBinding?.swipeEnquiryDetails?.isRefreshing = true
        mBinding?.swipeEnquiryDetails?.setOnRefreshListener {
            mBinding?.swipeEnquiryDetails?.isRefreshing = false
            if (!Utility.checkIfInternetConnected(this)) {

                Utility.displayMessage(getString(R.string.no_internet_connection), this)
            } else {
                Log.d("swipe called", "onCreate: here ")
                pageNo = 1
                callAPi()
            }
        }

        mBinding?.FilterTextLayout?.visibility = View.GONE
        mBinding?.backButtonEnqOr?.setOnClickListener {
            this.onBackPressed()
        }
        when(type)
        {
            1.toLong() -> {
                mBinding?.textView32?.text = "Ongoing           Enquiries"
            }
            2.toLong() -> {
                mBinding?.textView32?.text = "Incomplete and Closed Enquiries"
            }
            3.toLong() -> {
                mBinding?.textView32?.text = "Ongoing           Orders"
            }
            4.toLong() -> {
                mBinding?.textView32?.text = "Incomplete and Closed Orders"
            }
            5.toLong() -> {
                mBinding?.textView32?.text = "Completed           Orders"

            }


        }

        Availibility.clear()
        Availibility.add("All")
        Availibility.add("Made To Order")
        Availibility.add("Availble in Stock")

        categoryList.clear()
        categoryList.add("All")
        categoryList.add("Saree")
        categoryList.add("Dupatta")
        categoryList.add("Stole")
        categoryList.add("Fabric")
        categoryList.add("Home Accessories")
        categoryList.add("Fashion Accessories")

        val spClusterAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, Availibility)
        spClusterAdapter.setDropDownViewResource(R.layout.spinner_item)
        mBinding?.spCluster1?.adapter = spClusterAdapter

        val spClusterAdapter1 = ArrayAdapter<String>(this, R.layout.spinner_item, clusterList)
        spClusterAdapter1.setDropDownViewResource(R.layout.spinner_item)
        mBinding?.spCluster?.adapter = spClusterAdapter1

        val spClusterAdapter2 = ArrayAdapter<String>(this, R.layout.spinner_item, categoryList)
        spClusterAdapter2.setDropDownViewResource(R.layout.spinner_item)
        mBinding?.spCluster11?.adapter = spClusterAdapter2


        mBinding?.days30?.setOnClickListener {
            mBinding?.days30?.setBackgroundResource(R.drawable.bg_textbox)
            mBinding?.days60?.setBackgroundResource(R.drawable.bd_underline)
            mBinding?.days90?.setBackgroundResource(R.drawable.bd_underline)
            mBinding?.fromDateText?.text = getDate(30)
            mBinding?.toDateText?.text = getDate(0)

        }
        mBinding?.days60?.setOnClickListener {
            mBinding?.days30?.setBackgroundResource(R.drawable.bd_underline)
            mBinding?.days60?.setBackgroundResource(R.drawable.bg_textbox)
            mBinding?.days90?.setBackgroundResource(R.drawable.bd_underline)
            mBinding?.fromDateText?.text = getDate(60)
            mBinding?.toDateText?.text = getDate(0)
        }
        mBinding?.days90?.setOnClickListener {
            mBinding?.days30?.setBackgroundResource(R.drawable.bd_underline)
            mBinding?.days60?.setBackgroundResource(R.drawable.bd_underline)
            mBinding?.days90?.setBackgroundResource(R.drawable.bg_textbox)
            mBinding?.fromDateText?.text = getDate(90)
            mBinding?.toDateText?.text = getDate(0)
        }
        mEOVM?.DataListener =this
        initializeData()
        mBinding?.filterBtn?.setOnClickListener {
            filterString = ""
           showString = 0
            hideKeyboard(view!!)
            buyerBrand = if(mBinding?.editBuyerBrand?.text.toString().isNullOrEmpty()) null else  mBinding?.editBuyerBrand?.text.toString()
            if(buyerBrand != null)
            {
                filterString = filterString + "Buyer brand : "+  mBinding?.editBuyerBrand?.text.toString() +  " | "
                showString = 1
            }

            weaverIdOrBrand = if(mBinding?.editArtisanBrand?.text.toString().isNullOrEmpty()) null else  mBinding?.editArtisanBrand?.text.toString()
            if(weaverIdOrBrand != null)
            {
                filterString = filterString + "Artisan brand : "+  mBinding?.editArtisanBrand?.text.toString() + " | "
                showString = 1
            }
            if(mBinding?.spCluster?.selectedItemPosition == 0)
            {
                clusterId = -1
            }
            else{
                clusterId = mBinding?.spCluster?.selectedItemPosition
                filterString = filterString + "Cluster : "+  clusterList[mBinding?.spCluster?.selectedItemPosition!!] + " | "
                showString = 1
            }
            if(mBinding?.spCluster1?.selectedItemPosition == 0)
            {
                availability = -1
            }
            else{
                availability = mBinding?.spCluster1?.selectedItemPosition
                filterString = filterString + "Availability : "+  Availibility[mBinding?.spCluster1?.selectedItemPosition!!] + " | "
                showString = 1
            }
            if(mBinding?.spCluster11?.selectedItemPosition == 0)
            {
                productCategory = -1
            }
            else{
                productCategory = mBinding?.spCluster11?.selectedItemPosition
                filterString = filterString + "Product Category : "+  categoryList[mBinding?.spCluster1?.selectedItemPosition!!] + " | "
                showString = 1
            }
            pageNo = 1
            val selectedRadioButtonId: Int = mBinding?.radioGroup2?.checkedRadioButtonId ?: -1
            selectedRadioButton = findViewById(selectedRadioButtonId)
            Log.d(TAG, "onCreate: " + selectedRadioButton.text.toString())
            when(selectedRadioButton.text.toString()){
                "Antaran Co Design" -> {
                    madeWithAntaran = 1
                }
                "Artisan Self Design" -> {
                    madeWithAntaran = 0
                }
                "Both" -> {
                    madeWithAntaran = -1
                }

            }
            fromDate = mBinding?.fromDateText?.text.toString()
            toDate = mBinding?.toDateText?.text.toString()
            if(showString == 1)
            {
                filterString = filterString + "Range : "+  fromDate + " to " +toDate
                mBinding?.FilterTextLayout?.visibility = View.VISIBLE
                mBinding?.filtersApplied?.text = filterString

            }
            else{
                mBinding?.FilterTextLayout?.visibility = View.GONE
            }
            enquiryId = null
            mBinding?.searchByEnq?.text?.clear()
            mBinding?.filterLayout?.visibility = View.GONE
            callAPi()
        }
        mBinding?.toDate?.setOnClickListener {

            val c: Calendar = Calendar.getInstance()
            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    mBinding?.toDateText?.setText(
                        year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString(),
                        TextView.BufferType.EDITABLE
                    )
                }, mYear, mMonth, mDay
            )
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000)
            datePickerDialog.show()

        }
        mBinding?.fromDate?.setOnClickListener {

            val c: Calendar = Calendar.getInstance()
            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    mBinding?.fromDateText?.setText(
                        year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString(),
                        TextView.BufferType.EDITABLE
                    )
                }, mYear, mMonth, mDay
            )
//            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000000000000)
            datePickerDialog.show()

        }
        mBinding?.SearchBtn?.setOnClickListener {
            val enquiryId1 =  mBinding?.searchByEnq?.text
            mBinding?.SearchBtn?.clearFocus();
            hideKeyboard(view!!);
            if(enquiryId1?.equals("")!!)
            {
            }
            else{
                enquiryId = mBinding?.searchByEnq?.text.toString()
                pageNo = 1
                Log.d("searchEnq", "onCreate: " + enquiryId)
                callAPi()

            }
        }
//        mBinding?.EnquiryListRecyclerView?.addOnScrollListener(object :
//            RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    Log.d("-----", "end")
//                    pageNo = pageNo?.plus(1)
//                    callAPi()
//
//
////
//                }
//            }
//        })
        mBinding?.EnquiryListRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
//                    Toast.makeText(this@YourActivity, "Last", Toast.LENGTH_LONG).show()
                    Log.d("-----", "end")
                    if(scrollcall == 0)
                    {
                        pageNo = pageNo?.plus(1)
                        callAPi()
                    }

                }
            }
        })



            mBinding?.EnquiryCount?.text = enquiryCount.toString()
        mBinding?.filterLayoutToggle?.setOnClickListener {
            if(mBinding?.filterLayout?.visibility == View.GONE)
            {
                Log.d(TAG, "onCreate: Layout closed")
                mBinding?.filterLayout?.visibility = View.VISIBLE
            }
            else{
                Log.d(TAG, "onCreate: Layout open")

                mBinding?.filterLayout?.visibility = View.GONE
            }
        }
    }
    private fun callAPi(){
        scrollcall = 1
        when(type)
        {
            1.toLong() -> {
                mEOVM.getEnquiryList(
                    availability!!,
                    buyerBrand,
                    clusterId!!,
                    enquiryId,
                    fromDate!!,
                    madeWithAntaran!!,
                    pageNo!!,
                    productCategory!!,
                    statusId,
                    toDate!!,
                    weaverIdOrBrand
                )
            }
            2.toLong() -> {
                mEOVM.getEnquiryClosedList(
                    availability!!,
                    buyerBrand,
                    clusterId!!,
                    enquiryId,
                    fromDate!!,
                    madeWithAntaran!!,
                    pageNo!!,
                    productCategory!!,
                    statusId,
                    toDate!!,
                    weaverIdOrBrand
                )
            }
            3.toLong() -> {
                mEOVM.getOrderList(
                    availability!!,
                    buyerBrand,
                    clusterId!!,
                    enquiryId,
                    fromDate!!,
                    madeWithAntaran!!,
                    pageNo!!,
                    productCategory!!,
                    statusId,
                    toDate!!,
                    weaverIdOrBrand
                )
            }
            4.toLong() -> {
                mEOVM.getOrderIncompletedList(
                    availability!!,
                    buyerBrand,
                    clusterId!!,
                    enquiryId,
                    fromDate!!,
                    madeWithAntaran!!,
                    pageNo!!,
                    productCategory!!,
                    statusId,
                    toDate!!,
                    weaverIdOrBrand
                )
            }
            5.toLong() -> {
                mEOVM.getOrderList(
                    availability!!,
                    buyerBrand,
                    clusterId!!,
                    enquiryId,
                    fromDate!!,
                    madeWithAntaran!!,
                    pageNo!!,
                    productCategory!!,
                    statusId,
                    toDate!!,
                    weaverIdOrBrand
                )
            }


        }
        Log.d("apicalled", "callAPi: ")
        mBinding?.pbLoader?.visibility=View.VISIBLE

    }
    private fun getClusters(){
        clusterList.clear()
        clusterList.add("All")
        clusterDetailsList= ClusterPredicates.getAllClusters()
        clusterDetailsList?.forEach {
            clusterList.add(it?.cluster ?: "")
        }

    }

    fun hideKeyboard(view: View) {
        val imm: InputMethodManager = view.context
            .getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    private fun initializeData(){

        availability = -1
        buyerBrand = null
        clusterId = -1
        enquiryId =null
        fromDate =getDate(30)
        madeWithAntaran =-1
        pageNo = 1
        productCategory = -1
        when(type)
        {
            1.toLong() -> {
                statusId = 2
            }
            2.toLong() -> {
                statusId = null
            }
            3.toLong() -> {
                statusId = 2
            }
            4.toLong() -> {
                statusId = null
            }
            5.toLong() -> {
                statusId = 1
            }
        }
        toDate =getDate(0)
        weaverIdOrBrand = null
        callAPi()

        mBinding?.fromDateText?.text = fromDate
        mBinding?.toDateText?.text = toDate

    }
    private fun setRecyclerList(enquiryList: ArrayList<EnquiryData>){
        mBinding?.EnquiryListRecyclerView?.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        mEnquiryListAdapter = ELAdapter(this, enquiryList, type)
        mBinding?.EnquiryListRecyclerView?.adapter = mEnquiryListAdapter
//        mEnquiryListAdapter?.averageRatingListener =this

//        mEnqListAdapter?.enqListener = this  //important to set adapter first and then call listener
//        Timer().schedule(object : TimerTask() {
//            override fun run() {
//                loadData()
//            }
//        }, 100)

    }

    fun getDate(days: Long) : String{
        val currentDateTime=System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val resultdate = Date(currentDateTime)
        val toDate = dateFormat.format(resultdate)
        Log.d(TAG, "toDate: " + toDate.toString())
        Log.d(TAG, "currentDateTime: " + currentDateTime)
        var Days_30 : Long?=null
        Days_30 = days*(24*60).toLong()*(60*1000).toLong()
        val date30 = currentDateTime - Days_30
        val resultdate2 = Date(date30)
        val fromDate = dateFormat.format(resultdate2)
        return fromDate.toString()
    }
    override fun DataFetchSuccess(dataResponse: EnqiuiryListResponse){
        if(pageNo==1.toLong())
        {
            mBinding?.EnquiryListRecyclerView?.smoothScrollToPosition(0)
            enquiryList.clear()
            dataResponse?.data.forEach{
                enquiryList.add(it)
            }
            setRecyclerList(dataResponse?.data)
            mBinding?.EnquiryListRecyclerView?.smoothScrollToPosition(0)

        }
        else{
            dataResponse?.data.forEach{
                enquiryList.add(it)
            }
            mEnquiryListAdapter?.updateProductList(enquiryList)
        }
        mBinding?.pbLoader?.visibility=View.GONE
        scrollcall = 0


    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

}
