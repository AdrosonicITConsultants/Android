package com.adrosonic.craftexchangemarketing.ui.modules.artisan.auth.login

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

import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.predicates.AddressPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.UserPredicates
import com.adrosonic.craftexchangemarketing.databinding.FragmentArtisanLoginPasswordBinding
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.login.ArtisanResponse
import com.adrosonic.craftexchangemarketing.repository.data.model.UserAuthModel
import com.adrosonic.craftexchangemarketing.repository.data.request.authModel.AdminAuthModel
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.login.AdminResponse
import com.adrosonic.craftexchangemarketing.ui.modules.authentication.register.RegisterActivity
import com.adrosonic.craftexchangemarketing.ui.modules.authentication.reset.ResetPasswordActivity
import com.adrosonic.craftexchangemarketing.ui.modules.cx_demovideo.CXVideoActivity
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"
private var mUserConfig = UserConfig()


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
//        clickSpan.setSpan(clickableSpan, 10, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

//        mBinding?.textViewClickHere?.text = clickSpan
//        mBinding?.textViewClickHere?.movementMethod = LinkMovementMethod.getInstance()
//        mBinding?.textViewClickHere?.highlightColor = Color.TRANSPARENT

        mBinding?.textForgotPwd?.setOnClickListener {
            startActivity(Intent(activity, ResetPasswordActivity::class.java))
//                .putExtra("profile",ConstantsDirectory.BUYER)
        }

        mBinding?.buttonNext?.setOnClickListener{

            if(Utility.checkIfInternetConnected(requireContext())) {
                if (mBinding?.textBoxPassword?.text.toString() != "" && mBinding?.textBoxUsername?.text.toString() != "") {


                    craftexchangemarketingRepository
                        .getLoginService()
                        .authenticateAdmin(
                            "application/json",
                            AdminAuthModel(
                                    mBinding?.textBoxUsername?.text.toString(),
                                    mBinding?.textBoxPassword?.text.toString()
                            )
                        )
                        .enqueue(object : Callback, retrofit2.Callback<AdminResponse> {
                            override fun onFailure(call: Call<AdminResponse>, t: Throwable) {
                                t.printStackTrace()
                                Toast.makeText(
                                    activity,
                                    "${t.printStackTrace()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onResponse(
                                call: Call<AdminResponse>, response: Response<AdminResponse>
                            ) {
                                if (response.body()?.valid == true) {
                                    Prefs.putString(
                                        ConstantsDirectory.USER_PWD,
                                        mBinding?.textBoxPassword?.text.toString()
                                    )
//                                    Prefs.putBoolean(ConstantsDirectory.IS_LOGGED_IN, true)
//                                    UserPredicates.insertArtisan(response.body()!!)
//                                    AddressPredicates.insertArtisanAddress(response.body()!!)


                                    mUserConfig.deviceName = "Android"


//                                    Prefs.putString(
//                                        ConstantsDirectory.USER_PWD,
//                                        mBinding?.textBoxPassword?.text.toString()
//                                    )
//                                    Prefs.putBoolean(ConstantsDirectory.IS_LOGGED_IN, true)
//                                    Prefs.putString(
//                                        ConstantsDirectory.USER_ID,
//                                        response.body()?.data?.user?.id.toString()
//                                    )
//                                    Prefs.putString(
//                                        ConstantsDirectory.ACC_TOKEN,
//                                        response.body()?.data?.acctoken
//                                    )
//                                    Prefs.putString(
//                                        ConstantsDirectory.FIRST_NAME,
//                                        response.body()?.data?.user?.firstName
//                                    )
//                                    Prefs.putString(
//                                        ConstantsDirectory.LAST_NAME,
//                                        response.body()?.data?.user?.lastName
//                                    )

                                    startActivity(Intent(activity, CXVideoActivity::class.java))
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "${response.body()?.errorMessage}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        })
                } else {
                    Toast.makeText(activity, "Enter Correct Credentials", Toast.LENGTH_SHORT).show()
                }
            }else{
                Utility.displayMessage(requireActivity().getString(R.string.no_internet_connection),requireContext())
            }
        }
    }
}
