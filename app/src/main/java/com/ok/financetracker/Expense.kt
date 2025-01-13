package com.ok.financetracker

// Data class representing an expense entry
data class Expense(
    // Name of the expense (e.g., "Groceries", "Electricity bill")
    val name: String,

    // The amount of money spent for this expense
    val price: Double,

    // The category of the expense (e.g., "Food", "Utilities", etc.)
    val category: String,

    // The date the expense occurred in "yyyy-MM-dd" format (e.g., "2025-01-12")
    val date: String,

    // The month the expense occurred (default is 0, can be set when needed)
    val month: Int = 0,

    // The year the expense occurred (default is 0, can be set when needed)
    val year: Int = 0
)
