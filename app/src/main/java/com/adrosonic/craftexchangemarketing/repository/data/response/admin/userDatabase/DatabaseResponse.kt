package com.adrosonic.craftexchangemarketing.repository.data.response.admin.userDatabase



data class DatabaseResponse (
    var data: List<User>,
    var valid: Boolean,
    var errorMessage: String,
    var errorCode: Long
)
data class User(
    var id :Int,
    var weaverId : String,
    var rating : Float,
    var status : Int,
    var email : String,
    var cluster: String,
    var brandName : String,
    var firstName : String,
    var lastName : String,
    var mobile : String,
    var dateAdded :String

)
