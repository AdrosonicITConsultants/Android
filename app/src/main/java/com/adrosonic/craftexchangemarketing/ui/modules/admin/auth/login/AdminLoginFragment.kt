package com.adrosonic.craftexchangemarketing.ui.modules.admin.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.predicates.AdminPredicates
import com.adrosonic.craftexchangemarketing.databinding.FragmentAdminLoginBinding
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.request.authModel.AdminAuthModel
import com.adrosonic.craftexchangemarketing.repository.data.response.admin.login.AdminResponse
import com.adrosonic.craftexchangemarketing.ui.modules.admin.landing.adminLandingIntent
import com.adrosonic.craftexchangemarketing.ui.modules.authentication.reset.ResetPasswordActivity
import com.adrosonic.craftexchangemarketing.ui.modules.authentication.reset.resetFirstTimeIntent
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"
private var mUserConfig = UserConfig()

class AdminLoginFragment :Fragment(){
    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            AdminLoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
        const val TAG = "AdminLogin"
    }
    private var mBinding: FragmentAdminLoginBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_login, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        mBinding?.textForgotPwd?.setOnClickListener {
            startActivity(Intent(activity, ResetPasswordActivity::class.java))
//                .putExtra("profile",ConstantsDirectory.BUYER)
        }

        mBinding?.buttonNext?.setOnClickListener {

            if(Utility.checkIfInternetConnected(requireContext())) {
                if (mBinding?.textBoxPassword?.text.toString() != "" && mBinding?.textBoxUsername?.text.toString() != "") {
                    craftexchangemarketingRepository
                        .getLoginService()
                        .authenticateLoginAdmin(
                            "application/json",
                            AdminAuthModel(
                                mBinding?.textBoxUsername?.text.toString(),
                                mBinding?.textBoxPassword?.text.toString()
                            )
                        )
                        .enqueue(object : Callback, retrofit2.Callback<JsonElement> {
                            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                                t.printStackTrace()
                                Toast.makeText(
                                    activity,
                                    "Login failure ${t.printStackTrace()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("Login", "onFailure :${t.message}")
                                Log.e("Login", "onFailure :${t.localizedMessage}")
                            }

                            override fun onResponse(
                                call: Call<JsonElement>,
                                response: Response<JsonElement>
                            ) {

                                val valid = response.body()?.asJsonObject?.get("valid")?.asBoolean
                                val errorMessage = response.body()?.asJsonObject?.get("errorMessage")
                                val errorCode = response.body()?.asJsonObject?.get("errorCode")?.asInt

                                Log.e("Login", "onResponse :${call.request().url}")
                                Log.e("Login", "onResponse :${response.message()}")
                                Log.e("Login", "onResponse :${errorMessage}")
                                Log.e("Login", "onResponse :${errorCode}")
                                if (valid!!) {
                                    Toast.makeText(context, "valid", Toast.LENGTH_SHORT).show()
                                    when (errorCode) {
                                        701 -> {
                                            val resetToken = response.body()?.asJsonObject?.get("body")?.asString
                                            Toast.makeText(context, errorMessage.toString(), Toast.LENGTH_SHORT).show()
                                            startActivity(
                                                context?.resetFirstTimeIntent(
                                                    mBinding?.textBoxUsername?.text.toString(),
                                                    resetToken.toString()
                                                )
                                            )
                                        }
                                        702 -> {
                                            val resetToken = response.body()?.asJsonObject?.get("body")?.asString
                                            Toast.makeText(
                                                context,
                                                "Your password has expired and must be changed.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            startActivity(
                                                context?.resetFirstTimeIntent(
                                                    mBinding?.textBoxUsername?.text.toString(),
                                                    resetToken.toString()
                                                )
                                            )
                                        }
                                        703 -> {
                                            Toast.makeText(context, "Account is locked as maximum number of login attempts has exceeded. Please try after 5 minutes or unlock by clicking forgot password.", Toast.LENGTH_SHORT).show()
                                        }
                                        0 -> {
                                            val body = Gson().fromJson(
                                                response.body(),
                                                AdminResponse::class.java
                                            )
                                            Prefs.putString(
                                                ConstantsDirectory.USER_PWD,
                                                mBinding?.textBoxPassword?.text.toString()
                                            )
                                            Prefs.putBoolean(ConstantsDirectory.IS_LOGGED_IN, true)
                                            AdminPredicates.insertAdmin(body.data)
                                            mUserConfig.deviceName = "Android"
                                            Prefs.putString(
                                                ConstantsDirectory.USER_ID,
                                                body.data.user?.id.toString()
                                            )
                                            Prefs.putString(
                                                ConstantsDirectory.ACC_TOKEN,
                                                body.data.acctoken
                                            )
                                            mUserConfig.adminUserRoles =
                                                body.data.user?.refMarketingRoleId ?: 0
                                            startActivity(context?.adminLandingIntent())
                                        }
                                    }

                                } else {
                                    Toast.makeText(
                                        activity,
                                        "Invalid user name or password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        })
                } else {
                    Toast.makeText(activity, "Enter Username and Password", Toast.LENGTH_SHORT).show()
                }
            }else{
                Utility.displayMessage(requireActivity().getString(R.string.no_internet_connection),requireContext())
            }
        }

        }


}