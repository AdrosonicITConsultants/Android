package com.adrosonic.craftexchange.ui.modules.buyer.wishList

import android.app.Dialog
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
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.viewModels.LandingViewModel
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_wishlist.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.database.predicates.WishlistPredicates
import com.adrosonic.craftexchange.repository.data.response.buyer.enquiry.generateEnquiry.GenerateEnquiryResponse
import com.adrosonic.craftexchange.syncManager.SyncCoordinator
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import kotlinx.android.synthetic.main.dialog_gen_enquiry_update_or_new.*

class wishlistFragment : Fragment(),
    LandingViewModel.wishlistFetchedInterface,
    EnquiryViewModel.GenerateEnquiryInterface,
    WishlistAdapter.WishListUpdatedListener,
    WishlistAdapter.EnquiryGeneratedListener{

    val mViewModel: LandingViewModel by viewModels()
    val mEnqVM : EnquiryViewModel by viewModels()
    private lateinit var wishlistAdapter: WishlistAdapter
    var coordinator: SyncCoordinator? = null

    var dialog : Dialog ?= null
    var exDialog : Dialog ?= null
    var sucDialog : Dialog ?= null

    var productID : Long ?= 0L
    var enqID : String?=""
    var enqCode : String?= ""
    var prodName : String?=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wishlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.listener = this
        mEnqVM.listener = this

        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mViewModel.getwishlisteProductIds()
        }

        dialog = Utility.enquiryGenProgressDialog(requireActivity())

        buyerWishlist.layoutManager =LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        wishlistAdapter = WishlistAdapter(requireContext(), mViewModel.getwishListMutableData().value)
        buyerWishlist.adapter = wishlistAdapter
        wishlistAdapter.listener=this
        wishlistAdapter.enqListener=this
        Log.e("Wishlist", "Size :" + mViewModel.getwishListMutableData().value?.size)

        mViewModel.getwishListMutableData()
            .observe(viewLifecycleOwner, Observer<RealmResults<ProductCatalogue>> {
                Log.e("Wishlist", "updateWishlist ${it.size}")
                wishlistAdapter.updateWishlist(it)
            })
        setVisiblities()
        deleteAll.setOnClickListener {
            showDeleteDialog()
        }
        swipe_refresh_layout.isRefreshing = true
        swipe_refresh_layout.setOnRefreshListener {
            if (!Utility.checkIfInternetConnected(requireContext())) {
                Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
            } else {
                mViewModel.getwishlisteProductIds()
            }
        }
        wishlist_elements.text =
            "Your wishlist has ${mViewModel.getwishListMutableData().value?.size} items"
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Wishlist", "Onsucces")
                swipe_refresh_layout.isRefreshing = false
                mViewModel.getwishListMutableData()
                setVisiblities()
            }
            )
        } catch (e: Exception) {
            Log.e("Wishlist", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Wishlist", "OnFailure")
                swipe_refresh_layout.isRefreshing = false
                mViewModel.getwishListMutableData()
               Utility.displayMessage(
                    "Error while fetching wishlist. Pleas try again after some time",
                    requireContext()
                )
                setVisiblities()
            }
            )
        } catch (e: Exception) {
            Log.e("Wishlist", "Exception onFailure " + e.message)
        }
    }

    fun showDeleteDialog() {
        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_removefrom_wishlist)
        dialog.show()
        val tvCancel = dialog.findViewById(R.id.txt_cancel) as TextView
        val tvDelete = dialog.findViewById(R.id.txt_back) as TextView
        tvCancel.setOnClickListener {
            dialog.cancel()
        }
        tvDelete.setOnClickListener {
            //todo set all the wishlisted products isWishlisted property to one and call performm locally available action
            //todo also call mutable live data to update the list

            WishlistPredicates.getWishListedData()?.forEach {
                WishlistPredicates.updateProductWishlisting(it.productId,0,1)
            }
            mViewModel.getwishListMutableData()
            setVisiblities()
            if(Utility.checkIfInternetConnected(requireContext())) {
                coordinator = SyncCoordinator(requireContext())
                coordinator?.performLocallyAvailableActions()
            }
        }
    }

    override fun onSelected(productId: Long, isWishListed: Long) {
        WishlistPredicates.updateProductWishlisting(productId,isWishListed,1)
        mViewModel.getwishListMutableData()
        setVisiblities()
        if(Utility.checkIfInternetConnected(requireContext())) {
            coordinator = SyncCoordinator(requireContext())
            coordinator?.performLocallyAvailableActions()
        }
    }

    fun setVisiblities() {
        if (mViewModel.getwishListMutableData().value?.size!! > 0) {
            buyerWishlist.visibility = View.VISIBLE
            empty_view.visibility = View.GONE
            deleteAll.visibility=View.VISIBLE
            wishlist_elements.text =
                "Your wishlist has ${mViewModel.getwishListMutableData().value?.size} items"
        } else {
            buyerWishlist.visibility = View.GONE
            empty_view.visibility = View.VISIBLE
            deleteAll.visibility=View.GONE
            wishlist_elements.text = "Your wishlist is empty"

        }
    }

    override fun onSuccessEnquiryGeneration(enquiry: GenerateEnquiryResponse) {
        try {
            Handler(Looper.getMainLooper()).post {
                Log.e("EnquiryGeneration", "Onsucces")
                dialog?.dismiss()
                enqID = enquiry?.data?.enquiry?.id.toString()
                enqCode = enquiry?.data?.enquiry?.code.toString()
                sucDialog = Utility.enquiryGenSuccessDialog(requireActivity(),enqID.toString(),enqCode.toString())
                Handler().postDelayed({ sucDialog?.show() }, 500)
            }
        } catch (e: Exception) {
            Log.e("EnquiryGeneration", "Exception onSuccess " + e.message)
        }
    }

    override fun onExistingEnquiryGeneration(productName: String, id: String, code:String) {
        try {
            Handler(Looper.getMainLooper()).post {
                Log.e("ExistingEnqGeneration", "Onsuccess")
                dialog?.dismiss()

                enqID = id
                enqCode = code
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

    companion object {
        @JvmStatic
        fun newInstance() =
            wishlistFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

}