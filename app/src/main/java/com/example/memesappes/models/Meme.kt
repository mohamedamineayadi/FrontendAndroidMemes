package com.example.memesappes.models

data class Meme (
    var _id:String,
    var text: String,
    var createdBy: String,
    var fullname_creator: String,
    var image: String
)

data class MemeHome (
    var _id:String,
    var text: String,
    var createdBy: String,
    var fullname_creator: String,
    var image: String,
    var nbrLike: Int,
    var participants: MutableList<UserHome>
)

data class UserHome (
    var fullname: String,
    var email: String,
)

data class MemeLikeHome (
    var isLiked: Boolean,
    var nbrLike: Int,
    var participants: MutableList<UserHome>,
    var p: Int
)