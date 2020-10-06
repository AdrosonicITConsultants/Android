package com.adrosonic.craftexchangemarketing.repository.data.request.dashboard

open class ArtDashParam {
    var token0 : String?=""
    var token1 : String?=""
    var token2 : String?=""
    var token3 : String?=""
    var token4 : String?=""
    var token5 : String?=""
    var token6 : String?=""


    override fun toString(): String {
        return "{" + "\"ds0.Token\":" + '\"'.toString() + "$token0" + '\"'.toString() +
                ",\"ds2.Token\":" + '\"'.toString() + "$token1" + '\"'.toString() +
                ",\"ds12.Token\":" + '\"'.toString() + "$token2" + '\"'.toString() +
                ",\"ds16.Token\":" + '\"'.toString() + "$token3" + '\"'.toString() +
                ",\"ds18.Token\":" + '\"'.toString() + "$token4" + '\"'.toString() +
                ",\"ds22.Token\":" + '\"'.toString() + "$token5" + '\"'.toString() +
                ",\"ds30.Token\":" + '\"'.toString() + "$token6" + '\"'.toString() +"}"
    }

}


