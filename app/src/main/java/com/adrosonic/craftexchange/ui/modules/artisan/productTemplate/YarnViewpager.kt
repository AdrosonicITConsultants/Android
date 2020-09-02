package com.adrosonic.craftexchange.ui.modules.artisan.productTemplate

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.BuyerCustomProduct
import com.adrosonic.craftexchange.database.predicates.BuyerCustomProductPredicates
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.uploadData.Dye
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.uploadData.ProductUploadData
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.uploadData.Yarn
import com.adrosonic.craftexchange.repository.data.response.artisan.productTemplate.uploadData.YarnCount
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.google.gson.GsonBuilder
import java.lang.Exception


class YarnViewpager(context: Context, productId: Long,isTemplate:Boolean) : PagerAdapter()
//    , ViewPager.OnPageChangeListener
{
    var yarnType=0L
    var yarnCount=""
    var dyeId=0L
    private val mUserConfig = UserConfig()
    var jsonProductData: String = ""
    var productUploadData: ProductUploadData? = null
    lateinit var imgYarn : ImageView
    lateinit var yarnTitle : TextView
    lateinit var spYarnType: Spinner
    lateinit var spYarnCount : Spinner
    lateinit var etYarnCount: EditText
    lateinit var spYarnDye : Spinner
    private var isTemplate: Boolean? = null
    private var context: Context? = null
    private var productId: Long = 0
    private var arrYarn: List<Yarn>? = null
    private var arrYarnCount: List<YarnCount>? = null
    private var arrDye: List<Dye>? = null
    private var layoutInflater: LayoutInflater? = null
    private var layouts = arrayOf<Int>(0, 1, 2)
    var arrYarneStr = ArrayList<String>()
    var arrYarnCountStr = ArrayList<String>()
    var arrDyeStr = ArrayList<String>()
    var listener: YarnViewpager.yarnListner? = null
    var artisanProductEntry: ArtisanProducts?=null
    var buyerProductEntry: BuyerCustomProduct?=null
    var typeSelection=""
    var dyeSelection=""

    interface yarnListner {
        fun sendYarnData(position: Int, yarnType: Long, yarnCount: String, dye: Long)
    }

    init {
        this.context = context
        this.productId = productId
        this.isTemplate = isTemplate
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return layouts.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater!!.inflate(R.layout.warp_weft_yarn, null)
        imgYarn = view.findViewById<View>(R.id.img_yarn) as ImageView
        yarnTitle = view.findViewById<View>(R.id.txt_yarn_title) as TextView
        spYarnType = view.findViewById<View>(R.id.sp_yarn_type) as Spinner
        spYarnCount = view.findViewById<View>(R.id.sp_yarn_count) as Spinner
        etYarnCount = view.findViewById<View>(R.id.et_yarn_count) as EditText
        spYarnDye = view.findViewById<View>(R.id.sp_yarn_dye) as Spinner

        jsonProductData = mUserConfig.productUploadJson.toString()
        val gson = GsonBuilder().create()
        productUploadData = gson.fromJson(jsonProductData, ProductUploadData::class.java)
        arrYarn = productUploadData?.data?.yarns
        arrDye = productUploadData?.data?.dyes

        Log.e("YarnPager","position : $position")
        arrYarneStr.clear()
        arrYarneStr.add("Select type")
        arrYarnCountStr.add("Select count")
        arrYarn?.forEach { arrYarneStr.add(it.yarnDesc) }
        val spYarnAdapter =ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item, arrYarneStr)
        spYarnAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spYarnType.adapter = spYarnAdapter

        val spProdTypeAdapter = ArrayAdapter<String>(view.context, android.R.layout.simple_spinner_item, arrYarnCountStr)
        spProdTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spYarnCount.adapter = spProdTypeAdapter

        spYarnType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                arrYarnCountStr.clear()
                val yarnType = arrYarneStr.get(pos)
                if (pos > 0) {
                    arrYarn?.forEach {
                        if (it.yarnDesc.equals(yarnType, true)) arrYarnCount =  it.yarnType.yarnCounts
                    }
                    Log.e("Viewpager", "arrYarnCount: ${arrYarnCount?.size}")
                    if(arrYarnCount!=null) {
                        if (arrYarnCount!!.size > 0) {
                        spYarnCount.visibility = View.VISIBLE
                        etYarnCount.visibility = View.GONE
                        arrYarnCountStr.clear()
                        arrYarnCountStr.add("Select count")
                        arrYarnCount?.forEach { arrYarnCountStr.add(it.count) }
                        Log.e("Viewpager", "arrYarnCountStr 1111: ${arrYarnCountStr.size}")
//                        val spProdTypeAdapter = ArrayAdapter<String>(view!!.context, android.R.layout.simple_spinner_item, arrYarnCountStr)
//                        spProdTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                        spYarnCount.setAdapter(spProdTypeAdapter)
                            spProdTypeAdapter.notifyDataSetChanged()
                        if(productId>0){
                            spYarnCount.setSelection(arrYarnCountStr.indexOf(yarnCount))
                        }
                    } else {
                        Log.e("Viewpager", "arrYarnCountStr 2222: ${arrYarnCountStr.size}")
                        etYarnCount.visibility = View.VISIBLE
                        spYarnCount.visibility = View.GONE
                    }
                    }else {
                        etYarnCount.visibility = View.VISIBLE
                        spYarnCount.visibility = View.GONE
                    }

                      callListener(position)
                }
            }
        }
        spYarnCount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                    callListener(position)
            }

        }

        arrDyeStr.clear()
        arrDyeStr.add("Select dye")
        arrDye?.forEach { arrDyeStr.add(it.dyeDesc) }
        val spDyeAdapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item, arrDyeStr)
        spDyeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spYarnDye.adapter = spDyeAdapter
        spYarnDye.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                callListener(position)
            }
        }
        etYarnCount.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(expr: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                callListener(position)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })


        when (position) {
            0 -> {
                yarnTitle.text = "Warp "
                Utility.setImageResource(context, imgYarn, R.drawable.ic_warp_icon)
            }
            1 -> {
                yarnTitle.text = "Weft "
                Utility.setImageResource(context, imgYarn, R.drawable.ic_weft_icon)
            }
            2 -> {
                yarnTitle.text = "Extra Weft (Optional) "
                Utility.setImageResource(context, imgYarn, R.drawable.ic_extraweft_icon)
            }
        }

        if(productId>0){
          if(isTemplate!!){
              artisanProductEntry= ProductPredicates.getArtisanProductsByRemoteId(productId)
              when(position) {
                  0 -> {
                       yarnType=artisanProductEntry?.warpYarnId?:0
                       yarnCount=artisanProductEntry?.warpYarnCount?:""
                       dyeId=artisanProductEntry?.warpDyeId?:0
                  }
                  1 -> {
                      yarnType=artisanProductEntry?.weftYarnId?:0
                      yarnCount=artisanProductEntry?.weftYarnCount?:""
                      dyeId=artisanProductEntry?.weftDyeId?:0
                  }
                  2 -> {
                      yarnType=artisanProductEntry?.extraWeftYarnId?:0
                      yarnCount=artisanProductEntry?.extraWeftYarnCount?:""
                      dyeId=artisanProductEntry?.extraWeftDyeId?:0
                  }
              }
          }
          else{
              buyerProductEntry= BuyerCustomProductPredicates.getCustomProductFormRemotId(productId)
              when(position) {
                  0 -> {
                      yarnType=buyerProductEntry?.warpYarnId?:0
                      yarnCount=buyerProductEntry?.warpYarnCount?:""
                      dyeId=buyerProductEntry?.warpDyeId?:0
                  }
                  1 -> {
                      yarnType=buyerProductEntry?.weftYarnId?:0
                      yarnCount=buyerProductEntry?.weftYarnCount?:""
                      dyeId=buyerProductEntry?.weftDyeId?:0
                  }
                  2 -> {
                      yarnType=buyerProductEntry?.extraWeftYarnId?:0
                      yarnCount=buyerProductEntry?.extraWeftYarnCount?:""
                      dyeId=buyerProductEntry?.extraWeftDyeId?:0
                  }
              }
          }
          arrYarn?.forEach {if(it.id.equals(yarnType))typeSelection=it.yarnDesc }
          spYarnType.setSelection(arrYarneStr.indexOf(typeSelection))

          etYarnCount.setText(yarnCount, TextView.BufferType.EDITABLE)
          spYarnCount.setSelection(arrYarnCountStr.indexOf(yarnCount))

          arrDye?.forEach { if(it.id.equals(dyeId))dyeSelection=it.dyeDesc  }
          spYarnDye.setSelection(arrDyeStr.indexOf(dyeSelection))

          callListener(position)
        }
        val vp = container as ViewPager
        vp.addView(view, position)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }

    override fun getItemPosition(`object`: Any): Int {
        return super.getItemPosition(`object`)
    }

    fun callListener(position: Int) {
        var yarnTypeId = 0L
        var dyeId = 0L
        var yarnCnt=""

        var type= try{ spYarnType.selectedItem.toString()}catch (e:Exception){""}
        var count=try{if(etYarnCount.visibility==View.VISIBLE) etYarnCount.text.toString()  else spYarnCount.selectedItem.toString()}catch (e:Exception){""}
        var dye=try{spYarnDye.selectedItem.toString()}catch (e:Exception){""}

        Log.e("Viewpager", "Type: $type, count: $count , dye: $dye")
        arrYarn?.forEach { if (it.yarnDesc.equals(type)) yarnTypeId = it.id }
        arrDye?.forEach { if (it.dyeDesc.equals(dye)) dyeId = it.id }
        if(!count.equals("Select count"))yarnCnt=count
        listener?.sendYarnData(position, yarnTypeId, yarnCnt, dyeId)
    }

}