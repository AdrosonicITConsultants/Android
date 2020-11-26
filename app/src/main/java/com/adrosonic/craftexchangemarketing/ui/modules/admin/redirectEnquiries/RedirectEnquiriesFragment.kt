package com.adrosonic.craftexchangemarketing.ui.modules.admin.redirectEnquiries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.FragmentRedirectedEnquiriesBinding
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.adapter.RedirectedEnquiriesAdapter

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class RedirectEnquiriesFragment : Fragment()
{
    private var param1: String? = null
    private var param2: String? = null
//    val mViewModel:DatabaseViewModel by viewModels()
    var mBinding : FragmentRedirectedEnquiriesBinding?= null
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
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_redirected_enquiries, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.let {
            mBinding?.viewPagerViewEnquiries?.adapter = RedirectedEnquiriesAdapter(it)
            mBinding?.prodTab?.setupWithViewPager(mBinding?.viewPagerViewEnquiries)

        }
    }

    companion object {
        fun newInstance() =
            RedirectEnquiriesFragment()
        const val TAG = "RedirectEnquiries"
    }

}