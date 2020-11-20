package com.adrosonic.craftexchangemarketing.repository.data.request.chat



open class SendChatRequest{
    var enquiryId: Long = 0
    var messageFrom: Long = 0
    var messageTo: Long = 0
    var messageString: String = ""
    var mediaType: Long = 0
//    messageJson: {"enquiryId":1834,"messageFrom":160,"messageTo":159,"messageString":"sending","mediaType":1}
    override fun toString(): String {
        return "{"+"\"enquiryId\":" + enquiryId +
                ",\"messageFrom\":" + messageFrom +
                ",\"messageTo\":"+ messageTo +
                ",\"messageString\":\""+ messageString+'\"'.toString() +
                ",\"mediaType\":"+ mediaType+"}"
    }

}

