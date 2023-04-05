package com.example.memesappes.models

data class User (
    var _id:String,
    var fullname: String,
    var email: String,
    var password: String,
    var approved: Boolean,
    var token: String,
    var resetpwd: String,
    var exist:Boolean
    )