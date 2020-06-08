package com.adrosonic.craftexchange.ui.modules.buyer.authentication.register

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerRegisterWebBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.model.*
import com.adrosonic.craftexchange.repository.data.registerResponse.*
import com.adrosonic.craftexchange.ui.modules.authentication.login.LoginActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"


class BuyerRegisterWebFragment : Fragment() {

    companion object {
//        @JvmStatic
//        fun newInstance(param1: String,param2: String,param3: String) =
//            BuyerRegisterWebFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1,param1)
//                    putString(ARG_PARAM2,param2)
//                    putString(ARG_PARAM3,param3)
//                }
//            }
        fun newInstance() = BuyerRegisterWebFragment()
        const val TAG = "BuyerRegisterWeb"
    }

    var mBinding : FragmentBuyerRegisterWebBinding ?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_register_web, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val clickSpan = SpannableString("terms & Condition")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                Toast.makeText(activity,"Terms n Conditions",Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        clickSpan.setSpan(clickableSpan, 0, clickSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        mBinding?.textTnct?.append(clickSpan)
        mBinding?.textTnct?.movementMethod = LinkMovementMethod.getInstance()
        mBinding?.textTnct?.highlightColor = Color.TRANSPARENT

        var addrType =
            AddressType(
                "",
                0
            )
        var country = Country(Prefs.getString(ConstantsDirectory.COUNTRY_ID,"").toLong())
//        var country =
//            Country(
//                1,
//                "INDIA"
//            )
        var addr = Address(
            addrType,
            Prefs.getString(ConstantsDirectory.CITY, ""),
            country,
            Prefs.getString(ConstantsDirectory.DISTRICT, ""),
            0,
            Prefs.getString(ConstantsDirectory.LANDMARK, ""),
            Prefs.getString(ConstantsDirectory.ADDR_LINE1, ""),
            Prefs.getString(ConstantsDirectory.ADDR_LINE2, ""),
            Prefs.getString(ConstantsDirectory.PINCODE, ""),
            Prefs.getString(ConstantsDirectory.STATE, ""),
            Prefs.getString(ConstantsDirectory.STREET, "")
        )

        var company =
            BuyerCompanyDetails(
                Prefs.getString(ConstantsDirectory.CIN, ""),
                Prefs.getString(ConstantsDirectory.COMP_NAME, ""),
                "",
                Prefs.getString(ConstantsDirectory.GST, ""),
                0,
                "Test"
            )

        var poc =
            BuyerPointOfContact(
                Prefs.getString(ConstantsDirectory.POC_CONTACT, ""),
                Prefs.getString(ConstantsDirectory.POC_EMAIL, ""),
                Prefs.getString(ConstantsDirectory.POC_FNAME, ""),
                0,
                Prefs.getString(ConstantsDirectory.POC_LNAME, "")
            )

        mBinding?.buttonComplete?.setOnClickListener{

            var registerRequest =
                User(
                    addr,
                    Prefs.getString(ConstantsDirectory.ALT_MOBILE, ""),
                    company,
                    poc,
                    Prefs.getString(ConstantsDirectory.DESIGNATION, ""),
                    Prefs.getString(ConstantsDirectory.USER_EMAIL, ""),
                    Prefs.getString(ConstantsDirectory.FIRST_NAME, ""),
                    Prefs.getString(ConstantsDirectory.LAST_NAME, ""),
                    Prefs.getString(ConstantsDirectory.MOBILE, ""),
                    Prefs.getString(ConstantsDirectory.PAN, ""),
                    Prefs.getString(ConstantsDirectory.USER_PWD, ""),
                    Prefs.getLong(ConstantsDirectory.REF_ROLE_ID, 0),
                    mBinding?.textBoxSociallink?.text.toString(),
                    mBinding?.textBoxWeblink?.text.toString()
                )

            if(mBinding?.checkBoxTnc?.isChecked == true){
                CraftExchangeRepository
                    .getRegisterService()
                    .registerBuyer("application/json",registerRequest)
                    .enqueue(object:Callback, retrofit2.Callback<RegisterResponse> {
                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            t.printStackTrace()
                        }
                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: retrofit2.Response<RegisterResponse>) {
                            if(response.body()?.valid == true){
                                Log.e(TAG, response.toString())
                                Toast.makeText(activity,"User Registered Successfully",Toast.LENGTH_SHORT).show()
                                Prefs.clear()
                                Prefs.putString(ConstantsDirectory.PROFILE,"Buyer")
                                Prefs.putLong(ConstantsDirectory.REF_ROLE_ID,2)
                                startActivity(Intent(activity,LoginActivity::class.java))}
                        }

                    })
            }else{
                Toast.makeText(activity,"Read TnC",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
