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
import com.adrosonic.craftexchange.databinding.FragmentBuyerRegisterCompanyBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty

private const val ARG_PARAM1 = "param1"

class BuyerRegisterCompanyFragment : Fragment() {

    companion object {
//        @JvmStatic
//        fun newInstance(param1: String) =
//            BuyerRegisterCompanyFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1,param1)
//                }
//            }
        fun newInstance() = BuyerRegisterCompanyFragment()
        const val TAG = "BuyerRegisterComp"
    }


    private var mBinding: FragmentBuyerRegisterCompanyBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_register_company, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var asterik = SpannableString("*")
        asterik.setSpan(ForegroundColorSpan(Color.RED), 0, asterik.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mBinding?.textCompname?.append(asterik)
        mBinding?.textPan?.append(asterik)

        mBinding?.buttonNext?.setOnClickListener{

            if(mBinding?.textBoxCompname?.nonEmpty() == true &&
                mBinding?.textBoxPan?.nonEmpty() == true) {

                Prefs.putString(ConstantsDirectory.COMP_NAME,mBinding?.textBoxCompname?.text.toString())
                Prefs.putString(ConstantsDirectory.CIN,mBinding?.textBoxCin?.text.toString())
                Prefs.putString(ConstantsDirectory.GST,mBinding?.textBoxGst?.text.toString())
                Prefs.putString(ConstantsDirectory.PAN,mBinding?.textBoxPan?.text.toString())
                Prefs.putString(ConstantsDirectory.POC_FNAME,mBinding?.textBoxPocFname?.text.toString())
                Prefs.putString(ConstantsDirectory.POC_LNAME,mBinding?.textBoxPocLname?.text.toString())
                Prefs.putString(ConstantsDirectory.POC_CONTACT,mBinding?.textBoxPocContact?.text.toString())
                Prefs.putString(ConstantsDirectory.POC_EMAIL,mBinding?.textBoxPocEmail?.text.toString())

                if (savedInstanceState == null) {
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.register_container,
                            BuyerRegisterAddressFragment.newInstance(),"Register Buyer Address Details")
                        ?.addToBackStack(null)
                        ?.commit()
                }
            }else{
                mBinding?.textBoxCompname?.nonEmpty{ mBinding?.textBoxCompname?.error = it }
                mBinding?.textBoxPan?.nonEmpty{ mBinding?.textBoxPan?.error = it }
            }
        }
    }
}
