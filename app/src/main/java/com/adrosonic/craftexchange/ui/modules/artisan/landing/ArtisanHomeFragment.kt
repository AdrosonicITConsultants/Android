package com.adrosonic.craftexchange.ui.modules.artisan.landing

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.CraftUser
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCard
import com.adrosonic.craftexchange.databinding.FragmentArtisanHomeBinding
import com.adrosonic.craftexchange.ui.modules.artisan.productTemplate.addProductIntent
import com.adrosonic.craftexchange.ui.modules.artisan.products.ArtisanProductAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ArtisanProductsViewModel
import com.adrosonic.craftexchange.viewModels.CMSViewModel
import com.adrosonic.craftexchange.viewModels.ProfileViewModel
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArtisanHomeFragment : Fragment(),
    ArtisanProductsViewModel.productsFetchInterface,
    ProfileViewModel.FetchUserDetailsInterface,
    CMSViewModel.CMSDataInterface{
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentArtisanHomeBinding ?= null
    private var artisanProductAdapter: ArtisanProductAdapter?= null
    var artisanId : Long ?= 0
    val mViewModel: ArtisanProductsViewModel by viewModels()
    val mProVM : ProfileViewModel by viewModels()
    val mCMSVM : CMSViewModel by viewModels()
    var craftUser : MutableLiveData<CraftUser> ?= null

    var listSize : Int ?= 0

    var brandLogo : String ?= ""
    var urlBrand : String ?=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_home, container, false)
        artisanId = Prefs.getString(ConstantsDirectory.USER_ID,"").toLong()
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.listener = this
        mProVM.listener = this
        mCMSVM.cmsListener = this

        refreshProfile()
        setupRecyclerView()

        mViewModel.getProductCategoryListMutableData(artisanId)
            .observe(viewLifecycleOwner, Observer<RealmResults<ArtisanProducts>>{
                artisanProductAdapter?.updateCategoryList(it)
            })

        mProVM.getUserMutableData()
            .observe(viewLifecycleOwner, Observer<CraftUser> {
                craftUser = MutableLiveData(it)
            })

        var welcomeText = "${activity?.getString(R.string.hello)} ${Prefs.getString(ConstantsDirectory.FIRST_NAME,"User")}"
        mBinding?.welcomeText?.text = welcomeText
        setBrandImage()

        mBinding?.btnAddProd?.setOnClickListener {
            startActivity(context?.addProductIntent())
        }

//        TODO : Fix later Refresh issue
        mBinding?.swipeRefreshLayout?.setOnRefreshListener {
            if (!Utility.checkIfInternetConnected(requireContext())) {
                mBinding?.swipeRefreshLayout?.isRefreshing = false
                Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
            } else {
                mBinding?.swipeRefreshLayout?.isRefreshing = true
                refreshProfile()
            }
        }
    }

    fun setVisibilities(){
        listSize = mViewModel.getProductCategoryListMutableData(artisanId)?.value?.size
        if(listSize != 0){
            mBinding?.emptyView?.visibility = View.GONE
            mBinding?.productRecyclerList?.visibility = View.VISIBLE
        }else{
            mBinding?.emptyView?.visibility = View.VISIBLE
            mBinding?.productRecyclerList?.visibility = View.GONE
        }
    }

    fun setBrandImage(){
        brandLogo = craftUser?.value?.brandLogo
        urlBrand = Utility.getBrandLogoUrl(Prefs.getString(ConstantsDirectory.USER_ID,"").toLong(),brandLogo)
        mBinding?.brandLogoArtisan?.let {
            mBinding?.progress?.let { it1 ->
                ImageSetter.setImageWithProgress(requireActivity(), urlBrand!!, it, it1,
                    R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
            }
        }
    }


    fun refreshProfile(){
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
            setVisibilities()
        } else {
            mViewModel.getProductsOfArtisan()
            mViewModel.getProductCategoryListMutableData(artisanId)
            mProVM.getArtisanProfileDetails(requireContext())
            craftUser = mProVM.getUserMutableData()
            setBrandImage()
            mCMSVM.getCategoriesData()
            setVisibilities()
        }
    }

    private fun setupRecyclerView(){
        mBinding?.productRecyclerList?.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL, false)
        artisanProductAdapter = ArtisanProductAdapter(requireContext(), mViewModel.getProductCategoryListMutableData(artisanId).value)
        mBinding?.productRecyclerList?.adapter = artisanProductAdapter
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("CAtegoryList", "Onsuccess")
                mBinding?.swipeRefreshLayout?.isRefreshing = false
                craftUser = mProVM.getUserMutableData()
                mViewModel.getProductCategoryListMutableData(artisanId)
                setVisibilities()
                setBrandImage()
            })
        } catch (e: Exception) {
            Log.e("CAtegoryList", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Wishlist", "OnFailure")
                mBinding?.swipeRefreshLayout?.isRefreshing = false
                mViewModel.getProductCategoryListMutableData(artisanId)
                setVisibilities()
//                Utility.displayMessage("Error while fetching wishlist. Pleas try again after some time", requireContext())
                })
        } catch (e: Exception) {
            Log.e("CAtegoryList", "Exception onFailure " + e.message)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshProfile()
    }


    companion object {
        fun newInstance() = ArtisanHomeFragment()
        const val TAG = "ArtHomeFrag"
    }

    override fun onCMSFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("CMS", "OnFailure")
            }
            )
        } catch (e: Exception) {
            Log.e("CMS", "Exception onFailure " + e.message)
        }
    }

    override fun onCMSSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("CMS", "onSuccess")
                artisanProductAdapter?.notifyDataSetChanged()
            }
            )
        } catch (e: Exception) {
            Log.e("CMS", "Exception onFailure " + e.message)
        }
    }
}
