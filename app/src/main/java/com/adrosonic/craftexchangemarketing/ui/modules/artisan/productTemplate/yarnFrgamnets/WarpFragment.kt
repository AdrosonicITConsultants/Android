package com.adrosonic.craftexchangemarketing.ui.modules.artisan.productTemplate.yarnFrgamnets

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.ArtisanProducts
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.BuyerCustomProduct
import com.adrosonic.craftexchangemarketing.database.entities.realmEntities.EnquiryProductDetails
import com.adrosonic.craftexchangemarketing.database.predicates.BuyerCustomProductPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.EnquiryPredicates
import com.adrosonic.craftexchangemarketing.database.predicates.ProductPredicates
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.uploadData.Dye
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.uploadData.ProductUploadData
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.uploadData.Yarn
import com.adrosonic.craftexchangemarketing.repository.data.response.artisan.productTemplate.uploadData.YarnCount
import com.adrosonic.craftexchangemarketing.utils.UserConfig
import com.adrosonic.craftexchangemarketing.utils.Utility
import com.google.gson.GsonBuilder
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


interface WarpFragmentListner {
    fun sendWarpData( yarnType: Long, yarnCount: String, dye: Long)
}
class WarpFragment : Fragment() {

    private var productId: Long? = 0
    private var isTemplate: Boolean? = true

    var listener: WarpFragmentListner? = null
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
    lateinit var spYarnType: Spinner
    lateinit var spYarnCount : Spinner
    lateinit var etYarnCount: EditText
    lateinit var spYarnDye : Spinner

    var artisanProductEntry: EnquiryProductDetails?=null
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        var view=inflater.inflate(R.layout.warp_weft_yarn, container, false)
        imgYarn = view.findViewById<View>(R.id.img_yarn) as ImageView
        yarnTitle = view.findViewById<View>(R.id.txt_yarn_title) as TextView
        spYarnType = view.findViewById<View>(R.id.sp_yarn_type) as Spinner
        spYarnCount = view.findViewById<View>(R.id.sp_yarn_count) as Spinner
        etYarnCount = view.findViewById<View>(R.id.et_yarn_count) as EditText
        spYarnDye = view.findViewById<View>(R.id.sp_yarn_dye) as Spinner

        yarnTitle.text = "Warp "
        Utility.setImageResource(context, imgYarn, R.drawable.ic_warp_icon)

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
        val spYarnAdapter =ArrayAdapter<String>(this.requireContext(), R.layout.spinner_item, arrYarneStr)
        spYarnAdapter.setDropDownViewResource(R.layout.spinner_item)
        spYarnType.adapter = spYarnAdapter

        val spCountAdapter = ArrayAdapter<String>(this.requireContext(), R.layout.spinner_item, arrYarnCountStr)
        spCountAdapter.setDropDownViewResource(R.layout.spinner_item)
        spYarnCount.adapter = spCountAdapter

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
        val spDyeAdapter = ArrayAdapter<String>(requireContext(), R.layout.spinner_item, arrDyeStr)
        spDyeAdapter.setDropDownViewResource(R.layout.spinner_item)
        spYarnDye.adapter = spDyeAdapter
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

        if(productId!!>0){
            if(isTemplate!!){
                artisanProductEntry= EnquiryPredicates.getEnqProduct(productId,false)//ProductPredicates.getArtisanProductsByRemoteId(productId)
                yarnType=artisanProductEntry?.warpYarnId?:0
                yarnCount=artisanProductEntry?.warpYarnCount?:""
                dyeId=artisanProductEntry?.warpDyeId?:0
                }
            else{
                buyerProductEntry= BuyerCustomProductPredicates.getCustomProductFormRemotId(productId)
                yarnType=buyerProductEntry?.warpYarnId?:0
                yarnCount=buyerProductEntry?.warpYarnCount?:""
                dyeId=buyerProductEntry?.warpDyeId?:0
                Log.e("Viewpager", "Warp dyeId: $dyeId")
            }
            arrYarn?.forEach {if(it.id.equals(yarnType))typeSelection=it.yarnDesc }
            spYarnType.setSelection(arrYarneStr.indexOf(typeSelection))

            etYarnCount.setText(yarnCount, TextView.BufferType.EDITABLE)
            spYarnCount.setSelection(arrYarnCountStr.indexOf(yarnCount))
            spCountAdapter.notifyDataSetChanged()
            Log.e("Viewpager", "Warp dyeId: $dyeId")
            arrDye?.forEach { if(it.id.equals(dyeId))dyeSelection=it.dyeDesc  }
            spYarnDye.setSelection(arrDyeStr.indexOf(dyeSelection))
            callListener()
        }
        return view
    }

    fun callListener() {
        var yarnTypeId = 0L
        var dyeId = 0L
        var yarnCnt=""

        var type= try{ spYarnType.selectedItem.toString()}catch (e: Exception){""}
        var count=try{if(etYarnCount.visibility==View.VISIBLE) etYarnCount.text.toString()  else spYarnCount.selectedItem.toString()}catch (e: Exception){""}
        var dye=try{spYarnDye.selectedItem.toString()}catch (e: Exception){""}

        Log.e("Viewpager", "Warp Type: $type, count: $count , dye: $dye")
        arrYarn?.forEach { if (it.yarnDesc.equals(type)) yarnTypeId = it.id }
        arrDye?.forEach { if (it.dyeDesc.equals(dye)) dyeId = it.id }
        if(!count.equals("Select count"))yarnCnt=count
        Log.e("Viewpager", "Warp Type: $yarnTypeId, count: $yarnCnt , dye: $dyeId")
        UserConfig.shared.warpDyeId=yarnTypeId
        UserConfig.shared.warpYarnCount=yarnCnt
        UserConfig.shared.warpYarnId=dyeId
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: Long, param2: Boolean) =
            WarpFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_PARAM1, param1)
                    putBoolean(ARG_PARAM2, param2)
                }
            }
    }
}