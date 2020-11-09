package com.adrosonic.craftexchange.ui.modules.artisan.deliveryReceipt

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.Orders
import com.adrosonic.craftexchange.database.predicates.WishlistPredicates
import com.adrosonic.craftexchange.databinding.FragmentFinPay2Binding
import com.adrosonic.craftexchange.databinding.FragmentUploadDelRec1Binding
import com.adrosonic.craftexchange.enums.EnquiryStages
import com.adrosonic.craftexchange.enums.getId
import com.adrosonic.craftexchange.syncManager.SyncCoordinator
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.OrdersViewModel
import com.adrosonic.craftexchange.viewModels.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UploadDelRec1Fragment : Fragment(),
    TransactionViewModel.UploadPaymentInterface,
    OrdersViewModel.changeStatusInterface {

    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentUploadDelRec1Binding?= null

    var enqID : String?= ""
    var orderStatus : String?= ""
    private var url : String?=""

    var weft : String ?= ""
    var warp : String ?= ""
    var extraweft : String ?= ""
    var prodCategory : String ?= ""
    var status : String ?= ""
    var filename : String?= ""
    var absolutePath : String?= ""

    var loadingDialog : Dialog ?= null
    var plsWaitDialog : Dialog ?= null

    var disDate : String?=""
    var etaDate : String?=""

    private var PICK_IMAGE: Int = 1
    private val PERMISSION_REQUEST_CODE = 200

    val mOrdVM : OrdersViewModel by viewModels()
    val mTranVM : TransactionViewModel by viewModels()

    private var orderDetails : Orders?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload_del_rec1, container, false)
        if(param1!=null){
            enqID = if(param1!!.isNotEmpty())param1 else "0"
        }
        if(param2!=null){
            orderStatus = if(param2!!.isNotEmpty())param2 else "0"
        }
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mTranVM.uploadPaymentListener = this
        mOrdVM.changeStatusListener = this
        orderDetails = enqID?.toLong()?.let { mOrdVM?.loadSingleOrderDetails(it,0) }
        if(orderDetails != null){
            setDetails()
        }

        loadingDialog = Utility.multiLoadingDialog(requireContext(),"Uploading...")
        plsWaitDialog = Utility.loadingDialog(requireContext())

        mBinding?.btnBack?.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding?.uploadPhoto?.setOnClickListener {
            if(Utility.checkIfInternetConnected(requireActivity())){
                if(Utility.checkPermission(requireActivity())){
                    selectFromGallery()
                }else{
                    requestPermission()
                }
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireActivity())
            }
        }

        mBinding?.dispatchDateImg?.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    mBinding?.dispatchDate?.setText(
                        year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString(),
                        TextView.BufferType.EDITABLE
                    )
                }, mYear, mMonth, mDay)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()

//            view?.onTouchEvent(motionEvent) ?: true
        }

        mBinding?.etaDateImg?.setOnClickListener {
            val c: Calendar = Calendar.getInstance()
            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)
