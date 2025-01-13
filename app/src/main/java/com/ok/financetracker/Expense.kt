package com.ok.financetracker

data class Expense(
    val name: String,
    val price: Double,
    val category: String,
    val date: String,  // Date in "yyyy-MM-dd" format
    val month: Int = 0,  // Default value of 0
    val year: Int = 0    // Default value of 0
)
