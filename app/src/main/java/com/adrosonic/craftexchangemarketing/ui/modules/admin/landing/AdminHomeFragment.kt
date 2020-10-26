package com.adrosonic.craftexchangemarketing.ui.modules.admin.landing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels

import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ProductCard
import com.adrosonic.craftexchangemarketing.database.predicates.AdminPredicates
import com.adrosonic.craftexchangemarketing.databinding.FragmentAdminHomeBinding
import com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile.ArtisanProfileActivity
import com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile.BuyerProfileActivity
import com.adrosonic.craftexchangemarketing.ui.modules.dashboard.OpenEnquirySummaryActivity
import com.adrosonic.craftexchangemarketing.ui.modules.main.MainActivity
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.ArtisanProductsViewModel
import com.pixplicity.easyprefs.library.Prefs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdminHomeFragment : Fragment(){
//    ArtisanProductsViewModel.productsFetchInterface,
////    ProfileViewModel.FetchUserDetailsInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentAdminHomeBinding?= null
    private var mProduct = mutableListOf<ProductCard>()
//    private var artisanProductAdapter: ArtisanProductAdapter?= null
    var artisanId : Long ?= 0
    val mViewModel: ArtisanProductsViewModel by viewModels()
//    val mProVM : ProfileViewModel by viewModels()
//    var craftUser : MutableLiveData<CraftUser> ?= null
    var brandLogo : String ?= ""
    var urlBrand : String ?=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_home, container, false)
        artisanId = Prefs.getString(ConstantsDirectory.USER_ID,"").toLong()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mViewModel.listener = this
//        mProVM.listener = this
//        refreshProfile()
//        setupRecyclerView()

//        mViewModel.getProductCategoryListMutableData(artisanId)
//            .observe(viewLifecycleOwner, Observer<RealmResults<ArtisanProducts>>{
//                artisanProductAdapter?.updateCategoryList(it)
//            })

//        mProVM.getUserMutableData()
//            .observe(viewLifecycleOwner, Observer<CraftUser> {
//                craftUser = MutableLiveData(it)
//            })

//        var welcomeText = "${activity?.getString(R.string.hello)} ${Prefs.getString(ConstantsDirectory.FIRST_NAME,"User")}"
//        mBinding?.welcomeText?.text = welcomeText
//        setBrandImage()
            mBinding?.microEnterpriseBusinessSummaryBtn?.setOnClickListener {
                Prefs.putString(ConstantsDirectory.DASHBOARD,"MEBS")
                startActivity(Intent(activity, OpenEnquirySummaryActivity::class.java))
            }
            mBinding?.microEnterpriseRevenueBtn?.setOnClickListener {
                Prefs.putString(ConstantsDirectory.DASHBOARD,"MER")
                startActivity(Intent(activity, OpenEnquirySummaryActivity::class.java))
            }
            mBinding?.enquirySummaryBtn?.setOnClickListener {
                Prefs.putString(ConstantsDirectory.DASHBOARD,"ES")
                startActivity(Intent(activity, OpenEnquirySummaryActivity::class.java))
            }
            mBinding?.logoutbtn?.setOnClickListener {
                if(Utility.checkIfInternetConnected(requireContext())){
//                    Utility.displayMessage(getString(R.string.no_artisanId_text),requireContext().getApplicationContext())
//                    startActivity(Intent(activity, LoginActivity::class.java))
                    val builder = AlertDialog.Builder( requireContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
                    builder.setMessage(R.string.logout_text)
                        .setPositiveButton("Yes"){ dialog, id ->
                            dialog.cancel()
                            mViewModel?.logoutUser()
                            AdminPredicates.deleteData()
                            Utility.deleteCache(requireContext())
                            Utility.deleteImageCache(requireContext())
                            startActivity(Intent(activity, MainActivity::class.java))


                        }
                    builder.create().show()
                }else{
                    Utility.displayMessage(getString(R.string.message_operation_not_supported_offline),requireContext().getApplicationContext())
                }
            }
//        mBinding?.btnAddProd?.setOnClickListener {
//            if (Utility.checkIfInternetConnected(this)) {
//                    val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
//                    builder.setMessage(R.string.logout_text)
//                        .setPositiveButton("Yes"){ dialog, id ->
//                            dialog.cancel()
//                            mViewModel?.logoutUser()
//                            UserPredicates.deleteData()
//                            Utility.deleteCache(applicationContext)
//                            Utility.deleteImageCache(applicationContext)
//                            startActivity(roleselectIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
//                        }
//                        .setNegativeButton("No"){ dialog, id ->
//                            dialog.cancel()
//                        }
//                    builder.create().show()
//
//                }else{
//                    Utility.displayMessage(getString(R.string.message_operation_not_supported_offline),applicationContext)
//                }
//            }
//            else -> {
//                supportActionBar?.title = ""
//            }
//        }

//        TODO : Fix later Refresh issue

        mBinding?.swipeRefreshLayout?.setOnRefreshListener {
            if (!Utility.checkIfInternetConnected(requireContext())) {
                mBinding?.swipeRefreshLayout?.isRefreshing = false
                Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
            } else {
                mBinding?.swipeRefreshLayout?.isRefreshing = true
//                refreshProfile()
                mBinding?.swipeRefreshLayout?.isRefreshing = false

            }
        }
    }

