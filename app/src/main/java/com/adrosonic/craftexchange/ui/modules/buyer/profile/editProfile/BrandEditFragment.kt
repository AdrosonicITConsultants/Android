package com.adrosonic.craftexchange.ui.modules.buyer.profile.editProfile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBrandEditBinding
import com.adrosonic.craftexchange.ui.modules.buyer.profile.BuyerProfileActivity.Companion.craftUser
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BrandEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentBrandEditBinding ?= null


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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_brand_edit, container, false)

        mBinding?.gst?.setText(craftUser?.gstNo ?: "")
        mBinding?.cin?.setText(craftUser?.cin ?: "")
        mBinding?.pan?.setText(craftUser?.pancard ?: "")

        mBinding?.name?.setText(craftUser?.poc_firstName ?: "")
        mBinding?.mobile?.setText(craftUser?.poc_contactNo ?: "")
        mBinding?.email?.setText(craftUser?.poc_email ?: "")

        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding?.gst?.addTextChangedListener {
            Prefs.putString(ConstantsDirectory.GST, mBinding?.gst?.text.toString())
        }
        mBinding?.cin?.addTextChangedListener {
            Prefs.putString(ConstantsDirectory.CIN, mBinding?.cin?.text.toString())
        }
        mBinding?.pan?.addTextChangedListener {
            Prefs.putString(ConstantsDirectory.PAN, mBinding?.pan?.text.toString())
        }
        mBinding?.name?.addTextChangedListener {
            Prefs.putString(ConstantsDirectory.POC_NAME, mBinding?.name?.text.toString())
        }
        mBinding?.mobile?.addTextChangedListener {
            Prefs.putString(ConstantsDirectory.POC_CONTACT, mBinding?.mobile?.text.toString())
        }
        mBinding?.email?.addTextChangedListener {
            Prefs.putString(ConstantsDirectory.POC_EMAIL, mBinding?.email?.text.toString())
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BrandEditFragment()
        const val TAG = "BrandEditFrag"
    }
}
