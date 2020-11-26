package com.adrosonic.craftexchangemarketing.ui.modules.admin.redirectEnquiries

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
import androidx.recyclerview.widget.RecyclerView
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.FragmentEnquiriesBinding
import com.adrosonic.craftexchangemarketing.enums.RedirectEnqTypes
import com.adrosonic.craftexchangemarketing.enums.getId
import com.adrosonic.craftexchangemarketing.repository.data.response.redirectedEnquiries.CustomEnquiries
import com.adrosonic.craftexchangemarketing.ui.modules.admin.redirectEnquiries.adapter.CustomEnquiryListAdapter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.RedirectedEnquiryViewModel
import java.util.ArrayList


private const val ARG_PARAM1 = "roleId"

class EnquiriesFragment(roleId1:Long) :Fragment(),
        RedirectedEnquiryViewModel.FetchEnquiryInterface
{
    var custEnqArrList: ArrayList<CustomEnquiries> = arrayListOf()
    var scrollcall = 0
    var pageNo=1
    private  var roleId=0L
    private lateinit var customEnqListAdapter: CustomEnquiryListAdapter
    var mBinding : FragmentEnquiriesBinding?= null
    val mViewModel: RedirectedEnquiryViewModel by viewModels()
    init {
        this.roleId=roleId1
    }

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_enquiries, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel?.fetchEnqListener=this
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mBinding?.swipeRefreshLayout?.isRefreshing=true
            pageNo = 1
            callApi()
        }
//        mBinding?.enquiriesList?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        prodAdapter = ProductCatalogueListAdapter(requireContext(), mViewModel.getProductsMutableData(roleId,"","","").value,false)
//        mBinding?.productList?.adapter = prodAdapter
//        Log.e("ArtisanProduct", "Size :" + mViewModel.getProductsMutableData(roleId,"","","").value?.size)
//        mBinding?.swipeRefreshLayout?.setOnRefreshListener {
//            mBinding?.swipeRefreshLayout?.isRefreshing=true
//            mViewModel.getArtisanProducts()
//        }
//        mViewModel.getProductsMutableData(roleId,"","","").observe(viewLifecycleOwner, Observer<RealmResults<AdminProductCatalogue>> {
//            prodAdapter.updateProducts(it)
//        })

        setRecyclerList()
        mBinding?.enquiriesList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    if(scrollcall == 0)
                    {
                        pageNo = pageNo?.plus(1)
                        callApi()
                    }

                }
            }
        })


    }
    private fun callApi() {
        scrollcall = 1
        when (roleId) {
            RedirectEnqTypes.CUSTOM.getId()->mViewModel.getAdminCustomIncomingEnquiries(pageNo,"date","desc")
            RedirectEnqTypes.FAULTY.getId()->mViewModel.getAdminFaultyIncomingEnquiries(pageNo,"date","desc")
            RedirectEnqTypes.OTHERS.getId()->mViewModel.getAdminOtherIncomingEnquiries(pageNo,"date","desc")
        }
    }

    override fun onFetchFailure() {
        mBinding?.swipeRefreshLayout?.isRefreshing=false
        scrollcall = 0
    }

    override fun onFetchSuccess(custEnqList: List<CustomEnquiries>?) {
        if(pageNo==1)
        {
            mBinding?.enquiriesList?.smoothScrollToPosition(0)
            custEnqArrList?.clear()
            custEnqList?.forEach{
                custEnqArrList.add(it)
                Log.e("ProductDetails","productID: ${it.productId}")
            }
            setRecyclerList()
            mBinding?.enquiriesList?.smoothScrollToPosition(0)

        }
        else{
            custEnqList?.forEach{
                custEnqArrList.add(it)
            }
            customEnqListAdapter?.updateProducts(custEnqArrList)
        }
        mBinding?.swipeRefreshLayout?.isRefreshing=false

        mBinding?.totalProdCount?.text="Found ${custEnqArrList.size} enquiries"
        scrollcall = 0
    }
    private fun setRecyclerList(){
        mBinding?.enquiriesList?.layoutManager = LinearLayoutManager( requireContext(), LinearLayoutManager.VERTICAL,false )
        customEnqListAdapter = CustomEnquiryListAdapter(requireContext(), custEnqArrList, roleId)
        mBinding?.enquiriesList?.adapter = customEnqListAdapter
    }
    companion object {
        @JvmStatic
        fun newInstance(roleId: Long) =
            EnquiriesFragment(roleId).apply {
                arguments = Bundle().apply {
                    putLong(ARG_PARAM1, roleId)
                }
            }
    }

}