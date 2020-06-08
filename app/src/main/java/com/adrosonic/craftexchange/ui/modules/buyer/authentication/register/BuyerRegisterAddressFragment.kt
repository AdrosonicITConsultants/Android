package com.adrosonic.craftexchange.ui.modules.buyer.authentication.register

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
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBuyerRegisterAddressBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.registerResponse.CountryResponse
import com.adrosonic.craftexchange.repository.data.registerResponse.Datum
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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

    var countryList = ArrayList<Datum>()
    var nameArray = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_buyer_register_address, container, false)

//        if(Prefs.getBoolean(ConstantsDirectory.IS_FIRST_TIME,true)){
            CraftExchangeRepository
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
                                countryList.add(Datum(id.toLong(),name))
                                nameArray.add(name)
                            }
                            countrySpinner(nameArray)
                            Prefs.putBoolean(ConstantsDirectory.IS_FIRST_TIME,false)
                            Toast.makeText(activity,"countries added",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(activity,"${response.body()?.errorMessage}",Toast.LENGTH_SHORT).show()
                        }
                    }
                })
//        }

        return mBinding?.root
    }

    fun countrySpinner(array : ArrayList<String>){
        var adapter=ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, array)
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

        var asterik = SpannableString("*")
        asterik.setSpan(ForegroundColorSpan(Color.RED), 0, asterik.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mBinding?.textAddr1?.append(asterik)
        mBinding?.textCountry?.append(asterik)
        mBinding?.textPincode?.append(asterik)


        mBinding?.buttonNext?.setOnClickListener{

            if(mBinding?.textBoxAddr1?.nonEmpty() == true &&
                mBinding?.textBoxPincode?.nonEmpty() == true &&
                mBinding?.textBoxCountry?.nonEmpty() == true){

                Prefs.putString(ConstantsDirectory.ADDR_LINE1,mBinding?.textBoxAddr1?.text.toString())
                Prefs.putString(ConstantsDirectory.ADDR_LINE2,mBinding?.textBoxAddr2?.text.toString())
                Prefs.putString(ConstantsDirectory.STREET,mBinding?.textBoxStreet?.text.toString())
                Prefs.putString(ConstantsDirectory.LANDMARK,mBinding?.textBoxLandmark?.text.toString())
                Prefs.putString(ConstantsDirectory.DISTRICT,mBinding?.textBoxDistrict?.text.toString())
                Prefs.putString(ConstantsDirectory.CITY,mBinding?.textBoxCity?.text.toString())
                Prefs.putString(ConstantsDirectory.STATE,mBinding?.textBoxState?.text.toString())
                Prefs.putString(ConstantsDirectory.COUNTRY,mBinding?.textBoxCountry.toString())
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
            }
        }
    }
}
