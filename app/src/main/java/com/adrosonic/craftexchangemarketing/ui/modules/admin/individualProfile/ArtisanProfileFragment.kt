package com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ArtisanProfileFragmentLayoutBinding


private const val ARG_PARAM1 = "param1"

class ArtisanProfileFragment : Fragment(){

    private  var mBinding : ArtisanProfileFragmentLayoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        mBinding = DataBindingUtil.inflate<>(inflater)
        return inflater.inflate(R.layout.artisan_profile_fragment_layout, container, false)
    }
}