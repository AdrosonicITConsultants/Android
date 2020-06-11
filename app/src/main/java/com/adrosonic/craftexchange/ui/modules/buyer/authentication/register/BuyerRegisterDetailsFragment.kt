package com.adrosonic.craftexchange.ui.modules.buyer.authentication.register

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerRegisterDetailsBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.Validator
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

        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.buttonNext?.setOnClickListener{
            if(mBinding?.textBoxFirstname?.nonEmpty() == true &&
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
            }
        }
    }

}
