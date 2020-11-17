package com.adrosonic.craftexchange.ui.modules.buyer.profile.editProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentDeliveryEditBinding
import com.adrosonic.craftexchange.ui.modules.buyer.profile.BuyerProfileActivity
import com.adrosonic.craftexchange.ui.modules.buyer.profile.BuyerProfileActivity.Companion.craftUser
import com.adrosonic.craftexchange.ui.modules.buyer.profile.BuyerProfileActivity.Companion.delAddr
import com.adrosonic.craftexchange.ui.modules.buyer.profile.BuyerProfileActivity.Companion.regAddr
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DeliveryEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentDeliveryEditBinding ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_delivery_edit, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding?.companyName?.text = craftUser?.companyName ?: " - "
        if(delAddr?.line1 == ""){
            mBinding?.compAddr?.setText(regAddr?.line1)
            Prefs.putString(ConstantsDirectory.ADDR_LINE1, regAddr?.line1)
        }else{
            mBinding?.compAddr?.setText(delAddr?.line1)
            Prefs.putString(ConstantsDirectory.ADDR_LINE1, delAddr?.line1)
        }
        mBinding?.country?.text = delAddr?.country ?: " - "


        mBinding?.compAddr?.addTextChangedListener {
            Prefs.putString(ConstantsDirectory.ADDR_LINE1, mBinding?.compAddr?.text.toString())
        }

        mBinding?.compAddr?.addTextChangedListener {
            Prefs.putString(ConstantsDirectory.ADDR_LINE1, mBinding?.compAddr?.text.toString())
        }
    }

    companion object {
        fun newInstance() = DeliveryEditFragment()
        const val TAG = "DeliEditFrag"
    }
}
