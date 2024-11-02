package com.example.navdrawerkotpractice.Domain

data class LivreDomain(
    var id: Int,
    var titre: String,
    var auteur: String,
    var genre: String,
    var langue: String,
    var price: Double,
    var description: String,
    var picPath: String
)
