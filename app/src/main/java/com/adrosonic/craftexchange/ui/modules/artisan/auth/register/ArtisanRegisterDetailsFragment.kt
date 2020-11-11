package com.adrosonic.craftexchange.ui.modules.artisan.auth.register

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentArtisanRegisterDetailsBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.clusterResponse.CLusterResponse
import com.adrosonic.craftexchange.ui.modules.pdfViewer.PdfViewerActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class ArtisanRegisterDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = ArtisanRegisterDetailsFragment()
        const val TAG = "ARTisanREGDetails"
    }

    private var mBinding: FragmentArtisanRegisterDetailsBinding ?= null
    var clusterArray = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_artisan_register_details, container, false)

        var asterik = SpannableString("*")
        asterik.setSpan(ForegroundColorSpan(Color.RED), 0, asterik.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        mBinding?.textFirstname?.append(asterik)
        mBinding?.textPincode?.append(asterik)
        mBinding?.textCluster?.append(asterik)
        mBinding?.textMobile?.append(asterik)
        mBinding?.valueArtisanId?.text = Prefs.getString(ConstantsDirectory.ARTISAN_ID,"")

        mBinding?.textBoxFirstname?.setText(Prefs.getString(ConstantsDirectory.FIRST_NAME,""))
        mBinding?.textBoxLastname?.setText(Prefs.getString(ConstantsDirectory.LAST_NAME,""))
        mBinding?.textBoxPincode?.setText(Prefs.getString(ConstantsDirectory.PINCODE,""))
        mBinding?.textBoxMobile?.setText(Prefs.getString(ConstantsDirectory.MOBILE,""))
        mBinding?.textBoxDistrict?.setText(Prefs.getString(ConstantsDirectory.DISTRICT,""))
        mBinding?.textBoxPan?.setText(Prefs.getString(ConstantsDirectory.PAN,""))
        mBinding?.textBoxState?.setText(Prefs.getString(ConstantsDirectory.STATE,""))
//        mBinding?.textBoxCluster?.setText(Prefs.getString(ConstantsDirectory.ADDR_LINE2,""))
        mBinding?.textBoxAddress?.setText(Prefs.getString(ConstantsDirectory.ADDR_LINE1,""))
        mBinding?.buttonReach?.setOnClickListener {
            Utility.reachUsDialog(it.context)
        }
        mBinding?.textViewHelp?.setOnClickListener {
            val intent = Intent(context, PdfViewerActivity::class.java)
            intent.putExtra("ViewType", "HELP")
            startActivity(intent)
        }
        CraftExchangeRepository
            .getClusterService()
            .getAllClusters().enqueue(object: Callback, retrofit2.Callback<CLusterResponse>{
                override fun onFailure(call: Call<CLusterResponse>, t: Throwable) {
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<CLusterResponse>,
                    response: Response<CLusterResponse>
                ) {
                    if(response.body()?.valid == true){

                        var obj = response.body()
                        var jsonString = Gson().toJson(obj)
                        val jsonRootObject = JSONObject(jsonString)
                        val jsonArray = jsonRootObject.optJSONArray("data")
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val id = Integer.parseInt(jsonObject.optString("id").toString())
                            val desc = jsonObject.optString("desc").toString()
//                                countryList.add(Country(id.toLong(),name))
                            if(!clusterArray.contains(desc)){
                                clusterArray.add(desc)
                            }
                        }
//                        Log.e(TAG,"name : $nameArray")
//                        Prefs.putBoolean(ConstantsDirectory.IS_FIRST_TIME,false)
                        clusterSpinner(clusterArray)
//                            Toast.makeText(activity,"countries added",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(activity,"${response.body()?.errorMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        return mBinding?.root
    }

    fun clusterSpinner(array : ArrayList<String>){
        var adapter= ArrayAdapter(requireActivity(),android.R.layout.simple_spinner_item, array)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding?.textBoxCluster?.adapter = adapter
        mBinding?.textBoxCluster?.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener{
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
                Prefs.putString(ConstantsDirectory.CLUSTER_ID,id.toString())
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.textBoxPan?.addTextChangedListener {
            var boolean = Utility.isValidPan(mBinding?.textBoxPan?.text.toString())
            if(mBinding?.textBoxPan?.text?.isNotEmpty()!! ) {
                if(boolean){
                    mBinding?.buttonNext?.isClickable = true
                }else{
                    mBinding?.textBoxPan?.error =activity?.getString(R.string.pan_invalid_text)
                    mBinding?.buttonNext?.isClickable = false
                }
            }
        }

        mBinding?.buttonNext?.setOnClickListener{

            if(mBinding?.textBoxFirstname?.nonEmpty() == true &&
                mBinding?.textBoxPincode?.nonEmpty() == true &&
                mBinding?.textBoxPincode?.minLength(6) == true &&
                mBinding?.textBoxMobile?.nonEmpty() == true &&
                mBinding?.textBoxMobile?.minLength(10) == true &&
                mBinding?.textBoxCluster?.nonEmpty() == true){

                Prefs.putString(ConstantsDirectory.FIRST_NAME,mBinding?.textBoxFirstname?.text.toString())
                Prefs.putString(ConstantsDirectory.LAST_NAME,mBinding?.textBoxLastname?.text.toString())
                Prefs.putString(ConstantsDirectory.PINCODE,mBinding?.textBoxPincode?.text.toString())
                Prefs.putString(ConstantsDirectory.MOBILE,mBinding?.textBoxMobile?.text.toString())
                Prefs.putString(ConstantsDirectory.DISTRICT,mBinding?.textBoxDistrict?.text.toString())
                Prefs.putString(ConstantsDirectory.PAN,mBinding?.textBoxPan?.text.toString())
                Prefs.putString(ConstantsDirectory.STATE,mBinding?.textBoxState?.text.toString())
                Prefs.putString(ConstantsDirectory.ADDR_LINE1,mBinding?.textBoxAddress?.text.toString())


                if (savedInstanceState == null) {
                    activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(R.id.register_container,
                            ArtisanRegisterProductsFragment.newInstance(),"Register Artisan Products")
                        ?.addToBackStack(null)
                        ?.commit()
                }
            }else{
                mBinding?.textBoxFirstname?.nonEmpty{ mBinding?.textBoxFirstname?.error = it }
                mBinding?.textBoxMobile?.nonEmpty{ mBinding?.textBoxMobile?.error = it }
                mBinding?.textBoxMobile?.minLength(10){
                    mBinding?.textBoxMobile?.error =  activity?.getString(R.string.mobile_no_invalid_text)
            }
                mBinding?.textBoxPincode?.nonEmpty{ mBinding?.textBoxPincode?.error = it }
                mBinding?.textBoxPincode?.minLength(6){
                    mBinding?.textBoxPincode?.error = activity?.getString(R.string.pincode_invalid_text)
                }

            }
        }
    }

}
