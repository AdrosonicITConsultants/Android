package com.adrosonic.craftexchangemarketing.repository.data.request.dashboard

open class OpenEnquirySummaryParms {
    var token0 : String?=""

    override fun toString(): String {
        return "{" + "\"ds0.Token\":" + '\"'.toString() + "$token0" + '\"'.toString() +
                "}"
    }
}

open class OpenMicroEnterpriseRevenueParms {
    var token0 : String?=""

    override fun toString(): String {
        return "{" + "\"ds0.Token\":" + '\"'.toString() + "$token0" + '\"'.toString() +
                "}"
    }
}

open class OpenMicroEnterpriseSummaryParms {
    var token0 : String?=""
    var token4 : String?=""
    var token7 : String?=""
    var token8 : String?=""
    var token9 : String?=""

    override fun toString(): String {
        return "{" + "\"ds0.Token\":" + '\"'.toString() + "$token0" + '\"'.toString() +
                ",\"ds4.Token\":" + '\"'.toString() + "$token4" + '\"'.toString() +
                ",\"ds7.Token\":" + '\"'.toString() + "$token7" + '\"'.toString() +
                ",\"ds8.Token\":" + '\"'.toString() + "$token8" + '\"'.toString() +
                ",\"ds9.Token\":" + '\"'.toString() + "$token9" + '\"'.toString() +
                "}"
    }
}