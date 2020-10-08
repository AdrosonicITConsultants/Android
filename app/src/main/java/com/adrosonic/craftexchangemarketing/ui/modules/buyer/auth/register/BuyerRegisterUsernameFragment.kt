package com.adrosonic.craftexchangemarketing.ui.modules.buyer.auth.register


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.FragmentBuyerRegisterUsernameBinding
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.model.OtpVerifyModel
import com.adrosonic.craftexchangemarketing.repository.data.registerResponse.RegisterResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


private const val ARG_PARAM1 = "param1"

class BuyerRegisterUsernameFragment : Fragment() {

    companion object {
//        @JvmStatic
//        fun newInstance(param1: String) =
//            BuyerRegisterUsernameFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                }
//            }
        fun newInstance() = BuyerRegisterUsernameFragment()
        const val TAG = "BuyerRegisterEmail"
    }

    private var mBinding: FragmentBuyerRegisterUsernameBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_register_username, container, false)
        mBinding?.textBoxUsername?.setText(Prefs.getString(ConstantsDirectory.USER_EMAIL,""))
        mBinding?.textBoxOtp?.setText("")
        mBinding?.sendOtpLoader = false
        return mBinding?.root
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
                craftexchangemarketingRepository
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
                                Utility.messageDialog(requireContext(), requireActivity().getString(R.string.otp_send_success)  )

                            } else{
                                hideProgress()
                                Toast.makeText(activity,"${response.body()?.errorMessage}",Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            hideProgress()
                            t.printStackTrace()
                            Toast.makeText(activity,"${t.printStackTrace()}",Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }

        mBinding?.buttonVerify?.setOnClickListener{
        if(mBinding?.textBoxUsername?.nonEmpty() == true &&
            mBinding?.textBoxOtp?.nonEmpty() == true ){

            craftexchangemarketingRepository
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
                        response: Response<RegisterResponse>) {
                        if(response.body()?.valid == true){
                            Log.e(TAG, response.toString())

                            Prefs.putString(ConstantsDirectory.USER_EMAIL,mBinding?.textBoxUsername?.text.toString())

                            if (savedInstanceState == null) {
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.register_container,
                                        BuyerRegisterPasswordFragment.newInstance(),"Register Buyer Password")
                                    ?.addToBackStack(null)
                                    ?.commit()
                            }
                        }else{
                            hideProgress()
                            Toast.makeText(activity,"${response.body()?.errorMessage}",Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        t.printStackTrace()
                        Toast.makeText(activity,"${t.printStackTrace()}",Toast.LENGTH_SHORT).show()

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
