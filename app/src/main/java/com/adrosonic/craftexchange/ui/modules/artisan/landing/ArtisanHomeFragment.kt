package com.adrosonic.craftexchange.ui.modules.artisan.landing

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ProductCard
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.databinding.FragmentArtisanHomeBinding
import com.adrosonic.craftexchange.ui.modules.artisan.products.ArtisanProductAdapter
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ArtisanHomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentArtisanHomeBinding ?= null
    private var mProduct = mutableListOf<ProductCard>()
    private var artisanProductAdapter: ArtisanProductAdapter?= null
    var artisanId : Long ?= 0


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

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseList()
        artisanProductAdapter =
            ArtisanProductAdapter(
                requireContext(),
                mProduct
            )
        setupRecyclerView()
    }


    private fun setupRecyclerView(){
        mBinding?.productRecyclerList?.adapter = artisanProductAdapter
        mBinding?.productRecyclerList?.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL, false)
        artisanProductAdapter?.setProducts(mProduct)
        artisanProductAdapter?.notifyDataSetChanged()

    }

    private fun initialiseList(){
        var productList = ProductPredicates.getProductCategoriesOfArtisan(artisanId)
        mProduct.clear()
        if (productList != null) {
            for (size in productList){
                Log.i("Stat","$size")
                var artisanId = size.artisanId
                var productId = size.productCategoryId
                var productTitle = size.productCategoryDesc
//                var status =size.productStatusId
//                var desc = size.productSpecs
                var prod = ProductCard(artisanId,productId,productTitle,null,null)
                mProduct.add(prod)
            }
        }
    }

    companion object {
        fun newInstance() = ArtisanHomeFragment()
        const val TAG = "ArtHomeFrag"
    }
}
