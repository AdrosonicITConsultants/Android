package com.adrosonic.craftexchange.ui.modules.artisan.auth.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.appcompat.widget.SearchView

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentArtisanLoginUsernameBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.loginResponse.LoginValidationResponse
import com.adrosonic.craftexchange.ui.modules.artisan.landing.PDFViewerActivity
import com.adrosonic.craftexchange.ui.modules.authentication.register.RegisterActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.fragment_buyer_login_username.*
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"

class ArtisanLoginUsernameFragment : Fragment() {
    private var param1: String? = null

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            ArtisanLoginUsernameFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
        const val TAG = "ArtisanLoginEmail"
    }


    private var mBinding: FragmentArtisanLoginUsernameBinding ?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_login_username, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var profile = arguments?.get(ARG_PARAM1).toString()

        val clickSpan = SpannableString("New User? Click here to register.")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                startActivity(
                    Intent(requireActivity(), RegisterActivity::class.java).putExtra(
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
                    .validateUserName(mBinding?.textBoxUsername?.text.toString(),Prefs.getLong(ConstantsDirectory.REF_ROLE_ID,0))
                    .enqueue(object : Callback, retrofit2.Callback<LoginValidationResponse> {
                        override fun onFailure(call: Call<LoginValidationResponse>, t: Throwable) {
                            t.printStackTrace()
                            Toast.makeText(requireActivity(),"${t.printStackTrace()}",Toast.LENGTH_SHORT).show()
                        }
                        override fun onResponse(
                            call: Call<LoginValidationResponse>,
                            response: Response<LoginValidationResponse>
                        ) {
                            if (response.body()?.valid == true) {
                                Prefs.putString(ConstantsDirectory.USER_EMAIL, mBinding?.textBoxUsername?.text.toString())
                                if (savedInstanceState == null) {
                                    activity?.supportFragmentManager?.beginTransaction()
                                        ?.replace(
                                            R.id.login_container,
                                            ArtisanLoginPasswordFragment.newInstance(profile),
                                            "Login Artisan Password"
                                        )
                                        ?.addToBackStack(null)
                                        ?.commit()
                                }
                            } else {
                                mBinding?.textBoxUsername?.error="${response.body()?.errorMessage}"
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

        mBinding?.privacyPolicy?.setOnClickListener{

            val intent = Intent(context, PDFViewerActivity::class.java)
            intent.putExtra("ViewType", "PRIVACY_POLICY_PDF")
            startActivity(intent)
        }

        mBinding?.legalDisclaimer?.setOnClickListener {

            val intent = Intent(context, PDFViewerActivity::class.java)
            intent.putExtra("ViewType", "LEGAL_DISCLAIMER")
            startActivity(intent)
        }

//        mBinding?.loginButton?.setOnClickListener {
//            login_button.setReadPermissions(listOf(EMAIL))
//            callbackManager = CallbackManager.Factory.create()
//
//            LoginManager.getInstance().registerCallback(callbackManager, object :FacebookCallback<LoginResult>{
//                override fun onSuccess(result: LoginResult?) {
//                   val grapghRequest = GraphRequest.newMeRequest(result?.accessToken){obj, response ->
//                       try {
//                           if (obj.has("id")){
//                                Log.d("FACEBOOKDATA", obj.getString("name"))
//                                Log.d("FACEBOOKDATA", obj.getString("email"))
//                                Log.d("FACEBOOKDATA", obj.getString("picture"))
//                           }
//                       }catch (e: Exception){
//
//                       }
//
//                   }
//                    val param = Bundle()
//                    param.putString("fields", "name,email,id,picture.type(large)")
//                    grapghRequest.parameters = param
//                    grapghRequest.executeAsync()
//                }
//
//                override fun onCancel() {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onError(error: FacebookException?) {
//                    TODO("Not yet implemented")
//                }
//
//            })
//
//        }


    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        callbackManager.onActivityResult(requestCode, resultCode, data)
//    }


}
