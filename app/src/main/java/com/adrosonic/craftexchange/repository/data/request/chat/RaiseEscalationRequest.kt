package com.adrosonic.craftexchange.repository.data.request.chat

import com.adrosonic.craftexchange.repository.data.request.taxInv.DateeFormat
import java.io.Serializable


open class RaiseEscalationRequest : Serializable {
    var autoEscalationTypeId: Long?= 0L
    var category: Long?= 0L
    var createdOn: DateeFormat?= null
    var enquiryId: Long?= 0L
    var escalationFrom: Long?= 0L
    var escalationTo: Long?= 0L
    var escalationToadmin: Long ?= 0L
    var escalationToadminDatetime: DateeFormat?= null
    var id: Long?= 0L
    var isManual: Long?= 0L
    var isResolve: Long ?= 0L
    var modifiedOn: DateeFormat ?= null
    var resolvedOn: DateeFormat ?= null
    var text: String ?= ""
}
