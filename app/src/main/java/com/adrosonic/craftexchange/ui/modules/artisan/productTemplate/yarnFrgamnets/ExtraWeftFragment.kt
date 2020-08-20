package com.adrosonic.craftexchange.ui.modules.artisan.productTemplate.yarnFrgamnets

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchange.database.entities.realmEntities.BuyerCustomProduct
import com.adrosonic.craftexchange.database.predicates.BuyerCustomProductPredicates
import com.adrosonic.craftexchange.database.predicates.ProductPredicates
import com.adrosonic.craftexchange.repository.data.response.artisan.products.productTemplate.uploadData.Dye
import com.adrosonic.craftexchange.repository.data.response.artisan.products.productTemplate.uploadData.ProductUploadData
import com.adrosonic.craftexchange.repository.data.response.artisan.products.productTemplate.uploadData.Yarn
import com.adrosonic.craftexchange.repository.data.response.artisan.products.productTemplate.uploadData.YarnCount
import com.adrosonic.craftexchange.utils.UserConfig
import com.adrosonic.craftexchange.utils.Utility
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.warp_weft_yarn.view.*
import java.lang.Exception


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ExtraWeftFragment : Fragment() {
    interface ExtraWeftFragmentListner {
        fun sendYarnEweftData(yarnType: Long, yarnCount: String, dye: Long)
    }
    private var productId: Long? = 0
    private var isTemplate: Boolean? = true

    var listener: ExtraWeftFragmentListner? = null
    private var arrYarn: List<Yarn>? = null
    private var arrYarnCount: List<YarnCount>? = null
    private var arrDye: List<Dye>? = null
    var yarnType=0L
    var yarnCount=""
    var dyeId=0L
    private val mUserConfig = UserConfig()
    var jsonProductData: String = ""
    var productUploadData: ProductUploadData? = null

    var arrYarneStr = ArrayList<String>()
    var arrYarnCountStr = ArrayList<String>()
    var arrDyeStr = ArrayList<String>()

    lateinit var imgYarn : ImageView
    lateinit var yarnTitle : TextView
    lateinit var optionalText : TextView
    lateinit var spYarnType: Spinner
    lateinit var spYarnCount : Spinner
    lateinit var etYarnCount: EditText
    lateinit var spYarnDye : Spinner

    var artisanProductEntry: ArtisanProducts?=null
    var buyerProductEntry: BuyerCustomProduct?=null

    var typeSelection=""
    var dyeSelection=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            productId = it.getLong(ARG_PARAM1)
            isTemplate = it.getBoolean(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view=inflater.inflate(R.layout.warp_weft_yarn, container, false)
        imgYarn = view.findViewById<View>(R.id.img_yarn) as ImageView
        yarnTitle = view.findViewById<View>(R.id.txt_yarn_title) as TextView
        optionalText = view.findViewById<View>(R.id.txt_optional) as TextView
        spYarnType = view.findViewById<View>(R.id.sp_yarn_type) as Spinner
        spYarnCount = view.findViewById<View>(R.id.sp_yarn_count) as Spinner
        etYarnCount = view.findViewById<View>(R.id.et_yarn_count) as EditText
        spYarnDye = view.findViewById<View>(R.id.sp_yarn_dye) as Spinner

        yarnTitle.text = "Extra weft  "
        optionalText.visibility=View.VISIBLE
        Utility.setImageResource(context, imgYarn, R.drawable.ic_extraweft_icon)

        jsonProductData = mUserConfig.productUploadJson.toString()
        val gson = GsonBuilder().create()
        productUploadData = gson.fromJson(jsonProductData, ProductUploadData::class.java)
        arrYarn = productUploadData?.data?.yarns
        arrDye = productUploadData?.data?.dyes

        arrYarneStr.clear()
        arrYarneStr.add("Select type")
        arrYarnCountStr.clear()
        arrYarnCountStr.add("Select count")
        arrYarn?.forEach { arrYarneStr.add(it.yarnDesc) }
        val spYarnAdapter = ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_spinner_item, arrYarneStr)
        spYarnAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spYarnType.setAdapter(spYarnAdapter)

        val spCountAdapter = ArrayAdapter<String>(this.requireContext(), android.R.layout.simple_spinner_item, arrYarnCountStr)
        spCountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spYarnCount.setAdapter(spCountAdapter)

        spYarnType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                arrYarnCountStr?.clear()
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
                            spCountAdapter.notifyDataSetChanged()
                            if(productId!!>0){
                                spYarnCount.setSelection(arrYarnCountStr.indexOf(yarnCount))
                            }
                        } else {
                            etYarnCount.visibility = View.VISIBLE
                            spYarnCount.visibility = View.GONE
                        }
                    }else {
                        etYarnCount.visibility = View.VISIBLE
                        spYarnCount.visibility = View.GONE
                    }
                    callListener()
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
                callListener()
            }
        }

        arrDyeStr.clear()
        arrDyeStr.add("Select dye")
        arrDye?.forEach { arrDyeStr.add(it.dyeDesc) }
        val spDyeAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, arrDyeStr)
        spDyeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spYarnDye.setAdapter(spDyeAdapter)
        spYarnDye.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                callListener()
            }
        }
        etYarnCount.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(expr: CharSequence?, p1: Int, p2: Int, p3: Int) {
                callListener()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        if(productId!!>0) {
            if (isTemplate!!) {
                artisanProductEntry = ProductPredicates.getArtisanProductsByRemoteId(productId)
                yarnType = artisanProductEntry?.extraWeftYarnId ?: 0
                yarnCount = artisanProductEntry?.extraWeftYarnCount ?: ""
                dyeId = artisanProductEntry?.extraWeftDyeId ?: 0

            } else {
                buyerProductEntry =
                    BuyerCustomProductPredicates.getCustomProductFormRemotId(productId)
                yarnType = buyerProductEntry?.extraWeftYarnId ?: 0
                yarnCount = buyerProductEntry?.extraWeftYarnCount ?: ""
                dyeId = buyerProductEntry?.extraWeftDyeId ?: 0
            }

            arrYarn?.forEach { if (it.id.equals(yarnType)) typeSelection = it.yarnDesc }
            spYarnType.setSelection(arrYarneStr.indexOf(typeSelection))

            etYarnCount.setText(yarnCount, TextView.BufferType.EDITABLE)
            spYarnCount.setSelection(arrYarnCountStr.indexOf(yarnCount))
            spCountAdapter.notifyDataSetChanged()
            arrDye?.forEach { if (it.id.equals(dyeId)) dyeSelection = it.dyeDesc }
            spYarnDye.setSelection(arrDyeStr.indexOf(dyeSelection))
            callListener()
        }
        return view
    }
    fun callListener() {
        var yarnTypeId = 0L
        var dyeId = 0L
        var yarnCnt=""

        var type= try{ spYarnType.getSelectedItem().toString()}catch (e: Exception){""}
        var count=try{if(etYarnCount.visibility==View.VISIBLE) etYarnCount.text.toString()  else spYarnCount.getSelectedItem().toString()}catch (e: Exception){""}
        var dye=try{spYarnDye.getSelectedItem().toString()}catch (e: Exception){""}

        Log.e("Viewpager", "Extraweft Type: $type, count: $count , dye: $dye")
        arrYarn?.forEach { if (it.yarnDesc.equals(type)) yarnTypeId = it.id }
        arrDye?.forEach { if (it.dyeDesc.equals(dye)) dyeId = it.id }
        if(!count.equals("Select count"))yarnCnt=count

        Log.e("Viewpager", "Extraweft Type: $yarnTypeId, count: $yarnCnt , dye: $dyeId")
        UserConfig.shared.extraWeftYarnId=yarnTypeId
        UserConfig.shared.extraWeftYarnCount=yarnCnt
        UserConfig.shared.extraWeftDyeId=dyeId
    }
    companion object {

        @JvmStatic
        fun newInstance(param1: Long, param2: Boolean) =
            ExtraWeftFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PARAM1, param1)
                    putBoolean(ARG_PARAM2, param2)
                }
            }
    }
}