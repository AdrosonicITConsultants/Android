package com.adrosonic.craftexchange.ui.modules.artisan.auth.register

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
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
import com.adrosonic.craftexchange.databinding.FragmentArtisanRegisterPasswordBinding
import com.adrosonic.craftexchange.ui.modules.pdfViewer.PdfViewerActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.*

class ArtisanRegisterPasswordFragment : Fragment() {

    companion object {
        fun newInstance() = ArtisanRegisterPasswordFragment()
        const val TAG = "ArtisanRegPass"
    }

    private var mBinding: FragmentArtisanRegisterPasswordBinding ?= null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_register_password, container, false)
        mBinding?.textBoxPassword?.setText(Prefs.getString(ConstantsDirectory.USER_PWD,""))
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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
                    Prefs.putString(ConstantsDirectory.USER_PWD,mBinding?.textBoxRetypePwd?.text.toString())

                    if (savedInstanceState == null) {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.register_container,
                                ArtisanRegisterDetailsFragment.newInstance(),"Register Buyer Details")
                            ?.addToBackStack(null)
                            ?.commit()
                    }
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
                    Utility.messageDialog(requireContext(), requireActivity().getString(R.string.pwd_validation_text)  )
                }
            }else{
                Toast.makeText(activity,"Passwords are mismatched",Toast.LENGTH_SHORT).show()
            }
        }
        mBinding?.buttonReach?.setOnClickListener {
            Utility.reachUsDialog(it.context)
        }
        mBinding?.textViewHelp?.setOnClickListener {
//            val intent = Intent(context, PdfViewerActivity::class.java)
//            intent.putExtra("ViewType", "HELP")
//            startActivity(intent)
            Utility.supportDialog(requireContext())
        }
        mBinding?.changeLanguage?.setOnClickListener {
            showLanguageSelectionDialog()
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
