package com.adrosonic.craftexchange.ui.modules.raiseConcern

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adrosonic.craftexchange.R
import com.adrosonic.craftexchange.ui.modules.role.RoleSelectFragment
import com.adrosonic.craftexchange.ui.modules.transaction.ViewDocumentActivity
import com.adrosonic.craftexchange.utils.ConstantsDirectory
import com.pixplicity.easyprefs.library.Prefs

fun Context.raiseConcernIntent(enquiryId:Long,isView : Boolean): Intent {
    val intent = Intent(this, RaiseConcernActivity::class.java)
    intent.putExtra("enquiryId", enquiryId)
    intent.putExtra("isView", isView)
    return intent
}

class RaiseConcernActivity : AppCompatActivity() {

    var enqID : Long?= 0L
    var isView : Boolean?= false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_raise_concern)

        if (intent.extras != null) {
            enqID = intent.getLongExtra("enquiryId",0)
            isView = intent.getBooleanExtra("isView",false)
        }

//        if(isView == false){
            when(Prefs.getString(ConstantsDirectory.PROFILE,"")){
                ConstantsDirectory.ARTISAN -> {
                    if (savedInstanceState == null) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.raise_concern_container,
                                RaiseConArtisanFragment.newInstance(enqID.toString(),""))
                            .commit()
                    }
                }
                ConstantsDirectory.BUYER -> {
                    if (savedInstanceState == null) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.raise_concern_container,
                                RaiseConBuyerFragment.newInstance(enqID.toString(),""))
                            .commit()
                    }
                }
            }
//        }else{
//
//        }
    }
}