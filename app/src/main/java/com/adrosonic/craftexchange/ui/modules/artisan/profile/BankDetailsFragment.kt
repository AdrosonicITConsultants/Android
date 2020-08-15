package com.adrosonic.craftexchange.ui.modules.artisan.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.PaymentAccount
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.FragmentBankDetailsBinding
import com.adrosonic.craftexchange.ui.modules.artisan.profile.editProfile.artisanEditProfileIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.viewModels.ProfileViewModel
import com.pixplicity.easyprefs.library.Prefs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BankDetailsFragment : Fragment(),
ProfileViewModel.FetchUserDetailsInterface{
    private var param1: String? = null
    private var param2: String? = null


    private var mBinding: FragmentBankDetailsBinding ?= null

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bank_details, container, false)

        mBinding?.accNo?.text = bank?.accNoUPIMobile ?:" - "
        mBinding?.bankName?.text = bank?.bankName ?:" - "
        mBinding?.benificiaryName?.text = bank?.name ?:" - "
        mBinding?.branch?.text = bank?.branch ?:" - "
        mBinding?.ifscCode?.text = bank?.ifsc ?:" - "

        mBinding?.gpay?.text = gpay?.accNoUPIMobile ?:" - "
        mBinding?.paytm?.text = paytm?.accNoUPIMobile ?:" - "
        mBinding?.phonepe?.text = phonepe?.accNoUPIMobile ?:" - "

        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.btnEditBankDetails?.setOnClickListener {
            startActivity(context?.artisanEditProfileIntent()?.putExtra("Section","Bank"))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO : Observer in this class to be implmented
        mBinding?.bankdetailsSwipe?.isRefreshing = true
        mBinding?.bankdetailsSwipe?.isRefreshing = false

    }

    companion object {
        fun newInstance() = BankDetailsFragment()
        var bank : PaymentAccount?= UserPredicates.getPaymentDetails(Prefs.getString(ConstantsDirectory.USER_ID,""),1)
        var gpay : PaymentAccount?= UserPredicates.getPaymentDetails(Prefs.getString(ConstantsDirectory.USER_ID,""),2)
        var phonepe : PaymentAccount?=UserPredicates.getPaymentDetails(Prefs.getString(ConstantsDirectory.USER_ID,""),3)
        var paytm : PaymentAccount?= UserPredicates.getPaymentDetails(Prefs.getString(ConstantsDirectory.USER_ID,""),4)

    }

    override fun onSuccess() {
//        Utility?.displayMessage("Welcome!",applicationContext)
        Log.e("ArtProBrand","Success")
        mBinding?.bankdetailsSwipe?.isRefreshing = false
    }

    override fun onFailure() {
//        Utility?.displayMessage("Error in fetching user details!",applicationContext)
        Log.e("ArtProBrand","Failure")
        mBinding?.bankdetailsSwipe?.isRefreshing = false
    }
}
