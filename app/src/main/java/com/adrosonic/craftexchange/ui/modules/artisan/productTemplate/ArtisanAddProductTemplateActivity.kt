package com.adrosonic.craftexchange.ui.modules.artisan.productTemplate

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityArtisanAddProductTemplateBinding
import com.adrosonic.craftexchange.repository.data.request.artisan.productTemplate.ArtisanAddProductRequest
import com.adrosonic.craftexchange.repository.data.request.artisan.productTemplate.RelatedProduct
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.adrosonic.craftexchange.viewModels.ArtisanProductTemplateViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_artisan_add_product_template.*
import quicktype.*


fun Context.addProductIntent(): Intent {
    return Intent(this, ArtisanAddProductTemplateActivity::class.java).apply {
    }
}

class ArtisanAddProductTemplateActivity : AppCompatActivity(),
    View.OnClickListener,
    ProdImageListAdapter.ProdUpdateListener,
    WeaveSelectionAdapter.selectionListener,
    CareInstructionsSelectionAdapter.selectionListener,
    YarnViewpager.yarnListner {
    private var mBinding: ActivityArtisanAddProductTemplateBinding? = null
    val mViewModel: ArtisanProductTemplateViewModel by viewModels()
    private lateinit var prodImgListAdapter: ProdImageListAdapter
    private var pairList = ArrayList<String>()
    private var deletedPaths = ArrayList<String>()
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
    var arrListRelatedProduct=ArrayList<RelatedProduct>()
    var productAvalability = false

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
    var careIdList=ArrayList<Long>()
    var status=1L
    var reedCountId=1L
    //    private var parentViewsList=ArrayList<LinearLayout>()
//    private var childViewsList=ArrayList<Any>()
    private var dots = ArrayList<TextView>()
    private var statusList = ArrayList<ImageView>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArtisanAddProductTemplateBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

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

        statusList.add(img_status_step1)
        statusList.add(img_status_step2)
        statusList.add(img_status_step3)
        statusList.add(img_status_step4)
        statusList.add(img_status_step5)
        statusList.add(img_status_step6)
        statusList.add(img_status_step7)
        statusList.add(img_status_step8)
        statusList.add(img_status_step9)
        statusList.add(img_status_step10)
        statusList.add(img_status_step11)

        btn_back.setOnClickListener{
            showCancelDialog()
        }
        ///////////////////////Add Photo////////////////////////
        add_photo_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        prodImgListAdapter = ProdImageListAdapter(this, pairList)
        prodImgListAdapter.listener = this
        add_photo_recycler.adapter = prodImgListAdapter
        txt_add_product_image.setOnClickListener {
            //todo add check for permissions dialog
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
        }

        ///////////////////////weave types////////////////////////
        weaveSelctionList.clear()
        arrWeaf?.forEach { weaveSelctionList.add(Triple(it.weaveDesc ?: "", false, it.id)) }
        weave_recycler_list.layoutManager = LinearLayoutManager(this)
        weaveSelectionAdapter = WeaveSelectionAdapter(this, weaveSelctionList)
        weaveSelectionAdapter.listener = this
        weave_recycler_list.adapter = weaveSelectionAdapter

        ///////////general details//////////////
        arrProductCategory?.forEach { arrProdCategoryStr.add(it.productDesc) }
        val spProdCataAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrProdCategoryStr)
        spProdCataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
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

                val prodCategory = arrProdCategoryStr.get(position)
                for (category in arrProductCategory!!) {
                    if (category.productDesc.equals(prodCategory, true)) {
//                        prodCatId=category.id
                        arrProductType = category.productTypes
                        category.productTypes.forEach { arrProdTypeStr?.add(it.productDesc) }
                    }
                }

                val spProdTypeAdapter = ArrayAdapter<String>( applicationContext, android.R.layout.simple_spinner_item,   arrProdTypeStr  )
                spProdTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sp_prod_type.setAdapter(spProdTypeAdapter)
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
                  if(it.productDesc.equals(prodType)){
//                      prodTypeId=it.id
//                      prodCatId=it.productCategoryID
                      arrRelatedProdType= it.relatedProductType
                  }
                }
                setVisiblitiesAndTextsOnType(arrProdTypeStr.get(position),arrRelatedProdType)
            }

        }

        ////////////////////warp_weft_yarns//////////////////////////
        val viewPagerAdapter = YarnViewpager(this, arrYarn, arrDyes)
        yarn_pager.setAdapter(viewPagerAdapter)
        viewPagerAdapter.listener=this
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
            }

            override fun onPageSelected(position: Int) {
                // Check if this is the page you want.
                setDotsColor(position)
            }
        })

        /////////////////reed count/////////////////////
        arrReedCount?.forEach { arrReedCountStr.add(it.count) }
        val spReedCountAdapter =ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrReedCountStr)
        spReedCountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_reed_count.setAdapter(spReedCountAdapter)

        //////////////////////////////care//////////////////
        arrProductCare?.forEach { careSelctionList.add(Triple(it.productCareDesc ?: "", false, it.id)) }
        wash_care_recycler_list.layoutManager = LinearLayoutManager(this)
        careSelectionAdapter = CareInstructionsSelectionAdapter(this,  careSelctionList)
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
        /////////////////////////Save and Upload///////////////////////////
        txt_save_upload.setOnClickListener {saveUploadProduct() }
        txt_save_upload_top.setOnClickListener {  saveUploadProduct() }
    }

    override fun onUpdate(pairList: ArrayList<String>, deletedIds: ArrayList<String>) {
        this.pairList = pairList
        this.deletedPaths = deletedIds
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showCancelDialog()
            return true
        } else return super.onKeyDown(keyCode, event)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConstantsDirectory.PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK && null != data)
            when (requestCode) {
                ConstantsDirectory.PICK_IMAGE -> {
                    val uri = data?.data
                    if (uri != null) {
                        var absolutePath = Utility.getRealPathFromFileURI(applicationContext, uri!!)
                        pairList.add(absolutePath)
                        prodImgListAdapter.notifyDataSetChanged()
                    }
                }
            }
    }

    override fun onWeaveItemSelected(pairList: ArrayList<Triple<String, Boolean, Long>>) {
        this.weaveSelctionList = pairList
    }

    override fun onCareItemSelected(pairList: ArrayList<Triple<String, Boolean, Long>>) {
        this.careSelctionList = pairList
    }
    override fun sendYarnData(position:Int, yarnType:Long,yarnCount:String,dye:Long) {
        Log.e("Viewpager","{$position} position/yarnType :"+yarnType+" :yarnCount :"+yarnCount+" : dye :"+dye)
        when(position){
            0->{
                warpYarnId=yarnType
                warpYarnCount=yarnCount
                warpDyeId=dye
            }
            1->{
                weftYarnId=yarnType
                weftYarnCount=yarnCount
                weftDyeId=dye
            }
            2->{
                extraWeftYarnId=yarnType
                extraWeftYarnCount=yarnCount
                extraWeftDyeId=dye
            }
        }
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

    fun setVisiblitiesAndTextsOnType(type: String,arrRelatedProdType:List<ProductType>?) {
        arrProdLengthStr?.clear()
        arrProdWidthStr?.clear()
        arrSubProdLengthStr?.clear()
        arrSubProdWidthStr?.clear()
        txt_prod_type.text = type
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
            sp_prod_width.visibility=View.GONE
            et_prod_width.visibility=View.VISIBLE
        }
        else {
            sp_prod_width.visibility=View.VISIBLE
            et_prod_width.visibility=View.GONE
            val spwidthAdapter =ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrProdWidthStr)
            spwidthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sp_prod_width.setAdapter(spwidthAdapter)
        }
        if(arrProdLengthStr.size<=0){
            sp_prod_length.visibility=View.GONE
            et_prod_length.visibility=View.VISIBLE
        }
        else {
            sp_prod_length.visibility=View.VISIBLE
            et_prod_length.visibility=View.GONE
            val spLenghtAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrProdLengthStr)
            spLenghtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sp_prod_length.setAdapter(spLenghtAdapter)
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
        if (arrRelatedProdType!!.size>0) {
            //todo inflater to be called post API itegratipn
            txt_related_prod_type.visibility = View.VISIBLE
            ll_sub_prod.visibility = View.VISIBLE
            txt_related_prod_type.text = arrRelatedProdType.get(0).productDesc

            arrRelatedProdType.get(0).productLengths.forEach { arrSubProdLengthStr.add(it.length) }
            val splengthAdapter =ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,arrSubProdLengthStr )
            splengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sp_sub_prod_length.setAdapter(splengthAdapter)

            arrRelatedProdType.get(0).productWidths.forEach { arrSubProdWidthStr.add(it.width) }
            val spwidthAdapter =ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,arrSubProdWidthStr )
            spwidthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sp_sub_prod_width.setAdapter(spwidthAdapter)
