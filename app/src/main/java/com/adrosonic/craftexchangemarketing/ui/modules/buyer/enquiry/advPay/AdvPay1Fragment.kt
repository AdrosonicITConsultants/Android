package com.adrosonic.craftexchangemarketing.ui.modules.buyer.enquiry.advPay

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchangemarketing.databinding.FragmentAdvPay1Binding
import com.adrosonic.craftexchangemarketing.databinding.FragmentArtisanHomeBinding
import com.adrosonic.craftexchangemarketing.enums.AvailableStatus
import com.adrosonic.craftexchangemarketing.enums.getId
import com.adrosonic.craftexchangemarketing.ui.modules.buyer.enquiry.CommonEnquiryFragment
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.adrosonic.craftexchangemarketing.utils.ImageSetter
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.adrosonic.craftexchangemarketing.viewModels.EnquiryViewModel
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_artisan_add_product_template.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AdvPay1Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    var enqID : String?= ""
    var piID : String?= ""

    private var mBinding: FragmentAdvPay1Binding?= null

    val mEnqVM : EnquiryViewModel by viewModels()

    private var enquiryDetails : OngoingEnquiries?= null
    private var url : String?=""

    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    var status : String ?= ""
    var calculatedAmount : Float?= 0F

    var percentSelected : Long?= 30L


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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_adv_pay1, container, false)
        if(param1!=null){
            enqID = if(param1!!.isNotEmpty())param1 else "0"
        }
        if(param2!=null){
            piID = if(param2!!.isNotEmpty())param2 else "0"
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enquiryDetails = enqID?.toLong()?.let { mEnqVM?.getSingleOnEnqData(it) }?.value

        if(enquiryDetails != null){
            setDetails()
        }

        mBinding?.per30Btn?.setOnClickListener {
            percentSelected = 30L
            Utility?.displayMessage("30 percent",requireContext())
            setAdvancePercent(percentSelected)
        }

        mBinding?.per50Btn?.setOnClickListener {
            percentSelected = 50L
            setAdvancePercent(percentSelected)
        }

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.btnProceedAdvPay?.setOnClickListener {
            if (savedInstanceState == null) {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.enquiry_payment_container,
                        AdvPay2Fragment.newInstance(enqID.toString(),
                            calculatedAmount!!,percentSelected.toString(),piID.toString()))
                    ?.commit()
            }
        }
    }

    fun setDetails(){
        mBinding?.enquiryCode?.text = enquiryDetails?.enquiryCode ?: "N.A"
        setProductImage()
        setProductName()
        setProductAvailability()
        mBinding?.productAmount?.text = "₹ ${enquiryDetails?.totalAmount ?: 0}"
        setAdvancePercent(percentSelected)
    }

    fun setProductImage(){
        val image = enquiryDetails?.productImages?.split((",").toRegex())
            ?.dropLastWhile { it.isEmpty() }?.toTypedArray()?.get(0)

        if (enquiryDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT) {
            url = Utility.getCustomProductImagesUrl(enquiryDetails?.productID, image)
        } else {
            url = Utility.getProductsImagesUrl(enquiryDetails?.productID, image)
        }
        mBinding?.productImage?.let {
            ImageSetter.setImage(
                requireActivity(),
                url!!,
                it,
                R.drawable.artisan_logo_placeholder,
                R.drawable.artisan_logo_placeholder,
                R.drawable.artisan_logo_placeholder
            )
        }
    }

    fun setProductName(){
        //Product name or Product cloth details
        if (enquiryDetails?.productName != "") {
            mBinding?.productName?.text = enquiryDetails?.productName
        } else {
            //TODO : set text as prod cat / werft / warn / extraweft
            var weaveList = Utility?.getWeaveType()
            var catList = Utility?.getProductCategory()

            weaveList?.forEach {
                if (it.first == enquiryDetails?.weftYarnID) {
                    weft = it.second
                }
                if (it.first == enquiryDetails?.warpYarnID) {
                    warp = it.second
                }
                if (it.first == enquiryDetails?.extraWeftYarnID) {
                    extraweft = it.second
                }
            }
            catList?.forEach {
                if (it.first == enquiryDetails?.productCategoryID) {
                    prodCategory = it.second
                }
            }
            var fp = SpannableString("${prodCategory} / ")
            var sp = "${warp} X ${weft} X ${extraweft}"
            fp.setSpan(context?.let { ContextCompat.getColor(it, R.color.black_text) }
                ?.let {
                    ForegroundColorSpan(
                        it
                    )
                }, 0, fp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            mBinding?.productName?.text = fp
            mBinding?.productName?.append(sp)
        }
    }

    fun setProductAvailability(){
        //ProductAvailability
        when (enquiryDetails?.productStatusID) {
            AvailableStatus.IN_STOCK.getId() -> {
                status = context?.getString(R.string.in_stock)
                mBinding?.productAvailability?.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_green
                    )
                }?.let { mBinding?.productAvailability?.setTextColor(it) }
            }
            AvailableStatus.MADE_TO_ORDER.getId() -> {
                status = context?.getString(R.string.made_to_order)
                mBinding?.productAvailability?.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_magenta
                    )
                }?.let { mBinding?.productAvailability?.setTextColor(it) }
            }
            else -> {
                status = "Custom Design by you"
                mBinding?.productAvailability?.text = status
                context?.let {
                    ContextCompat.getColor(
                        it, R.color.dark_magenta
                    )
                }?.let { mBinding?.productAvailability?.setTextColor(it) }
            }
        }
    }

    fun setAdvancePercent(percent: Long?) {
        when(percent){
            30L -> {
//                Utility?.displayMessage("30 percent",requireContext())

                calculatedAmount = enquiryDetails?.totalAmount?.toFloat()?.times(0.3F)
                mBinding?.calculatedAmount?.text = "₹ ${calculatedAmount ?: 0}"

                mBinding?.percent30Bg?.setBackgroundResource(R.drawable.bg_adv_amt_selected_silver)
                mBinding?.percent50Bg?.setBackgroundResource(R.drawable.bg_adv_amt_unselected)
            }

            50L -> {
//                Utility?.displayMessage("50 percent",requireContext())

                calculatedAmount = enquiryDetails?.totalAmount?.toFloat()?.times(0.5F)
                mBinding?.calculatedAmount?.text = "₹ ${calculatedAmount ?: 0}"

                mBinding?.percent30Bg?.setBackgroundResource(R.drawable.bg_adv_amt_unselected)
                mBinding?.percent50Bg?.setBackgroundResource(R.drawable.bg_adv_amt_selected_gold)
            }
        }
    }

    companion object {
        fun newInstance(param1: String,param2: String) =
            AdvPay1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}