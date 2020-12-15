package com.adrosonic.craftexchange.ui.modules.order.revisedAdvPayment

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
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.OngoingEnquiries
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.databinding.FragmentAdvPay1Binding
import com.adrosonic.craftexchange.databinding.FragmentSelectRevisedAdvPayBinding
import com.adrosonic.craftexchange.enums.AvailableStatus
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.repository.data.response.orders.advPayment.RevisedStatusResponse
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.advPay.AdvPay1Fragment
import com.adrosonic.craftexchange.ui.modules.buyer.enquiry.advPay.AdvPay2Fragment
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.EnquiryViewModel
import com.adrosonic.craftexchange.viewModels.OrdersViewModel


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SelectRevisedAdvPayFragment : Fragment()
,OrdersViewModel.FetchReviseStatusInterface{
    private var param1: Long? = null
    private var param2: Long? = null

    var enqID : Long? = 0
    var piID : Long? = 0

    private var mBinding: FragmentSelectRevisedAdvPayBinding?= null

    val mOrderVM : OrdersViewModel by viewModels()

    private var orderDetails : Orders?= null
    private var url : String?=""

    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    var status : String ?= ""
    var calculatedAmount : Float?= 0F
    var percentSelected : Long?= 30L
    var totalAmount : Long?= 30L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getLong(ARG_PARAM1)
            param2 = it.getLong(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_revised_adv_pay, container, false)
        if(param1!=null){
            enqID = if(param1!=null)param1 else 0
        }
        if(param2!=null){
            piID = if(param2!=null)param2 else 0
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(Utility.checkIfInternetConnected(requireContext())){
            mBinding?.pbLoader?.visibility=View.VISIBLE
            mOrderVM?.reviseStatusListener=this
            enqID?.let {mOrderVM?.getRevisedAdvancedPaymentStatus(it?.toLong())  }
        }else Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
        orderDetails = enqID?.toLong()?.let { mOrderVM?.getSingleOnOrderData(it,0) }?.value
        if(orderDetails != null){
            setDetails()
        }

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.btnProceedAdvPay?.setOnClickListener {

            if (savedInstanceState == null) {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.order_payment_container,
                        UploadRevPayReciptFragment.newInstance(enqID.toString(),
                            calculatedAmount!!,percentSelected.toString(),piID.toString(),totalAmount.toString()))
                    ?.commit()
            }
        }

    }

    fun setDetails(){
        mBinding?.enquiryCode?.text = orderDetails?.enquiryCode ?: "N.A"
        setProductImage()
        setProductName()
        setProductAvailability()
        mBinding?.productAmount?.text = "₹ ${orderDetails?.totalAmount ?: 0}"
    }

    fun setProductImage(){
        val image = orderDetails?.productImages?.split((",").toRegex())
            ?.dropLastWhile { it.isEmpty() }?.toTypedArray()?.get(0)

        if (orderDetails?.productType == ConstantsDirectory.CUSTOM_PRODUCT) {
            url = Utility.getCustomProductImagesUrl(orderDetails?.productId, image)
        } else {
            url = Utility.getProductsImagesUrl(orderDetails?.productId, image)
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
        if (orderDetails?.productName != "") {
            mBinding?.productName?.text = orderDetails?.productName
        } else {
            //TODO : set text as prod cat / werft / warn / extraweft
            var weaveList = Utility?.getWeaveType()
            var catList = Utility?.getProductCategory()

            weaveList?.forEach {
                if (it.first == orderDetails?.weftYarnId) {
                    weft = it.second
                }
                if (it.first == orderDetails?.warpYarnId) {
                    warp = it.second
                }
                if (it.first == orderDetails?.extraWeftYarnId) {
                    extraweft = it.second
                }
            }
            catList?.forEach {
                if (it.first == orderDetails?.productCategoryId) {
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
        when (orderDetails?.productStatusId) {
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
    override fun onReviseStatusSuccess(data: RevisedStatusResponse) {
        mBinding?.pbLoader?.visibility=View.GONE
        piID=data?.piId
        calculatedAmount=data?.pendingAmount.toFloat()
        percentSelected=data?.percentage
        totalAmount=data?.totalAmount
        mBinding?.selectAmountText?.text="Advance amount paid as "+data?.percentage+"% of order amount: ₹ "+data?.totalAmount
        mBinding?.calculatedAmount?.text="₹ "+data?.pendingAmount
    }

    override fun onReviseStatusFailure() {
        mBinding?.pbLoader?.visibility=View.GONE
        Utility.displayMessage("Unable to fetch details",requireContext())
    }
    companion object {

        fun newInstance(param1: Long,param2: Long) =
            SelectRevisedAdvPayFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PARAM1, param1)
                    putLong(ARG_PARAM2, param2)
                }
            }
    }
}