//
        } else {
            txt_related_prod_type.visibility = View.GONE
            ll_sub_prod.visibility = View.GONE
        }
    }

    fun setproductAvailability(productAvalability: Boolean) {
        if (productAvalability) {
            status=1
            img_made_to_order.setBackgroundResource(R.drawable.bg_availability_unselected)
            img_in_stock.setBackgroundResource(R.drawable.bg_availability_selected)
            txt_made_to_order.setTextColor(this.resources.getColor(R.color.clickable_text_color))
            txt_available.setTextColor(this.resources.getColor(R.color.black_text))
        } else {
            status=2
            img_made_to_order.setBackgroundResource(R.drawable.bg_availability_selected)
            img_in_stock.setBackgroundResource(R.drawable.bg_availability_unselected)
            txt_made_to_order.setTextColor(this.resources.getColor(R.color.black_text))
            txt_available.setTextColor(this.resources.getColor(R.color.clickable_text_color))
        }
    }

    fun setDotsColor(position:Int){
        dots.forEach { it.setTextColor(  ContextCompat.getColor(this, R.color.darker_gray)) }
        dots.get(position).setTextColor(Color.parseColor("#009A2F"))
    }
    fun saveUploadProduct() {

        weaveIdList.clear()
        careIdList.clear()
        weaveSelctionList.forEach { if(it.second)weaveIdList?.add(it.third) }
        careSelctionList.forEach { if(it.second)careIdList?.add(it.third) }
        var width=if(arrProdWidthStr.size<=0)et_prod_width.text.toString() else sp_prod_width.selectedItem.toString()
        var length=if(arrProdLengthStr.size<=0)et_prod_length.text.toString() else sp_prod_length.selectedItem.toString()

        arrReedCount?.forEach { if(it.count.equals(sp_reed_count.selectedItem.toString()) ){reedCountId=it.id}}

        arrProductCategory?.forEach { if(it.productDesc.equals(sp_prod_category.selectedItem.toString())){
            prodCatId=it.id
            it.productTypes.forEach {
                if(it.productDesc.equals(sp_prod_type.selectedItem.toString())){
                    prodTypeId=it.id
                }
            }
        }
        }
        Log.e("saveUploadProduct","${sp_prod_category.selectedItem.toString() } ProductCatId :"+prodCatId)
        Log.e("saveUploadProduct","${sp_prod_type.selectedItem.toString() } ProductTypeId :"+prodTypeId)
//        if(arrRelatedProdType!!.size>0){
//            arrRelatedProdType?.forEach { arrListRelatedProduct.add(RelatedProduct(it.id,it.)) }
//        }
        if(pairList.isEmpty()) Utility.displayMessage("Please add atleast 1 product image",applicationContext)
        else if(et_prod_name.text.isBlank()) Utility.displayMessage("Please enter product name at step 2",applicationContext)
        else if(et_prod_code.text.isBlank()) Utility.displayMessage("Please enter product code at step 2",applicationContext)
        else if(sp_prod_category.selectedItem.toString().isBlank()) Utility.displayMessage("Please select product category at step 2",applicationContext)
        else if(sp_prod_type.selectedItem.toString().isBlank()) Utility.displayMessage("Please select product type at step 2",applicationContext)
        else if(weaveIdList.isEmpty()) Utility.displayMessage("Please select weave type at step 3",applicationContext)
        else if(warpDyeId<=0) Utility.displayMessage("Please select warp dye Id at step 4",applicationContext)
        else if(warpYarnCount.isBlank()) Utility.displayMessage("Please select warp yarn count at step 4",applicationContext)
        else if(warpYarnId<=0) Utility.displayMessage("Please select warp yarn Id at step 4",applicationContext)
        else if(weftDyeId<=0) Utility.displayMessage("Please select weft dye Id at step 4",applicationContext)
        else if(weftYarnCount.isBlank()) Utility.displayMessage("Please select weft yarn count at step 4",applicationContext)
        else if(weftYarnId<=0) Utility.displayMessage("Please select weft yarn Id at step 4",applicationContext)
        else if(sp_reed_count.selectedItem.toString().isBlank()) Utility.displayMessage("Please select reed count at step 5",applicationContext)
        else if(width.isBlank()) Utility.displayMessage("Please enter width at step 6",applicationContext)
        else if(length.isBlank()) Utility.displayMessage("Please enter length at step 6",applicationContext)
        //todo add step 6 validations for related items
        else if(careIdList.isEmpty()) Utility.displayMessage("Please select weave type at step 7",applicationContext)
        else if(et_prod_weight.text.isBlank()) Utility.displayMessage("Please enter product weight at step 9",applicationContext)
        else if(et_dscrp.text.isBlank()) Utility.displayMessage("Please enter description",applicationContext)
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

                var template= ArtisanAddProductRequest()
                template.tag=et_prod_name.text.toString()
                template.code=et_prod_code.text.toString()
                template.productCategoryId=prodCatId?:0
                template.productTypeId=prodTypeId?:0
                Log.e("saveUploadProduct","Type: ${template.productTypeId } ProductCatId ${template.productCategoryId}")
                template.productSpec=et_dscrp.text.toString()
                template.weight=et_prod_weight.text.toString()
                template.weight=et_prod_weight.text.toString()
                template.careIds=careIdList
                template.weaveIds=weaveIdList
                template.statusId=status
                template.gsm=et_gsm.text.toString()
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
                template.reedCountId=reedCountId.toString()
                template.relatedProduct=null

//             ProductPredicates.insertArtisanProductOffline(template)
//                mViewModel.uploadProduct(Gson().toJson(template), pairList)
                mViewModel.uploadProduct(template.toString(), pairList)
//                finish()
            }
         }
    }


}

//et_prod_name.text.toString(),et_prod_code.text.toString(), prodCatId!!,prodTypeId!!,et_dscrp.text.toString(),et_prod_weight.text.toString(),careIdList,weaveIdList,status,
//et_gsm.text.toString(),warpDyeId,warpYarnCount,warpYarnId,weftDyeId,weftYarnCount,weftYarnId,extraWeftYarnId,extraWeftYarnCount,extraWeftDyeId,
//width,  length, sp_reed_count.selectedItem.toString(),null

