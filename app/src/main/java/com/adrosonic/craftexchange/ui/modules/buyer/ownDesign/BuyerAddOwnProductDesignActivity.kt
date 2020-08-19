package com.adrosonic.craftexchange.ui.modules.buyer.ownDesign

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.BuyerCustomProduct
import com.adrosonic.craftexchange.database.entities.realmEntities.RelatedProducts
import com.adrosonic.craftexchange.database.predicates.BuyerCustomProductPredicates
import com.adrosonic.craftexchange.database.predicates.ProductImagePredicates
import com.adrosonic.craftexchange.database.predicates.RelateProductPredicates
import com.adrosonic.craftexchange.database.predicates.WeaveTypesPredicates
import com.adrosonic.craftexchange.databinding.ActivityBuyerAddOwnProductDesignBinding
import com.adrosonic.craftexchange.repository.data.request.artisan.productTemplate.RelatedProduct
import com.adrosonic.craftexchange.repository.data.request.buyer.OwnDesignRequest
import com.adrosonic.craftexchange.repository.data.request.buyer.UpdateOwnDesignRequest
import com.adrosonic.craftexchange.repository.data.response.artisan.products.productTemplate.uploadData.*
import com.adrosonic.craftexchange.repository.data.response.artisan.products.productTemplate.uploadData.ProductType
import com.adrosonic.craftexchange.syncManager.SyncCoordinator
import com.adrosonic.craftexchange.ui.modules.artisan.productTemplate.ProdImageListAdapter
import com.adrosonic.craftexchange.ui.modules.artisan.productTemplate.WeaveSelectionAdapter
import com.adrosonic.craftexchange.ui.modules.artisan.productTemplate.YarnViewpager
import com.adrosonic.craftexchange.ui.modules.artisan.productTemplate.yarnFrgamnets.YarnFrgamentAdapter
import com.adrosonic.craftexchange.utils.*
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_artisan_add_product_template.*
import kotlinx.android.synthetic.main.activity_buyer_add_own_product_design.*
import kotlinx.android.synthetic.main.activity_buyer_add_own_product_design.yarn_pager


fun Context.ownDesignIntent(): Intent {
    return Intent(this, BuyerAddOwnProductDesignActivity::class.java).apply {
    }
}
fun Context.ownDesignIntent(id: Long): Intent {
    val intent = Intent(this, BuyerAddOwnProductDesignActivity::class.java)
    intent.putExtra("productId", id)
    return intent.apply { }
//    return Intent(this, ArtisanAddProductTemplateActivity::class.java).apply {
}
class BuyerAddOwnProductDesignActivity : AppCompatActivity(),
    View.OnClickListener,
