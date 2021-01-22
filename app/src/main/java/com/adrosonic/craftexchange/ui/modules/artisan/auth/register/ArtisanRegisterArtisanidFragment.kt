package com.adrosonic.craftexchange.ui.modules.artisan.auth.register

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
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
import com.adrosonic.craftexchange.databinding.FragmentArtisanRegisterArtisanidBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.model.artisan.ArtisanidModel
import com.adrosonic.craftexchange.repository.data.registerResponse.RegisterResponse
import com.adrosonic.craftexchange.ui.modules.pdfViewer.PdfViewerActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


class ArtisanRegisterArtisanidFragment : Fragment() {

    companion object {
        fun newInstance() = ArtisanRegisterArtisanidFragment()
        const val TAG = "ArtisanRegArtId"
    }

    private var mBinding: FragmentArtisanRegisterArtisanidBinding ?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_register_artisanid, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.buttonNext?.setOnClickListener{
            if(mBinding?.textBoxArtisanId?.nonEmpty() == true) {
//                showProgress()
                CraftExchangeRepository
                    .getRegisterService()
                    .verifyArtisanDetails("application/json",
                        ArtisanidModel(
                            "",
                            mBinding?.textBoxArtisanId?.text.toString()
                        )
                    )
                    .enqueue(object : Callback, retrofit2.Callback<RegisterResponse> {
                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: Response<RegisterResponse>
                        ) {
                            if(response.body()?.valid == true){
                                Log.e(TAG, response.toString())
                                Prefs.putString(ConstantsDirectory.ARTISAN_ID,mBinding?.textBoxArtisanId?.text.toString())

                                if (savedInstanceState == null) {
                                    activity?.supportFragmentManager?.beginTransaction()
                                        ?.replace(R.id.register_container,
                                            ArtisanRegisterUsernameFragment.newInstance(),"Register Artisan Username")
                                        ?.addToBackStack(null)
                                        ?.commit()
                                }
                            } else{
                                if(response.body()!=null)Toast.makeText(requireActivity(),"${response.body()?.errorMessage}", Toast.LENGTH_SHORT).show()
                                else Toast.makeText(requireActivity(),"No message available", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            t.printStackTrace()
                            Toast.makeText(requireActivity(),"${t.printStackTrace()}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }else{ mBinding?.textBoxArtisanId?.nonEmpty{ mBinding?.textBoxArtisanId?.error = it } }
        }

        mBinding?.privacyPolicy?.setOnClickListener {
            val intent = Intent(context, PdfViewerActivity::class.java)
            intent.putExtra("ViewType", "PRIVACY_POLICY_PDF")
            startActivity(intent)
        }
        mBinding?.buttonReach?.setOnClickListener {
            Utility.reachUsDialog(it.context)
        }
        mBinding?.changeLanguage?.setOnClickListener { showLanguageSelectionDialog() }
        mBinding?.textViewHelp?.setOnClickListener {
//            val intent = Intent(context, PdfViewerActivity::class.java)
//            intent.putExtra("ViewType", "HELP")
//            startActivity(intent)
            Utility.supportDialog(requireContext())
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
