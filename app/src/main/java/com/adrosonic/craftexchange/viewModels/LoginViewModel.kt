package com.adrosonic.craftexchange.viewModels

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.loginResponse.LoginValidationResponse
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    interface LoginViewModelCallback {
        fun displayToast(message: String)
        fun showProgress()
        fun hideProgress()
        fun authenticationSuccessful()
//        fun exchangeauthenticationSuccessful()
    }

    private lateinit var api: CraftExchangeRepository
    var listener: LoginViewModelCallback? = null


    fun validateUsername(emailOrMobile : String){
        api.getLoginService()
            .validateUserName(emailOrMobile,
                Prefs.getLong(ConstantsDirectory.REF_ROLE_ID,0))
            .enqueue(object : Callback, retrofit2.Callback<LoginValidationResponse> {
                override fun onFailure(call: Call<LoginValidationResponse>, t: Throwable) {
                    t.printStackTrace()
                    failure(ERR_AUTH_CX_USER)
                }
                override fun onResponse(
                    call: Call<LoginValidationResponse>,
                    response: Response<LoginValidationResponse>
                ) {
                    if (response.body()?.valid == true) {
                        Prefs.putString(ConstantsDirectory.USER_EMAIL, emailOrMobile)
                        listener?.authenticationSuccessful()
                    } else {
                        runOnUiThread(Runnable {listener?.displayToast(response.body()?.errorMessage!!)})
//                        Toast.makeText(activity, "Enter Valid Email", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun failure(message: String) {
        runOnUiThread(Runnable { listener?.hideProgress() })
        runOnUiThread(Runnable { listener?.displayToast(message) })
        Log.e(TAG, message)
    }

    private fun runOnUiThread(runnable: Runnable) {
        Handler(Looper.getMainLooper()).post { runnable.run() }
    }

    companion object{
        private val TAG = LoginViewModel::class.java.simpleName
        private val ERR_AUTH_CX_USER ="Error Authenticating User"

    }

}