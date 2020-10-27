package com.adrosonic.craftexchange.repository.data.request.dashboard

open class BuyerDashParam {
    var email : String?=""
    var token : String?=""
//    var token1 : String?=""

    override fun toString(): String {
        return "{" + "\"ds47.email\":" + '\"'.toString() + "$email" + '\"'.toString() +
                ",\"ds47.Token\":" + '\"'.toString() + "$token" + '\"'.toString() +
                ",\"ds46.email\":" + '\"'.toString() + "$email" + '\"'.toString() +
                ",\"ds46.Token\":" + '\"'.toString() + "$token" + '\"'.toString() +
                ",\"ds48.email\":" + '\"'.toString() + "$email" + '\"'.toString() +
                ",\"ds48.Token\":" + '\"'.toString() + "$token" + '\"'.toString() +
                ",\"ds44.Token\":" + '\"'.toString() + "$token" + '\"'.toString() + "}"
    }

}


