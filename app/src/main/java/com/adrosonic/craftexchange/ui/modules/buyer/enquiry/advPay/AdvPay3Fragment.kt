package com.adrosonic.craftexchange.ui.modules.buyer.enquiry.advPay

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.databinding.FragmentAdvPay2Binding
import com.adrosonic.craftexchange.databinding.FragmentAdvPay3Binding
import com.adrosonic.craftexchange.enums.AvailableStatus
import com.adrosonic.craftexchange.enums.DocumentType
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.ui.modules.chat.chatLogDetailsIntent
import com.adrosonic.craftexchange.ui.modules.enquiry.enquiryDetails
import com.adrosonic.craftexchange.ui.modules.transaction.transactionIntent
import com.adrosonic.craftexchange.ui.modules.transaction.viewDocument
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.pixplicity.easyprefs.library.Prefs

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"


class AdvPay3Fragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private var param3: Boolean? = null

    var enqID : String?= ""

    private var mBinding: FragmentAdvPay3Binding?= null

    val mEnqVM : EnquiryViewModel by viewModels()

    private var enquiryDetails : OngoingEnquiries?= null
    private var url : String?=""

    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    var status : String ?= ""

    var amountPaid : String?=""
    var isRevised : Boolean=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            param3 = it.getBoolean(ARG_PARAM3)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_adv_pay3, container, false)
        if(param1!=null){
            enqID = if(param1!!.isNotEmpty())param1 else "0"
        }

        if(param2!=null){
            amountPaid = if(param2!!.isNotEmpty())param2 else "0"
        }
        if(param3!=null){
            isRevised = if(param3!=null)param3?:false else false
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        enquiryDetails = enqID?.toLong()?.let { mEnqVM?.getSingleOnEnqData(it) }?.value

        if(enquiryDetails != null){
            setDetails()
        }

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.btnViewTransac?.setOnClickListener {
//            requireActivity()?.startActivity(requireActivity()?.transactionIntent())
            if(isRevised){
                val intent = Intent(enqID?.let { it1 -> requireContext()?.viewDocument(it1.toLong(),DocumentType.REVADVANCEPAY.getId()) })
                startActivity(intent)
            }
            else {
                val intent = Intent(enqID?.let { it1 -> requireContext()?.viewDocument(it1.toLong(), DocumentType.ADVANCEPAY.getId()) })
                startActivity(intent)
            }
        }
        mBinding?.btnChat?.setOnClickListener {
            enqID?.let {  startActivity(Intent(requireContext()?.chatLogDetailsIntent(it.toLong())))}
        }
    }


    fun setDetails(){
        mBinding?.enquiryCode?.text = enquiryDetails?.enquiryCode ?: "N.A"
        mBinding?.amountPaid?.text = "₹ $amountPaid"

        setProductImage()
        setProductName()
        setProductAvailability()
        if(isRevised){
            mBinding?.psText?.text="The proforma invoice will be updated."
            mBinding?.enqToOrderText?.visibility=View.GONE
        }
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



    companion object {
        fun newInstance(param1: String, param2:String,param3:Boolean) =
            AdvPay3Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putBoolean(ARG_PARAM3, param3)
                }
            }
    }
}