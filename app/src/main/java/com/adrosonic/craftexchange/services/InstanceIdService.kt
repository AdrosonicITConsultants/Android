package com.adrosonic.craftexchange.services

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService


class InstanceIdService: FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val token = FirebaseInstanceId.getInstance().token
        if (token is String)
            Log.d("MyRefreshToken",token)
        else
            Log.d("MyRefreshToken","token not instantiated")
    }
}