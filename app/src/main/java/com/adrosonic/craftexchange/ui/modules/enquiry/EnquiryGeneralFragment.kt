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
import com.adrosonic.craftexchange.databinding.FragmentEnquiryGeneralBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.pixplicity.easyprefs.library.Prefs

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EnquiryGeneralFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var mBinding : FragmentEnquiryGeneralBinding?= null

    //    var enqDetails : OngoingEnquiries?= null
    var enqID : Long?= 0
    var enqStatus : Long?= 0

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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_enquiry_general, container, false)
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
                mBinding?.regAddr?.text = enqDetails?.line1 ?: " - "
                if(enqDetails?.line2 != ""){
                    mBinding?.delAddr?.text = enqDetails?.line2
                }else{
                    mBinding?.delAddr?.text = enqDetails?.line1
                }

                mBinding?.country?.text = enqDetails?.country ?: " - "
                mBinding?.delCountry?.text = enqDetails?.country ?: " - "

                mBinding?.email?.text = enqDetails?.email ?: " - "
                mBinding?.mobile?.text = enqDetails?.mobile ?: " - "
            }
            //ongoing
            2L -> {
                var enqDetails = mEnqVM.getSingleOnEnqData(Prefs.getString(ConstantsDirectory.ENQUIRY_ID,"").toLong()).value
                mBinding?.regAddr?.text = enqDetails?.line1 ?: " - "
                if(enqDetails?.line2 != ""){
                    mBinding?.delAddr?.text = enqDetails?.line2
                }else{
                    mBinding?.delAddr?.text = enqDetails?.line1
                }

                mBinding?.country?.text = enqDetails?.country ?: " - "
                mBinding?.delCountry?.text = enqDetails?.country ?: " - "

                mBinding?.email?.text = enqDetails?.email ?: " - "
                mBinding?.mobile?.text = enqDetails?.mobile ?: " - "
            }
        }




    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Long,param2: Long) =
            EnquiryGeneralFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PARAM1, param1)
                    putLong(ARG_PARAM2, param2)
                }
            }
    }
}