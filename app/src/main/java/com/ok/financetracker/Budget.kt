// Package declaration indicating this class is part of the 'com.ok.financetracker' package
package com.ok.financetracker

// Data class representing a budget for a specific category
data class Budget(
    // Category of the budget (e.g., "Food", "Entertainment", etc.)
    val category: String,

    // The total allocated budget amount for this category
    var totalBudget: Double,

    // The amount of money already spent in this category
    var amountSpent: Double
)
