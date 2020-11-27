package com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database

//import com.adrosonic.craftexchangemarketing.databinding.FragmentCommonEnquiryBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.FragmentCommonUserBinding
import com.adrosonic.craftexchangemarketing.ui.modules.admin.user_database.adapter.AdminDatabaseAdapter
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.viewModels.DatabaseViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CommonUserFragment : Fragment()
{
    private var mUserConfig = UserConfig()
    private var param1: String? = null
    private var param2: String? = null
    val mViewModel:DatabaseViewModel by viewModels()
    var mBinding : FragmentCommonUserBinding?= null
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_common_user, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.let {
            mBinding?.viewPagerViewDatabase?.adapter = AdminDatabaseAdapter(it)
            mBinding?.viewUserTab?.setupWithViewPager(mBinding?.viewPagerViewDatabase)

        }
    }

    companion object {
        fun newInstance() = CommonUserFragment()
        const val TAG = "CommonUserFragment"
    }

}