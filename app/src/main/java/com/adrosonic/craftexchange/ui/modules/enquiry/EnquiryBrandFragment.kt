package com.adrosonic.craftexchange.ui.modules.enquiry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.databinding.FragmentEnquiryBrandBinding
import com.adrosonic.craftexchange.databinding.FragmentEnquiryGeneralBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.pixplicity.easyprefs.library.Prefs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EnquiryBrandFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var mBinding : FragmentEnquiryBrandBinding?= null

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_enquiry_brand, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enqDetails = mEnqVM.getSingleEnqMutableData(Prefs.getString(ConstantsDirectory.ENQUIRY_ID,"").toLong()).value

        mBinding?.gst?.text = enqDetails?.gst ?: " - "

        mBinding?.name?.text = enqDetails?.pocFirstName ?: " - "
        mBinding?.mobile?.text = enqDetails?.pocContact ?: " - "
        mBinding?.email?.text = enqDetails?.pocEmail ?: " - "


    }

    companion object {

        fun newInstance(param1: String) =
            EnquiryBrandFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}