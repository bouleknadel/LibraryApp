package com.example.navdrawerkotpractice.Domain

data class DemandeBook(
    val id_demande: Int,
    val titre: String,
    val genre: String,
    val date_demande: String,
    val picPath: String,
    val nom: String,
    val prenom: String,
    val email: String
)