package com.adrosonic.craftexchange.ui.modules.buyer.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.adrosonic.craftexchange.R

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BrandFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_brand, container, false)
    }

    companion object {
        fun newInstance() = BrandFragment()
        const val TAG = "BrandFrag"
    }
}
