package com.adrosonic.craftexchangemarketing.ui.modules.admin.productCatalog.addProduct

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.EnquiryProductDetails
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.RelatedProducts
import com.adrosonic.craftexchangemarketing.database.predicates.*
import com.adrosonic.craftexchangemarketing.databinding.ActivityArtisanAddProductTemplateBinding
import com.adrosonic.craftexchangemarketing.repository.data.request.artisan.productTemplate.*
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.uploadData.*
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.uploadData.ProductCare
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.uploadData.ProductType

import com.adrosonic.craftexchangemarketing.syncManager.SyncCoordinator
import com.adrosonic.craftexchangemarketing.ui.modules.admin.selectArtisan.selectArtisanActivityIntent
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.productTemplate.CareInstructionsSelectionAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.productTemplate.ProdImageListAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.productTemplate.WeaveSelectionAdapter
import com.adrosonic.craftexchangemarketing.ui.modules.artisan.productTemplate.yarnFrgamnets.YarnFrgamentAdapter
import com.adrosonic.craftexchangemarketing.utils.*
import com.adrosonic.craftexchangemarketing.viewModels.DownLoadProdImagesViewModel
import com.adrosonic.craftexchangemarketing.viewModels.ProductCatViewModal
import com.adrosonic.craftexchangemarketing.viewModels.ProductCatalogueViewModel
import com.google.gson.GsonBuilder
import com.wajahatkarim3.easyvalidation.core.view_ktx.contains
import kotlinx.android.synthetic.main.activity_artisan_add_product_template.*
import java.io.File


fun Context.addAdminProductIntent(): Intent {
    return Intent(this, AdminAddProductTemplateActivity::class.java).apply {
    }
}

fun Context.addAdminProductIntent(id: Long): Intent {
    val intent = Intent(this, AdminAddProductTemplateActivity::class.java)
    intent.putExtra("productId", id)
    return intent.apply { }
//    return Intent(this, ArtisanAddProductTemplateActivity::class.java).apply {
}