//    YarnViewpager.yarnListner,
    ProdImageListAdapter.ProdUpdateListener,
    WeaveSelectionAdapter.selectionListener{

    private var mBinding:ActivityBuyerAddOwnProductDesignBinding? = null
    private lateinit var prodImgListAdapter: ProdImageListAdapter
    private var pairList = ArrayList<Triple<Boolean,Long, String>>()
    private var deletedPaths =ArrayList<Pair<Long,String>>()
    private lateinit var weaveSelectionAdapter: WeaveSelectionAdapter
    var weaveSelctionList = ArrayList<Triple<String, Boolean, Long>>()

    private val mUserConfig = UserConfig()
    var jsonProductData: String = ""
    var productUploadData: ProductUploadData? = null
    var arrProductCategory: List<ProductCategory>? = null
    var arrProductType: List<ProductType>? = null
    var arrRelatedProdType: List<ProductType>? = null
    var arrWeaf: List<Weaf>? = null
    var arrYarn: List<Yarn>? = null
    var arrReedCount: List<ReedCount>? = null
    var arrDyes: List<Dye>? = null

    var warpDyeId=0L
    var warpYarnCount=""
    var warpYarnId=0L
    var weftDyeId=0L
    var weftYarnCount=""
    var weftYarnId=0L
    var extraWeftDyeId=0L
    var extraWeftYarnCount=""
    var extraWeftYarnId=0L

    var prodCatId:Long?=null
    var prodTypeId:Long?=null
    var weaveIdList=ArrayList<Long>()
    var relatedProduct =ArrayList<RelatedProduct>()
    var status=1L
    var reedCountId=1L
    private var dots = ArrayList<TextView>()

    var arrProdCategoryStr = ArrayList<String>()
    var arrProdTypeStr = ArrayList<String>()
    var arrReedCountStr = ArrayList<String>()
    var arrSubProdWidthStr = ArrayList<String>()
    var arrSubProdLengthStr = ArrayList<String>()
    var arrListRelatedProduct=ArrayList<RelatedProduct>()
    var arrProdWidthStr = ArrayList<String>()
    var arrProdLengthStr = ArrayList<String>()

    var productId = 0L
    var productTypeDescForUpdate=""
    var productEntry: BuyerCustomProduct?=null
    var weaveIdStored:ArrayList<Long>?=null
    var relatedProdStored: RelatedProducts?=null
    var yarnPosition=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBuyerAddOwnProductDesignBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)
        mBinding?.parentStep1?.setOnClickListener(this)
        mBinding?.parentStep2?.setOnClickListener(this)
        mBinding?.parentStep3?.setOnClickListener(this)
        mBinding?.parentStep4?.setOnClickListener(this)
        mBinding?.parentStep5?.setOnClickListener(this)
        mBinding?.parentStep6?.setOnClickListener(this)
        mBinding?.parentStep7?.setOnClickListener(this)
        mBinding?.parentStep8?.setOnClickListener(this)

        jsonProductData = mUserConfig.productUploadJson.toString()
        val gson = GsonBuilder().create()
        productUploadData = gson.fromJson(jsonProductData, ProductUploadData::class.java)
        arrProductCategory = productUploadData?.data?.productCategories
        arrWeaf = productUploadData?.data?.weaves
        arrYarn = productUploadData?.data?.yarns
        arrReedCount = productUploadData?.data?.reedCounts
        arrDyes = productUploadData?.data?.dyes

        if (intent.extras != null) {
            productId = intent.getLongExtra("productId", 0)
            Log.e("Offline", "own design activity prodId :" + productId)
            if (productId > 0) {
                mBinding?.txtSaveUploadTop?.text="Update"
                mBinding?.txtSaveUpload?.text="Update"
                productEntry= BuyerCustomProductPredicates.getCustomProductFormRemotId(productId)
            } else {
                mBinding?.txtSaveUploadTop?.text="Save"
                mBinding?.txtSaveUpload?.text="Save"
            }
        } else {
            mBinding?.txtSaveUploadTop?.text="Save"
            mBinding?.txtSaveUpload?.text="Save"
        }

        mBinding?.btnBack?.setOnClickListener{
            showCancelDialog()
        }
        loadData()
        /////////////////////////Save and Upload///////////////////////////
        mBinding?.txtSaveUpload?.setOnClickListener {saveUploadProduct() }
        mBinding?.txtSaveUploadTop?.setOnClickListener {  saveUploadProduct() }
        mBinding?.txtReset?.setOnClickListener { resetAll() }

        mBinding?.etProdWidth?.addTextChangedListener(generalTextWatcher)
        mBinding?.etProdLength?.addTextChangedListener(generalTextWatcher)
        mBinding?.etGsm?.addTextChangedListener(generalTextWatcher)
        mBinding?.etDscrp?.addTextChangedListener(generalTextWatcher)
    }

    fun loadData(){
        ///////////////////////Add Photo////////////////////////
        mBinding?.addPhotoRecycler?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        prodImgListAdapter = ProdImageListAdapter(this, false,pairList)
        prodImgListAdapter.listener = this
        mBinding?.addPhotoRecycler?.adapter = prodImgListAdapter
        mBinding?.txtAddProductImage?.setOnClickListener {
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
        mBinding?.weaveRecyclerList?.layoutManager = LinearLayoutManager(this)
        weaveSelectionAdapter = WeaveSelectionAdapter(this, weaveSelctionList)
        weaveSelectionAdapter.listener = this
        mBinding?.weaveRecyclerList?.adapter = weaveSelectionAdapter

        ///////////general details//////////////
        arrProdCategoryStr.clear()
        arrProdCategoryStr.add("Select product category")
        arrProductCategory?.forEach { arrProdCategoryStr.add(it.productDesc) }
        val spProdCataAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrProdCategoryStr)
        spProdCataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        mBinding?.spProdCategory?.setAdapter(spProdCataAdapter)
        mBinding?.spProdCategory?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

                val spProdTypeAdapter = ArrayAdapter<String>( applicationContext, android.R.layout.simple_spinner_item,   arrProdTypeStr  )
                spProdTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                mBinding?.spProdType?.setAdapter(spProdTypeAdapter)
                if(productId>0){
                    var type=""
                    arrProductType?.forEach {
                        if(it.id.equals(productEntry?.productTypeId))type=it.productDesc
                    }
                    mBinding?.spProdType?.setSelection(arrProdTypeStr.indexOf(type))
                }
                setStatusResource()
            }

        }
        mBinding?.spProdType?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val prodType = arrProdTypeStr.get(position)
                arrProductType?.forEach {
                    if(it.productDesc.equals(prodType)){
//                      prodTypeId=it.id
//                      prodCatId=it.productCategoryID
                        arrRelatedProdType= it.relatedProductType
                    }
                }
               if(position>0) setVisiblitiesAndTextsOnType(arrProdTypeStr.get(position),arrRelatedProdType)
            }

        }
        ////////////////////warp_weft_yarns//////////////////////////
        supportFragmentManager.let{
            yarn_pager?.adapter = YarnFrgamentAdapter(it,productId,false)
        }
        yarn_pager?.setOffscreenPageLimit(3)
