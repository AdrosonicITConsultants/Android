package com.adrosonic.craftexchange.ui.modules.artisan.profile.editProfile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil

import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.predicates.AddressPredicates
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.FragmentMyDetailsEditProfileBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.response.artisan.editProfile.EditDetailsResponse
import com.adrosonic.craftexchange.repository.data.model.profile.Country
import com.adrosonic.craftexchange.repository.data.request.editProfileModel.EditArtisanDetails
import com.adrosonic.craftexchange.ui.modules.artisan.profile.artisanProfileIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File
import javax.security.auth.callback.Callback

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MyDetailsEditProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var file : File?= null
    private var profilePicBody : MultipartBody?= null
    private var fileReqBody : RequestBody?= null
    private var headerBoundary : String ?=""
    private var editDetailsCall : Call<EditDetailsResponse>?= null

    private var mBinding: FragmentMyDetailsEditProfileBinding ?= null


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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_details_edit_profile, container, false)

        //TODO Implement logo edit


        var username = "${craftUser?.firstName ?: ""} ${craftUser?.lastName ?: ""}"
        mBinding?.name?.text = username
        mBinding?.email?.text = craftUser?.email ?: " - "
        mBinding?.mobile?.text = craftUser?.mobile ?: " - "
        mBinding?.address?.setText(regAddr?.line1 ?: " - ")
        return mBinding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)



        mBinding?.btnSave?.setOnClickListener {

            //TODO implement proper address edit
            var country = Country(1)
//            var addressObj = EditArtisanDetails(country, regAddr?.district.toString(),mBinding?.address.toString(),
//                regAddr?.pincode.toString(), regAddr?.state.toString())

            var addressObj = EditArtisanDetails(mBinding?.address?.text.toString())

            var address = Gson().toJson(addressObj)

            var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
            var filePath = Prefs.getString(ConstantsDirectory.BRAND_LOGO,"")
            if(filePath.isNotEmpty()){
                file = File(filePath)
                fileReqBody = RequestBody.create(MediaType.parse("image/*"), file!!)
                profilePicBody = MultipartBody.Builder()
//                        .addFormDataPart("profilePic", file?.name, fileReqBody!!)
                    .addFormDataPart("profilePic",file?.name,fileReqBody!!)
                    .build()
                headerBoundary="multipart/form-data;boundary="+ profilePicBody?.boundary

                editDetailsCall = CraftExchangeRepository
                    .getUserService()
                    .editArtisanDetailsPhoto(headerBoundary!!,token,address,profilePicBody!!)

            }else{
                editDetailsCall = CraftExchangeRepository
                    .getUserService()
                    .editArtisanDetails(token,address)
            }

            editDetailsCall?.enqueue(object: Callback, retrofit2.Callback<EditDetailsResponse> {
                override fun onFailure(call: Call<EditDetailsResponse>, t: Throwable) {
//                        hideProgress()
                    t.printStackTrace()
                }
                override fun onResponse(
                    call: Call<EditDetailsResponse>,
                    response: retrofit2.Response<EditDetailsResponse>) {

                    if(response.body()?.valid == true){
//                        Toast.makeText(requireContext(),response.body()?.data, Toast.LENGTH_SHORT).show()
                        Toast.makeText(requireContext(),R.string.profile_update_success,Toast.LENGTH_SHORT).show()
                        startActivity(context?.artisanProfileIntent()?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
//                        AddressPredicates.editArtisanAddress(Prefs.getString(ConstantsDirectory.USER_ID,""),addressObj)
                    }else{

                        Toast.makeText(requireContext(),response.body()?.errorMessage, Toast.LENGTH_SHORT).show()

//                        var jsonObject: JSONObject?
//                        try
//                        {
//                            jsonObject = JSONObject(response.errorBody()?.charStream()!!.readText())
//                            val errorMessage = jsonObject.getString("message")
//                            Toast.makeText(requireContext(),errorMessage, Toast.LENGTH_SHORT).show()
//                        }
//                        catch (e: JSONException) {
//                            e.printStackTrace()
//                        }
                    }
                }

            })


        }
    }

    fun successDialog(){
        val builder = AlertDialog.Builder(requireContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
        builder.setMessage(R.string.profile_update_success)
            .setNeutralButton("Ok"){ dialog, id ->
                dialog.cancel()
                startActivity(context?.artisanProfileIntent()?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            }

        builder.create().show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyDetailsEditProfileFragment()
        const val TAG = "MyDetEditFrag"
        var craftUser = UserPredicates.findUser(Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())
        var regAddr = AddressPredicates.getAddressAddrType(ConstantsDirectory.REGISTERED,
            Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())
    }
}
