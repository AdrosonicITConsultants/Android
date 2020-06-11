package com.adrosonic.craftexchange.ui.modules.artisan.authentication.register

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentArtisanRegisterProductsBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.clusterResponse.ProductResponse
import com.adrosonic.craftexchange.repository.data.model.artisan.Address
import com.adrosonic.craftexchange.repository.data.model.artisan.Country
import com.adrosonic.craftexchange.repository.data.model.artisan.User
import com.adrosonic.craftexchange.repository.data.registerResponse.RegisterResponse
import com.adrosonic.craftexchange.ui.modules.authentication.login.LoginActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class ArtisanRegisterProductsFragment : Fragment() {

    companion object {
        fun newInstance() = ArtisanRegisterProductsFragment()
        const val TAG = "ArtisanRegProducts"
    }

    private var mBinding: FragmentArtisanRegisterProductsBinding ?= null
    var productArray = ArrayList<String>()
    var listProducts = ArrayList<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_register_products, container, false)
        CraftExchangeRepository
            .getClusterService()
//            .getProductCategories(Prefs.getString(ConstantsDirectory.CLUSTER_ID,"1").toInt())
            .getProductCategories()
            .enqueue(object: Callback, retrofit2.Callback<ProductResponse>{
                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>
                ) {
                    if(response.body()?.valid == true){
                        var obj = response.body()
                        var jsonString = Gson().toJson(obj)
                        val jsonRootObject = JSONObject(jsonString)
                        val jsonArray = jsonRootObject.optJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
//                            val id = Integer.parseInt(jsonObject.optString("id").toString())
                            val jsonObject = jsonArray.getJSONObject(i)
                            var prdObj = jsonObject.optJSONObject("productCategory")
                            var prdDesc = prdObj.optString("productDesc")
                            if(!productArray.contains(prdDesc)){
                                productArray.add(prdDesc)
                            }
                        }
//                        Log.e(TAG,"name : $productArray")
//                        Prefs.putBoolean(ConstantsDirectory.IS_FIRST_TIME,false)
                        productSpinner(productArray)
//                            Toast.makeText(activity,"countries added",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(activity,"${response.body()?.errorMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        return mBinding?.root
    }

    fun productSpinner(array : ArrayList<String>){
        var adapter= ArrayAdapter(requireActivity(),android.R.layout.simple_spinner_item, array)
        adapter.setDropDownViewResource(android.R.layout.select_dialog_multichoice)
        mBinding?.listProducts?.adapter = adapter
        mBinding?.listProducts?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO()
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var id = parent?.getItemIdAtPosition(position)?.inc()
                listProducts.add(id!!)
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val clickSpan = SpannableString("terms & Condition")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                Toast.makeText(activity,"Terms n Conditions", Toast.LENGTH_SHORT).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        clickSpan.setSpan(clickableSpan, 0, clickSpan.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        mBinding?.textTnct?.append(clickSpan)
        mBinding?.textTnct?.movementMethod = LinkMovementMethod.getInstance()
        mBinding?.textTnct?.highlightColor = Color.TRANSPARENT

        var country = Country(1,"INDIA")
        var addr = Address(country,Prefs.getString(ConstantsDirectory.DISTRICT, ""),
            Prefs.getString(ConstantsDirectory.ADDR_LINE1, ""),
            Prefs.getString(ConstantsDirectory.PINCODE, ""),
            Prefs.getString(ConstantsDirectory.STATE, ""))

        var registerRequest = User(addr,Prefs.getString(ConstantsDirectory.CLUSTER_ID,"1").toLong(),
            Prefs.getString(ConstantsDirectory.USER_EMAIL,""),Prefs.getString(ConstantsDirectory.FIRST_NAME,""),
            Prefs.getString(ConstantsDirectory.LAST_NAME,""),Prefs.getString(ConstantsDirectory.MOBILE,""),
            Prefs.getString(ConstantsDirectory.PAN,""),Prefs.getString(ConstantsDirectory.USER_PWD,""),
            listProducts,Prefs.getLong(ConstantsDirectory.REF_ROLE_ID,1),Prefs.getString(ConstantsDirectory.CLUSTER_ID,""))

        mBinding?.buttonComplete?.setOnClickListener {
            if(mBinding?.checkBoxTnc?.isChecked == true){
                CraftExchangeRepository
                    .getRegisterService()
                    .registerArtisan("application/json",registerRequest)
                    .enqueue(object: Callback, retrofit2.Callback<RegisterResponse> {
                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            t.printStackTrace()
                        }
                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: retrofit2.Response<RegisterResponse>) {
                            if(response.body()?.valid == true){
                                Log.e(TAG, response.toString())
                                Toast.makeText(activity,"User Registered Successfully",Toast.LENGTH_SHORT).show()
                                Prefs.clear()
                                Prefs.putString(ConstantsDirectory.PROFILE,"Artisan")
                                Prefs.putLong(ConstantsDirectory.REF_ROLE_ID,1)
                                startActivity(Intent(activity, LoginActivity::class.java).addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                                ))}
                        }

                    })
            }else{
                Toast.makeText(activity,"Read TnC",Toast.LENGTH_SHORT).show()
            }
        }
    }

}
