package com.example.navdrawerkotpractice

data class BookInfo(
    val title: String,
    val authors: List<String>,
    val categories: List<String>,
    val language: String,
    val description: String,
    val imageLinks: ImageLinks,
    val saleInfo: SaleInfo
)
