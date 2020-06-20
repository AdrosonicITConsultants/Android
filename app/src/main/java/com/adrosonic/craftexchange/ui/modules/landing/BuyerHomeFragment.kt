package com.adrosonic.craftexchange.ui.modules.landing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerHomeBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BuyerHomeFragment : Fragment() {

    companion object {
//        @JvmStatic
//        fun newInstance(param1: String) =
//            BuyerHomeFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                }
//            }
        fun newInstance() = BuyerHomeFragment()
        const val TAG = "BuyerHomeFrag"
    }

    private var mBinding: FragmentBuyerHomeBinding ?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_home, container, false)
        var firstname = Prefs.getString(ConstantsDirectory.FIRST_NAME,"User")
        mBinding?.textUser?.text = firstname
        return mBinding?.root
    }


}
