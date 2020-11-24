package com.adrosonic.craftexchangemarketing.ui.modules.teamManagement

import android.app.Dialog
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.FragmentProfileDetailsBinding
import com.adrosonic.craftexchangemarketing.databinding.FragmentTeamListBinding
import com.adrosonic.craftexchangemarketing.repository.data.response.team.AdminProfileData
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.TeamViewModel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileDetailsFragment : Fragment(),
TeamViewModel.AdminDetailsInterface{
    private var param1: Int? = null
    private var param2: String? = null

    var adminId : Int ?= 0

    val mTeamVM : TeamViewModel by viewModels()

    private var mBinding: FragmentProfileDetailsBinding?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
            if(param1!=null){
                adminId = param1
            }
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_details, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTeamVM?.adminListener = this

        if(Utility.checkIfInternetConnected(requireContext())){
            viewLoading()
            adminId?.let { mTeamVM?.getAdminProfile(it) }
        }else{
            Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
        }

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.swipeProfile?.setOnRefreshListener {
            if(Utility.checkIfInternetConnected(requireContext())){
                viewLoading()
                adminId?.let { mTeamVM?.getAdminProfile(it) }
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
            }
        }
    }

    fun viewLoading(){
        mBinding?.swipeProfile?.isRefreshing = true
        mBinding?.profileDetailsLayout?.visibility = View.GONE
    }

    fun hideLoading(){
        mBinding?.swipeProfile?.isRefreshing = false
        mBinding?.profileDetailsLayout?.visibility = View.VISIBLE
    }

    fun setDetails(adminData : List<AdminProfileData>){
        var itr = adminData.iterator()
        if(itr!=null){
            while (itr.hasNext()){
                var adData = itr.next()

                mBinding?.name?.text = adData?.username
                mBinding?.role?.text = adData?.role
                mBinding?.email?.text = adData?.email
                mBinding?.mobileNo?.text = adData?.mobile

                var date = adData?.memberSince?.split("T")?.get(0)
                mBinding?.date?.text = date

                when(adData?.status){
                    1 -> {
                        val img = requireActivity()?.resources?.getDrawable(R.drawable.green_round_dot)
                        mBinding?.status?.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null)
                    }
                    else -> {
                        val img = requireActivity()?.resources?.getDrawable(R.drawable.red_round_dot)
                        mBinding?.status?.setCompoundDrawablesWithIntrinsicBounds(null,null,img,null)
                    }
                }
            }
        }
    }

    override fun onSuccessAdminDetails() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                hideLoading()
                var adminData = Utility.getAdminDetails()
                adminData?.let { setDetails(it) }
            })
        } catch (e: Exception) {
            Log.e("AdminProfile", "Exception onSuccess" + e.message)
        }
    }

    override fun onFailureAdminDetails() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
               hideLoading()
                Utility.displayMessage("Error fetching details",requireContext())
            })
        } catch (e: Exception) {
            Log.e("AdminProfile", "Exception onFailure" + e.message)
        }
    }

    companion object {
        fun newInstance(param1: Int, param2: String) =
            ProfileDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}