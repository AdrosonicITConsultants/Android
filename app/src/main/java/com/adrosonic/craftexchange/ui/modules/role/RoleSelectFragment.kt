package com.adrosonic.craftexchange.ui.modules.role

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentRoleSelectBinding
import com.adrosonic.craftexchange.ui.modules.authentication.login.LoginActivity

class RoleSelectFragment : Fragment() {

    companion object {
        fun newInstance() = RoleSelectFragment()
    }

    private var mBinding: FragmentRoleSelectBinding ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_role_select, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        mBinding?.roleArtisan?.setOnClickListener{
//            Toast.makeText(activity,"Clicked ArRtisan",Toast.LENGTH_LONG).show()
//        }
        mBinding?.roleBuyer?.setOnClickListener{
            Toast.makeText(activity,"Buyer",Toast.LENGTH_LONG).show()
            startActivity(Intent(activity,LoginActivity::class.java))
        }
    }

}
