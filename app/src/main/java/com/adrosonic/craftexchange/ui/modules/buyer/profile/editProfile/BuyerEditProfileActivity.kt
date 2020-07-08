package com.adrosonic.craftexchange.ui.modules.buyer.profile.editProfile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.database.predicates.AddressPredicates
import com.adrosonic.craftexchange.database.predicates.UserPredicates
import com.adrosonic.craftexchange.databinding.ActivityBuyerEditProfileBinding
import com.adrosonic.craftexchange.repository.CraftExchangeRepository
import com.adrosonic.craftexchange.repository.data.editProfile.EditProfileResponse
import com.adrosonic.craftexchange.repository.data.model.profile.*
import com.adrosonic.craftexchange.ui.modules.buyer.profile.buyerProfileIntent
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.adrosonic.craftexchange.utils.Utility
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.io.File
import javax.security.auth.callback.Callback

fun Context.buyerEditProfileIntent(): Intent {
    return Intent(this, BuyerEditProfileActivity::class.java).apply {
    }
}

class BuyerEditProfileActivity : AppCompatActivity() {

    companion object{
        var craftUser = UserPredicates.findUser(Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())
        var delAddr = AddressPredicates.getAddressAddrType(ConstantsDirectory.DELIVERY,
            Prefs.getString(ConstantsDirectory.USER_ID,"0").toLong())
    }

    private var mBinding : ActivityBuyerEditProfileBinding ?= null
    private var file : File?= null
    private var logoBody : MultipartBody?= null
    private var fileReqBody : RequestBody?= null
    private var headerBoundary : String ?=""
    private var editProfileCall : Call<EditProfileResponse>?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBuyerEditProfileBinding.inflate(layoutInflater)
        val view = mBinding?.root
        setContentView(view)

        supportFragmentManager.let{
            mBinding?.viewPagerEdit?.adapter = BuyerEditPPagerAdapter(it)
            mBinding?.tabLayoutEdit?.setupWithViewPager(mBinding?.viewPagerEdit)
        }

        Prefs.putBoolean(ConstantsDirectory.IS_EDITTABLE,true)

        mBinding?.btnSave?.setOnClickListener {
            if(Prefs.getBoolean(ConstantsDirectory.IS_EDITTABLE,false)){

                var country = Country(delAddr?.country_id?.toLong()!!)

                var address = Address(delAddr?.city.toString(),country, delAddr?.district.toString(),
                    delAddr?.landmark.toString(),Prefs.getString(ConstantsDirectory.ADDR_LINE1,""),
                    delAddr?.line2.toString(), delAddr?.pincode.toString(), delAddr?.state.toString(),
                    delAddr?.street.toString())

                var company = BuyerCompanyDetails(Prefs.getString(ConstantsDirectory.CIN,""),
                craftUser?.companyName.toString(),"","",Prefs.getString(ConstantsDirectory.GST,""),
                    craftUser?.companyid!!,"")

                var poc = BuyerPointOfContact(Prefs.getString(ConstantsDirectory.POC_CONTACT,""),Prefs.getString(ConstantsDirectory.POC_EMAIL,""),
                    Prefs.getString(ConstantsDirectory.POC_NAME,""),
                    craftUser?.poc_id,"")

                var profileDetailsObj = EditProfileDetails(address,Prefs.getString(ConstantsDirectory.ALT_MOBILE,""),
                company,poc,Prefs.getString(ConstantsDirectory.DESIGNATION,""),Prefs.getString(ConstantsDirectory.PAN,""))

                var profileDetails = Gson().toJson(profileDetailsObj)

                var token = "Bearer ${Prefs.getString(ConstantsDirectory.ACC_TOKEN,"")}"
                var filePath = Prefs.getString(ConstantsDirectory.BRAND_LOGO,"")
                if(filePath.isNotEmpty()){
                    file = File(filePath)
                    fileReqBody = RequestBody.create(MediaType.parse("image/*"), file!!)
                    logoBody = MultipartBody.Builder()
//                        .addFormDataPart("profilePic", file?.name, fileReqBody!!)
                        .addFormDataPart("logo",file?.name,fileReqBody!!)
                        .build()
                    headerBoundary="multipart/form-data;boundary="+ logoBody?.boundary

                    editProfileCall = CraftExchangeRepository
                        .getUserService()
                        .editBuyerDetailsPhoto(headerBoundary!!,token,
                            profileDetails,logoBody!!)
                }else{
                    editProfileCall = CraftExchangeRepository
                        .getUserService()
                        .editBuyerDetails(token,profileDetails)
                }

                editProfileCall?.enqueue(object: Callback, retrofit2.Callback<EditProfileResponse> {
                    override fun onFailure(call: Call<EditProfileResponse>, t: Throwable) {
//                        hideProgress()
                        t.printStackTrace()
                    }
                    override fun onResponse(
                        call: Call<EditProfileResponse>,
                        response: retrofit2.Response<EditProfileResponse>) {

                        if(response.body()?.valid == true){
                            Toast.makeText(applicationContext,R.string.profile_update_success,Toast.LENGTH_SHORT).show()
                            UserPredicates.editBuyerDetails(response.body()!!)
                            AddressPredicates.editBuyerDelievryAddress(response.body()!!,Prefs.getString(ConstantsDirectory.DELIVERY,"Delivery"))
                            removeEditPrefs()
                            startActivity(buyerProfileIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
//                            successDialog()
                        }else{
//                            hideProgress()
                            var jsonObject: JSONObject?= null
                            try
                            {
                                jsonObject = JSONObject(response.errorBody()?.charStream()!!.readText())
                                val errorMessage = jsonObject.getString("message")
                                 Toast.makeText(applicationContext,errorMessage, Toast.LENGTH_SHORT).show()
                            }
                            catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }

                })


            }else{
                Utility.messageDialog(this,getString(R.string.enter_valid_details))
            }
        }

        mBinding?.btnBack?.setOnClickListener {
            onBackPressed()
        }
    }

    fun removeEditPrefs(){
        Prefs.remove(ConstantsDirectory.DESIGNATION)
        Prefs.remove(ConstantsDirectory.ALT_MOBILE)
        Prefs.remove(ConstantsDirectory.GST)
        Prefs.remove(ConstantsDirectory.PAN)
        Prefs.remove(ConstantsDirectory.CIN)
        Prefs.remove(ConstantsDirectory.POC_NAME)
        Prefs.remove(ConstantsDirectory.POC_EMAIL)
        Prefs.remove(ConstantsDirectory.POC_CONTACT)
        Prefs.remove(ConstantsDirectory.ADDR_LINE1)
        Prefs.remove(ConstantsDirectory.IS_EDITTABLE)
    }

    fun successDialog(){
        val builder = AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
        builder.setMessage(R.string.profile_update_success)
            .setNeutralButton("Ok"){ dialog, id ->
                dialog.cancel()
                startActivity(buyerProfileIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            }

        builder.create().show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
        removeEditPrefs()
    }
}
