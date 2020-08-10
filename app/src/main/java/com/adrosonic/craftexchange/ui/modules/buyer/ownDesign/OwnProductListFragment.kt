package com.adrosonic.craftexchange.ui.modules.buyer.ownDesign

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
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.BuyerCustomProduct
import com.adrosonic.craftexchange.database.predicates.BuyerCustomProductPredicates
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.syncManager.SyncCoordinator
import com.adrosonic.craftexchange.ui.modules.buyer.wishList.WishlistAdapter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.OwnProductViewModel
import io.realm.RealmResults
import kotlinx.android.synthetic.main.dialog_gen_enquiry_update_or_new.*
import kotlinx.android.synthetic.main.own_product_list_fragment.*
import kotlinx.android.synthetic.main.own_product_list_fragment.deleteAll
import kotlinx.android.synthetic.main.own_product_list_fragment.empty_view
import kotlinx.android.synthetic.main.own_product_list_fragment.swipe_refresh_layout

class OwnProductListFragment : Fragment(),
    EnquiryViewModel.GenerateEnquiryInterface,
    OwnProductViewModel.OwnProductFetchedInterface,
    OwnProductAdapter.OwnProductListUpdatedListener,
    WishlistAdapter.EnquiryGeneratedListener{

    val mViewModel: OwnProductViewModel by viewModels()
    val mEnqVM : EnquiryViewModel by viewModels()

    private lateinit var adapter: OwnProductAdapter
    var coordinator: SyncCoordinator? = null

    var dialog : Dialog ?= null
    var mUser : UserConfig?= null
    var productID : Long ?= 0L


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.own_product_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.listener = this
        mEnqVM.listener = this

        dialog = Utility?.enquiryGenProgressDialog(requireContext())


        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mViewModel.getCustomProducts()
        }
        buyerOwnProductList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        adapter = OwnProductAdapter(requireContext(), mViewModel.getCustomDesignListMutableData().value)
        buyerOwnProductList.adapter = adapter
        adapter.listener=this
        adapter.enqListener=this

        Log.e("CustomProduct", "Size :" + mViewModel.getCustomDesignListMutableData().value?.size)
        mViewModel.getCustomDesignListMutableData()
            .observe(viewLifecycleOwner, Observer<RealmResults<BuyerCustomProduct>>() {
                Log.e("CustomProduct", "own prod size ${it.size}")
                adapter?.updateWishlist(it)
            })
        setVisiblities()
        deleteAll.setOnClickListener {
            showDeleteDialog(0)
        }
        swipe_refresh_layout.isRefreshing = true
        swipe_refresh_layout.setOnRefreshListener {
            if (!Utility.checkIfInternetConnected(requireContext())) {
                Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
            } else {
                mViewModel.getCustomProducts()
            }
        }
        own_design_elements.text ="Your Custom Products Total Items: ${mViewModel.getCustomDesignListMutableData().value?.size}"
        button_custom_design.setOnClickListener {
            startActivity(it.context.ownDesignIntent())
        }

    }



    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Wishlist", "Onsucces")
                swipe_refresh_layout.isRefreshing = false
                mViewModel?.getCustomDesignListMutableData()
                setVisiblities()
            }
            )
        } catch (e: Exception) {
            Log.e("CustomProduct", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Wishlist", "OnFailure")
                swipe_refresh_layout.isRefreshing = false
                mViewModel?.getCustomDesignListMutableData()
                Utility.displayMessage(
                    "Error while fetching custom products. Pleas try again after some time",
                    requireContext()
                )
                setVisiblities()
            }
            )
        } catch (e: Exception) {
            Log.e("CustomProduct", "Exception onFailure " + e.message)
        }
    }


    fun setVisiblities() {
        if (mViewModel.getCustomDesignListMutableData().value?.size!! > 0) {
            buyerOwnProductList.visibility = View.VISIBLE
            empty_view.visibility = View.GONE
            deleteAll.visibility=View.VISIBLE
            own_design_elements.text = "Your Custom Products Total Items: ${mViewModel.getCustomDesignListMutableData().value?.size}"
        } else {
            buyerOwnProductList.visibility = View.GONE
            empty_view.visibility = View.VISIBLE
            deleteAll.visibility=View.GONE
            own_design_elements.text = "Your wishlist is empty"

        }
    }
    fun showDeleteDialog(productId: Long) {
        var dialog = Dialog(requireContext())
        dialog?.setContentView(R.layout.dialog_removefrom_wishlist)
        dialog?.show()
        val tvCancel = dialog?.findViewById(R.id.txt_cancel) as TextView
        val tvDelete = dialog?.findViewById(R.id.txt_back) as TextView
        tvCancel.setOnClickListener {
            dialog.cancel()
        }
        tvDelete.setOnClickListener {
            //todo set all the wishlisted products isWishlisted property to one and call performm locally available action
            //todo also call mutable live data to update the list

            if(productId>0){
                BuyerCustomProductPredicates.updateProductForDeletion(productId)
            }
            else {
                BuyerCustomProductPredicates.getAllCustomProducts()?.forEach {
                    BuyerCustomProductPredicates.updateProductForDeletion(it.id)
                }
            }
            mViewModel?.getCustomDesignListMutableData()
            setVisiblities()
            if(Utility.checkIfInternetConnected(requireContext())) {
                coordinator = SyncCoordinator(requireContext())
                coordinator?.performLocallyAvailableActions()
            }
//            adapter?.notifyItemRemoved()
            dialog.cancel()
        }
    }
    override fun onDeleted(productId: Long) {
        showDeleteDialog(productId)
    }
    companion object {
        fun newInstance() = OwnProductListFragment()
    }

    override fun onSuccessEnquiryGeneration(enquiry: GenerateEnquiryResponse) {
        try {
            Handler(Looper.getMainLooper()).post {dialog?.dismiss()
                Utility?.enquiryGenSuccessDialog(requireContext(), enquiry?.data?.enquiry?.code.toString()).show()
                Log.e("EnquiryGeneration", "Onsucces")
            }
        } catch (e: Exception) {
            dialog?.dismiss()
            Log.e("EnquiryGeneration", "Exception onSuccess " + e.message)
        }
    }

    override fun onExistingEnquiryGeneration(productName: String, id: String) {
        try {
            Handler(Looper.getMainLooper()).post {
                dialog?.dismiss()
                var exDialog = Utility?.enquiryGenExistingDialog(requireContext(),id,productName)
                exDialog.show()

                exDialog.btn_generate_new_enquiry?.setOnClickListener {
                    exDialog?.dismiss()
                    dialog?.show()
                    productID?.let { it1 -> mEnqVM?.generateEnquiry(it1,true,mUser?.deviceName.toString() ) }
                }
                Log.e("ExistingEnqGeneration", "Onsuccess")
            }
        } catch (e: Exception) {
            dialog?.dismiss()
            Log.e("ExistingEnqGeneration", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailedEnquiryGeneration() {
        try {
            Handler(Looper.getMainLooper()).post {dialog?.dismiss()
                Log.e("EnquiryGeneration", "onFailure")
                Utility.displayMessage("Enquiry Generation Failed",requireContext())
            }
        } catch (e: Exception) {dialog?.dismiss()
            Log.e("EnquiryGeneration", "Exception onFailure " + e.message)
        }
    }

    override fun onEnquiryGenClick(productId: Long, isCustom: Boolean) {
        mEnqVM.ifEnquiryExists(productId,isCustom)
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            dialog?.show()
            mEnqVM.ifEnquiryExists(productId,false)
            productID = productId
        }
    }

}