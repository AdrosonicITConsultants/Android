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
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import com.wajahatkarim3.easyvalidation.core.view_ktx.validEmail

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

        Prefs.putString(ConstantsDirectory.GST,craftUser?.gstNo ?: " ")
        Prefs.putString(ConstantsDirectory.CIN,craftUser?.cin ?: " ")
        Prefs.putString(ConstantsDirectory.PAN,craftUser?.pancard ?: " ")
        Prefs.putString(ConstantsDirectory.POC_NAME,craftUser?.poc_firstName ?: " ")
        Prefs.putString(ConstantsDirectory.POC_EMAIL,craftUser?.poc_email ?: " ")
        Prefs.putString(ConstantsDirectory.POC_CONTACT,craftUser?.poc_contactNo ?: " ")


        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.gst?.addTextChangedListener {
            var boolean = Utility.isValidGST(mBinding?.gst?.text.toString())
            if(mBinding?.gst?.text?.isNotEmpty()!! ) {
                if(boolean){
                    Prefs.putString(ConstantsDirectory.GST, mBinding?.gst?.text.toString())
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
                }else{
                    mBinding?.gst?.error =activity?.getString(R.string.gst_invalid_text)
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,false)
                }
            }else{
                Prefs.putString(ConstantsDirectory.GST, mBinding?.cin?.text.toString())
                Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
            }
        }

        mBinding?.cin?.addTextChangedListener {
            var boolean = Utility.isValidCIN(mBinding?.cin?.text.toString())
            if(mBinding?.cin?.text?.isNotEmpty()!! ) {
                if(boolean){
                    Prefs.putString(ConstantsDirectory.CIN, mBinding?.cin?.text.toString())
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
                }else{
                    mBinding?.cin?.error =activity?.getString(R.string.cin_invalid_text)
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,false)
                }
            }else{
                Prefs.putString(ConstantsDirectory.CIN, mBinding?.cin?.text.toString())
                Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
            }
        }

        mBinding?.pan?.addTextChangedListener {
            var boolean = Utility.isValidPan(mBinding?.pan?.text.toString())
            if(mBinding?.pan?.text?.isNotEmpty()!! ) {
                if(boolean){
                    Prefs.putString(ConstantsDirectory.PAN, mBinding?.pan?.text.toString())
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
                }else{
                    mBinding?.pan?.error =activity?.getString(R.string.pan_invalid_text)
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,false)
                }
            }else{
                mBinding?.pan?.nonEmpty{
                    mBinding?.pan?.error = it
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,false)}
            }
        }
        mBinding?.name?.addTextChangedListener {
            Prefs.putString(ConstantsDirectory.POC_NAME, mBinding?.name?.text.toString())
        }

        mBinding?.mobile?.addTextChangedListener {
            if(mBinding?.mobile?.text?.isNotEmpty()!!) {
                if(mBinding?.mobile?.minLength(10) == false){
                    mBinding?.mobile?.minLength(10) { mBinding?.mobile?.error = activity?.getString(R.string.mobile_no_invalid_text) }
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,false)
                }else{
                    Prefs.putString(ConstantsDirectory.POC_CONTACT, mBinding?.mobile?.text.toString())
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
                }
            }else{
                Prefs.putString(ConstantsDirectory.POC_CONTACT, mBinding?.mobile?.text.toString())
                Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
            }
        }

        mBinding?.email?.addTextChangedListener {
            if(mBinding?.email?.text?.isNotEmpty()!!) {
                if(mBinding?.email?.validEmail() == false){
                    mBinding?.email?.validEmail { mBinding?.email?.error = it }
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,false)
                }else{
                    Prefs.putString(ConstantsDirectory.POC_EMAIL, mBinding?.email?.text.toString())
                    Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
                }
            }else{
                Prefs.putString(ConstantsDirectory.POC_EMAIL, mBinding?.email?.text.toString())
                Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = BrandEditFragment()
        const val TAG = "BrandEditFrag"
    }
}