//        val viewPagerAdapter = YarnViewpager(this,productId, false)
//        mBinding?.yarnPager?.setAdapter(viewPagerAdapter)
//        viewPagerAdapter.listener=this
        dots.clear()
        mBinding?.sliderDots?.removeAllViews()
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
            mBinding?.sliderDots?.addView(d, params)
            dots.add(d)
        } while (dots.size < 3)
        setDotsColor(0)
        mBinding?.yarnPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                setStatusResource()
            }

            override fun onPageSelected(position: Int) {
                // Check if this is the page you want.
                setDotsColor(position)
            }
        })
        /////////////////reed count/////////////////////
        arrReedCountStr.clear()
        arrReedCountStr.add("Select reed count")
        arrReedCount?.forEach { arrReedCountStr.add(it.count) }
        val spReedCountAdapter =ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrReedCountStr)
        spReedCountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding?.spReedCount?.setAdapter(spReedCountAdapter)
        mBinding?.spReedCount?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                setStatusResource()
            }
        }
        ////////////////////////////////update////////////////////////
        if(productId>0){
            var imageList= ProductImagePredicates.getImagesList(productId)
            Log.e("Offline", "activity imageList :" +imageList.size)
            imageList.forEach {  pairList.add(Triple(true,productId,it))}

            for (category in arrProductCategory!!) {
                if (category.productDesc.equals(productEntry?.productCategoryDscrp?:"", true)) {
                    arrProductType = category.productTypes
                }
            }

            arrProductType?.forEach {
                Log.e("Offline", "activity forEach :" +it.productDesc)
                if(it.id.equals(productEntry?.productTypeId))productTypeDescForUpdate=it.productDesc
                if (it.productDesc.equals(productTypeDescForUpdate)) {
                    arrRelatedProdType = it.relatedProductType
                }
                arrProdTypeStr?.add(it.productDesc)
            }
            Log.e("Offline", "activity arrRelatedProdType :" +arrRelatedProdType?.size)
            relatedProdStored= RelateProductPredicates.getRelatedProductOfProduct(productId)

            Log.e("Offline", "activity productTypeDesc :" +productEntry?.productCategoryDscrp)
            mBinding?.spProdCategory?.setSelection(arrProdCategoryStr.indexOf(productEntry?.productCategoryDscrp?:""))
            mBinding?.spProdType?.setSelection(arrProdTypeStr.indexOf(productTypeDescForUpdate))//todo

            setVisiblitiesAndTextsOnType(productTypeDescForUpdate, arrRelatedProdType)
            weaveIdStored= WeaveTypesPredicates.getWeaveList(productId)
            Log.e("Offline", "activity weaveIdStored :" + weaveIdStored?.joinToString())
            weaveSelctionList?.forEach {
                if(weaveIdStored!!.contains(it.third)) {
                    val pos= weaveSelctionList.indexOf(it)
                    weaveSelctionList.set(pos, Triple(it.first,true,it.third))
                    weaveSelectionAdapter.notifyItemRangeChanged(pos, weaveSelctionList.size)
                }
            }
            arrReedCount?.forEach {
                if(it.id.equals(productEntry?.reedCountId?:""))mBinding?.spReedCount?.setSelection(arrReedCountStr.indexOf(it.count))
            }

            mBinding?.etGsm?.setText(productEntry?.gsm?:"", TextView.BufferType.EDITABLE)
            mBinding?.etDscrp?.setText(productEntry?.productSpe?:"", TextView.BufferType.EDITABLE)
            setStatusResource()
            Utility.setImageResource(applicationContext, mBinding?.imgStatusStep2, R.drawable.ic_add_prod_status_filled)
            Utility.setImageResource(applicationContext, mBinding?.imgStatusStep4, R.drawable.ic_add_prod_status_filled)
        }

    }

