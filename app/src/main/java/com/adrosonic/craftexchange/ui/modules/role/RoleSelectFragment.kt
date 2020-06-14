package com.adrosonic.craftexchange.ui.modules.role

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentRoleSelectBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.clusterResponse.ProductResponse
import com.adrosonic.craftexchange.ui.modules.authentication.login.LoginActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RoleSelectFragment : Fragment() {

    companion object {
        fun newInstance() = RoleSelectFragment()

        const val TAG = "Role Selection"
    }

    private var mBinding: FragmentRoleSelectBinding ?= null

    private var param1: String? = null
    private var param2: String? = null

//    var productArray = ArrayList<String>()
//    var countryId = ArrayList<Int>()
//        var nameArray = ArrayList<String>()


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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_role_select, container, false)
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.roleArtisan?.setOnClickListener{
            startActivity(Intent(activity, LoginActivity::class.java))
            Prefs.putString(ConstantsDirectory.PROFILE,"Artisan")
            Prefs.putLong(ConstantsDirectory.REF_ROLE_ID,1)
        }

        mBinding?.roleBuyer?.setOnClickListener{
            startActivity(Intent(activity, LoginActivity::class.java))
            Prefs.putString(ConstantsDirectory.PROFILE,"Buyer")
            Prefs.putLong(ConstantsDirectory.REF_ROLE_ID,2)
        }
    }

}
