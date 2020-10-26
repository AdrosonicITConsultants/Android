package com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ArtisanAccountFragmnetBinding
import com.adrosonic.craftexchangemarketing.databinding.ArtisanProfileFragmentLayoutBinding

class ArtisanAccountFragment : Fragment(){
    private  var mBinding : ArtisanAccountFragmnetBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_login, container, false)
        return inflater.inflate(R.layout.artisan_account_fragmnet, container, false)
    }
}