package com.adrosonic.craftexchangemarketing.ui.modules.authentication.reset

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.FragmentResetPasswordFirstTimeBinding

import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.resetResponse.ResetResponse
import com.adrosonic.craftexchangemarketing.repository.data.request.authModel.AdminAuthModel
import com.adrosonic.craftexchangemarketing.repository.data.request.authModel.ResetPasswordModel
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.*
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ResetPasswordFirstTimeFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ResetPasswordFirstTimeFragment()
                .apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        const val TAG = "ResetPwdFirstTime"

    }

    private var mBinding: FragmentResetPasswordFirstTimeBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password_first_time, container, false)
//        mBinding?.profileTag?.text = Prefs.getString(ConstantsDirectory.PROFILE,"")
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val username = arguments?.get(ARG_PARAM1).toString()
        val resetToken = arguments?.get(ARG_PARAM2).toString()

        mBinding?.buttonNext?.setOnClickListener{

            if(mBinding?.textBoxPassword?.text.toString() == mBinding?.textBoxRetypePwd?.text.toString()){
                if(
                    mBinding?.textBoxPassword?.nonEmpty() == true &&
                            mBinding?.textBoxPassword?.atleastOneNumber() == true &&
                            mBinding?.textBoxPassword?.atleastOneSpecialCharacters() == true &&
                            mBinding?.textBoxPassword?.atleastOneUpperCase() == true &&
                            mBinding?.textBoxPassword?.minLength(8) == true &&
                            mBinding?.textBoxRetypePwd?.nonEmpty() == true &&
                            mBinding?.textBoxCurrentPassword?.nonEmpty() == true
                ){
                    craftexchangemarketingRepository
                        .getResetPwdService()
                        .resetUserPassword("application/json",
                            ResetPasswordModel(
                                mBinding?.textBoxCurrentPassword?.text.toString(),
                                mBinding?.textBoxPassword?.text.toString(),
                                resetToken,
                                username
                            )
                        )
                        .enqueue(object : Callback,retrofit2.Callback<ResetResponse>{
                            override fun onFailure(call: Call<ResetResponse>, t: Throwable) {
                                t.printStackTrace()
                                Toast.makeText(activity,"${t.printStackTrace()}",Toast.LENGTH_SHORT).show()
                            }
                            override fun onResponse(
                                call: Call<ResetResponse>, response: Response<ResetResponse>) {
                                if(response.body()?.valid == true){
                                    Prefs.putString(ConstantsDirectory.USER_PWD,mBinding?.textBoxRetypePwd?.text.toString())
                                    if (savedInstanceState == null) {
                                        activity?.supportFragmentManager?.beginTransaction()
                                            ?.replace(R.id.reset_container,
                                                ResetSuccessFragment.newInstance(true),"Reset Buyer Success")
                                            ?.addToBackStack(null)
                                            ?.commit()
                                    }
                                    else{
                                        Toast.makeText(activity,"${response.body()?.errorMessage}",Toast.LENGTH_SHORT).show()
                                    }
                                }
                                else{
                                    Toast.makeText(activity,response.body()?.errorMessage,Toast.LENGTH_SHORT).show()
                                }
                            }

                        })
                }else{
                    mBinding?.textBoxPassword?.validator()
                        ?.nonEmpty()
                        ?.atleastOneNumber()
                        ?.atleastOneSpecialCharacters()
                        ?.atleastOneUpperCase()
                        ?.minLength(8)
                        ?.addErrorCallback { mBinding?.textBoxPassword?.error = it }
                        ?.check()
                    mBinding?.textBoxRetypePwd?.nonEmpty{ mBinding?.textBoxRetypePwd?.error = it }
                    Utility.messageDialog(requireContext(), requireActivity().getString(R.string.pwd_validation_text)  )                }
            }else{
                Toast.makeText(activity,"Passwords are mismatched",Toast.LENGTH_SHORT).show()
            }
        }
    }


}
