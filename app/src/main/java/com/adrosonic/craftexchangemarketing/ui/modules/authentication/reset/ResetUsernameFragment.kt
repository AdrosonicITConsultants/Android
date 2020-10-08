package com.adrosonic.craftexchangemarketing.ui.modules.authentication.reset

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.FragmentResetUsernameBinding
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.model.OtpVerifyModel
import com.adrosonic.craftexchangemarketing.repository.data.resetResponse.ResetResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class ResetUsernameFragment : Fragment() {

    companion object{
        fun newInstance() =
            ResetUsernameFragment()
        const val TAG = "BuyerResetEmail"
    }
    private var mBinding: FragmentResetUsernameBinding?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_username, container, false)
        mBinding?.textBoxOtp?.setText("")
//        mBinding?.profileTag?.text = Prefs.getString(ConstantsDirectory.PROFILE,"")
        return mBinding?.root
    }

    private fun showProgress(){
        mBinding?.sendOtpLoader = true
        mBinding?.textBoxUsername?.isFocusableInTouchMode = false
        mBinding?.textBoxOtp?.isFocusableInTouchMode = false
        mBinding?.buttonSendOtp?.isClickable = false
        mBinding?.buttonVerify?.isClickable = false
//        mBinding?.buttonReach?.isClickable = false
    }
    private fun hideProgress(){
        mBinding?.sendOtpLoader = false
        mBinding?.textBoxUsername?.isFocusableInTouchMode = true
        mBinding?.textBoxOtp?.isFocusableInTouchMode = true
        mBinding?.buttonSendOtp?.isClickable = true
        mBinding?.buttonVerify?.isClickable = true
//        mBinding?.buttonReach?.isClickable = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.buttonSendOtp?.setOnClickListener{
            if(mBinding?.textBoxUsername?.text.toString().isNotEmpty()){
                showProgress()
                craftexchangemarketingRepository
                    .getResetPwdService()
                    .sendOtp(mBinding?.textBoxUsername?.text.toString())
                    .enqueue(object : Callback, retrofit2.Callback<ResetResponse> {
                        override fun onResponse(
                            call: Call<ResetResponse>, response: Response<ResetResponse>) {
                            if(response.body()?.valid == true){
                                Log.e(TAG, response.toString())
                                hideProgress()
                                Utility.messageDialog(requireContext(), requireActivity().getString(R.string.otp_send_success)  )
                            } else{
                                hideProgress()
                                Toast.makeText(activity,"${response.body()?.errorMessage}",Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResetResponse>, t: Throwable) {
                            hideProgress()
                            t.printStackTrace()
                            Toast.makeText(activity,"${t.printStackTrace()}",Toast.LENGTH_SHORT).show()
                        }
                    })
            }else{
                Toast.makeText(activity,"Enter Username",Toast.LENGTH_SHORT).show()
            }

        }

        mBinding?.buttonVerify?.setOnClickListener{
            if(mBinding?.textBoxUsername?.text.toString().isNotEmpty() &&
                mBinding?.textBoxOtp?.text.toString().isNotEmpty()){
                craftexchangemarketingRepository
                    .getResetPwdService()
                    .verifyEmailOtp("application/json",
                        OtpVerifyModel(
                            mBinding?.textBoxUsername?.text.toString(),
                            mBinding?.textBoxOtp?.text.toString(),
                            0
                        )
                    )
                    .enqueue(object : Callback, retrofit2.Callback<ResetResponse> {
                        override fun onResponse(
                            call: Call<ResetResponse>, response: Response<ResetResponse>) {

                            if(response.body()?.valid == true){
                                Log.e(TAG, response.toString())

                                if (savedInstanceState == null) {
                                    activity?.supportFragmentManager?.beginTransaction()
                                        ?.replace(R.id.reset_container,
                                            ResetPasswordFragment.newInstance(mBinding?.textBoxUsername?.text.toString()),"Reset Password")
                                        ?.addToBackStack(null)
                                        ?.commit()
                                }
                            }else{
                                Toast.makeText(activity,"${response.body()?.errorMessage}",Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ResetResponse>, t: Throwable) {
                            t.printStackTrace()
                            Toast.makeText(activity,"${t.printStackTrace()}",Toast.LENGTH_SHORT).show()
                        }

                    })
            }else{
                Toast.makeText(activity,"Enter Credentials",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
