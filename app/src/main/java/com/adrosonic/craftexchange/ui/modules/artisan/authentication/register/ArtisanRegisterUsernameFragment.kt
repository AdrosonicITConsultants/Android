package com.adrosonic.craftexchange.ui.modules.artisan.authentication.register

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentArtisanRegisterUsernameBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.model.OtpVerifyModel
import com.adrosonic.craftexchange.repository.data.registerResponse.RegisterResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class ArtisanRegisterUsernameFragment : Fragment() {

    companion object {
        fun newInstance() = ArtisanRegisterUsernameFragment()
        const val TAG = "ArtisanRegArtId"
    }

    private var mBinding: FragmentArtisanRegisterUsernameBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_register_username, container, false)
        mBinding?.sendOtpLoader = false
        return mBinding?.root
    }
    private fun alertDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
        builder.setMessage(message)
            .setPositiveButton("Ok"){ dialog, id ->
                dialog.cancel()
            }
        builder.create().show()
    }
    private fun showProgress(){
        mBinding?.sendOtpLoader = true
        mBinding?.textBoxUsername?.isFocusableInTouchMode = false
        mBinding?.textBoxOtp?.isFocusableInTouchMode = false
        mBinding?.buttonSendOtp?.isClickable = false
        mBinding?.buttonVerify?.isClickable = false
        mBinding?.buttonReach?.isClickable = false
    }
    private fun hideProgress(){
        mBinding?.sendOtpLoader = false
        mBinding?.textBoxUsername?.isFocusableInTouchMode = true
        mBinding?.textBoxOtp?.isFocusableInTouchMode = true
        mBinding?.buttonSendOtp?.isClickable = true
        mBinding?.buttonVerify?.isClickable = true
        mBinding?.buttonReach?.isClickable = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.buttonSendOtp?.setOnClickListener{
            if(mBinding?.textBoxUsername?.nonEmpty{ mBinding?.textBoxUsername?.error = it } == true) {
                showProgress()
                CraftExchangeRepository
                    .getRegisterService()
                    .sendVerifyEmailOtp(mBinding?.textBoxUsername?.text.toString())
                    .enqueue(object : Callback, retrofit2.Callback<RegisterResponse> {
                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: Response<RegisterResponse>
                        ) {
                            if(response.body()?.valid == true){
                                Log.e(TAG, response.toString())
                                hideProgress()
                                alertDialog("OTP sent successfully!")
                            } else{
                                hideProgress()
                                Toast.makeText(activity,"${response.body()?.errorMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            hideProgress()
                            t.printStackTrace()
                            Toast.makeText(activity,"${t.printStackTrace()}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }

        mBinding?.buttonVerify?.setOnClickListener{
            if(mBinding?.textBoxUsername?.nonEmpty() == true &&
                mBinding?.textBoxOtp?.nonEmpty() == true ){

                CraftExchangeRepository
                    .getRegisterService()
                    .verifyEmailOtp("application/json",
                        OtpVerifyModel(
                            mBinding?.textBoxUsername?.text.toString(),
                            mBinding?.textBoxOtp?.text.toString(),
                            Prefs.getLong(ConstantsDirectory.REF_ROLE_ID, 0)
                        )
                    )
                    .enqueue(object : Callback, retrofit2.Callback<RegisterResponse> {
                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: Response<RegisterResponse>
                        ) {
                            if(response.body()?.valid == true){
                                Log.e(TAG, response.toString())

                                Prefs.putString(ConstantsDirectory.USER_EMAIL,mBinding?.textBoxUsername?.text.toString())

                                if (savedInstanceState == null) {
                                    activity?.supportFragmentManager?.beginTransaction()
                                        ?.replace(R.id.register_container,
                                            ArtisanRegisterPasswordFragment.newInstance(),"Register Artisan Password")
                                        ?.addToBackStack(null)
                                        ?.commit()
                                }
                            }else{
                                Toast.makeText(activity,"${response.body()?.errorMessage}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            t.printStackTrace()
                            Toast.makeText(activity,"${t.printStackTrace()}", Toast.LENGTH_SHORT).show()
                        }

                    })
            }else{
                mBinding?.textBoxUsername?.nonEmpty{ mBinding?.textBoxUsername?.error = it }
                mBinding?.textBoxOtp?.nonEmpty{ mBinding?.textBoxOtp?.error = it
                }
            }
        }
    }

}
