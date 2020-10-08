package com.adrosonic.craftexchangemarketing.ui.modules.buyer.auth.register

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.adrosonic.craftexchangemarketing.R
import com.adrosonic.craftexchangemarketing.databinding.FragmentBuyerRegisterAddressBinding
import com.adrosonic.craftexchangemarketing.repository.craftexchangemarketingRepository
import com.adrosonic.craftexchangemarketing.repository.data.registerResponse.CountryResponse
import com.adrosonic.craftexchangemarketing.utils.ConstantsDirectory
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


class BuyerRegisterAddressFragment : Fragment() {

    companion object {
//        @JvmStatic
//        fun newInstance(param1: String,param2: String) =
//            BuyerRegisterAddressFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1,param1)
//                    putString(ARG_PARAM2,param2)
//                }
//            }
        fun newInstance() = BuyerRegisterAddressFragment()
        const val TAG = "BuyerRegisterAddr"
    }

    private var mBinding: FragmentBuyerRegisterAddressBinding ?= null

    var countryList = ArrayList<CountryResponse>()
    var nameArray = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_register_address, container, false)

        craftexchangemarketingRepository
            .getRegisterService()
            .getAllCountries().enqueue(object: Callback, retrofit2.Callback<CountryResponse>{
                override fun onFailure(call: Call<CountryResponse>, t: Throwable) {
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<CountryResponse>,
                    response: Response<CountryResponse>
                ) {
                    if(response.body()?.valid == true){

                        var obj = response.body()
                        var jsonString = Gson().toJson(obj)
                        val jsonRootObject = JSONObject(jsonString)
                        val jsonArray = jsonRootObject.optJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val id = Integer.parseInt(jsonObject.optString("id").toString())
                            val name = jsonObject.optString("name").toString()
//                                countryList.add(Country(id.toLong(),name))
                            if(!nameArray.contains(name)){
                                nameArray.add(name)
                            }
                        }
//                        Log.e(TAG,"name : $nameArray")
//                        Prefs.putBoolean(ConstantsDirectory.IS_FIRST_TIME,false)
                        countrySpinner(nameArray)
//                            Toast.makeText(activity,"countries added",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(activity,"${response.body()?.errorMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            })


        var asterik = SpannableString("*")
        asterik.setSpan(ForegroundColorSpan(Color.RED), 0, asterik.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mBinding?.textAddr1?.append(asterik)
        mBinding?.textCountry?.append(asterik)
        mBinding?.textPincode?.append(asterik)

        mBinding?.textBoxAddr1?.setText(Prefs.getString(ConstantsDirectory.ADDR_LINE1,""))
        mBinding?.textBoxAddr2?.setText(Prefs.getString(ConstantsDirectory.ADDR_LINE2,""))
        mBinding?.textBoxStreet?.setText(Prefs.getString(ConstantsDirectory.STREET,""))
        mBinding?.textBoxLandmark?.setText(Prefs.getString(ConstantsDirectory.LANDMARK,""))
        mBinding?.textBoxDistrict?.setText(Prefs.getString(ConstantsDirectory.DISTRICT,""))
        mBinding?.textBoxCity?.setText(Prefs.getString(ConstantsDirectory.CITY,""))
        mBinding?.textBoxState?.setText(Prefs.getString(ConstantsDirectory.STATE,""))
//        mBinding?.textBoxCountry?.setText(Prefs.getString(ConstantsDirectory.POC_CONTACT,""))
        mBinding?.textBoxPincode?.setText(Prefs.getString(ConstantsDirectory.PINCODE,""))

        return mBinding?.root
    }

    fun countrySpinner(array : ArrayList<String>){
        var adapter=ArrayAdapter(requireActivity(),android.R.layout.simple_spinner_item, array)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding?.textBoxCountry?.adapter = adapter
        mBinding?.textBoxCountry?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
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
                Prefs.putString(ConstantsDirectory.COUNTRY_ID,id.toString())
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.buttonNext?.setOnClickListener{

            if(mBinding?.textBoxAddr1?.nonEmpty() == true &&
                mBinding?.textBoxPincode?.nonEmpty() == true &&
                mBinding?.textBoxPincode?.minLength(6) == true &&
                mBinding?.textBoxCountry?.nonEmpty() == true){

                Prefs.putString(ConstantsDirectory.ADDR_LINE1,mBinding?.textBoxAddr1?.text.toString())
                Prefs.putString(ConstantsDirectory.ADDR_LINE2,mBinding?.textBoxAddr2?.text.toString())
                Prefs.putString(ConstantsDirectory.STREET,mBinding?.textBoxStreet?.text.toString())
                Prefs.putString(ConstantsDirectory.LANDMARK,mBinding?.textBoxLandmark?.text.toString())
                Prefs.putString(ConstantsDirectory.DISTRICT,mBinding?.textBoxDistrict?.text.toString())
                Prefs.putString(ConstantsDirectory.CITY,mBinding?.textBoxCity?.text.toString())
                Prefs.putString(ConstantsDirectory.STATE,mBinding?.textBoxState?.text.toString())
//                Prefs.putString(ConstantsDirectory.COUNTRY,mBinding?.textBoxCountry.toString())
                Prefs.putString(ConstantsDirectory.PINCODE,mBinding?.textBoxPincode?.text.toString())

                if (savedInstanceState == null) {
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.register_container,
                            BuyerRegisterWebFragment.newInstance(),"Register Buyer Web Details")
                        ?.addToBackStack(null)
                        ?.commit()
                }
            }else{
                mBinding?.textBoxAddr1?.nonEmpty{ mBinding?.textBoxAddr1?.error = it }
                mBinding?.textBoxPincode?.nonEmpty{ mBinding?.textBoxPincode?.error = it }
                mBinding?.textBoxPincode?.minLength(6){ mBinding?.textBoxPincode?.error = activity?.getString(R.string.pincode_invalid_text) }

            }
        }
    }
}
