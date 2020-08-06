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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCard
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCatalogue
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.FragmentArtisanHomeBinding
import com.adrosonic.craftexchange.ui.modules.artisan.productTemplate.addProductIntent
import com.adrosonic.craftexchange.ui.modules.artisan.products.ArtisanProductAdapter
import com.adrosonic.craftexchange.ui.modules.buyer.wishList.WishlistAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ArtisanProductsViewModel
import com.adrosonic.craftexchange.viewModels.LandingViewModel
import com.pixplicity.easyprefs.library.Prefs
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_wishlist.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArtisanHomeFragment : Fragment(),
ArtisanProductsViewModel.productsFetchInterface{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentArtisanHomeBinding ?= null
    private var mProduct = mutableListOf<ProductCard>()
    private var artisanProductAdapter: ArtisanProductAdapter?= null
    var artisanId : Long ?= 0
    val mViewModel: ArtisanProductsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_home, container, false)
        var welcomeText = "${activity?.getString(R.string.hello)} ${Prefs.getString(ConstantsDirectory.FIRST_NAME,"User")}"
        mBinding?.welcomeText?.text = welcomeText

        var brandLogo = Utility.craftUser?.brandLogo
        var urlBrand = Utility?.getBrandLogoUrl(Prefs.getString(ConstantsDirectory.USER_ID,"").toLong(),brandLogo)
              mBinding?.brandLogoArtisan?.let {
            ImageSetter.setImage(requireActivity(),urlBrand, it,
                R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
        }
        artisanId = Prefs.getString(ConstantsDirectory.USER_ID,"").toLong()
        mBinding?.btnAddProd?.setOnClickListener {
            startActivity(context?.addProductIntent())
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel?.listener = this
        if (!Utility.checkIfInternetConnected(requireContext())) {
            Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
        } else {
            mViewModel.getProductCategoryListMutableData(artisanId)
        }
        setupRecyclerView()
        mViewModel.getProductCategoryListMutableData(artisanId)
            .observe(viewLifecycleOwner, Observer<RealmResults<ArtisanProducts>>{
                artisanProductAdapter?.updateCategoryList(it)
            })
//        TODO : Fix later Refresh issue
//        mBinding?.swipeRefreshLayout?.isRefreshing = true
        mBinding?.swipeRefreshLayout?.setOnRefreshListener {
            if (!Utility.checkIfInternetConnected(requireContext())) {
                mBinding?.swipeRefreshLayout?.isRefreshing = false
                Utility.displayMessage(getString(R.string.no_internet_connection), requireContext())
            } else {
                mViewModel.getProductsOfArtisan()
            }
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
                mViewModel?.getProductCategoryListMutableData(artisanId)
            }
            )
        } catch (e: Exception) {
            Log.e("CAtegoryList", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("Wishlist", "OnFailure")
                mBinding?.swipeRefreshLayout?.isRefreshing = false
                mViewModel?.getProductCategoryListMutableData(artisanId)
                Utility.displayMessage(
                    "Error while fetching wishlist. Pleas try again after some time",
                    requireContext()
                )
            }
            )
        } catch (e: Exception) {
            Log.e("CAtegoryList", "Exception onFailure " + e.message)
        }
    }


    companion object {
        fun newInstance() = ArtisanHomeFragment()
        const val TAG = "ArtHomeFrag"
    }
}
