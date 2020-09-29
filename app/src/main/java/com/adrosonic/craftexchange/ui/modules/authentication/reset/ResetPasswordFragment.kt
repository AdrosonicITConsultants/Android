package com.adrosonic.craftexchange.ui.modules.authentication.reset

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentResetPasswordBinding

import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.resetResponse.ResetResponse
import com.adrosonic.craftexchange.repository.data.model.UserAuthModel
import com.adrosonic.craftexchange.repository.data.request.authModel.AdminAuthModel
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.*
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"

class ResetPasswordFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            ResetPasswordFragment()
                .apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
        const val TAG = "ResetPwd"

    }

    private var mBinding: FragmentResetPasswordBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false)
//        mBinding?.profileTag?.text = Prefs.getString(ConstantsDirectory.PROFILE,"")
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var email = arguments?.get(ARG_PARAM1).toString()
        mBinding?.buttonNext?.setOnClickListener{

            if(mBinding?.textBoxPassword?.text.toString() == mBinding?.textBoxRetypePwd?.text.toString()){
                if(
                    mBinding?.textBoxPassword?.nonEmpty() == true &&
                            mBinding?.textBoxPassword?.atleastOneNumber() == true &&
                            mBinding?.textBoxPassword?.atleastOneSpecialCharacters() == true &&
                            mBinding?.textBoxPassword?.atleastOneUpperCase() == true &&
                            mBinding?.textBoxPassword?.minLength(8) == true &&
                            mBinding?.textBoxRetypePwd?.nonEmpty() == true
                ){
                    CraftExchangeRepository
                        .getResetPwdService()
                        .resetPassword("application/json",
                            AdminAuthModel(
                                email,
                                mBinding?.textBoxRetypePwd?.text.toString()
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
                                                ResetSuccessFragment.newInstance(),"Reset Buyer Success")
                                            ?.addToBackStack(null)
                                            ?.commit()
                                    }else{
                                        Toast.makeText(activity,"${response.body()?.errorMessage}",Toast.LENGTH_SHORT).show()
                                    }
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