//    fun setBrandImage(){
//        brandLogo = craftUser?.value?.brandLogo
//        urlBrand = Utility.getBrandLogoUrl(Prefs.getString(ConstantsDirectory.USER_ID,"").toLong(),brandLogo)
//        mBinding?.brandLogoArtisan?.let {
//            mBinding?.progress?.let { it1 ->
//                ImageSetter.setImageWithProgress(requireActivity(), urlBrand!!, it, it1,
//                    R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
//            }
//        }
//    }


    fun refreshProfile(){
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
//            mViewModel.getProductsOfArtisan()
//            mViewModel.getProductCategoryListMutableData(artisanId)
//            mProVM.getArtisanProfileDetails(requireContext())
//            craftUser = mProVM.getUserMutableData()
//            setBrandImage()
        }
    }
//
//    private fun setupRecyclerView(){
//        mBinding?.productRecyclerList?.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL, false)
//        artisanProductAdapter = ArtisanProductAdapter(requireContext(), mViewModel.getProductCategoryListMutableData(artisanId).value)
//        mBinding?.productRecyclerList?.adapter = artisanProductAdapter
//    }

//    override fun onSuccess() {
//        try {
//            Handler(Looper.getMainLooper()).post(Runnable {
//                Log.e("CAtegoryList", "Onsuccess")
//                mBinding?.swipeRefreshLayout?.isRefreshing = false
//                craftUser = mProVM.getUserMutableData()
//                mViewModel.getProductCategoryListMutableData(artisanId)
//                setBrandImage()
//            }
//            )
//        } catch (e: Exception) {
//            Log.e("CAtegoryList", "Exception onSuccess " + e.message)
//        }
//    }

//    override fun onFailure() {
//        try {
//            Handler(Looper.getMainLooper()).post(Runnable {
//                Log.e("Wishlist", "OnFailure")
//                mBinding?.swipeRefreshLayout?.isRefreshing = false
//                mViewModel.getProductCategoryListMutableData(artisanId)
//                Utility.displayMessage(
//                    "Error while fetching wishlist. Pleas try again after some time",
//                    requireContext()
//                )
//            }
//            )
//        } catch (e: Exception) {
//            Log.e("CAtegoryList", "Exception onFailure " + e.message)
//        }
//    }

    override fun onResume() {
        super.onResume()
        refreshProfile()
    }


    companion object {
        fun newInstance() = AdminHomeFragment()
        const val TAG = "AdminHomeFrag"
    }
}
