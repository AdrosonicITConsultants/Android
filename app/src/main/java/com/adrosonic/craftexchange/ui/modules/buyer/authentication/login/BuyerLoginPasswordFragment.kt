package com.adrosonic.craftexchange.ui.modules.buyer.authentication.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.FragmentBuyerLoginPasswordBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.loginResponse.buyer.BuyerResponse
import com.adrosonic.craftexchange.repository.data.model.UserAuthModel
import com.adrosonic.craftexchange.ui.modules.authentication.register.RegisterActivity
import com.adrosonic.craftexchange.ui.modules.authentication.reset.ResetPasswordActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"

class BuyerLoginPasswordFragment : Fragment() {

//    companion object {
//        fun newInstance() = BuyerLoginPasswordFragment()
//    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            BuyerLoginPasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    private var mBinding: FragmentBuyerLoginPasswordBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_login_password, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var profile = arguments?.get(ARG_PARAM1)

        val clickSpan = SpannableString("New User? Click here to register.")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                startActivity(Intent(activity, RegisterActivity::class.java).putExtra("profile",
                    profile.toString()))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        clickSpan.setSpan(clickableSpan, 10, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        mBinding?.textViewClickHere?.text = clickSpan
        mBinding?.textViewClickHere?.movementMethod = LinkMovementMethod.getInstance()
        mBinding?.textViewClickHere?.highlightColor = Color.TRANSPARENT

        mBinding?.textForgotPwd?.setOnClickListener {
            startActivity(Intent(activity, ResetPasswordActivity::class.java))
//                .putExtra("profile",ConstantsDirectory.BUYER)
        }

        mBinding?.buttonNext?.setOnClickListener{
            if(mBinding?.textBoxPassword?.text.toString() != ""){

                CraftExchangeRepository
                    .getLoginService()
                    .authenticateBuyer("application/json",
                        UserAuthModel(
                            Prefs.getString(ConstantsDirectory.USER_EMAIL, null),
                            mBinding?.textBoxPassword?.text.toString(),
                            Prefs.getLong(ConstantsDirectory.REF_ROLE_ID, 0)
                        )
                    )
                    .enqueue(object : Callback, retrofit2.Callback<BuyerResponse>{
                        override fun onFailure(call: Call<BuyerResponse>, t: Throwable) {
                            t.printStackTrace()
                            Toast.makeText(activity,"${t.printStackTrace()}",Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(
                            call: Call<BuyerResponse>, response: Response<BuyerResponse>) {
                            if(response.body()?.valid == true){
                                Prefs.putString(ConstantsDirectory.USER_PWD,mBinding?.textBoxPassword?.text.toString())
                                Prefs.putBoolean(ConstantsDirectory.IS_LOGGED_IN,true)
                                UserPredicates.insertBuyer(response.body()!!)
                                Toast.makeText(activity,"Login Successful! - landing screen Buyer",Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(activity,"${response.body()?.errorMessage}",Toast.LENGTH_SHORT).show()
                            }
                        }

                    })
            }else{
                Toast.makeText(activity,"Enter Correct Password",Toast.LENGTH_SHORT).show()
            }

        }

    }

}
