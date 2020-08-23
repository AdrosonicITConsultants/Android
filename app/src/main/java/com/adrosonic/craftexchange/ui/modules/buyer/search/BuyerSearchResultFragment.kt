package com.adrosonic.craftexchange.ui.modules.buyer.search

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
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
import com.adrosonic.craftexchange.ui.modules.artisan.search.adapter.ArtisanSearchAdapter
import com.adrosonic.craftexchange.ui.modules.buyer.ownDesign.OwnProductAdapter
import com.adrosonic.craftexchange.ui.modules.buyer.search.adapter.BuyerSearchAdapter
import com.adrosonic.craftexchange.ui.modules.buyer.wishList.WishlistAdapter
import com.adrosonic.craftexchange.ui.modules.search.FilterCollectionAdapter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.SearchViewModel
import com.wajahatkarim3.easyvalidation.core.collection_ktx.startWithNonNumberList
import kotlinx.android.synthetic.main.dialog_gen_enquiry_update_or_new.*


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

    private var searchFilter : String ?= ""
    private var searchFilterId : Long ?= 0
    var pageNo : Long ?= 1

    var searchFilterList = arrayListOf<SearchProductData>()
    var searchProdList = arrayListOf<SearchProductData>()

    var dialog : Dialog?= null
    var productID : Long ?= 0L

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

        mAdapter = BuyerSearchAdapter(requireContext(),searchProdList)
        mBinding?.buyerSearchList?.adapter = mAdapter
        mBinding?.buyerSearchList?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


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
                    ?.replace(R.id.ss_container, BuyerSuggestionFragment.newInstance())
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
                searchFilterId?.let { searchFilter?.let { it1 -> pageNo?.let { it2 ->
                    getSearchResults(it1,
                        it2, it)
                } } }
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
                    pageNo = pageNo?.plus(1)
                    pageNo?.let { searchFilterId?.let { it1 ->
                        searchFilter?.let { it2 ->
                            mViewModel?.getProductsForBuyer(
                                it2,
                                it1, it)
                        }
                    } }
                }
            }
        })
    }

    fun getSearchResults(query : String, resultPageNo: Long, suggId : Long){
        mViewModel?.getProductsForBuyer(query,suggId,resultPageNo)
    }

    private fun setFilterRecycler(mFilterList : ArrayList<Pair<String,Long>>){
        mBinding?.filterRecycler?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        filterAdapter = FilterCollectionAdapter(requireContext(),mFilterList)
        mBinding?.filterRecycler?.adapter = filterAdapter
    }

    fun setSearchRecycler(context : Context,list : ArrayList<SearchProductData>){

        mBinding?.listSizeText?.text = "Found ${mAdapter?.itemCount} items"
        if(mAdapter?.itemCount == 0){
            mBinding?.emptyText?.visibility = View.VISIBLE
        }else{
            mBinding?.emptyText?.visibility = View.GONE
        }
        mAdapter?.notifyDataSetChanged()
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

        when(filterSelected?.second){
            1L -> {
                //Show all
                searchFilterList.clear()
                searchFilterList = searchProdList
                setSearchRecycler(requireContext(),searchFilterList)
            }
            2L -> {
                //Antaran
                searchFilterList.clear()
                var itr = searchProdList.iterator()
                if(itr != null){
                    while (itr.hasNext()){
                        if(itr.next().madeWithAnthran == 1L){
                            searchFilterList.add(itr.next())
                        }
                    }
                }
                setSearchRecycler(requireContext(),searchFilterList)
            }
            3L -> {
                //Artisan
                searchFilterList.clear()
                var itr = searchProdList.iterator()
                if(itr != null){
                    while (itr.hasNext()){
                        if(itr.next().madeWithAnthran == 0L){
                            searchFilterList.add(itr.next())
                        }
                    }
                }
                setSearchRecycler(requireContext(),searchFilterList)
            }
        }
    }

    override fun onSuccessSearch(search: SearchProductResponse) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("BuyerSearchList", "Onsuccess Size : "+search.data.size)
                mBinding?.searchBuyerSwipe?.isRefreshing = false

//                searchProdList.clear()
                search.data.forEach { searchProdList.add(it) }
                if(searchProdList.isEmpty()){
                    setSearchRecycler(requireContext(), searchProdList)
                    mAdapter?.notifyDataSetChanged()
                }else{
                    setSearchRecycler(requireContext(),searchProdList)
                    mAdapter?.notifyDataSetChanged()
                }
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
                Utility.enquiryGenSuccessDialog(requireContext(), enquiry.data.enquiry.code.toString())
                Log.e("EnquiryGeneration", "Onsucces")
                dialog?.cancel()
            }
        } catch (e: Exception) {
            Log.e("EnquiryGeneration", "Exception onSuccess " + e.message)
            dialog?.cancel()
        }
    }

    override fun onExistingEnquiryGeneration(productName: String, id: String) {
        try {

            Handler(Looper.getMainLooper()).post {
                dialog?.cancel()
                var exDialog = Utility.enquiryGenExistingDialog(requireContext(),id,productName)
                Handler().postDelayed({ exDialog.show() }, 500)

                exDialog.btn_generate_new_enquiry?.setOnClickListener {
                    exDialog.cancel()
//                    Handler().postDelayed({ dialog?.show() }, 500)

                    productID?.let { it1 -> mEnqVM.generateEnquiry(it1,true,"Android") }
                }
                Log.e("ExistingEnqGeneration", "Onsuccess")
            }
        } catch (e: Exception) {
            dialog?.cancel()
            Log.e("ExistingEnqGeneration", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailedEnquiryGeneration() {
        try {
            Handler(Looper.getMainLooper()).post {
                Log.e("EnquiryGeneration", "onFailure")
                Utility.displayMessage("Enquiry Generation Failed",requireContext())
                dialog?.cancel()
            }
        } catch (e: Exception) {
            Log.e("EnquiryGeneration", "Exception onFailure " + e.message)
            dialog?.cancel()
        }
    }

    override fun onEnquiryGenClick(productId: Long, isCustom: Boolean) {
        mEnqVM.ifEnquiryExists(productId,isCustom)
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mEnqVM.ifEnquiryExists(productId,true)
            productID = productId
            dialog?.show()
        }
    }

}