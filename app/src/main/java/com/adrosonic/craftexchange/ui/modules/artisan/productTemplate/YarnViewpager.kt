package com.adrosonic.craftexchange.ui.modules.artisan.productTemplate

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.repository.data.response.artisan.products.productTemplate.uploadData.Dye
import com.adrosonic.craftexchange.repository.data.response.artisan.products.productTemplate.uploadData.Yarn
import com.adrosonic.craftexchange.repository.data.response.artisan.products.productTemplate.uploadData.YarnCount
import com.adrosonic.craftexchange.ui.modules.buyer.productDetails.productId
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import kotlinx.android.synthetic.main.activity_artisan_add_product_template.*
import kotlinx.android.synthetic.main.activity_buyer_add_own_product_design.*


class YarnViewpager(context: Context, productId: Long, arrYarn: List<Yarn>?, arrDye: List<Dye>?) : PagerAdapter()
//    , ViewPager.OnPageChangeListener
{
    var currentPosition=0
    lateinit var imgYarn : ImageView
    lateinit var yarnTitle : TextView
    lateinit var spYarnType: Spinner
    lateinit var spYarnCount : Spinner
    lateinit var etYarnCount: EditText
    lateinit var spYarnDye : Spinner
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
    private val userConfig = UserConfig()

    interface yarnListner {
        fun sendYarnData(position: Int, yarnType: Long, yarnCount: String, dye: Long)
    }

    init {
        this.context = context
        this.productId = productId
        this.arrYarn = arrYarn
        this.arrDye = arrDye
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
        Log.e("YarnPager","position : $position")
        arrYarneStr.clear()
        arrYarn?.forEach { arrYarneStr.add(it.yarnDesc) }
        val spYarnAdapter =ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item, arrYarneStr)
        spYarnAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spYarnType.setAdapter(spYarnAdapter)
        spYarnType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                arrYarnCountStr?.clear()
                val yarnType = arrYarneStr.get(position)
                Log.e("Viewpager","yarnType: $yarnType")
                arrYarn?.forEach {
                    if (it.yarnDesc.equals(yarnType, true)) arrYarnCount = it.yarnType.yarnCounts
                 }
                Log.e("Viewpager","arrYarnCount: ${arrYarnCount?.size}")
                if (arrYarnCount!!.size > 0) {
                    etYarnCount.visibility = View.GONE
                    arrYarnCount?.forEach { arrYarnCountStr.add(it.count) }
                    Log.e("Viewpager","arrYarnCountStr 1111: ${arrYarnCountStr?.size}")
                    val spProdTypeAdapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item, arrYarnCountStr)
                    spProdTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spYarnCount.setAdapter(spProdTypeAdapter)
                } else {
                    Log.e("Viewpager","arrYarnCountStr 2222: ${arrYarnCountStr?.size}")
                    etYarnCount.visibility = View.VISIBLE
                    spYarnCount.visibility = View.GONE
                }
                Log.e("Viewpager","arrYarnCount: ${arrYarnCount?.size}")
                if (etYarnCount.visibility == View.VISIBLE) callListener(
                    position,
                    spYarnType.selectedItem.toString(),
                    etYarnCount.text.toString(),
                    spYarnDye.selectedItem.toString()
                )
                else callListener(
                    position,
                    spYarnType.selectedItem.toString(),
                    spYarnCount.selectedItem.toString(),
                    spYarnDye.selectedItem.toString()
                )
                Log.e("Viewpager","yarnType: $yarnType")
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
                if (etYarnCount.visibility == View.VISIBLE) callListener(
                    position,
                    spYarnType.selectedItem.toString(),
                    etYarnCount.text.toString(),
                    spYarnDye.selectedItem.toString()
                )
                else callListener(
                    position,
                    spYarnType.selectedItem.toString(),
                    spYarnCount.selectedItem.toString(),
                    spYarnDye.selectedItem.toString()
                )
            }

        }

        arrDyeStr.clear()
        arrDye?.forEach { arrDyeStr.add(it.dyeDesc) }
        val spDyeAdapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item, arrDyeStr)
        spDyeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spYarnDye.setAdapter(spDyeAdapter)
        spYarnDye.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (etYarnCount.visibility == View.VISIBLE) callListener(
                    position,
                    spYarnType.selectedItem.toString(),
                    etYarnCount.text.toString(),
                    spYarnDye.selectedItem.toString()
                )
                else callListener(
                    position,
                    spYarnType.selectedItem.toString(),
                    spYarnCount.selectedItem.toString(),
                    spYarnDye.selectedItem.toString()
                )
            }
        }
        etYarnCount.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(expr: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                callListener(
                    position,
                    spYarnType.selectedItem.toString(),
                    etYarnCount.text.toString(),
                    spYarnDye.selectedItem.toString()
                )
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })


        when (position) {
            0 -> {
                yarnTitle.text = "Warp "
                Utility.setImageResource(context, imgYarn, R.drawable.ic_warp_icon)
                if(productId>0) {
                    if (userConfig.warpYarnId!! > 0) {
                        var yarnType = ""
                        arrYarn?.forEach {
                            if (it.id.equals(userConfig.warpDyeId)) yarnType = it.yarnDesc
                        }
                        spYarnType.setSelection(arrYarneStr.indexOf(yarnType))
                    }
                    if (userConfig.warpYarnCount!!.isNotBlank()) {
                        var yarnCount = userConfig.warpYarnCount
                        arrYarnCount?.forEach {
                            arrYarnCountStr.add(it.count)
                            if (it.count.equals(userConfig.warpYarnCount)) yarnCount = it.count
                        }
                        if (arrYarnCountStr != null) {
                            if (arrYarnCountStr!!.size > 0) spYarnCount.setSelection( arrYarnCountStr!!.indexOf(  yarnCount ))
                            else etYarnCount.setText(yarnCount, TextView.BufferType.EDITABLE)
                        } else etYarnCount.setText(yarnCount, TextView.BufferType.EDITABLE)
                    }
                    if (userConfig.warpDyeId!! > 0) {
                        var dyeTypes = ""
                        arrDye?.forEach {
                            if (it.id.equals(userConfig.warpDyeId)) dyeTypes = it.dyeDesc
                        }
                        spYarnDye.setSelection(arrDyeStr.indexOf(dyeTypes))
                    }
                }
            }
            1 -> {
                yarnTitle.text = "Weft "
                Utility.setImageResource(context, imgYarn, R.drawable.ic_weft_icon)
                if(productId>0) {
                    if (userConfig.weftDyeId!! > 0) {
                        var yarnType = ""
                        arrYarn?.forEach {
                            if (it.id.equals(userConfig.weftDyeId)) yarnType = it.yarnDesc
                        }
                        spYarnType.setSelection(arrYarneStr.indexOf(yarnType))
                    }
                    if (userConfig.weftYarnCount!!.isNotBlank()) {
                        var yarnCount = userConfig.weftYarnCount
                        arrYarnCount?.forEach {
                            if (it.count.equals(userConfig.weftYarnCount)) yarnCount = it.count
                        }
                        if (arrYarnCount != null)
                            if (arrYarnCount!!.size > 0) spYarnCount.setSelection(
                                arrYarnCountStr!!.indexOf(
                                    yarnCount
                                )
                            )
                            else etYarnCount.setText(yarnCount, TextView.BufferType.EDITABLE)
                    }
                    if (userConfig.weftYarnId!! > 0) {
                        var dyeTypes = ""
                        arrDye?.forEach {
                            if (it.id.equals(userConfig.weftYarnId)) dyeTypes = it.dyeDesc
                        }
                        spYarnDye.setSelection(arrDyeStr.indexOf(dyeTypes))
                    }
                }
            }
            2 -> {
                yarnTitle.text = "Extra Weft (Optional) "
                if(productId>0) {
                    Utility.setImageResource(context, imgYarn, R.drawable.ic_extraweft_icon)
                    if (userConfig.extraWeftDyeId!! > 0) {
                        var yarnType = ""
                        arrYarn?.forEach {
                            if (it.id.equals(userConfig.extraWeftDyeId)) yarnType = it.yarnDesc
                        }
                        spYarnType.setSelection(arrYarneStr.indexOf(yarnType))
                    }
                    if (userConfig.extraWeftYarnCount!!.isNotBlank()) {
                        var yarnCount = ""
                        arrYarnCount?.forEach {
                            if (it.count.equals(userConfig.extraWeftYarnCount)) yarnCount = it.count
                        }
                        if (arrYarnCount != null)
                            if (arrYarnCount!!.size > 0) spYarnCount.setSelection(
                                arrYarnCountStr!!.indexOf(
                                    yarnCount
                                )
                            )
                            else etYarnCount.setText(yarnCount, TextView.BufferType.EDITABLE)
                    }
                    if (userConfig.extraWeftYarnId!! > 0) {
                        var dyeTypes = ""
                        arrDye?.forEach {
                            if (it.id.equals(userConfig.extraWeftYarnId)) dyeTypes = it.dyeDesc
                        }
                        spYarnDye.setSelection(arrDyeStr.indexOf(dyeTypes))
                    }
                }
            }
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
    fun callListener(position: Int, yarnType: String, yarnCount: String, dye: String) {
        var yarnTypeId = 0L
        var dyeId = 0L
        arrYarn?.forEach { if (it.yarnDesc.equals(yarnType)) yarnTypeId = it.id }
        arrDye?.forEach { if (it.dyeDesc.equals(dye)) dyeId = it.id }

        when (position) {
            0 -> {
                //warp
                userConfig.warpDyeId = dyeId
                userConfig.warpYarnCount = yarnCount
                userConfig.warpYarnId = yarnTypeId
            }
            1 -> {
                //weft
                userConfig.weftDyeId = dyeId
                userConfig.weftYarnCount = yarnCount
                userConfig.weftYarnId = yarnTypeId
            }
            2 -> {
                //extra weft
                userConfig.extraWeftDyeId = dyeId
                userConfig.extraWeftYarnCount = yarnCount
                userConfig.extraWeftYarnId = yarnTypeId
            }
        }
        listener?.sendYarnData(position, yarnTypeId, yarnCount, dyeId)
    }

    fun setSelections(position: Int) {
        arrYarn?.forEach {
            if (it.yarnDesc.equals(spYarnType.selectedItem.toString(), true)) {
                arrYarnCount = it.yarnType.yarnCounts
            } }
        when (position) {
            0 -> {
                //warp
                if(userConfig.warpYarnId!!>0 ){
                    var yarnType=""
                    arrYarn?.forEach {if(it.id.equals(userConfig.warpDyeId))yarnType=it.yarnDesc}
                    spYarnType.setSelection(arrYarneStr.indexOf(yarnType))
                }
                if(userConfig.warpYarnCount!!.isNotBlank()) {
                    var yarnCount=""
                    arrYarnCount?.forEach { if(it.count.equals(userConfig.warpYarnCount))yarnCount=it.count }
                    if(arrYarnCount!!.size>0)spYarnCount.setSelection(arrYarnCountStr!!.indexOf(yarnCount))
                    else etYarnCount.setText(yarnCount,TextView.BufferType.EDITABLE)
                }
                if(userConfig.warpDyeId!!>0) {
                    var dyeTypes=""
                    arrDye?.forEach { if(it.id.equals(userConfig.warpDyeId))dyeTypes=it.dyeDesc}
                    spYarnDye.setSelection(arrDyeStr.indexOf(dyeTypes))
                }
            }
            1 -> {
                //weft
                if(userConfig.weftDyeId!!>0 ){
                    var yarnType=""
                    arrYarn?.forEach {if(it.id.equals(userConfig.weftDyeId))yarnType=it.yarnDesc}
                    spYarnType.setSelection(arrYarneStr.indexOf(yarnType))
                }
                if(userConfig.weftYarnCount!!.isNotBlank()) {
                    var yarnCount=""
                    arrYarnCount?.forEach { if(it.count.equals(userConfig.weftYarnCount))yarnCount=it.count }
                    if(arrYarnCount!!.size>0)spYarnCount.setSelection(arrYarnCountStr!!.indexOf(yarnCount))
                    else etYarnCount.setText(yarnCount,TextView.BufferType.EDITABLE)
                }
                if(userConfig.weftYarnId!!>0) {
                    var dyeTypes=""
                    arrDye?.forEach { if(it.id.equals(userConfig.weftYarnId))dyeTypes=it.dyeDesc}
                    spYarnDye.setSelection(arrDyeStr.indexOf(dyeTypes))
                }
            }
            2 -> {
                //extra weft
                if(userConfig.extraWeftDyeId!!>0 ){
                    var yarnType=""
                    arrYarn?.forEach {if(it.id.equals(userConfig.extraWeftDyeId))yarnType=it.yarnDesc}
                    spYarnType.setSelection(arrYarneStr.indexOf(yarnType))
                }
                if(userConfig.extraWeftYarnCount!!.isNotBlank()) {
                    var yarnCount=""
                    arrYarnCount?.forEach { if(it.count.equals(userConfig.extraWeftYarnCount))yarnCount=it.count }
                    if(arrYarnCount!!.size>0)spYarnCount.setSelection(arrYarnCountStr!!.indexOf(yarnCount))
                    else etYarnCount.setText(yarnCount,TextView.BufferType.EDITABLE)
                }
                if(userConfig.extraWeftYarnId!!>0) {
                    var dyeTypes=""
                    arrDye?.forEach { if(it.id.equals(userConfig.extraWeftYarnId))dyeTypes=it.dyeDesc}
                    spYarnDye.setSelection(arrDyeStr.indexOf(dyeTypes))
                }
            }
        }
    }

//    override fun onPageScrollStateChanged(state: Int) {
//    }
//
//    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//    }
//
//    override fun onPageSelected(position: Int) {
//        currentPosition=position
//    }
}