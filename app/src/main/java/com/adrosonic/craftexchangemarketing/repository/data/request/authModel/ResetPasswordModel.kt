package com.adrosonic.craftexchangemarketing.repository.data.request.authModel

data class ResetPasswordModel (
   var currentPassword: String,
   var newPassword: String,
   var resetToken: String,
   var userName: String
)

