package com.adrosonic.craftexchangemarketing.ui.modules.authentication.reset

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.FragmentResetSuccessBinding
import com.adrosonic.craftexchangemarketing.ui.modules.authentication.login.LoginActivity
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

class ResetSuccessFragment : Fragment() {

    companion object {
        fun newInstance() = ResetSuccessFragment()
    }

    private var mBinding: FragmentResetSuccessBinding?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_success, container, false)
//        mBinding?.profileTag?.text = Prefs.getString(ConstantsDirectory.PROFILE,"")
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var profile = Prefs.getString(ConstantsDirectory.PROFILE,null)

        mBinding?.buttonLoginNow?.setOnClickListener {
            when(profile){

                ConstantsDirectory.ADMIN -> {
                    startActivity(Intent(activity, LoginActivity::class.java).addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                    Prefs.putString(ConstantsDirectory.PROFILE,"Admin")
                }

            }
        }
    }
}
