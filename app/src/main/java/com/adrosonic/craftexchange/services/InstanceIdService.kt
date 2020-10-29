package com.adrosonic.craftexchange.services

import android.util.Log
import com.adrosonic.craftexchange.utils.UserConfig
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService


class InstanceIdService: FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val token = FirebaseInstanceId.getInstance().getToken()
        if (token is String) {
            UserConfig.shared.deviceRegistrationToken = token
            Log.d("MyRefreshToken", token)
        }
        else
            Log.d("MyRefreshToken","token not instantiated")
    }
}