class AdminAddProductTemplateActivity : AppCompatActivity(),
    View.OnClickListener,
    ProdImageListAdapter.ProdUpdateListener,
    WeaveSelectionAdapter.selectionListener,
    CareInstructionsSelectionAdapter.selectionListener,
    DownLoadProdImagesViewModel.DownloadImagesCallback,
    ProductCatViewModal.UploadProdInterface,
    ProductCatViewModal.DeleteProdInterface
{
    val mViewModel: DownLoadProdImagesViewModel by viewModels()
    val mProdVm: ProductCatViewModal by viewModels()
    private var mBinding: ActivityArtisanAddProductTemplateBinding? = null
    private lateinit var prodImgListAdapter: ProdImageListAdapter
    private var pairList = ArrayList<Triple<Boolean,Long, String>>()
    private var deletedPaths =ArrayList<Pair<Long,String>>()
    private lateinit var careSelectionAdapter: CareInstructionsSelectionAdapter
    private lateinit var weaveSelectionAdapter: WeaveSelectionAdapter

    var careSelctionList = ArrayList<Triple<String, Boolean, Long>>()
    var weaveSelctionList = ArrayList<Triple<String, Boolean, Long>>()

    private val mUserConfig = UserConfig()
    var jsonProductData: String = ""
    var productUploadData: ProductUploadData? = null
    var arrProductCategory: List<ProductCategory>? = null
    var arrProductType: List<ProductType>? = null
    var arrRelatedProdType: List<ProductType>? = null
    var arrProductCare: List<ProductCare>? = null
    var arrWeaf: List<Weaf>? = null
    var arrYarn: List<Yarn>? = null
    var arrReedCount: List<ReedCount>? = null
    var arrDyes: List<Dye>? = null

    var arrProdCategoryStr = ArrayList<String>()
    var arrProdTypeStr = ArrayList<String>()
    var arrReedCountStr = ArrayList<String>()
    var arrProdWidthStr = ArrayList<String>()
    var arrProdLengthStr = ArrayList<String>()
    var arrSubProdWidthStr = ArrayList<String>()
    var arrSubProdLengthStr = ArrayList<String>()
    var arrListRelatedProduct = ArrayList<RelatedProduct>()
    var productAvalability = false

    var warpDyeId = 0L
    var warpYarnCount = ""
    var warpYarnId = 0L
    var weftDyeId = 0L
    var weftYarnCount = ""
    var weftYarnId = 0L
    var extraWeftDyeId = 0L
    var extraWeftYarnCount = ""
    var extraWeftYarnId = 0L

    var prodCatId: Long? = null
    var prodTypeId: Long? = null
    var weaveIdList = ArrayList<Long>()
    var careIdList = ArrayList<Long>()
    var relatedProduct = ArrayList<RelatedProduct>()
    var status = 1L
    var reedCountId = 1L
    private var dots = ArrayList<TextView>()
    var productId = 0L
    var productEntry: EnquiryProductDetails?=null
    var weaveIdStored:ArrayList<Long>?=null
    var careIdsStored:ArrayList<Long>?=null
    var relatedProdStored:RelatedProducts?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArtisanAddProductTemplateBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        mProdVm?.uploadProdListener=this
        jsonProductData = mUserConfig.productUploadJson.toString()
        val gson = GsonBuilder().create()
        productUploadData = gson.fromJson(jsonProductData, ProductUploadData::class.java)
        arrProductCategory = productUploadData?.data?.productCategories
        arrProductCare = productUploadData?.data?.productCare
        arrWeaf = productUploadData?.data?.weaves
        arrYarn = productUploadData?.data?.yarns
        arrReedCount = productUploadData?.data?.reedCounts
        arrDyes = productUploadData?.data?.dyes

        //todo set animation
        //todo set click listener
        //todo set data


        if (intent.extras != null) {
            productId = intent.getLongExtra("productId", 0)
            Log.e("Offline", "template activity prodId :" + productId)
            if (productId > 0) {
                img_delete.visibility = View.VISIBLE
                mBinding?.txtSelectBrand?.text="Update Product"
//                productEntry=ProductPredicates.getArtisanProductsByRemoteId(productId)
                productEntry=mProdVm.getEnqProductDetails(productId).value

            } else {
//                mBinding?.txtSelectBrand?.text="Save"
                img_delete.visibility = View.GONE
            }
        } else {
//            txt_save_upload.text="Save"
            img_delete.visibility = View.GONE
        }
        parent_step1.setOnClickListener(this)
        parent_step2.setOnClickListener(this)
        parent_step3.setOnClickListener(this)
        parent_step4.setOnClickListener(this)
        parent_step5.setOnClickListener(this)
        parent_step6.setOnClickListener(this)
        parent_step7.setOnClickListener(this)
        parent_step8.setOnClickListener(this)
        parent_step9.setOnClickListener(this)
        parent_step10.setOnClickListener(this)
        parent_step11.setOnClickListener(this)

        btn_back.setOnClickListener {
            showCancelDialog()
        }
        img_delete.setOnClickListener {
            showDeleteDialog(productId)
        }
        productEntry?.let { loadData() }
        /////////////////////////Save and Upload///////////////////////////
        mBinding?.txtSelectBrand?.setOnClickListener { saveUploadProduct() }
//        txt_reset.setOnClickListener { resetAll() }
        ///////////////////////////////////////////////////////////////////
        mBinding?.etProdName?.addTextChangedListener(generalTextWatcher)
        mBinding?.etProdCode?.addTextChangedListener(generalTextWatcher)
        mBinding?.etProdWidth?.addTextChangedListener(generalTextWatcher)
        mBinding?.etProdLength?.addTextChangedListener(generalTextWatcher)
        mBinding?.etGsm?.addTextChangedListener(generalTextWatcher)
        mBinding?.etProdWeight?.addTextChangedListener(generalTextWatcher)
        mBinding?.etDscrp?.addTextChangedListener(generalTextWatcher)

    }

    fun loadData() {
            ///////////////////////Add Photo////////////////////////
            add_photo_recycler.layoutManager =LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            prodImgListAdapter = ProdImageListAdapter(this, true,pairList)
            prodImgListAdapter.listener = this
            add_photo_recycler.adapter = prodImgListAdapter
            txt_add_product_image.setOnClickListener {
                if(Utility.checkPermission(this)){
                    if (pairList.size < 3) {
                        val intent = Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                        intent.type = "image/*"
                        val mimeTypes = arrayOf<String>("image/*")
                        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                        startActivityForResult(intent, ConstantsDirectory.PICK_IMAGE)
                    } else {
                        Utility.displayMessage(getString(R.string.product_add_limit), applicationContext)
                    }
                }else{
                    Utility.requestPermission(this)
                }
            }
            ///////////////////////weave types////////////////////////
            weaveSelctionList.clear()
            arrWeaf?.forEach { weaveSelctionList.add(Triple(it.weaveDesc ?: "", false, it.id)) }
            weave_recycler_list.layoutManager = LinearLayoutManager(this)
            weaveSelectionAdapter = WeaveSelectionAdapter(this, weaveSelctionList)
            weaveSelectionAdapter.listener = this
            weave_recycler_list.adapter = weaveSelectionAdapter

            ///////////general details//////////////
            arrProdCategoryStr.clear()
            arrProdCategoryStr.add("Select product category")
            arrProductCategory?.forEach { arrProdCategoryStr.add(it.productDesc) }
            val spProdCataAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, arrProdCategoryStr)
            spProdCataAdapter.setDropDownViewResource(R.layout.spinner_item)
            sp_prod_category.setAdapter(spProdCataAdapter)
            sp_prod_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    arrProdTypeStr?.clear()
                    arrProdTypeStr.add("Select product type")
                    val prodCategory = arrProdCategoryStr.get(position)
                    for (category in arrProductCategory!!) {
                        if (category.productDesc.equals(prodCategory, true)) {
                            arrProductType = category.productTypes
                            category.productTypes.forEach { arrProdTypeStr?.add(it.productDesc) }
                        }
                    }
                    val spProdTypeAdapter = ArrayAdapter<String>( applicationContext,  R.layout.spinner_item,  arrProdTypeStr)
                    spProdTypeAdapter.setDropDownViewResource(R.layout.spinner_item)
                    sp_prod_type.setAdapter(spProdTypeAdapter)
                    setStatusResource()
                    if(productId>0){
                        var type=""
                        arrProductType?.forEach {
                            if(it.id.equals(productEntry?.productTypeId)){
                                type=it.productDesc
                            }
                        }
                        mBinding?.spProdType?.setSelection(arrProdTypeStr.indexOf(type))
                    }
                }

            }
            sp_prod_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val prodType = arrProdTypeStr.get(position)
                    arrProductType?.forEach {
                        if (it.productDesc.equals(prodType)) {
//                            prodTypeId=it.id
//                      prodCatId=it.productCategoryID
                            arrRelatedProdType = it.relatedProductType
                        }
                    }
                    if(position>0){
                        setVisiblitiesAndTextsOnType(arrProdTypeStr.get(position), arrRelatedProdType)
                    }
                    setStatusResource()
                }

            }

            ////////////////////warp_weft_yarns//////////////////////////
            supportFragmentManager.let{
                yarn_pager?.adapter = YarnFrgamentAdapter(it,productId,true)
            }
            yarn_pager?.setOffscreenPageLimit(3)
