package com.adrosonic.craftexchange.ui.modules.artisan.authentication.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.FragmentArtisanLoginPasswordBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.loginResponse.artisan.ArtisanResponse
import com.adrosonic.craftexchange.repository.data.model.UserAuthModel
import com.adrosonic.craftexchange.ui.modules.authentication.register.RegisterActivity
import com.adrosonic.craftexchange.ui.modules.authentication.reset.ResetPasswordActivity
import com.adrosonic.craftexchange.ui.modules.cx_video.CXVideoActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"

class ArtisanLoginPasswordFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            ArtisanLoginPasswordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
        const val TAG = "ArtisanLoginPwd"
    }

    private var mBinding: FragmentArtisanLoginPasswordBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_login_password, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val clickSpan = SpannableString("New User? Click here to register.")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                startActivity(Intent(activity, RegisterActivity::class.java))
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
                    .authenticateArtisan("application/json",
                        UserAuthModel(
                            Prefs.getString(ConstantsDirectory.USER_EMAIL, null),
                            mBinding?.textBoxPassword?.text.toString(),
                            Prefs.getLong(ConstantsDirectory.REF_ROLE_ID, 0)
                        )
                    )
                    .enqueue(object : Callback, retrofit2.Callback<ArtisanResponse>{
                        override fun onFailure(call: Call<ArtisanResponse>, t: Throwable) {
                            t.printStackTrace()
                            Toast.makeText(activity,"${t.printStackTrace()}", Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(
                            call: Call<ArtisanResponse>, response: Response<ArtisanResponse>
                        ) {
                            if(response.body()?.valid == true){
                                Prefs.putString(ConstantsDirectory.USER_PWD,mBinding?.textBoxPassword?.text.toString())
                                Prefs.putBoolean(ConstantsDirectory.IS_LOGGED_IN,true)
                                UserPredicates.insertArtisan(response.body()!!)
                                Toast.makeText(activity,"Login Successful! - landing screen Artist", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(activity, CXVideoActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                            }else{
                                Toast.makeText(activity,"${response.body()?.errorMessage}",Toast.LENGTH_SHORT).show()
                            }
                        }

                    })
            }else{
                Toast.makeText(activity,"Enter Correct Pwd", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
