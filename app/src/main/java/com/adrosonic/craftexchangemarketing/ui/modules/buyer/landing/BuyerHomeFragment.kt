package com.adrosonic.craftexchangemarketing.ui.modules.buyer.landing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.FragmentBuyerHomeBinding
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.productTemplate.addProductIntent
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.ownDesign.ownDesignIntent
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.viewProducts.ViewAntaranProductsFragment
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.viewProducts.ViewArtisanProductsFragment
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BuyerHomeFragment : Fragment() {

    companion object {
        fun newInstance() = BuyerHomeFragment()
        const val TAG = "BuyerHomeFrag"
    }

    private var mBinding: FragmentBuyerHomeBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_home, container, false)
        var firstname = Prefs.getString(ConstantsDirectory.FIRST_NAME, "User")
        mBinding?.textUser?.text = firstname
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.artisanCatalogue?.setOnClickListener {
            if (savedInstanceState == null) {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(
                        R.id.buyer_home_container,
                        ViewArtisanProductsFragment.newInstance()
                    )
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }

        mBinding?.antaranCatalogue?.setOnClickListener {
            if (savedInstanceState == null) {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(
                        R.id.buyer_home_container,
                        ViewAntaranProductsFragment.newInstance()
                    )
                    ?.addToBackStack(null)
                    ?.commit()
            }
        }
        mBinding?.buttonCustomDesign?.setOnClickListener {
            startActivity(context?.ownDesignIntent())
        }

    }


}