//    override fun sendYarnData(position: Int, yarnType: Long, yarnCount: String, dye: Long) {
//        Log.e("Viewpager","{$yarnPosition} position/yarnType :"+yarnType+" :yarnCount :"+yarnCount+" : dye :"+dye)
//        when(yarnPosition){
//            0->{
//                warpYarnId=yarnType
//                warpYarnCount=yarnCount
//                warpDyeId=dye
//            }
//            1->{
//                weftYarnId=yarnType
//                weftYarnCount=yarnCount
//                weftDyeId=dye
//            }
//            2->{
//                extraWeftYarnId=yarnType
//                extraWeftYarnCount=yarnCount
//                extraWeftDyeId=dye
//            }
//        }
////        setDotsColor(position)
//        setStatusResource()
//    }

    override fun onUpdate(pairList: ArrayList<Triple<Boolean,Long, String>>, deletedIds: ArrayList<Pair<Long,String>>) {
        this.pairList = pairList
        this.deletedPaths = deletedIds
        setStatusResource()
    }

    override fun onWeaveItemSelected(pairList: ArrayList<Triple<String, Boolean, Long>>) {
        this.weaveSelctionList = pairList
        setStatusResource()
    }

    override fun onClick(p0: View?) {
        val slideDown = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)
        val slideUp = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)
        when (p0!!.id) {
            R.id.parent_step1 -> {
                if (mBinding?.childStep1?.visibility == View.GONE) {
                    mBinding?.childStep1?.visibility = View.VISIBLE
                    mBinding?.childStep1?.animation = slideDown
                } else {
                    mBinding?.childStep1?.visibility = View.GONE
                    mBinding?.childStep1?.animation = slideUp
                }
            }
            R.id.parent_step2 -> {
                if (mBinding?.childStep2?.visibility == View.GONE) {
                    mBinding?.childStep2?.visibility = View.VISIBLE
                    mBinding?.childStep2?.animation = slideDown
                } else {
                    mBinding?.childStep2?.visibility = View.GONE
                    mBinding?.childStep2?.animation = slideUp
                }
            }
            R.id.parent_step3 -> {
                if (mBinding?.childStep3?.visibility == View.GONE) {
                    mBinding?.childStep3?.visibility = View.VISIBLE
                    mBinding?.childStep3?.animation = slideDown
                } else {
                    mBinding?.childStep3?.visibility = View.GONE
                    mBinding?.childStep3?.animation = slideUp
                }
            }
            R.id.parent_step4 -> {
                if (mBinding?.childStep4?.visibility == View.GONE) {
                    mBinding?.childStep4?.visibility = View.VISIBLE
                    mBinding?.childStep4?.animation = slideDown
                } else {
                    mBinding?.childStep4?.visibility = View.GONE
                    mBinding?.childStep4?.animation = slideUp
                }
            }
            R.id.parent_step5 -> {
                if (mBinding?.childStep5?.visibility == View.GONE) {
                    mBinding?.childStep5?.visibility = View.VISIBLE
                    mBinding?.childStep5?.animation = slideDown
                } else {
                    mBinding?.childStep5?.visibility = View.GONE
                    mBinding?.childStep5?.animation = slideUp
                }
            }
            R.id.parent_step6 -> {
                if (mBinding?.childStep6?.visibility == View.GONE) {
                    mBinding?.childStep6?.visibility = View.VISIBLE
                    mBinding?.childStep6?.animation = slideDown
                } else {
                    mBinding?.childStep6?.visibility = View.GONE
                    mBinding?.childStep6?.animation = slideUp
                }
            }
            R.id.parent_step7 -> {
                if (mBinding?.childStep7?.visibility == View.GONE) {
                    mBinding?.childStep7?.visibility = View.VISIBLE
                    mBinding?.childStep7?.animation = slideDown
                } else {
                    mBinding?.childStep7?.visibility = View.GONE
                    mBinding?.childStep7?.animation = slideUp
                }
            }
            R.id.parent_step8 -> {
                if (mBinding?.childStep8?.visibility == View.GONE) {
                    mBinding?.childStep8?.visibility = View.VISIBLE
                    mBinding?.childStep8?.animation = slideDown
                } else {
                    mBinding?.childStep8?.visibility = View.GONE
                    mBinding?.childStep8?.animation = slideUp
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConstantsDirectory.PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK && null != data) {
            when (requestCode) {
                ConstantsDirectory.PICK_IMAGE -> {
                    val uri = data?.data
                    if (uri != null) {

                        var absolutePath = Utility.getRealPathFromFileURI(applicationContext, uri!!)
                        pairList.add(Triple(false, 0, absolutePath))
                        prodImgListAdapter.notifyDataSetChanged()
                        setStatusResource()
                    }
                }
            }
        }
        if(requestCode == ConstantsDirectory.EDIT_IMAGE)
        {
            val position=data?.getIntExtra(ConstantsDirectory.EDIT_IMAGE_POSITION,0)?:0
            prodImgListAdapter.notifyItemRangeChanged(position,pairList.size)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showCancelDialog()
            return true
        } else return super.onKeyDown(keyCode, event)
    }

    fun setVisiblitiesAndTextsOnType(type: String,arrRelatedProdType:List<ProductType>?) {
        arrProdLengthStr?.clear()
        arrProdWidthStr?.clear()
        arrSubProdLengthStr?.clear()
        arrSubProdWidthStr?.clear()

        Log.e("SetData","type :"+type)
        for (t in arrProductType!!) {
            Log.e("SetData","type :"+t.productDesc)
            if (t.productDesc.equals(type, true)) {
                Log.e("SetData","type :"+t.productDesc)
                Log.e("SetData","productLengths :"+t.productLengths.size)
                Log.e("SetData","productWidths :"+t.productWidths.size)
                t.productLengths.forEach { arrProdLengthStr?.add(it.length) }
                t.productWidths.forEach { arrProdWidthStr?.add(it.width) }
            }
        }

        if(arrProdWidthStr.size<=0){
            mBinding?.spProdWidth?.visibility=View.GONE
            mBinding?.etProdWidth?.visibility=View.VISIBLE
            if(productId>0)mBinding?.etProdWidth?.setText(productEntry?.width?:"", TextView.BufferType.EDITABLE)
        }
        else {
            mBinding?.spProdWidth?.visibility=View.VISIBLE
            mBinding?.etProdWidth?.visibility=View.GONE
            val spwidthAdapter =ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrProdWidthStr)
            spwidthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mBinding?.spProdWidth?.setAdapter(spwidthAdapter)
            if(productId>0)mBinding?.spProdWidth?.setSelection(arrProdWidthStr.indexOf(productEntry?.width?:""))
        }
        if(arrProdLengthStr.size<=0){
            mBinding?.spProdLength?.visibility=View.GONE
            mBinding?.etProdLength?.visibility=View.VISIBLE
            if(productId>0)mBinding?.etProdLength?.setText(productEntry?.length?:"", TextView.BufferType.EDITABLE)
        }
        else {
            mBinding?.spProdLength?.visibility=View.VISIBLE
            mBinding?.etProdLength?.visibility=View.GONE
            val spLenghtAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrProdLengthStr)
            spLenghtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mBinding?.spProdLength?.setAdapter(spLenghtAdapter)
            if(productId>0)mBinding?.spProdLength?.setSelection(arrProdLengthStr.indexOf(productEntry?.length?:""))
        }

        if (type.equals("Fabric")) {
            mBinding?.parentStep7?.visibility = View.VISIBLE
//            child_step7.visibility = View.VISIBLE
            txt_step_8.text = "Step 8 : Enter description"
        } else {
            mBinding?.parentStep7?.visibility = View.GONE
            mBinding?.childStep7?.visibility = View.GONE
            txt_step_8.text = "Step 7 : Enter description"
        }
        if(arrRelatedProdType!=null) {
        if (arrRelatedProdType!!.size>0) {
            //todo inflater to be called post API itegratipn
            mBinding?.txtRelatedProdType?.visibility = View.VISIBLE
            mBinding?.llSubProd?.visibility = View.VISIBLE
            mBinding?.txtRelatedProdType?.text = arrRelatedProdType.get(0).productDesc

            arrRelatedProdType.get(0).productLengths.forEach { arrSubProdLengthStr.add(it.length) }
            val splengthAdapter =ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,arrSubProdLengthStr )
            splengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mBinding?.spSubProdLength?.setAdapter(splengthAdapter)

            arrRelatedProdType.get(0).productWidths.forEach { arrSubProdWidthStr.add(it.width) }
            val spwidthAdapter =ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,arrSubProdWidthStr )
            spwidthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mBinding?.spSubProdWidth?.setAdapter(spwidthAdapter)
            if(productId>0){
                mBinding?.spSubProdLength?.setSelection(arrSubProdLengthStr.indexOf(relatedProdStored?.productLength))
                mBinding?.spSubProdWidth?.setSelection(arrSubProdWidthStr.indexOf(relatedProdStored?.productWidth))
            }
        } else {
            mBinding?.txtRelatedProdType?.visibility = View.GONE
            mBinding?.llSubProd?.visibility = View.GONE
        }
        }else {
            mBinding?.txtRelatedProdType?.visibility = View.GONE
            mBinding?.llSubProd?.visibility = View.GONE
        }
        setStatusResource()
    }

    fun setDotsColor(position:Int){
        dots.forEach { it.setTextColor(  ContextCompat.getColor(this, R.color.darker_gray)) }
        dots.get(position).setTextColor(Color.parseColor("#009A2F"))
    }

    fun resetAll(){
        mBinding?.etDscrp?.text?.let { it.clear() }
        mBinding?.etGsm?.text?.let { it.clear() }
        mBinding?.etProdLength?.text?.let { it.clear() }
        mBinding?.etProdWidth?.text?.let { it.clear() }
        pairList.clear()
        loadData()
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

    fun saveUploadProduct() {
        try {
            getYarnData()
            weaveIdList.clear()
            weaveSelctionList.forEach { if(it.second)weaveIdList?.add(it.third) }
            var width=if(arrProdWidthStr.size<=0)mBinding?.etProdWidth?.text.toString() else mBinding?.spProdWidth?.selectedItem.toString()
            var length=if(arrProdLengthStr.size<=0)mBinding?.etProdLength?.text.toString() else mBinding?.spProdLength?.selectedItem.toString()

            arrReedCount?.forEach { if(it.count.equals(mBinding?.spReedCount?.selectedItem.toString()) ){reedCountId=it.id}}

            arrProductCategory?.forEach { if(it.productDesc.equals(mBinding?.spProdCategory?.selectedItem.toString())){
                prodCatId=it.id
                it.productTypes.forEach {
                    if(it.productDesc.equals(mBinding?.spProdType?.selectedItem.toString())){
                        prodTypeId=it.id
                    }
                }
            }
            }
            if(arrRelatedProdType!!.size>0){
                var relatedProductObj=RelatedProduct()
                relatedProductObj.length=mBinding?.spSubProdLength?.selectedItem.toString()
                relatedProductObj.width=mBinding?.spSubProdWidth?.selectedItem.toString()
                relatedProductObj.productTypeID=arrRelatedProdType?.get(0)?.id?:0
                relatedProduct.add(relatedProductObj)
            }
            Log.e("saveUploadProduct","relatedProduct :${mBinding?.spSubProdWidth?.selectedItem.toString() } ")

            if(pairList.isEmpty()) Utility.displayMessage("Please add atleast 1 product image",applicationContext)
            else if(mBinding?.spProdCategory?.selectedItemPosition==0) Utility.displayMessage("Please select product category at step 2",applicationContext)
            else if(mBinding?.spProdType?.selectedItemPosition==0) Utility.displayMessage("Please select product type at step 2",applicationContext)
            else if(weaveIdList.isEmpty()) Utility.displayMessage("Please select weave type at step 3",applicationContext)
            else if(warpDyeId<=0) Utility.displayMessage("Please select warp dye Id at step 4",applicationContext)
            else if(warpYarnCount.isBlank()) Utility.displayMessage("Please select warp yarn count at step 4",applicationContext)
            else if(warpYarnId<=0) Utility.displayMessage("Please select warp yarn Id at step 4",applicationContext)
            else if(weftDyeId<=0) Utility.displayMessage("Please select weft dye Id at step 4",applicationContext)
            else if(weftYarnCount.isBlank()) Utility.displayMessage("Please select weft yarn count at step 4",applicationContext)
            else if(weftYarnId<=0) Utility.displayMessage("Please select weft yarn Id at step 4",applicationContext)
            else if(mBinding?.spReedCount?.selectedItemPosition==0) Utility.displayMessage("Please select reed count at setp 5",applicationContext)
            else if(width.isBlank()) Utility.displayMessage("Please enter width at step 6",applicationContext)
            else if(length.isBlank()) Utility.displayMessage("Please enter length at step 6",applicationContext)
            //todo add step 6 validations for related items
            else if(mBinding?.etDscrp?.text!!.isBlank()) Utility.displayMessage("Please enter description",applicationContext)
            else{
                var dialog = Dialog(this)
                dialog?.setContentView(R.layout.dialog_save_upload)
                dialog?.show()
                val tvCancel = dialog?.findViewById(R.id.cancel) as TextView
                val tvSave = dialog?.findViewById(R.id.save) as TextView
                tvCancel.setOnClickListener {
                    dialog.cancel()
                }
                tvSave.setOnClickListener {
                    dialog.dismiss()
                    if(productId>0) callUpdate(width, length)
                    else callSave(width,length)
                }
            }
        } catch (e: Exception) {
            Utility.displayMessage("Please fill all details",applicationContext)
            Log.e("AddProductTemplate","while save click $e")
        }
    }

    fun callSave(width:String,length:String){
        var template= OwnDesignRequest()
        template.productCategoryId=prodCatId?:0
        template.productTypeId=prodTypeId?:0
        template.weaveIds="${weaveIdList}"
        Log.e("ArtisanProdLog","weave Ids ${template.weaveIds}")
        template.gsm=mBinding?.etGsm?.text.toString()
        template.warpDyeId=warpDyeId
        template.warpYarnCount=warpYarnCount
        template.warpYarnId=warpYarnId
        template.weftDyeId=weftDyeId
        template.weftYarnCount=weftYarnCount
        template.weftYarnId=weftYarnId
        template.extraWeftYarnId=extraWeftYarnId
        template.extraWeftYarnCount=extraWeftYarnCount
        template.extraWeftDyeId=extraWeftDyeId
        template.width=width
        template.length=length
        template.productSpec=mBinding?.etDscrp?.text.toString()
        template.reedCountId=reedCountId.toString()
        if(relatedProduct.size>0)   template.relatedProduct=relatedProduct.get(0).toString()

        var list=ArrayList<String>()
        pairList.forEach { list.add(it.third) }
        val dialogCompresion = CompressionProgressDialog()
        dialogCompresion.show(supportFragmentManager, resources.getString(R.string.compressing))
        dialogCompresion.isCancelable = false
        CompressImageTask(cacheDir.absolutePath,  list,  object : CompressTaskResult {
            override fun performFinalTask(result: ArrayList<String>) {
                dialogCompresion.dismiss()
                val pair=Utility.validTotalFileSize(result)
                val status = pair.first
                if (status) {
                    BuyerCustomProductPredicates.insertCustomProductOffline(template, list,relatedProduct)
                    if (Utility.checkIfInternetConnected(applicationContext)) {
                        val coordinator = SyncCoordinator(applicationContext)
                        coordinator?.performLocallyAvailableActions()
                    }
                    finish()
                } else
                    Utility.displayMessage("One of image size exceeds 1MB limit, kindly remove the it to continue", applicationContext)
            }
        }).execute()

    }

    fun callUpdate(width:String,length:String){

        var weavelist=ArrayList<com.adrosonic.craftexchange.repository.data.request.buyer.ProductWeaf>()
        weaveIdList.forEach { weavelist.add(com.adrosonic.craftexchange.repository.data.request.buyer.ProductWeaf(System.currentTimeMillis(),productId,it)) }

        var relProdList=ArrayList<com.adrosonic.craftexchange.repository.data.request.buyer.RelProduct>()
        if (arrRelatedProdType!!.size > 0) {
            var relprod= com.adrosonic.craftexchange.repository.data.request.buyer.RelProduct(arrRelatedProdType?.get(0)?.id ?: 0,mBinding?.spSubProdWidth?.selectedItem.toString(),mBinding?.spSubProdLength?.selectedItem.toString())
            relProdList.add(relprod)
        }

        var template = UpdateOwnDesignRequest(extraWeftDyeId,extraWeftYarnCount,extraWeftYarnId,mBinding?.etGsm?.text.toString(),productId,
            length,prodCatId?:0,prodTypeId?:0,weavelist,mBinding?.etDscrp?.text.toString()?:"",reedCountId,relProdList,
           warpDyeId,warpYarnCount,warpYarnId,weftDyeId,weftYarnCount,weftYarnId,"",width)

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
                    BuyerCustomProductPredicates.updateOwnProductOffline(
                        template,
                        list,
                        deletedPaths,
                        relProdList
                    )
                    if (Utility.checkIfInternetConnected(applicationContext)) {
                        val coordinator = SyncCoordinator(applicationContext)
                        coordinator?.performLocallyAvailableActions()
                    }

                    finish()
                } else
                    Utility.displayMessage(
                        "One of image size exceeds 1MB limit, kindly remove the it to continue",
                        applicationContext
                    )
            }
        }).execute()

    }

    fun setStatusResource() {
        getYarnData()
        weaveIdList.clear()
        weaveSelctionList.forEach { if (it.second) weaveIdList?.add(it.third) }

        var width=if(arrProdWidthStr.size<=0)mBinding?.etProdWidth?.text.toString() else mBinding?.spProdWidth?.selectedItem.toString()
        var length=if(arrProdLengthStr.size<=0)mBinding?.etProdLength?.text.toString() else mBinding?.spProdLength?.selectedItem.toString()


        if(pairList.size>0)Utility.setImageResource(applicationContext, mBinding?.imgStatusStep1, R.drawable.ic_add_prod_status_filled)
        else Utility.setImageResource(applicationContext, mBinding?.imgStatusStep1, R.drawable.ic_add_prod_status)

        if(mBinding?.spProdCategory?.selectedItemPosition!=0 && mBinding?.spProdType?.selectedItemPosition!=0) Utility.setImageResource(applicationContext, mBinding?.imgStatusStep2, R.drawable.ic_add_prod_status_filled)
        else Utility.setImageResource(applicationContext,mBinding?.imgStatusStep2, R.drawable.ic_add_prod_status)

        if(weaveIdList.size>0)Utility.setImageResource(applicationContext, mBinding?.imgStatusStep3, R.drawable.ic_add_prod_status_filled)
         else Utility.setImageResource(applicationContext, mBinding?.imgStatusStep3, R.drawable.ic_add_prod_status)

        if(warpDyeId<=0 ||warpYarnCount.isBlank() || warpYarnId<=0||weftDyeId<=0||weftYarnCount.isBlank()||weftYarnId<=0) Utility.setImageResource(applicationContext,mBinding?.imgStatusStep4, R.drawable.ic_add_prod_status)
        else Utility.setImageResource(applicationContext, mBinding?.imgStatusStep4, R.drawable.ic_add_prod_status_filled)

        if(mBinding?.spReedCount?.selectedItemPosition!=0) Utility.setImageResource(applicationContext, mBinding?.imgStatusStep5, R.drawable.ic_add_prod_status_filled)
        else Utility.setImageResource(applicationContext, mBinding?.imgStatusStep5, R.drawable.ic_add_prod_status)

        if(width.isNotBlank() && length.isNotBlank()) Utility.setImageResource(applicationContext, mBinding?.imgStatusStep6, R.drawable.ic_add_prod_status_filled)
        else Utility.setImageResource(applicationContext, mBinding?.imgStatusStep6, R.drawable.ic_add_prod_status)

        if(mBinding?.etGsm?.text!!.isNotBlank())Utility.setImageResource(applicationContext, mBinding?.imgStatusStep10, R.drawable.ic_add_prod_status_filled)
        else Utility.setImageResource(applicationContext, mBinding?.imgStatusStep10, R.drawable.ic_add_prod_status)

        if(mBinding?.etDscrp?.text!!.isNotBlank())Utility.setImageResource(applicationContext, mBinding?.imgStatusStep8, R.drawable.ic_add_prod_status_filled)
        else Utility.setImageResource(applicationContext, mBinding?.imgStatusStep8, R.drawable.ic_add_prod_status)
    }

    private val generalTextWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
        override fun beforeTextChanged( s: CharSequence, start: Int, count: Int, after: Int ) { }
        override fun afterTextChanged(s: Editable) {
            setStatusResource()
        }
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
    override fun onDestroy() {
        super.onDestroy()
        Utility.resetYarnData()
    }
}
