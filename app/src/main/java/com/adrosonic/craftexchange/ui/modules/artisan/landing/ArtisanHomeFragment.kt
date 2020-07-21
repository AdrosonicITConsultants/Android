package com.adrosonic.craftexchange.ui.modules.artisan.landing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.ArtisanProductList
import com.adrosonic.craftexchange.databinding.FragmentArtisanHomeBinding
import com.adrosonic.craftexchange.ui.modules.products.ProductListAdapter
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
    private var mProduct = mutableListOf<ArtisanProductList>()
    private var productListAdapter: ProductListAdapter ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            initialiseList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_home, container, false)
        var welcomeText = "${activity?.getString(R.string.hello)} ${Prefs.getString(ConstantsDirectory.FIRST_NAME,"User")}"
        mBinding?.welcomeText?.text = welcomeText

        var brandLogo = Utility.craftUser?.brandLogo
        var urlBrand = "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/User/${Prefs.getString(ConstantsDirectory.USER_ID,"")}/CompanyDetails/Logo/${brandLogo}"
              mBinding?.brandLogoArtisan?.let {
            ImageSetter.setImage(requireActivity(),urlBrand, it,
                R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
        }

        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }


    private fun setupRecyclerView(){

        val adapter = ProductListAdapter(requireContext(),mProduct)
        mBinding?.productRecyclerList?.adapter = adapter
        mBinding?.productRecyclerList?.layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL, false)
    }

    private fun initialiseList(){

        mProduct.add(ArtisanProductList("Sarees",R.drawable.demo_img))
        mProduct.add(ArtisanProductList("Dupatta",R.drawable.demo_img))
        mProduct.add(ArtisanProductList("Home Accessories",R.drawable.demo_img))
    }

    companion object {
        fun newInstance() = ArtisanHomeFragment()
        const val TAG = "ArtHomeFrag"
    }
}
