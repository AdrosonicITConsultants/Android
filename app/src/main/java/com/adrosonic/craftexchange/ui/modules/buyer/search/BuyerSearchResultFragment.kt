package com.adrosonic.craftexchange.ui.modules.buyer.search

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerSearchResultBinding
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.repository.data.response.search.SearchProductData
import com.adrosonic.craftexchange.repository.data.response.search.SearchProductResponse
import com.adrosonic.craftexchange.ui.modules.buyer.search.adapter.BuyerSearchAdapter
import com.adrosonic.craftexchange.ui.modules.search.FilterCollectionAdapter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.SearchViewModel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class BuyerSearchResultFragment : Fragment(),
    SearchViewModel.FetchBuyerSearchProducts,
    EnquiryViewModel.GenerateEnquiryInterface,
    BuyerSearchAdapter.EnquiryGeneratedListener,
    FilterCollectionAdapter.FilterSelectionListener {

    private var param1: String? = null
    private var param2: Long? = null
    private var FILTER_FLAG = 0

    private var mBinding: FragmentBuyerSearchResultBinding?= null

    private lateinit var filterAdapter : FilterCollectionAdapter
    private lateinit var mAdapter: BuyerSearchAdapter

    val mViewModel: SearchViewModel by viewModels()
    val mEnqVM : EnquiryViewModel by viewModels()


    var filterList = ArrayList<Pair<String,Long>>()
    var filterSelected : Pair<String,Long> ?= null
    var filterTypeSelected : Long ? = 0
    var madeWithAntharan : Long ?= 0

    private var searchFilter : String ?= ""
    private var searchFilterId : Long ?= 0
    var pageNo : Long ?= 1

    var searchFilterList = arrayListOf<SearchProductData>()
    var searchProdList = arrayListOf<SearchProductData>()

    var dialog : Dialog ?= null
    var exDialog : Dialog ?= null
    var sucDialog : Dialog ?= null

    var productID : Long ?= 0L
    var enqID : String?=""
    var enqCode:String?=""
    var prodName : String?=""

    var isFilterEnabled : Long? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getLong(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_search_result, container, false)
        searchFilter = param1
        searchFilterId = param2
        mBinding?.searchBuyerSwipe?.isEnabled = false   // disable swipe to refresh action
        dialog = Utility?.enquiryGenProgressDialog(requireContext())

        filterList.clear()
        filterList.add(Pair(requireActivity().getString(R.string.show_both),1))
        filterList.add(Pair(requireActivity().getString(R.string.antaran_co_design_collection),2))
        filterList.add(Pair(requireActivity().getString(R.string.artisan_self_design_collection),3))

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFilterRecycler(filterList)
        mAdapter = BuyerSearchAdapter(requireContext(), arrayListOf())
        mBinding?.buyerSearchList?.adapter = mAdapter

        filterAdapter.fListener = this
        mAdapter?.enqListener = this

        mEnqVM.listener = this
        mAdapter?.enqListener = this

        mViewModel?.buySearchListener = this
        if(Utility?.checkIfInternetConnected(requireContext())){
            searchFilterId?.let { searchFilter?.let { it1 -> pageNo?.let { it2 ->
                getSearchResults(it1,
                    it2, it)
            } } }
        }else{
            Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
        }


        var search = activity?.findViewById<SearchView>(R.id.search_artisan)
        search?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query:String):Boolean {
                return false
            }
            override fun onQueryTextChange(newText:String):Boolean {
                if(Utility?.checkIfInternetConnected(requireContext())){
                    activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.ss_container, BuyerSuggestionFragment.newInstance(0))
                    ?.addToBackStack(null)
                    ?.commit()
                }else{
                    Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
                }
                return false
            }
        })

        mBinding?.searchBuyerSwipe?.isRefreshing = true
        mBinding?.searchBuyerSwipe?.setOnRefreshListener {
            if(Utility?.checkIfInternetConnected(requireContext())){
                searchFilterId?.let { searchFilter?.let { it1 -> pageNo?.let { it2 -> getSearchResults(it1, it2, it) } } }
            }else{
                Utility?.displayMessage(getString(R.string.no_internet_connection),requireContext())
            }
        }

        val slideDown = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)

        mBinding?.filterByCollection?.setOnClickListener {
            if (mBinding?.collectionFilterWindow?.visibility == View.GONE) {
                mBinding?.collectionFilterWindow?.visibility = View.VISIBLE
                mBinding?.collapseBtn?.visibility = View.VISIBLE
                mBinding?.filterRecycler?.visibility = View.VISIBLE
                mBinding?.collectionFilterWindow?.animation = slideDown
            } else {
                mBinding?.collectionFilterWindow?.visibility = View.GONE
                mBinding?.collapseBtn?.visibility = View.GONE
                mBinding?.filterRecycler?.visibility = View.GONE
                mBinding?.collectionFilterWindow?.animation = slideUp
            }
        }

        mBinding?.collapseBtn?.setOnClickListener {
            mBinding?.collectionFilterWindow?.visibility = View.GONE
            mBinding?.collapseBtn?.visibility = View.GONE
            mBinding?.filterRecycler?.visibility = View.GONE
            mBinding?.collectionFilterWindow?.animation = slideUp
        }

        mBinding?.buyerSearchList?.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView:RecyclerView, newState:Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    Log.d("-----", "end")
                    mBinding?.searchBuyerSwipe?.isRefreshing = true
                    pageNo = pageNo?.plus(1)
                    pageNo?.let { searchFilterId?.let { it1 ->
                        searchFilter?.let { it2 ->
                            madeWithAntharan?.let { it3 -> mViewModel?.getSearchProductsForBuyer(it2, it1, it, it3) }
                        }
                    } }
                }
            }
        })
    }

    fun getSearchResults(query : String, resultPageNo: Long, suggId : Long){
        madeWithAntharan?.let {
            mViewModel?.getSearchProductsForBuyer(query,suggId,resultPageNo, it)
        }
    }

    private fun setFilterRecycler(mFilterList : ArrayList<Pair<String,Long>>){
        mBinding?.filterRecycler?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        filterAdapter = FilterCollectionAdapter(requireContext(),mFilterList)
        mBinding?.filterRecycler?.adapter = filterAdapter
    }

    fun setSearchResults(list : ArrayList<SearchProductData>){
        mBinding?.buyerSearchList?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        updateSearchList(list)
        if(mAdapter?.itemCount == 0){
            mBinding?.emptyText?.visibility = View.VISIBLE
        }else{
            mBinding?.emptyText?.visibility = View.GONE
        }
    }

    fun updateSearchList(list : ArrayList<SearchProductData>){
        mAdapter.updateList(list)
        mBinding?.listSizeText?.text = "Found ${mAdapter?.itemCount} items"
        if(mAdapter?.itemCount == 0){
            mBinding?.emptyText?.visibility = View.VISIBLE
        }else{
            mBinding?.emptyText?.visibility = View.GONE
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String,param2: Long) =
            BuyerSearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putLong(ARG_PARAM2, param2)
                }
            }
    }

    override fun onFilterSelected(pairList: Pair<String, Long>) {
        filterSelected = pairList
        filterTypeSelected = pairList.second
        when(filterSelected?.second){
            1L -> {
                //Show all
                updateSearchList(searchProdList)
                isFilterEnabled = 0L
            }
            2L -> {
                //Antaran
                searchFilterList.clear()
                var itr = searchProdList.iterator()
                if(itr != null){
                    while (itr.hasNext()){
                        var prod = itr.next()
                        when(prod.madeWithAnthran){
                            1L -> {
                                searchFilterList.add(prod)
                            }
                        }
                    }
                }
                updateSearchList(searchFilterList)
                isFilterEnabled = 1L
            }
            3L -> {
                //Artisan
                searchFilterList.clear()
                var itr = searchProdList.iterator()
                if(itr != null){
                    while (itr.hasNext()){
                        var prod = itr.next()
                        when(prod.madeWithAnthran){
                            0L -> {
                                searchFilterList.add(prod)
                            }
                        }
                    }
                }
                updateSearchList(searchFilterList)
                isFilterEnabled = 1L
            }
        }
    }

    override fun onSuccessSearch(search: SearchProductResponse) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("SearchResultList", "Onsuccess Size : "+search.data.size)
                mBinding?.searchBuyerSwipe?.isRefreshing = false
            }
            )
        } catch (e: Exception) {

            mBinding?.searchBuyerSwipe?.isRefreshing = false
            Log.e("BuyerSearchList", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailureSearch() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("BuyerSearchList", "OnFailure")
                mBinding?.searchBuyerSwipe?.isRefreshing = false
            }
            )
        } catch (e: Exception) {
            mBinding?.searchBuyerSwipe?.isRefreshing = false
            Log.e("BuyerSearchList", "Exception onFAilure " + e.message)
        }
    }

    override fun onSuccessEnquiryGeneration(enquiry: GenerateEnquiryResponse) {
        try {
            Handler(Looper.getMainLooper()).post {
                Log.e("EnquiryGeneration", "Onsucces")
                dialog?.dismiss()
                enqID = enquiry?.data?.enquiry?.code.toString()
                enqCode = enquiry?.data?.enquiry?.code.toString()
                sucDialog = Utility.enquiryGenSuccessDialog(requireActivity(),enqID.toString(),enqCode.toString())
                Handler().postDelayed({ sucDialog?.show() }, 500)
            }
        } catch (e: Exception) {
            Log.e("EnquiryGeneration", "Exception onSuccess " + e.message)
        }
    }

    override fun onExistingEnquiryGeneration(productName: String, id: String,code:String) {
        try {
            Handler(Looper.getMainLooper()).post {
                Log.e("ExistingEnqGeneration", "Onsuccess")
                dialog?.dismiss()

                enqID = id
                enqCode= code
                prodName = productName
                exDialog = Utility.enquiryGenExistingDialog(requireActivity(),enqID.toString(), enqCode.toString(),prodName.toString())

                var btn_gen = exDialog?.findViewById(R.id.btn_ex_generate_new_enquiry) as TextView
                btn_gen?.setOnClickListener {
                    productID?.let { it1 -> mEnqVM?.generateEnquiry(it1,false,"Android") }
                    exDialog?.cancel()
                    Handler().postDelayed({ dialog?.show() }, 500)
                }
                Handler().postDelayed({ exDialog?.show() }, 500)
            }
        } catch (e: Exception) {
            Log.e("ExistingEnqGeneration", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailedEnquiryGeneration() {
        try {
            Handler(Looper.getMainLooper()).post {dialog?.dismiss()
                Log.e("EnquiryGeneration", "onFailure")
            }
        } catch (e: Exception) {dialog?.dismiss()
            Log.e("EnquiryGeneration", "Exception onFailure " + e.message)
        }
    }

    override fun onEnquiryGenClick(productId: Long, isCustom: Boolean) {

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireActivity())
        } else {
            mEnqVM.ifEnquiryExists(productId,false)
            productID = productId
            dialog?.show()
        }
    }

}