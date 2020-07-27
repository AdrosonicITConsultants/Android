package com.adrosonic.craftexchange.ui.modules.artisan.productTemplate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
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
import quicktype.ProductUploadData

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
    private lateinit var itemSelectionAdapter: ItemSelectionAdapter
    var itemSelctionList = ArrayList<Triple<String, Boolean, Long>>()
    private val mUserConfig = UserConfig()
    var jsonProductData: String = ""
    var productUploadData: ProductUploadData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityArtisanAddProductTemplateBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        jsonProductData = mUserConfig.productUploadJson.toString()
        val gson = GsonBuilder().create()
        productUploadData = gson.fromJson(jsonProductData, ProductUploadData::class.java)

        //todo set animation
        //todo set click listener
        //todo set data


        val slideDown = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)
        val slideUp = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)
        parent_step1.setOnClickListener(this)
        parent_step1.setOnClickListener {
            if (child_step1.visibility == View.GONE) {
                child_step1.visibility = View.VISIBLE
                child_step1.animation = slideDown
            } else {
                child_step1.visibility = View.GONE
                child_step1.animation = slideUp
            }
        }
        parent_step2.setOnClickListener {
            if (child_step2.visibility == View.GONE) {
                child_step2.visibility = View.VISIBLE
                child_step2.animation = slideDown
            } else {
                child_step2.visibility = View.GONE
                child_step2.animation = slideUp
            }
        }
        parent_step3.setOnClickListener {
            if (child_step3.visibility == View.GONE) child_step3.visibility = View.VISIBLE
            else child_step3.visibility = View.GONE
        }
        parent_step4.setOnClickListener {
            if (child_step4.visibility == View.GONE) child_step4.visibility = View.VISIBLE
            else child_step4.visibility = View.GONE
        }
        parent_step5.setOnClickListener {
            if (child_step5.visibility == View.GONE) child_step5.visibility = View.VISIBLE
            else child_step5.visibility = View.GONE
        }
        parent_step7.setOnClickListener {
            if (child_step7.visibility == View.GONE) child_step7.visibility = View.VISIBLE
            else child_step7.visibility = View.GONE
        }


        add_photo_recycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
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

//        itemSelctionList.add(Triple("Weft Ikat",false,1))
//        itemSelctionList.add(Triple("LoinLoom weaving",true,2))
//        itemSelctionList.add(Triple("Extra weft jamdani",false,3))
//        itemSelctionList.add(Triple("abc",true,4))
//        itemSelctionList.add(Triple("xyz",false,5))
        productUploadData?.data?.productCare?.forEach {
            itemSelctionList.add(
                Triple(
                    it.productCareDesc ?: "", false, it.id
                )
            )
        }
        weave_recycler_list.layoutManager = LinearLayoutManager(this)

//        val dataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, productUploadData?.data?.productCategories)
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        sp_prod_category.setAdapter(dataAdapter)

        itemSelectionAdapter = ItemSelectionAdapter(this, itemSelctionList)
        itemSelectionAdapter.listener = this
        weave_recycler_list.adapter = itemSelectionAdapter
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
        this.itemSelctionList = pairList
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {

        }
    }
}
