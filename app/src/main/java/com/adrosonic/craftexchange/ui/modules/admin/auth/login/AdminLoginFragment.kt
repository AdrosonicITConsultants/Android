package com.adrosonic.craftexchange.ui.modules.admin.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.predicates.AdminPredicates
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.FragmentAdminLoginBinding
import com.adrosonic.craftexchange.databinding.FragmentArtisanLoginPasswordBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.authModel.AdminAuthModel
import com.adrosonic.craftexchange.repository.data.response.admin.login.AdminResponse
import com.adrosonic.craftexchange.ui.modules.artisan.auth.login.ArtisanLoginPasswordFragment
import com.adrosonic.craftexchange.ui.modules.authentication.reset.ResetPasswordActivity
import com.adrosonic.craftexchange.ui.modules.cx_demovideo.CXVideoActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
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
                    putString(com.adrosonic.craftexchange.ui.modules.admin.auth.login.ARG_PARAM1, param1)
                }
            }
        const val TAG = "ArtisanLoginPwd"
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


                    CraftExchangeRepository
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
                                    AdminPredicates.insertAdmin(response.body()!!)
//                                    AddressPredicates.insertArtisanAddress(response.body()!!)


                                    com.adrosonic.craftexchange.ui.modules.admin.auth.login.mUserConfig.deviceName = "Android"


//                                    Prefs.putString(
//                                        ConstantsDirectory.USER_PWD,
//                                        mBinding?.textBoxPassword?.text.toString()
//                                    )
                                    Prefs.putString(
                                        ConstantsDirectory.USER_ID,
                                        response.body()?.data?.user?.id.toString()
                                    )
                                    Prefs.putString(
                                        ConstantsDirectory.ACC_TOKEN,
                                        response.body()?.data?.acctoken
                                    )

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
                    Toast.makeText(activity, "Enter Username and Password", Toast.LENGTH_SHORT).show()
                }
            }else{
                Utility.displayMessage(requireActivity().getString(R.string.no_internet_connection),requireContext())
            }
        }

        }


}