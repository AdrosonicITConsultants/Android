package com.adrosonic.craftexchange.ui.modules.buyer.auth.register

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerRegisterDetailsBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BuyerRegisterDetailsFragment : Fragment() {

    companion object {
//        @JvmStatic
//        fun newInstance(param1: String,param2: String) =
//            BuyerRegisterDetailsFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
        fun newInstance() = BuyerRegisterDetailsFragment()
        const val TAG = "BuyerRegisterDet"
    }

    private var mBinding: FragmentBuyerRegisterDetailsBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_register_details, container, false)

        var asterik = SpannableString("*")
        asterik.setSpan(ForegroundColorSpan(Color.RED), 0, asterik.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mBinding?.textFirstname?.append(asterik)
        mBinding?.textMobile?.append(asterik)

        mBinding?.textBoxFirstname?.setText(Prefs.getString(ConstantsDirectory.FIRST_NAME,""))
        mBinding?.textBoxLastname?.setText(Prefs.getString(ConstantsDirectory.LAST_NAME,""))
        mBinding?.textBoxMobile?.setText(Prefs.getString(ConstantsDirectory.MOBILE,""))
        mBinding?.textBoxAltMobile?.setText(Prefs.getString(ConstantsDirectory.ALT_MOBILE,""))
        mBinding?.textBoxDesignation?.setText(Prefs.getString(ConstantsDirectory.DESIGNATION,""))

        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.textBoxAltMobile?.addTextChangedListener {
            if(mBinding?.textBoxAltMobile?.text?.isNotEmpty()!!) {
                if(mBinding?.textBoxAltMobile?.minLength(10) == false){
                    mBinding?.textBoxAltMobile?.minLength(10) { mBinding?.textBoxAltMobile?.error = activity?.getString(R.string.mobile_no_invalid_text) }
                    mBinding?.buttonNext?.isClickable = false
                }else{
                    mBinding?.buttonNext?.isClickable = true
                }
            }
        }

        mBinding?.buttonNext?.setOnClickListener{
            if(mBinding?.textBoxFirstname?.nonEmpty() == true &&
                mBinding?.textBoxMobile?.minLength(10) == true &&
                mBinding?.textBoxMobile?.nonEmpty() == true){
                Prefs.putString(ConstantsDirectory.FIRST_NAME,mBinding?.textBoxFirstname?.text.toString())
                Prefs.putString(ConstantsDirectory.LAST_NAME,mBinding?.textBoxLastname?.text.toString())
                Prefs.putString(ConstantsDirectory.MOBILE,mBinding?.textBoxMobile?.text.toString())
                Prefs.putString(ConstantsDirectory.ALT_MOBILE,mBinding?.textBoxAltMobile?.text.toString())
                Prefs.putString(ConstantsDirectory.DESIGNATION,mBinding?.textBoxDesignation?.text.toString())

                if (savedInstanceState == null) {
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.register_container,
                            BuyerRegisterCompanyFragment.newInstance(),"Register Buyer Company Details")
                        ?.addToBackStack(null)
                        ?.commit()
                }
            }else{
                mBinding?.textBoxFirstname?.nonEmpty{ mBinding?.textBoxFirstname?.error = it }
                mBinding?.textBoxMobile?.nonEmpty{ mBinding?.textBoxMobile?.error = it }
                mBinding?.textBoxMobile?.minLength(10){
                    mBinding?.textBoxMobile?.error =  activity?.getString(R.string.mobile_no_invalid_text)
                }

            }
        }
    }

}