//            val mHour = c.get(Calendar.HOUR_OF_DAY)
//            val mMin = c.get(Calendar.MINUTE)
//            val mSec = c.get(Calendar.SECOND)
            val datePickerDialog = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    mBinding?.revisedEta?.setText(
                        year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString(),
                        TextView.BufferType.EDITABLE
                    )
                }, mYear, mMonth, mDay)

            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }

        mBinding?.btnUploadReceipt?.setOnClickListener {

            var dispatchDate = "${mBinding?.dispatchDate?.text.toString()} 00:00:00"
            var eta = "${mBinding?.revisedEta?.text.toString()} 00:00:00"
            var receiptFilePath = absolutePath.toString()

            if (receiptFilePath.isEmpty()) Utility.displayMessage(getString(R.string.upld_delivery_receipt), requireContext())
            else if (dispatchDate.isEmpty()) Utility.displayMessage(getString(R.string.select_dispatch_date), requireContext())
            else{
                if(Utility.checkIfInternetConnected(requireContext())){
                    loadingDialog?.show()
                    enqID?.toLong()?.let { it1 -> mTranVM.uploadDeliveryReceipt(it1,dispatchDate,eta,receiptFilePath) }
                }else{
                    Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
                }
            }
        }
    }

    fun setDetails(){
        mBinding?.orderCode?.text = orderDetails?.orderCode ?: "N.A"
        setProductImage()
        setProductName()
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

    private fun selectFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf<String>("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK && null != data)
            when (requestCode) {
                PICK_IMAGE -> {
                    val uri = data?.data
                    absolutePath = uri?.let { Utility.getRealPathFromFileURI(requireContext(), it) }

                    if(Utility.validFileSize(absolutePath!!)){
                        uri?.let {
                            mBinding?.uploadPhoto?.let { it1 ->
                                ImageSetter.setCircleImage(requireActivity(),
                                    it, it1
                                )
                            }
                        }

                        filename = absolutePath?.lastIndexOf("/")?.plus(1)?.let {
                            absolutePath?.substring(
                                it
                            )
                        }
                        mBinding?.imgName?.text = filename

                    }else{
                        Utility.messageDialog(requireActivity(), requireActivity().getString(R.string.file_size_exceeded))
                    }
                }
            }
    }

    override fun onRequestPermissionsResult(requestCode:Int, permissions:Array<String>, grantResults:IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty())
            {
                val storageReadAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (storageReadAccepted){
                    selectFromGallery()
                }
                else
                {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                    {
                        showMessageOKCancel("You need to allow access to both the permissions",
                            DialogInterface.OnClickListener { dialog, which ->
                                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
                                selectFromGallery()
                            })
                        return
                    }
                }
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

    private fun showMessageOKCancel(message:String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    fun showDispatchOrderDialog(){
        var dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_delivery_receipt)
        dialog.show()
        val btnMrkDispatch = dialog.findViewById(R.id.btn_mark_order_dispatched) as Button
        var eta = dialog.findViewById(R.id.eta) as TextView
        var dispatchDate = dialog.findViewById(R.id.dispatch_date) as TextView

        eta.text = mBinding?.revisedEta?.text
        dispatchDate.text = mBinding?.dispatchDate?.text

        btnMrkDispatch.setOnClickListener {
            dialog?.cancel()
            if(Utility.checkIfInternetConnected(requireActivity())){
                plsWaitDialog?.show()
                enqID?.toLong()?.let { it1 -> mOrdVM.setCompleteOrderStage(it1,EnquiryStages.ORDER_DISPATCHED.getId()) }
            }else{
                Utility.displayMessage(getString(R.string.no_internet_connection),requireContext())
            }
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                Log.e("UploadDeliveryChallan", "OnFailure")
                loadingDialog?.cancel()
                Utility.displayMessage(getString(R.string.unable_to_upload_challan),requireContext())
            })
        } catch (e: Exception) {
            Log.e("FinalPayDetails", "Exception OnSuccess " + e.message)
        }
    }

    override fun onSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                loadingDialog?.cancel()
                Utility.displayMessage(getString(R.string.delivery_challan_uploaded),requireContext())
                showDispatchOrderDialog()
//                activity?.onBackPressed()
            })
        } catch (e: Exception) {
            Log.e("UploadDeliveryChallan", "Exception OnSuccess " + e.message)
        }
    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            UploadDelRec1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onStatusChangeSuccess() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                plsWaitDialog?.cancel()
                activity?.onBackPressed()
            })
        } catch (e: Exception) {
            Log.e("OrderDetails", "Exception onStatusChangeSuccess " + e.message)
        }
    }

    override fun onStatusChangeFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                plsWaitDialog?.cancel()
                Utility.displayMessage(getString(R.string.faile_to_dispatch),requireContext())
            })
        } catch (e: Exception) {
            Log.e("OrderDetails", "Exception onStatusChangeFailure " + e.message)
        }
    }
}