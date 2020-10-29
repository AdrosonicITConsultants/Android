package com.adrosonic.craftexchange.ui.modules.enquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.EnquiryPaymentDetails
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchange.databinding.FragmentEnquiryBankBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.pixplicity.easyprefs.library.Prefs


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EnquiryBankFragment : Fragment() {

    var mBinding : FragmentEnquiryBankBinding?= null

//    var enqDetails : OngoingEnquiries?= null
    var enqID : Long?= 0
    var enqStatus : Long?= 0
    var bank : EnquiryPaymentDetails?= null
    var userID : Long ?= 0

    val mEnqVM : EnquiryViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_enquiry_bank, container, false)

        arguments?.let {
            enqID = it.getLong(ARG_PARAM1)
            enqStatus = it.getLong(ARG_PARAM2)
        }
        return mBinding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when(enqStatus){
            //completed
            1L -> {
                var enqDetails = mEnqVM.getSingleCompEnqData(Prefs.getString(ConstantsDirectory.ENQUIRY_ID,"").toLong()).value
                userID = enqDetails?.userId

            }
            //ongoing
            2L -> {
                var enqDetails = mEnqVM.getSingleOnEnqData(Prefs.getString(ConstantsDirectory.ENQUIRY_ID,"").toLong()).value
                userID = enqDetails?.userId
            }
        }

        bank =  EnquiryPredicates.getEnqPaymentDetails(userID.toString(),1)
        mBinding?.accNo?.text = bank?.accNoUPIMobile ?: " - "
        mBinding?.bankName?.text = bank?.bankName ?: " - "
        mBinding?.benificiaryName?.text = bank?.name ?: " - "
        mBinding?.branch?.text = bank?.branch ?: " - "
        mBinding?.ifscCode?.text = bank?.ifsc ?: " - "

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Long,param2: Long) =
            EnquiryBankFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PARAM1, param1)
                    putLong(ARG_PARAM2, param2)
                }
            }
    }
}