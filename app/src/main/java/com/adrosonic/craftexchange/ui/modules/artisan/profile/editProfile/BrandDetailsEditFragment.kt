package com.adrosonic.craftexchange.ui.modules.artisan.profile.editProfile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.databinding.FragmentBrandDetailsBinding
import com.adrosonic.craftexchange.databinding.FragmentBrandDetailsEditBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.request.editProfileModel.CompanyDetails
import com.adrosonic.craftexchange.repository.data.request.editProfileModel.EditArtisanBrand
import com.adrosonic.craftexchange.repository.data.response.artisan.editProfile.EditDetailsResponse
import com.adrosonic.craftexchange.ui.modules.artisan.profile.ArtisanProfileActivity.Companion.craftUser
import com.adrosonic.craftexchange.ui.modules.artisan.profile.artisanProfileIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.ImageSetter
import com.adrosonic.craftexchange.utils.Utility
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.ResponseBody
import retrofit2.Call
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BrandDetailsEditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var mBinding: FragmentBrandDetailsEditBinding ?= null
    var productArray = ArrayList<String>()
    var listProducts = ArrayList<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_brand_details_edit, container, false)

        var imageName = Utility.craftUser?.brandLogo
        var url = "https://f3adac-craft-exchange-resource.objectstore.e2enetworks.net/User/${Prefs.getString(ConstantsDirectory.USER_ID,"")}/CompanyDetails/Logo/${imageName}"
        mBinding?.changeLogoImg?.let {
            ImageSetter.setImage(requireContext(),url, it,
                R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder,R.drawable.artisan_logo_placeholder)
        }
        productArray.add("Saree")
        productArray.add("Dupatta")
        productArray.add("Stole")
        productArray.add("Fabric")
        productArray.add("Home Accessories")
        productArray.add("Fashion Accessories")
        mBinding?.prodCategory?.setItems(productArray)

        mBinding?.cluster?.text = craftUser?.clusterdesc ?: "-"
        mBinding?.name?.setText(craftUser?.companyName ?: "")
        mBinding?.description?.setText(craftUser?.companyDesc ?: "")
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding?.btnSave?.setOnClickListener {
//            Toast.makeText(requireContext(),"Save Brand Details Feature to be implemented", Toast.LENGTH_LONG).show()
            listProducts = mBinding?.prodCategory?.selectedIndicies!!
            var brandDetails = CompanyDetails(mBinding?.name?.text.toString(),mBinding?.description?.text.toString())
            var editBrandDetailsObj = EditArtisanBrand(brandDetails,listProducts)

            var editBrandDetails = Gson().toJson(editBrandDetailsObj)

            var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
            CraftExchangeRepository
                .getUserService()
                .editArtisanBrandDetails(token,editBrandDetails)
                .enqueue(object: Callback, retrofit2.Callback<EditDetailsResponse> {
                    override fun onFailure(call: Call<EditDetailsResponse>, t: Throwable) {
//                        hideProgress()
                        t.printStackTrace()
                    }
                    override fun onResponse(
                        call: Call<EditDetailsResponse>,
                        response: retrofit2.Response<EditDetailsResponse>) {

                        Log.e(TAG,response.body().toString())

//                        successDialog()
                        if(response.body()?.valid == true){
////                        Toast.makeText(requireContext(),response.body()?.data, Toast.LENGTH_SHORT).show()
////                            successDialog()
                            Toast.makeText(requireContext(),R.string.profile_update_success,Toast.LENGTH_SHORT).show()
                            startActivity(context?.artisanProfileIntent()?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
//
////                            AddressPredicates.editArtisanAddress(Prefs.getString(ConstantsDirectory.USER_ID,""),addressObj)
                        }else{
//
                            Toast.makeText(requireContext(),response.body()?.errorMessage, Toast.LENGTH_SHORT).show()
////
//////                        var jsonObject: JSONObject?
//////                        try
//////                        {
//////                            jsonObject = JSONObject(response.errorBody()?.charStream()!!.readText())
//////                            val errorMessage = jsonObject.getString("message")
//////                            Toast.makeText(requireContext(),errorMessage, Toast.LENGTH_SHORT).show()
//////                        }
//////                        catch (e: JSONException) {
//////                            e.printStackTrace()
//////                        }
                        }
                    }

                })


        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = BrandDetailsEditFragment()
        const val TAG = "BrandEditFragment"
    }
}
