package com.adrosonic.craftexchange.ui.modules.artisan.profile.editProfile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.PaymentAccount
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.FragmentBankEditBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.editProfileModel.AccountType
import com.adrosonic.craftexchange.repository.data.request.editProfileModel.PaymentAccountDetails
import com.adrosonic.craftexchange.repository.data.response.artisan.editProfile.EditBankDetailsResponse
//import com.adrosonic.craftexchange.ui.modules.artisan.profile.BankDetailsFragment.Companion.bank
//import com.adrosonic.craftexchange.ui.modules.artisan.profile.BankDetailsFragment.Companion.gpay
//import com.adrosonic.craftexchange.ui.modules.artisan.profile.BankDetailsFragment.Companion.paytm
//import com.adrosonic.craftexchange.ui.modules.artisan.profile.BankDetailsFragment.Companion.phonepe
import com.adrosonic.craftexchange.ui.modules.artisan.profile.artisanProfileIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import okhttp3.ResponseBody
import retrofit2.Call
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BankEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentBankEditBinding ?= null
    private var paymentList = ArrayList<PaymentAccountDetails>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bank_edit, container, false)

        var bank : PaymentAccount?= UserPredicates.getPaymentDetails(Prefs.getString(ConstantsDirectory.USER_ID,""),1)
        var gpay : PaymentAccount?= UserPredicates.getPaymentDetails(Prefs.getString(ConstantsDirectory.USER_ID,""),2)
        var phonepe : PaymentAccount?=
            UserPredicates.getPaymentDetails(Prefs.getString(ConstantsDirectory.USER_ID,""),3)
        var paytm : PaymentAccount?= UserPredicates.getPaymentDetails(Prefs.getString(ConstantsDirectory.USER_ID,""),4)

        mBinding?.accNo?.setText(bank?.accNoUPIMobile ?:"")
        mBinding?.bankName?.setText(bank?.bankName ?:"")
        mBinding?.benificiaryName?.setText(bank?.name ?:"")
        mBinding?.branch?.setText(bank?.branch ?:"")
        mBinding?.ifscCode?.setText(bank?.ifsc ?:"")

        mBinding?.gpay?.setText(gpay?.accNoUPIMobile ?:"")
        mBinding?.paytm?.setText(paytm?.accNoUPIMobile ?:"")
        mBinding?.phonepe?.setText( phonepe?.accNoUPIMobile ?:"")

        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.paytm?.addTextChangedListener {
            if(mBinding?.paytm?.text?.isNotEmpty()!!) {
                if(mBinding?.paytm?.minLength(10) == false){
                    mBinding?.paytm?.minLength(10) { mBinding?.paytm?.error = activity?.getString(R.string.mobile_no_invalid_text) }
                    mBinding?.btnSave?.isClickable = false
                }else{
                    mBinding?.btnSave?.isClickable = true
                }
            }
        }

        mBinding?.btnSave?.setOnClickListener {
//            Toast.makeText(requireContext(),"Save Bank Details Feature to be implemented",Toast.LENGTH_LONG).show()

            paymentList.add(PaymentAccountDetails(mBinding?.accNo?.text.toString(), AccountType("bank",1),
                mBinding?.bankName?.text.toString(),mBinding?.branch?.text.toString(),0,mBinding?.ifscCode?.text.toString(),
                mBinding?.benificiaryName?.text.toString(), Prefs.getString(ConstantsDirectory.USER_ID,"").toLong()))

            paymentList.add(PaymentAccountDetails(mBinding?.gpay?.text.toString(), AccountType("gpay",2),
                "",mBinding?.branch?.text.toString(),0,"",
                "",Prefs.getString(ConstantsDirectory.USER_ID,"").toLong()))

            paymentList.add(PaymentAccountDetails(mBinding?.paytm?.text.toString(), AccountType("phonp",3),
               "",mBinding?.branch?.text.toString(),0,"",
                "",Prefs.getString(ConstantsDirectory.USER_ID,"").toLong()))

            paymentList.add(PaymentAccountDetails(mBinding?.phonepe?.text.toString(), AccountType("paytm",4),
                "",mBinding?.branch?.text.toString(),0,"",
               "",Prefs.getString(ConstantsDirectory.USER_ID,"").toLong()))

            var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
            CraftExchangeRepository
                .getUserService()
                .editArtisanBankDetails(token,paymentList)
                .enqueue(object: Callback, retrofit2.Callback<EditBankDetailsResponse> {
                    override fun onFailure(call: Call<EditBankDetailsResponse>, t: Throwable) {
//                        hideProgress()
                        t.printStackTrace()
                    }
                    override fun onResponse(
                        call: Call<EditBankDetailsResponse>,
                        response: retrofit2.Response<EditBankDetailsResponse>) {

                        Log.e(TAG,response.body().toString())

//                        successDialog()
                        if(response.body()?.valid == true){
//                        Toast.makeText(requireContext(),response.body()?.data, Toast.LENGTH_SHORT).show()
//                            successDialog()
                            Toast.makeText(requireContext(),R.string.profile_update_success,Toast.LENGTH_SHORT).show()
                            startActivity(context?.artisanProfileIntent()?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))

//                            AddressPredicates.editArtisanAddress(Prefs.getString(ConstantsDirectory.USER_ID,""),addressObj)
                        }else{

                            Toast.makeText(requireContext(),response.body()?.errorMessage, Toast.LENGTH_SHORT).show()
//
////                        var jsonObject: JSONObject?
////                        try
////                        {
////                            jsonObject = JSONObject(response.errorBody()?.charStream()!!.readText())
////                            val errorMessage = jsonObject.getString("message")
////                            Toast.makeText(requireContext(),errorMessage, Toast.LENGTH_SHORT).show()
////                        }
////                        catch (e: JSONException) {
////                            e.printStackTrace()
////                        }
                        }
                    }

                })

        }
    }

    fun successDialog(){
        val builder = AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
        builder.setMessage(R.string.profile_update_success)
            .setNeutralButton("Ok"){ dialog, id ->
                dialog.cancel()
                startActivity(context?.artisanProfileIntent()?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            }

        builder.create().show()
    }
    companion object {

        @JvmStatic
        fun newInstance() = BankEditFragment()
        const val TAG = "BankEditFragment"
    }
}
