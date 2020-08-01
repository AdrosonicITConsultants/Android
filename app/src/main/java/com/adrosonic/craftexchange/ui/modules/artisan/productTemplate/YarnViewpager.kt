package com.adrosonic.craftexchange.ui.modules.artisan.productTemplate

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
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
import com.adrosonic.craftexchange.utils.Utility
import kotlinx.android.synthetic.main.activity_artisan_add_product_template.*


class YarnViewpager(context: Context, arrYarn:List<Yarn>?, arrDye:List<Dye>?):PagerAdapter() {

    private var context: Context? = null
    private var arrYarn: List<Yarn>? = null
    private var arrYarnCount: List<YarnCount>? = null
    private var arrDye: List<Dye>? = null
    private var layoutInflater: LayoutInflater? = null
    private var layouts =arrayOf<Int>(0, 1, 2)
    var arrYarneStr = ArrayList<String>()
    var arrYarnCountStr = ArrayList<String>()
    var arrDyeStr = ArrayList<String>()
    var listener: YarnViewpager.yarnListner? = null
    interface yarnListner{
        fun sendYarnData(position:Int, yarnType:Long,yarnCount:String,dye:Long)
    }
    init {
        this.context=context
        this.arrYarn=arrYarn
        this.arrDye=arrDye
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view== `object`
    }

    override fun getCount(): Int {
        return layouts.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater =context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = layoutInflater!!.inflate(R.layout.warp_weft_yarn, null)

        val imgYarn = view.findViewById<View>(R.id.img_yarn) as ImageView
        val yarnTitle = view.findViewById<View>(R.id.txt_yarn_title) as TextView
        val spYarnType = view.findViewById<View>(R.id.sp_yarn_type) as Spinner
        val spYarnCount = view.findViewById<View>(R.id.sp_yarn_count) as Spinner
        val etYarnCount = view.findViewById<View>(R.id.et_yarn_count) as EditText
        val spYarnDye = view.findViewById<View>(R.id.sp_yarn_dye) as Spinner

        arrYarn?.forEach { arrYarneStr.add(it.yarnDesc) }
        val spYarnAdapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item, arrYarneStr)
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
                for (category in arrYarn!!) {
                    if (category.yarnDesc.equals(yarnType, true)) {
                        arrYarnCount = category.yarnType.yarnCounts
//                        category.productTypes.forEach { arrProdTypeStr?.add(it.productDesc) }
                    }
                }
                if(arrYarnCount!!.size>0) {
                    etYarnCount.visibility=View.GONE
                    arrYarnCount?.forEach { arrYarnCountStr.add(it.count) }
                    val spProdTypeAdapter = ArrayAdapter<String>(  context!!, android.R.layout.simple_spinner_item, arrYarnCountStr )
                    spProdTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spYarnCount.setAdapter(spProdTypeAdapter)
                }else{
                    etYarnCount.visibility=View.VISIBLE
                    spYarnCount.visibility=View.GONE
                }
                if(etYarnCount.visibility==View.VISIBLE)callListener(position,spYarnType.selectedItem.toString(),etYarnCount.text.toString(),spYarnDye.selectedItem.toString())
                else callListener(position,spYarnType.selectedItem.toString(),spYarnCount.selectedItem.toString(),spYarnDye.selectedItem.toString())

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
                if(etYarnCount.visibility==View.VISIBLE)callListener(position,spYarnType.selectedItem.toString(),etYarnCount.text.toString(),spYarnDye.selectedItem.toString())
                else callListener(position,spYarnType.selectedItem.toString(),spYarnCount.selectedItem.toString(),spYarnDye.selectedItem.toString())
            }

        }

        arrDye?.forEach { arrDyeStr.add(it.dyeDesc) }
        val spDyeAdapter =ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item, arrDyeStr)
        spDyeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spYarnDye.setAdapter(spDyeAdapter)
        spYarnDye.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(p0: AdapterView<*>?) {

        }
        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            if(etYarnCount.visibility==View.VISIBLE)callListener(position,spYarnType.selectedItem.toString(),etYarnCount.text.toString(),spYarnDye.selectedItem.toString())
            else callListener(position,spYarnType.selectedItem.toString(),spYarnCount.selectedItem.toString(),spYarnDye.selectedItem.toString())
        }
    }

        etYarnCount.addTextChangedListener (object : TextWatcher {
            override fun onTextChanged(expr: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                callListener(position,spYarnType.selectedItem.toString(),etYarnCount.text.toString(),spYarnDye.selectedItem.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        when(position){
            0->{
                yarnTitle.text="Warp "
                Utility.setImageResource(context,imgYarn,R.drawable.ic_warp_icon)

            }
            1->{
                yarnTitle.text="Weft "
                Utility.setImageResource(context,imgYarn,R.drawable.ic_weft_icon)
            }
            2->{
                yarnTitle.text="Extra Weft (Optional) "
                Utility.setImageResource(context,imgYarn,R.drawable.ic_extraweft_icon)
            }
        }
        val vp = container as ViewPager
        vp.addView(view, position)
        return view
    }

    fun callListener(position:Int, yarnType:String,yarnCount:String,dye:String){

        var yarnTypeId=0L
        var dyeId=0L
        arrYarn?.forEach { if(it.yarnDesc.equals(yarnType))yarnTypeId=it.id }
        arrDye?.forEach { if(it.dyeDesc.equals(dye))dyeId=it.id }
        listener?.sendYarnData(position,yarnTypeId,yarnCount,dyeId)
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        super.destroyItem(container, position, `object`)
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }


}