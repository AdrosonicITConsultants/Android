package com.adrosonic.craftexchangemarketing.ui.modules.admin.individualProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.ArtisanBrandFragmentBinding
import com.adrosonic.craftexchangemarketing.databinding.ArtisanProfileFragmentLayoutBinding

class ArtisanBrandFragment: Fragment() {

    private  var mBinding : ArtisanBrandFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        mBinding = DataBindingUtil.inflate(inflater, R.layout.artisan_brand_fragment, container, false)
        return inflater.inflate(R.layout.artisan_brand_fragment, container, false)
    }
}