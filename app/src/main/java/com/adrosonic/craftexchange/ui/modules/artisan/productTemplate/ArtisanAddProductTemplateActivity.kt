package com.adrosonic.craftexchange.ui.modules.artisan.productTemplate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.ActivityArtisanAddProductTemplateBinding
import com.adrosonic.craftexchange.databinding.ActivityArtisanLandingBinding
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
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
    ItemSelectionAdapter.selectionListener {
    private var mBinding: ActivityArtisanAddProductTemplateBinding? = null
    private lateinit var prodImgListAdapter: ProdImageListAdapter
    private var pairList = ArrayList<String>()
    private var deletedPaths = ArrayList<String>()
    private lateinit var careSelectionAdapter: ItemSelectionAdapter
    private lateinit var weaveSelectionAdapter: ItemSelectionAdapter
    var careSelctionList = ArrayList<Triple<String, Boolean, Long>>()
    var weaveSelctionList = ArrayList<Triple<String, Boolean, Long>>()

    private val mUserConfig = UserConfig()
    var jsonProductData: String = ""
    var productUploadData: ProductUploadData? = null
    var arrProductCategory:List<ProductCategory>?=null
    var arrProductType: List<ProductType>?=null
    var arrRelatedProdType: List<ProductType>?=null
    var arrProductCare: List<ProductCare>?=null
    var arrWeaf: List<Weaf>?=null
    var arrYarn: List<Yarn>?=null
    var arrReedCount: List<ReedCount>?=null
    var arrDyes: List<Dye>?=null

    var arrProdCategoryStr= ArrayList<String>()
    var arrProdTypeStr= ArrayList<String>()
//    private var parentViewsList=ArrayList<LinearLayout>()
//    private var childViewsList=ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArtisanAddProductTemplateBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        jsonProductData = mUserConfig.productUploadJson.toString()
        val gson = GsonBuilder().create()
        productUploadData = gson.fromJson(jsonProductData, ProductUploadData::class.java)
        arrProductCategory= productUploadData?.data?.productCategories
        arrProductCare=productUploadData?.data?.productCare
        arrWeaf=productUploadData?.data?.weaves
        arrYarn=productUploadData?.data?.yarns
        arrReedCount=productUploadData?.data?.reedCounts
        arrDyes=productUploadData?.data?.dyes

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


        add_photo_recycler.layoutManager =LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
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

        arrWeaf?.forEach { weaveSelctionList.add( Triple(it.weaveDesc ?: "", false, it.id   ) )  }
        weave_recycler_list.layoutManager = LinearLayoutManager(this)
        weaveSelectionAdapter = ItemSelectionAdapter(this, weaveSelctionList)
        weaveSelectionAdapter.listener = this
        weave_recycler_list.adapter = weaveSelectionAdapter

        //////////////////////////////product care//////////////////
        arrProductCare?.forEach { careSelctionList.add( Triple(it.productCareDesc ?: "", false, it.id   ) )  }
        wash_care_recycler_list.layoutManager = LinearLayoutManager(this)
        careSelectionAdapter = ItemSelectionAdapter(this, careSelctionList)
        careSelectionAdapter.listener = this
        weave_recycler_list.adapter = careSelectionAdapter

        ///////////general details//////////////
        arrProductCategory?.forEach {  arrProdCategoryStr.add(it.productDesc)}
        val spProdCataAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,arrProdCategoryStr)
        spProdCataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_prod_category.setAdapter(spProdCataAdapter)
        sp_prod_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) { }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                arrProdTypeStr?.clear()
               val prodCategory=arrProdCategoryStr.get(position)
               Log.e("Spinner","prodCategory :"+prodCategory)
               for(category in arrProductCategory!!){
                   if(category.productDesc.equals(prodCategory,true)){
                       Log.e("Spinner","prodCategory 111111111: "+category.productTypes.size)
                       category.productTypes.forEach { arrProdTypeStr?.add(it.productDesc)  }
                       Log.e("Spinner","prodCategory 2222222: "+arrProdTypeStr?.joinToString())
                   }
               }
                val spProdTypeAdapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item,arrProdTypeStr)
                spProdTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sp_prod_type.setAdapter(spProdTypeAdapter)
            }

        }

        /////////////////reed count/////////////////////
        arrReedCount?.forEach {  arrProdCategoryStr.add(it.productDesc)}
        val spProdCataAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,arrProdCategoryStr)
        spProdCataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sp_prod_category.setAdapter(spProdCataAdapter)
    }

    override fun onUpdate(pairList: ArrayList<String>, deletedIds: ArrayList<String>) {
        this.pairList = pairList
        this.deletedPaths = deletedIds
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

    override fun onItemSelected(pairList: ArrayList<Triple<String, Boolean, Long>>) {
        this.weaveSelctionList = pairList
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
}
