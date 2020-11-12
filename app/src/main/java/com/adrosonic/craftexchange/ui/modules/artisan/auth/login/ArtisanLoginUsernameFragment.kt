package com.adrosonic.craftexchange.ui.modules.artisan.auth.login

import android.app.Dialog
import android.content.Context
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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.adrosonic.craftexchange.LocalizationManager.LocaleManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.predicates.AddressPredicates
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.FragmentArtisanLoginUsernameBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.loginResponse.LoginValidationResponse
import com.adrosonic.craftexchange.repository.data.response.artisan.login.ArtisanResponse
import com.adrosonic.craftexchange.ui.modules.pdfViewer.PdfViewerActivity
import com.adrosonic.craftexchange.ui.modules.authentication.register.RegisterActivity
import com.adrosonic.craftexchange.ui.modules.cx_demovideo.CXVideoActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"

class ArtisanLoginUsernameFragment : Fragment() {
    private var param1: String? = null

    var mGoogleSignInClient: GoogleSignInClient?= null
    var callbackManager: CallbackManager?= null
    private val RC_SIGN_IN = 9001

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
    var mUserConfig : UserConfig ?= null


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

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

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
            val intent = Intent(context, PdfViewerActivity::class.java)
            intent.putExtra("ViewType", "PRIVACY_POLICY_PDF")
            startActivity(intent)
        }

        mBinding?.legalDisclaimer?.setOnClickListener {

            val intent = Intent(context, PdfViewerActivity::class.java)
            intent.putExtra("ViewType", "LEGAL_DISCLAIMER")
            startActivity(intent)
        }

        mBinding?.needHelp?.setOnClickListener {

            val intent = Intent(context, PdfViewerActivity::class.java)
            intent.putExtra("ViewType", "HELP")
            startActivity(intent)
        }
        mBinding?.googleLoginBtn?.setOnClickListener {
            // Build a GoogleSignInClient with the options specified by gso.
            mGoogleSignInClient = activity?.let { GoogleSignIn.getClient(it, gso) }!!
            signIn()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // callbackManager?.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
        else{
            callbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun signIn(){
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            //Signed in successfully
            val googleId = account?.id ?: ""
            Log.i("Google ID",googleId)

            val googleFirstName = account?.givenName ?: ""
            Log.i("Google First Name", googleFirstName)

            val googleLastName = account?.familyName ?: ""
            Log.i("Google Last Name", googleLastName)

            val googleEmail = account?.email ?: ""
            Log.i("Google Email", googleEmail)

            val googleProfilePicURL = account?.photoUrl.toString()
            Log.i("Google Profile Pic URL", googleProfilePicURL)

            val googleIdToken = account?.idToken ?: ""
            Log.i("Google ID Token", googleIdToken)
        mBinding?.changeLanguageText?.setOnClickListener {
            showLanguageSelectionDialog()
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

            if(Utility.checkIfInternetConnected(requireContext())) {

                CraftExchangeRepository
                    .getLoginService()
                    .authSocialArtisan("google", googleIdToken)
                    .enqueue(object : Callback, retrofit2.Callback<ArtisanResponse> {
                        override fun onFailure(call: Call<ArtisanResponse>, t: Throwable) {
                            t.printStackTrace()
                            Toast.makeText(
                                activity,
                                "${t.printStackTrace()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onResponse(
                            call: Call<ArtisanResponse>, response: Response<ArtisanResponse>
                        ) {
                            if (response.body()?.valid == true) {

                                Prefs.putBoolean(ConstantsDirectory.IS_LOGGED_IN, true)
                                UserPredicates.insertArtisan(response.body()!!)
                                AddressPredicates.insertArtisanAddress(response.body()!!)


                                mUserConfig?.deviceName = "Android"


                                Prefs.putBoolean(ConstantsDirectory.IS_LOGGED_IN, true)
                                Prefs.putString(
                                    ConstantsDirectory.USER_ID,
                                    response.body()?.data?.user?.id.toString()
                                )
                                Prefs.putString(
                                    ConstantsDirectory.ACC_TOKEN,
                                    response.body()?.data?.acctoken
                                )
                                Prefs.putString(
                                    ConstantsDirectory.FIRST_NAME,
                                    response.body()?.data?.user?.firstName
                                )
                                Prefs.putString(
                                    ConstantsDirectory.LAST_NAME,
                                    response.body()?.data?.user?.lastName
                                )

                                startActivity(Intent(activity, CXVideoActivity::class.java))
                            }else {
                                Toast.makeText(
                                    activity,
                                    "Register your Google Id & Try Again",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                    })

            }else{
                Utility.displayMessage(requireActivity().getString(R.string.no_internet_connection),requireContext())
            }



        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e("failed code=", e.statusCode.toString())
        }
    }
    fun showLanguageSelectionDialog() {
        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_select_language)
        dialog.show()
        val spLanguage = dialog.findViewById(R.id.sp_language) as Spinner
        val btnConfirm = dialog.findViewById(R.id.btn_confirm) as Button

        val spLanguageAdapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_spinner_item,resources.getStringArray(R.array.lang_selector))
        spLanguageAdapter.setDropDownViewResource(R.layout.spinner_item)
        spLanguage?.adapter = spLanguageAdapter
        btnConfirm.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext(),android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
            builder.setMessage("Are you sure? You want to set ${spLanguage?.selectedItem.toString()} as your app language?")
                .setCancelable(true)
                .setPositiveButton("OK") { dialog1, id ->
                    if(spLanguage?.selectedItem.toString().equals("Hindi"))setNewLocale(requireActivity(), LocaleManager.HINDI)
                    else setNewLocale(requireActivity(), LocaleManager.ENGLISH)
                    dialog1.cancel()
                    dialog.cancel()
                }
            builder.show()
        }
    }
    private fun setNewLocale(
        mContext: FragmentActivity,
        language: String
    ) {
        LocaleManager.setNewLocale(requireContext(), language)
        val intent: Intent = mContext.getIntent()
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
    }

}
