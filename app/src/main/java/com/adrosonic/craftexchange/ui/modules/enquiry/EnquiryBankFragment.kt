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
import com.adrosonic.craftexchange.database.entities.realmEntities.PaymentAccount
import com.adrosonic.craftexchange.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.FragmentArtEnqDetailsBinding
import com.adrosonic.craftexchange.databinding.FragmentEnquiryBankBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.pixplicity.easyprefs.library.Prefs


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EnquiryBankFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var mBinding : FragmentEnquiryBankBinding?= null

    var enqDetails : OngoingEnquiries?= null

    val mEnqVM : EnquiryViewModel by viewModels()

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_enquiry_bank, container, false)
        return mBinding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enqDetails = mEnqVM.getSingleEnqMutableData(Prefs.getString(ConstantsDirectory.ENQUIRY_ID,"").toLong()).value

        var bank : EnquiryPaymentDetails?= EnquiryPredicates.getEnqPaymentDetails(enqDetails?.userId.toString(),1)

        mBinding?.accNo?.text = bank?.accNoUPIMobile ?: " - "
        mBinding?.bankName?.text = bank?.bankName ?: " - "
        mBinding?.benificiaryName?.text = bank?.name ?: " - "
        mBinding?.branch?.text = bank?.branch ?: " - "
        mBinding?.ifscCode?.text = bank?.ifsc ?: " - "

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            EnquiryBankFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}