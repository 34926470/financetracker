package com.ok.financetracker

// Data class representing an income entry
data class Income(
    // Name of the income source (e.g., "Salary", "Freelance work")
    val name: String,

    // The amount of money received from this income source
    val amount: Double,

    // The category of the income (e.g., "Salary", "Investment", etc.)
    val category: String,

    // The date the income was received in "yyyy-MM-dd" format (e.g., "2025-01-12")
    val date: String,

    // The month the income was received (default is 0, can be set when needed)
    val month: Int = 0,

    // The year the income was received (default is 0, can be set when needed)
    val year: Int = 0
)
