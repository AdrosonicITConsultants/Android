package com.adrosonic.craftexchange.ui.modules.artisan.authentication.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentArtisanRegisterPasswordBinding
import com.adrosonic.craftexchange.ui.modules.buyer.authentication.register.BuyerRegisterDetailsFragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty

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
            if(
                mBinding?.textBoxPassword?.nonEmpty() == true &&
                mBinding?.textBoxRetypePwd?.nonEmpty() == true
            ){
                if(mBinding?.textBoxPassword?.text.toString() == mBinding?.textBoxRetypePwd?.text.toString()){

                    Prefs.putString(ConstantsDirectory.USER_PWD,mBinding?.textBoxRetypePwd?.text.toString())

                    if (savedInstanceState == null) {
                        activity?.supportFragmentManager?.beginTransaction()
                            ?.replace(R.id.register_container,
                                ArtisanRegisterDetailsFragment.newInstance(),"Register Buyer Details")
                            ?.addToBackStack(null)
                            ?.commit()
                    }
                }else{
                    Toast.makeText(activity,"Passwords are mismatched", Toast.LENGTH_SHORT).show()
                }
            }else{
                mBinding?.textBoxPassword?.nonEmpty{ mBinding?.textBoxPassword?.error = it }
                mBinding?.textBoxRetypePwd?.nonEmpty{ mBinding?.textBoxRetypePwd?.error = it }
            }
        }
    }

}
