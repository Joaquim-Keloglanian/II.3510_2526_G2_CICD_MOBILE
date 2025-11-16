package com.example.deviseapp.ui

data class CurrencyDisplay(
    val code: String,
    val name: String,
    val flag: String
) {
    fun label(): String = "$flag $code - $name"
    override fun toString(): String = label()
}