//            val viewPagerAdapter = YarnViewpager(this,productId, true)
//            yarn_pager.setAdapter(viewPagerAdapter)
//            viewPagerAdapter.listener = this
            dots.clear()
            slider_dots.removeAllViews()
            do {
                val d = TextView(this)
                d.text = "."
                d.textSize = 48F
                d.setTextColor(Color.parseColor("#A9A9A9"))
                var params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(8, 0, 8, 0);
                slider_dots.addView(d, params)
                dots.add(d)
            } while (dots.size < 3)
            setDotsColor(0)
            yarn_pager.addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    setStatusResource()
                }
                override fun onPageSelected(position: Int) {
                    setDotsColor(position)
                }
            })
            /////////////////reed count/////////////////////
            arrReedCountStr.add("Select reed count")
            arrReedCount?.forEach { arrReedCountStr.add(it.count) }
            val spReedCountAdapter =ArrayAdapter<String>(this, R.layout.spinner_item, arrReedCountStr)
            spReedCountAdapter.setDropDownViewResource(R.layout.spinner_item)
            sp_reed_count.setAdapter(spReedCountAdapter)
            sp_reed_count.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setStatusResource()
            }}
            //////////////////////////////care//////////////////
            arrProductCare?.forEach {
                careSelctionList.add(Triple(it.productCareDesc ?: "",false, it.id ))
            }
            wash_care_recycler_list.layoutManager = LinearLayoutManager(this)
            careSelectionAdapter = CareInstructionsSelectionAdapter(this, careSelctionList)
            careSelectionAdapter.listener = this
            wash_care_recycler_list.adapter = careSelectionAdapter

            ////////////////////////availability////////////////////
            setproductAvailability(productAvalability)
            img_made_to_order.setOnClickListener {
                if (productAvalability) {
                    productAvalability = false
                } else productAvalability = true
                setproductAvailability(productAvalability)
            }
            img_in_stock.setOnClickListener {
                if (productAvalability) {
                    productAvalability = false
                } else productAvalability = true
                setproductAvailability(productAvalability)
            }

        if(productId>0){
            var imageList=ProductImagePredicates.getImagesList(productId)
            Log.e("Offline", "activity imageList :" +imageList.size)
//            imageList.forEach {  pairList.add(Triple(true,productId,it))}
            if(imageList.size>0){
                mViewModel.listener=this
                mBinding?.pbLoader?.visibility=View.VISIBLE
                var imgList=ArrayList<String>()
                imageList?.forEach { if(!it.contains("cache/BrowsedImages/"))imgList.add(it) }
                mViewModel.downLoadArtisanImages(productId,imgList)
            }
            et_prod_name.setText(productEntry?.productTag?:"", TextView.BufferType.EDITABLE)
            et_prod_code.setText(productEntry?.productCode?:"", TextView.BufferType.EDITABLE)
            sp_prod_category.setSelection(arrProdCategoryStr.indexOf(productEntry?.productCategoryName?:""))
            arrProductCategory?.forEach {
                if(it.productDesc.equals(productEntry?.productCategoryName)){
                    arrProdTypeStr.clear()
                    arrProdTypeStr.add("Select product type")
                    it.productTypes?.forEach { arrProdTypeStr.add(it.productDesc) }
                }
            }
            prodTypeId=productEntry?.productTypeId
            Log.e("Offline", "activity arrProdTypeStr :" +arrProdTypeStr?.size)
            Log.e("Offline", "activity productTypeDesc :" +productEntry?.productTypeDesc?:"")
            Log.e("Offline", "activity idex :" +arrProdTypeStr.indexOf(productEntry?.productTypeDesc?:""))
            sp_prod_type.setSelection(arrProdTypeStr.indexOf(productEntry?.productTypeDesc?:""))

//            for (category in arrProductCategory!!)
                arrProductCategory?.forEach{
                if (it.productDesc.equals(productEntry?.productCategoryName?:"", true)) {
                    arrProductType = it.productTypes
                    it.productTypes.forEach { arrProdTypeStr?.add(it.productDesc) }
                }
            }
            arrProductType?.forEach {
                Log.e("Offline", "activity forEach :" +it.productDesc)
                if (it.productDesc.equals(productEntry?.productTypeDesc?:"")) {
                    arrRelatedProdType = it.relatedProductType
                }
            }
            Log.e("Offline", "activity arrRelatedProdType :" +arrRelatedProdType?.size)
            relatedProdStored=RelateProductPredicates.getRelatedProductOfProduct(productId)
            setVisiblitiesAndTextsOnType(productEntry?.productTypeDesc?:"", arrRelatedProdType)

            weaveIdStored=WeaveTypesPredicates.getWeaveList(productId)
            Log.e("Offline", "activity weaveIdStored :" + weaveIdStored?.joinToString())
            weaveSelctionList?.forEach {
                if(weaveIdStored!!.contains(it.third)) {
                   val pos= weaveSelctionList.indexOf(it)
                    weaveSelctionList.set(pos, Triple(it.first,true,it.third))
                    weaveSelectionAdapter.notifyItemRangeChanged(pos, weaveSelctionList.size)
                }
            }
            arrReedCount?.forEach {
                if(it.id.equals(productEntry?.reedCountId?:""))sp_reed_count.setSelection(arrReedCountStr.indexOf(it.count))
            }

            careIdsStored=ProductCaresPredicates.getProductCareList(productId)
            Log.e("Offline", "activity careIdsStored :" + careIdsStored?.joinToString())
            careSelctionList?.forEach {
                if(careIdsStored!!.contains(it.third)) {
                    val pos = careSelctionList.indexOf(it)
                    careSelctionList.set(pos, Triple(it.first, true, it.third))
                    careSelectionAdapter.notifyItemRangeChanged(pos, careSelctionList.size)
                }
            }
            Log.e("Offline", "activity productAvalability b4 :" + productEntry?.productStatusId!!)
            status=productEntry?.productStatusId?:1
            when(status){
                1L->{
                    Log.e("Offline", "activity 11111111111 :" + productAvalability)
                    setproductAvailability(true)

                }
                2L->{
                    Log.e("Offline", "activity 22222222222 :" + productAvalability)
                    setproductAvailability(false)
                }
            }

            et_gsm.setText(productEntry?.gsm?:"", TextView.BufferType.EDITABLE)
            et_prod_weight.setText(productEntry?.weight?:"", TextView.BufferType.EDITABLE)
            et_dscrp.setText(productEntry?.product_spe?:"", TextView.BufferType.EDITABLE)

            mUserConfig.warpYarnId=productEntry?.warpYarnId
            mUserConfig.warpYarnCount=productEntry?.warpYarnCount
            mUserConfig.warpDyeId=productEntry?.warpDyeId

            mUserConfig.weftYarnId=productEntry?.weftYarnId
            mUserConfig.weftYarnCount=productEntry?.weftYarnCount
            mUserConfig.weftDyeId=productEntry?.weftDyeId

            mUserConfig.extraWeftYarnId=productEntry?.extraWeftYarnId
            mUserConfig.extraWeftYarnCount=productEntry?.extraWeftYarnCount
            mUserConfig.extraWeftDyeId=productEntry?.extraWeftDyeId
            getYarnData()
            setStatusResource()
        }

    }

    override fun onUpdate(pairList: ArrayList<Triple<Boolean,Long, String>>, deletedIds: ArrayList<Pair<Long,String>>) {
        this.pairList = pairList
        this.deletedPaths = deletedIds
        setStatusResource()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showCancelDialog()
            return true
        } else return super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConstantsDirectory.PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK && null != data)
            when (requestCode) {
                ConstantsDirectory.PICK_IMAGE -> {
                    val uri = data?.data
                    if (uri != null) {

                        var absolutePath = Utility.getRealPathFromFileURI(applicationContext, uri!!)
                        pairList.add(Triple(false,0,absolutePath))
                        prodImgListAdapter.notifyDataSetChanged()
                        setStatusResource()
                    }
                }
            }
        if(requestCode == ConstantsDirectory.EDIT_IMAGE)
        {
            val position=data?.getIntExtra(ConstantsDirectory.EDIT_IMAGE_POSITION,0)?:0
            prodImgListAdapter.notifyItemRangeChanged(position,pairList.size)
        }
    }

    override fun onWeaveItemSelected(pairList: ArrayList<Triple<String, Boolean, Long>>) {
        this.weaveSelctionList = pairList
        setStatusResource()
    }

    override fun onCareItemSelected(pairList: ArrayList<Triple<String, Boolean, Long>>) {
        this.careSelctionList = pairList
        setStatusResource()
    }

    override fun onClick(p0: View?) {
        val slideDown = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)
        val slideUp = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)
        when (p0!!.id) {
            R.id.parent_step1 -> {
                if (child_step1.visibility == View.GONE) {
                    child_step1.visibility = View.VISIBLE
                    child_step1.animation = slideDown
                } else {
                    child_step1.visibility = View.GONE
                    child_step1.animation = slideUp
                }
            }
            R.id.parent_step2 -> {
                if (child_step2.visibility == View.GONE) {
                    child_step2.visibility = View.VISIBLE
                    child_step2.animation = slideDown
                } else {
                    child_step2.visibility = View.GONE
                    child_step2.animation = slideUp
                }
            }
            R.id.parent_step3 -> {
                if (child_step3.visibility == View.GONE) {
                    child_step3.visibility = View.VISIBLE
                    child_step3.animation = slideDown
                } else {
                    child_step3.visibility = View.GONE
                    child_step3.animation = slideUp
                }
            }
            R.id.parent_step4 -> {
                if (child_step4.visibility == View.GONE) {
                    child_step4.visibility = View.VISIBLE
                    child_step4.animation = slideDown
                } else {
                    child_step4.visibility = View.GONE
                    child_step4.animation = slideUp
                }
            }
            R.id.parent_step5 -> {
                if (child_step5.visibility == View.GONE) {
                    child_step5.visibility = View.VISIBLE
                    child_step5.animation = slideDown
                } else {
                    child_step5.visibility = View.GONE
                    child_step5.animation = slideUp
                }
            }
            R.id.parent_step6 -> {
                if (child_step6.visibility == View.GONE) {
                    child_step6.visibility = View.VISIBLE
                    child_step6.animation = slideDown
                } else {
                    child_step6.visibility = View.GONE
                    child_step6.animation = slideUp
                }
            }
            R.id.parent_step7 -> {
                if (child_step7.visibility == View.GONE) {
                    child_step7.visibility = View.VISIBLE
                    child_step7.animation = slideDown
                } else {
                    child_step7.visibility = View.GONE
                    child_step7.animation = slideUp
                }
            }
            R.id.parent_step8 -> {
                if (child_step8.visibility == View.GONE) {
                    child_step8.visibility = View.VISIBLE
                    child_step8.animation = slideDown
                } else {
                    child_step8.visibility = View.GONE
                    child_step8.animation = slideUp
                }
            }
            R.id.parent_step9 -> {
                if (child_step9.visibility == View.GONE) {
                    child_step9.visibility = View.VISIBLE
                    child_step9.animation = slideDown
                } else {
                    child_step9.visibility = View.GONE
                    child_step9.animation = slideUp
                }
            }
            R.id.parent_step10 -> {
                if (child_step10.visibility == View.GONE) {
                    child_step10.visibility = View.VISIBLE
                    child_step10.animation = slideDown
                } else {
                    child_step10.visibility = View.GONE
                    child_step10.animation = slideUp
                }
            }
            R.id.parent_step11 -> {
                if (child_step11.visibility == View.GONE) {
                    child_step11.visibility = View.VISIBLE
                    child_step11.animation = slideDown
                } else {
                    child_step11.visibility = View.GONE
                    child_step11.animation = slideUp
                }
            }
        }
    }

    fun showDeleteDialog(productId: Long) {
        var dialog = Dialog(this)
        dialog?.setContentView(R.layout.dialog_removefrom_wishlist)
        dialog?.show()
        val tvCancel = dialog?.findViewById(R.id.txt_cancel) as TextView
        val tvDelete = dialog?.findViewById(R.id.txt_back) as TextView
        tvCancel.setOnClickListener {
            dialog.cancel()
        }
        tvDelete.setOnClickListener {
            ProductPredicates.updateProductForDeletion(productId)
            if (Utility.checkIfInternetConnected(applicationContext)) {
              pbLoader?.visibility=View.VISIBLE
              mProdVm?.deletedProdListener=this
              mProdVm.deleteProduct(productId)
            }
            else Utility.displayMessage(getString(R.string.no_internet_connection),this)
            dialog.cancel()
        }
    }

    fun showCancelDialog() {
        var dialog = Dialog(this)
        dialog?.setContentView(R.layout.dialog_addprod_back)
        dialog?.show()
        val tvCancel = dialog?.findViewById(R.id.txt_cancel) as TextView
        val tvBack = dialog?.findViewById(R.id.txt_back) as TextView
        tvCancel.setOnClickListener {
            dialog.cancel()
        }
        tvBack.setOnClickListener {
            finish()
        }
    }

    fun setVisiblitiesAndTextsOnType(type: String, arrRelatedProdType: List<ProductType>?) {
        arrProdLengthStr?.clear()
        arrProdWidthStr?.clear()
        arrSubProdLengthStr?.clear()
        arrSubProdWidthStr?.clear()
        txt_prod_type.text = type
        Log.e("SetData", "type :" + type)
        for (t in arrProductType!!) {
            Log.e("SetData", "type :" + t.productDesc)
            if (t.productDesc.equals(type, true)) {
                Log.e("SetData", "type :" + t.productDesc)
                Log.e("SetData", "productLengths :" + t.productLengths.size)
                Log.e("SetData", "productWidths :" + t.productWidths.size)
                t.productLengths.forEach { arrProdLengthStr?.add(it.length) }
                t.productWidths.forEach { arrProdWidthStr?.add(it.width) }
            }
        }

        if (arrProdWidthStr.size <= 0) {
            sp_prod_width.visibility = View.GONE
            et_prod_width.visibility = View.VISIBLE
            if(productId>0)et_prod_width.setText(productEntry?.productWidth?:"", TextView.BufferType.EDITABLE)
        } else {
            sp_prod_width.visibility = View.VISIBLE
            et_prod_width.visibility = View.GONE
            val spwidthAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, arrProdWidthStr)
            spwidthAdapter.setDropDownViewResource(R.layout.spinner_item)
            sp_prod_width.setAdapter(spwidthAdapter)
            if(productId>0)sp_prod_width.setSelection(arrProdWidthStr.indexOf(productEntry?.productWidth?:""))
        }
        if (arrProdLengthStr.size <= 0) {
            sp_prod_length.visibility = View.GONE
            et_prod_length.visibility = View.VISIBLE
            if(productId>0)et_prod_length.setText(productEntry?.productLength?:"", TextView.BufferType.EDITABLE)
        } else {
            sp_prod_length.visibility = View.VISIBLE
            et_prod_length.visibility = View.GONE
            val spLenghtAdapter = ArrayAdapter<String>(this, R.layout.spinner_item, arrProdLengthStr)
            spLenghtAdapter.setDropDownViewResource(R.layout.spinner_item)
            sp_prod_length.setAdapter(spLenghtAdapter)
            if(productId>0)sp_prod_length.setSelection(arrProdLengthStr.indexOf(productEntry?.productLength?:""))
        }

        if (type.equals("Fabric")) {
            parent_step10.visibility = View.VISIBLE
            child_step10.visibility = View.VISIBLE
            txt_step_11.text = "Step 11 : Enter description"
        } else {
            parent_step10.visibility = View.GONE
            child_step10.visibility = View.GONE
            txt_step_11.text = "Step 10 : Enter description"
        }

        if(arrRelatedProdType!=null) {
            if (arrRelatedProdType!!.size > 0) {
                //todo inflater to be called post API itegratipn
                txt_related_prod_type.visibility = View.VISIBLE
                ll_sub_prod.visibility = View.VISIBLE
                txt_related_prod_type.text = arrRelatedProdType.get(0).productDesc

                arrRelatedProdType.get(0).productLengths.forEach { arrSubProdLengthStr.add(it.length) }
                val splengthAdapter = ArrayAdapter<String>(this,R.layout.spinner_item, arrSubProdLengthStr)
                splengthAdapter.setDropDownViewResource(R.layout.spinner_item)
                sp_sub_prod_length.setAdapter(splengthAdapter)

                arrRelatedProdType.get(0).productWidths.forEach { arrSubProdWidthStr.add(it.width) }
                val spwidthAdapter =ArrayAdapter<String>(this,R.layout.spinner_item, arrSubProdWidthStr)
                spwidthAdapter.setDropDownViewResource(R.layout.spinner_item)
                sp_sub_prod_width.setAdapter(spwidthAdapter)
                if(productId>0){
                    sp_sub_prod_length.setSelection(arrSubProdLengthStr.indexOf(relatedProdStored?.productLength))
                    sp_sub_prod_width.setSelection(arrSubProdWidthStr.indexOf(relatedProdStored?.productWidth))
                }

            } else {
                txt_related_prod_type.visibility = View.GONE
                ll_sub_prod.visibility = View.GONE
            }
        }else {
            txt_related_prod_type.visibility = View.GONE
            ll_sub_prod.visibility = View.GONE
        }
        setStatusResource()
    }

    fun setproductAvailability(productAvalability: Boolean) {
        if (productAvalability) {
            status = 1
            img_made_to_order.setBackgroundResource(R.drawable.bg_availability_unselected)
            img_in_stock.setBackgroundResource(R.drawable.bg_availability_selected)
            txt_made_to_order.setTextColor(this.resources.getColor(R.color.clickable_text_color))
            txt_available.setTextColor(this.resources.getColor(R.color.lighter_gray))
        } else {
            status = 2
            img_made_to_order.setBackgroundResource(R.drawable.bg_availability_selected)
            img_in_stock.setBackgroundResource(R.drawable.bg_availability_unselected)
            txt_made_to_order.setTextColor(this.resources.getColor(R.color.lighter_gray))
            txt_available.setTextColor(this.resources.getColor(R.color.clickable_text_color))
        }
        setStatusResource()
    }

    fun setDotsColor(position: Int) {
        dots.forEach { it.setTextColor(ContextCompat.getColor(this, R.color.darker_gray)) }
        dots.get(position).setTextColor(Color.parseColor("#009A2F"))
    }

    fun saveUploadProduct() {
        try {
            getYarnData()
            weaveIdList.clear()
            careIdList.clear()
            weaveSelctionList.forEach { if (it.second) weaveIdList?.add(it.third) }
            careSelctionList.forEach { if (it.second) careIdList?.add(it.third) }
            var width =
                if (arrProdWidthStr.size <= 0) et_prod_width.text.toString() else sp_prod_width?.selectedItem.toString()
            var length =
                if (arrProdLengthStr.size <= 0) et_prod_length.text.toString() else sp_prod_length?.selectedItem.toString()

            arrReedCount?.forEach {
                if (it.count.equals(sp_reed_count?.selectedItem.toString())) {
                    reedCountId = it.id
                }
            }

            arrProductCategory?.forEach {
                if (it.productDesc.equals(sp_prod_category?.selectedItem.toString())) {
                    prodCatId = it.id
                    it.productTypes.forEach {
                        if (it.productDesc.equals(sp_prod_type?.selectedItem.toString())) {
                            prodTypeId = it.id
                        }
                    }
                }
            }

            Log.e(
                "saveUploadProduct",
                "relatedProduct :${sp_prod_category.selectedItem.toString()} "
            )

            if (pairList.isEmpty()) Utility.displayMessage(
                "Please add atleast 1 product image",
                applicationContext
            )
            else if (et_prod_name.text.isBlank()) Utility.displayMessage(
                "Please enter product name at step 2",
                applicationContext
            )
            else if (et_prod_code.text.isBlank()) Utility.displayMessage(
                "Please enter product code at step 2",
                applicationContext
            )
            else if (sp_prod_category.selectedItemPosition==0) Utility.displayMessage("Please select product category at step 2", applicationContext)
            else if (sp_prod_type.selectedItemPosition==0) Utility.displayMessage("Please select product type at step 2", applicationContext)
            else if (weaveIdList.isEmpty()) Utility.displayMessage(
                "Please select weave type at step 3",
                applicationContext
            )
            else if (warpDyeId <= 0) Utility.displayMessage(
                "Please select warp dye Id at step 4",
                applicationContext
            )
            else if (warpYarnCount.isBlank()) Utility.displayMessage(
                "Please select warp yarn count at step 4",
                applicationContext
            )
            else if (warpYarnId <= 0) Utility.displayMessage(
                "Please select warp yarn Id at step 4",
                applicationContext
            )
            else if (weftDyeId <= 0) Utility.displayMessage(
                "Please select weft dye Id at step 4",
                applicationContext
            )
            else if (weftYarnCount.isBlank()) Utility.displayMessage(
                "Please select weft yarn count at step 4",
                applicationContext
            )
            else if (weftYarnId <= 0) Utility.displayMessage(
                "Please select weft yarn Id at step 4",
                applicationContext
            )
            else if (sp_reed_count.selectedItemPosition==0) Utility.displayMessage("Please select reed count at step 5", applicationContext)
            else if (width.isBlank()) Utility.displayMessage(
                "Please enter width at step 6",
                applicationContext
            )
            else if (length.isBlank()) Utility.displayMessage(
                "Please enter length at step 6",
                applicationContext
            )
            //todo add step 6 validations for related items
            else if (careIdList.isEmpty()) Utility.displayMessage(
                "Please select weave type at step 7",
                applicationContext
            )
            else if (et_prod_weight.text.isBlank()) Utility.displayMessage(
                "Please enter product weight at step 9",
                applicationContext
            )
            else if (et_dscrp.text.isBlank()) Utility.displayMessage(
                "Please enter description",
                applicationContext
            )
            else {
                   if(productId>0) callUpdate(width, length)
                   else callSave(width,length)
            }
        } catch (e: Exception) {
            Utility.displayMessage("Please fill all details", applicationContext)
            Log.e("AddProductTemplate", "while save click $e")
        }
    }

    fun callSave(width:String,length:String){
        if (arrRelatedProdType!!.size > 0) {
            var relatedProductObj = RelatedProduct()
            relatedProductObj.length = sp_sub_prod_length?.selectedItem.toString()
            relatedProductObj.width = sp_sub_prod_width?.selectedItem.toString()
            relatedProductObj.productTypeID = arrRelatedProdType?.get(0)?.id ?: 0
            relatedProduct.add(relatedProductObj)
        }
        var template = ArtisanAddProductRequest()
        template.tag = et_prod_name.text.toString()
        template.code = et_prod_code.text.toString()
        template.productCategoryId = prodCatId ?: 0
        template.productTypeId = prodTypeId ?: 0
        template.productSpec = et_dscrp.text.toString()
        template.weight = et_prod_weight.text.toString()
        template.careIds = careIdList
        template.weaveIds = weaveIdList
        template.statusId = status
        template.gsm = et_gsm.text.toString()
        template.warpDyeId = warpDyeId
        template.warpYarnCount = warpYarnCount
        template.warpYarnId = warpYarnId
        template.weftDyeId = weftDyeId
        template.weftYarnCount = weftYarnCount
        template.weftYarnId = weftYarnId
        template.extraWeftYarnId = extraWeftYarnId
        template.extraWeftYarnCount = extraWeftYarnCount
        template.extraWeftDyeId = extraWeftDyeId
        template.width = width
        template.length = length
        template.reedCountId = reedCountId.toString()
        if (relatedProduct.size > 0) template.relatedProduct = relatedProduct.get(0).toString()

        val dialogCompresion = CompressionProgressDialog()
        dialogCompresion.show(
            supportFragmentManager,
            resources.getString(R.string.compressing)
        )
        dialogCompresion.isCancelable = false
        var list=ArrayList<String>()
        pairList.forEach { list.add(it.third) }
        CompressImageTask(cacheDir.absolutePath, list, object : CompressTaskResult {
            override fun performFinalTask(result: ArrayList<String>) {
                dialogCompresion.dismiss()
                val pair = Utility.validTotalFileSize(result)
                val status = pair.first
                if (status) {
                    startActivity(selectArtisanActivityIntent(template.toString(),list.joinToString (";")))
                    finish()
                } else
                    Utility.displayMessage(
                        "One of image size exceeds 1MB limit, kindly remove the it to continue",
                        applicationContext
                    )
            }
        }).execute()

    }

    fun callUpdate(width:String,length:String){
        var carelist=ArrayList<String>()
        careIdList.forEach {
            var pc=com.adrosonic.craftexchangemarketing.repository.data.request.artisan.productTemplate.ProductCare()
            pc.id=0L
            pc.productCareId=it
            pc.productId=prodCatId.toString()
            carelist.add(pc.toString()) }

        var weavelist=ArrayList<String>()
        weaveIdList.forEach {
            var weave=com.adrosonic.craftexchangemarketing.repository.data.request.artisan.productTemplate.ProductWeaf()
            weave.id=productId
            weave.weaveId=it
            weavelist.add(weave.toString())
        }

//        var relProdList=ArrayList<com.adrosonic.craftexchangemarketing.repository.data.request.artisan.productTemplate.Re>()
//        if (arrRelatedProdType!!.size > 0) {
//           var relprod=RelProduct(arrRelatedProdType?.get(0)?.id ?: 0,sp_sub_prod_width?.selectedItem.toString(),sp_sub_prod_length?.selectedItem.toString())
//           relProdList.add(relprod)
//        }
        if (arrRelatedProdType!!.size > 0) {
            var relatedProductObj = RelatedProduct()
            relatedProductObj.length = sp_sub_prod_length?.selectedItem.toString()
            relatedProductObj.width = sp_sub_prod_width?.selectedItem.toString()
            relatedProductObj.productTypeID = arrRelatedProdType?.get(0)?.id ?: 0
            relatedProduct.add(relatedProductObj)
        }
//        var template = UpdateProductTemplateRequest(et_prod_code.text.toString(),extraWeftDyeId,extraWeftYarnCount,extraWeftYarnId,et_gsm.text.toString(),productId,
//        length,carelist.toList(),prodCatId?:0,status,prodTypeId?:0,weavelist,et_dscrp.text.toString(),reedCountId,relProdList,
//        et_prod_name.text.toString(),warpDyeId,warpYarnCount,warpYarnId,weftDyeId,weftYarnCount,weftYarnId,et_prod_weight.text.toString(),width)

        var template = UpdateProductTemplateRequest()
        template.id = productId.toString()
        template.tag = et_prod_name.text.toString()
        template.code = et_prod_code.text.toString()
        template.productCategoryId = prodCatId ?: 0
        template.productTypeId = prodTypeId ?: 0
        template.productSpec = et_dscrp.text.toString().replace("\n"," ")
        template.weight = et_prod_weight.text.toString()
        template.productCares = "["+carelist.joinToString(",")+"]"
        template.productWeaves ="["+weavelist.joinToString (",")+"]"
        template.statusId = status
        template.gsm = et_gsm.text.toString()
        template.warpDyeId = warpDyeId
        template.warpYarnCount = warpYarnCount
        template.warpYarnId = warpYarnId
        template.weftDyeId = weftDyeId
        template.weftYarnCount = weftYarnCount
        template.weftYarnId = weftYarnId
        template.extraWeftYarnId = extraWeftYarnId.toString()
        template.extraWeftYarnCount = extraWeftYarnCount
        template.extraWeftDyeId = extraWeftDyeId.toString()
        template.width = width
        template.length = length
        template.reedCountId = reedCountId
        if (relatedProduct.size > 0) template.relatedProduct = relatedProduct.get(0).toString()

        val dialogCompresion = CompressionProgressDialog()
        dialogCompresion.show(
            supportFragmentManager,
            resources.getString(R.string.compressing)
        )
        dialogCompresion.isCancelable = false
        var list=ArrayList<String>()
        pairList.forEach {
           if(!it.first) list.add(it.third)
        }
        CompressImageTask(cacheDir.absolutePath, list, object : CompressTaskResult {
            override fun performFinalTask(result: ArrayList<String>) {
                dialogCompresion.dismiss()
                val pair = Utility.validTotalFileSize(result)
                val status = pair.first
                if (status) {
                    if (Utility.checkIfInternetConnected(applicationContext)) {
                        pbLoader?.visibility=View.VISIBLE
                        mProdVm?.editProduct(template.toString(),list)
                    }else  Utility.displayMessage(getString(R.string.no_internet_connection),  applicationContext )

                } else
                    Utility.displayMessage(
                        "One of image size exceeds 1MB limit, kindly remove the it to continue",
                        applicationContext
                    )
            }
        }).execute()

    }

    fun resetAll() {
        et_dscrp.text.clear()
        et_gsm.text.clear()
        et_prod_length.text.clear()
        et_prod_width.text.clear()
        et_prod_weight.text.clear()
        et_prod_name.text.clear()
        et_prod_code.text.clear()
        pairList.clear()
        loadData()
    }

    fun setStatusResource() {
        getYarnData()
        weaveIdList.clear()
        weaveSelctionList.forEach { if (it.second) weaveIdList?.add(it.third) }
        careIdList.clear()
        careSelctionList.forEach { if (it.second) careIdList?.add(it.third) }
        var width=if(arrProdWidthStr.size<=0)et_prod_width.text.toString() else sp_prod_width?.selectedItem.toString()
        var length=if(arrProdLengthStr.size<=0)et_prod_length.text.toString() else sp_prod_length?.selectedItem.toString()


        if(pairList.size>0)Utility.setImageResource(applicationContext, img_status_step1, R.drawable.ic_add_prod_status_filled)
        else Utility.setImageResource(applicationContext, img_status_step1, R.drawable.ic_add_prod_status)

        if(et_prod_name?.text!!.isNotBlank() && et_prod_code.text!!.isNotBlank() && sp_prod_category.selectedItemPosition!=0 && sp_prod_type.selectedItemPosition!=0) Utility.setImageResource(applicationContext, img_status_step2, R.drawable.ic_add_prod_status_filled)
        else Utility.setImageResource(applicationContext,img_status_step2, R.drawable.ic_add_prod_status)

        if(weaveIdList.size>0)Utility.setImageResource(applicationContext, img_status_step3, R.drawable.ic_add_prod_status_filled)
         else Utility.setImageResource(applicationContext, img_status_step3, R.drawable.ic_add_prod_status)

        if(warpDyeId<=0 ||warpYarnCount.isBlank() || warpYarnId<=0||weftDyeId<=0||weftYarnCount.isBlank()||weftYarnId<=0) Utility.setImageResource(applicationContext,img_status_step4, R.drawable.ic_add_prod_status)
        else Utility.setImageResource(applicationContext, img_status_step4, R.drawable.ic_add_prod_status_filled)

        if(sp_reed_count.selectedItemPosition!=0) Utility.setImageResource(applicationContext, img_status_step5, R.drawable.ic_add_prod_status_filled)
        else Utility.setImageResource(applicationContext, img_status_step5, R.drawable.ic_add_prod_status)

        if(width.isNotBlank() && length.isNotBlank()) Utility.setImageResource(applicationContext, img_status_step6, R.drawable.ic_add_prod_status_filled)
        else Utility.setImageResource(applicationContext, img_status_step6, R.drawable.ic_add_prod_status)

        if(careIdList.size>0)Utility.setImageResource(applicationContext, img_status_step7, R.drawable.ic_add_prod_status_filled)
        else Utility.setImageResource(applicationContext, img_status_step7, R.drawable.ic_add_prod_status)

        if(status!=null)Utility.setImageResource(applicationContext, img_status_step8, R.drawable.ic_add_prod_status_filled)
        else Utility.setImageResource(applicationContext, img_status_step8, R.drawable.ic_add_prod_status)

        if(et_prod_weight.text.isNotBlank())Utility.setImageResource(applicationContext, img_status_step9, R.drawable.ic_add_prod_status_filled)
        else Utility.setImageResource(applicationContext, img_status_step9, R.drawable.ic_add_prod_status)

        if(et_gsm.text.isNotBlank())Utility.setImageResource(applicationContext, img_status_step10, R.drawable.ic_add_prod_status_filled)
        else Utility.setImageResource(applicationContext, img_status_step10, R.drawable.ic_add_prod_status)

        if(et_dscrp.text.isNotBlank())Utility.setImageResource(applicationContext, img_status_step11, R.drawable.ic_add_prod_status_filled)
        else Utility.setImageResource(applicationContext, img_status_step11, R.drawable.ic_add_prod_status)
    }

    fun getYarnData(){
        warpYarnId = mUserConfig.warpYarnId?:0
        warpYarnCount = mUserConfig.warpYarnCount?:""
        warpDyeId = mUserConfig.warpDyeId?:0

        weftYarnId = mUserConfig.weftYarnId?:0
        weftYarnCount = mUserConfig.weftYarnCount?:""
        weftDyeId = mUserConfig.weftDyeId?:0

        extraWeftYarnId = mUserConfig.extraWeftYarnId?:0
        extraWeftYarnCount = mUserConfig.extraWeftYarnCount?:""
        extraWeftDyeId = mUserConfig.extraWeftDyeId?:0
    }

    private val generalTextWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
        override fun beforeTextChanged( s: CharSequence, start: Int, count: Int, after: Int ) { }
        override fun afterTextChanged(s: Editable) {
            setStatusResource()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Utility.resetYarnData()
    }

    override fun onSuccess(imageList: ArrayList<String>) {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                pbLoader.visibility=View.GONE
                pairList.clear()
                Log.e("DownLoadProdImages", "5555555 : " + imageList.joinToString())
                imageList.forEach {

                    val myDir = File(this.cacheDir, "/"+ Utility.BROWSING_IMGS+"/$it")
                    Log.e("DownLoadProdImages", "66666 : ${myDir.absolutePath}")
                    pairList.add(Triple(false, 0, myDir.absolutePath))
                }
                setStatusResource()
                prodImgListAdapter.notifyDataSetChanged()
            }
            )
        } catch (e: Exception) {
            Log.e("ArtisanProduct", "Exception onSuccess " + e.message)
        }
    }

    override fun onFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                pbLoader?.visibility=View.GONE
                Utility.displayMessage("Unable to download product images",this)
            }
            )
        } catch (e: Exception) {
            Log.e("ArtisanProduct", "Exception onSuccess " + e.message)
        }
    }

    override fun onUploadSuccess() {
        finish()
    }

    override fun onUploadFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                pbLoader?.visibility=View.GONE
                Log.e("Offline", "onUploadFailure")
                Utility.displayMessage("Unable to edit product",this)
            }
            )
        } catch (e: Exception) {
            Log.e("ArtisanProduct", "Exception onSuccess " + e.message)
        }
    }

    override fun onDeleteSuccess() {
        finish()
    }

    override fun onDeleteFailure() {
        try {
            Handler(Looper.getMainLooper()).post(Runnable {
                pbLoader?.visibility=View.GONE
                Utility.displayMessage("Unable to delete product",this)
            }
            )
        } catch (e: Exception) {
            Log.e("ArtisanProduct", "Exception onSuccess " + e.message)
        }
    }
}


