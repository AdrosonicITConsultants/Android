package com.adrosonic.craftexchange.repository.data.request.dashboard

open class BuyerDashParam {
    var email : String?=""
    var token0 : String?=""
    var token1 : String?=""

    override fun toString(): String {
        return "{" + "\"ds0.email\":" + '\"'.toString() + "$email" + '\"'.toString() +
                ",\"ds0.Token\":" + '\"'.toString() + "$token0" + '\"'.toString() +
                ",\"ds44.Token\":" + '\"'.toString() + "$token1" + '\"'.toString() + "}"
    }

}


