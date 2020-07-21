package com.adrosonic.craftexchange.ui.modules.buyer.auth.login

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
import com.adrosonic.craftexchange.databinding.FragmentBuyerLoginUsernameBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.loginResponse.LoginValidationResponse
import com.adrosonic.craftexchange.ui.modules.auth_com.register.RegisterActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"

class BuyerLoginUsernameFragment : Fragment() {
//
    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            BuyerLoginUsernameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

//    companion object {
//        fun newInstance() = BuyerLoginUsernameFragment()
//    }

    private var mBinding: FragmentBuyerLoginUsernameBinding ?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_login_username, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var profile = arguments?.get(ARG_PARAM1).toString()

        val clickSpan = SpannableString("New User? Click here to register.")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                startActivity(
                    Intent(activity, RegisterActivity::class.java).putExtra(
                        "profile",
                        profile
                    )
                )
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

        mBinding?.buttonNext?.setOnClickListener {

            if (mBinding?.textBoxUsername?.text.toString() != "") {
                if (Utility.checkIfInternetConnected(requireContext())){
                    CraftExchangeRepository
                        .getLoginService()
                        .validateUserName(
                            mBinding?.textBoxUsername?.text.toString(),
                            Prefs.getLong(ConstantsDirectory.REF_ROLE_ID, 0)
                        )
                        .enqueue(object : Callback, retrofit2.Callback<LoginValidationResponse> {
                            override fun onFailure(
                                call: Call<LoginValidationResponse>,
                                t: Throwable
                            ) {
                                t.printStackTrace()
                                Toast.makeText(
                                    activity,
                                    "${t.printStackTrace()}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onResponse(
                                call: Call<LoginValidationResponse>,
                                response: Response<LoginValidationResponse>
                            ) {
                                if (response.body()?.valid == true) {
                                    Prefs.putString(
                                        ConstantsDirectory.USER_EMAIL,
                                        mBinding?.textBoxUsername?.text.toString()
                                    )
                                    if (savedInstanceState == null) {
                                        activity?.supportFragmentManager?.beginTransaction()
                                            ?.replace(
                                                R.id.login_container,
                                                BuyerLoginPasswordFragment.newInstance(profile),
                                                "Login Buyer Password"
                                            )
                                            ?.addToBackStack(null)
                                            ?.commit()
                                    }
                                } else {
                                    Toast.makeText(
                                        activity,
                                        response.body()?.errorMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        })
                }else{
                    Utility.displayMessage(requireActivity().getString(R.string.no_internet_connection),requireContext())
                }
            } else {
                Utility.displayMessage(requireActivity().getString(R.string.enter_email_mobile),requireContext())
            }
        }
    }
}
