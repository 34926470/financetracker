package com.ok.financetracker

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homeactivity_view) // Set the layout for this activity

        // Find the buttons by their ID
        val budgetButton: Button = findViewById(R.id.budget_button)
        val expenseButton: Button = findViewById(R.id.expense_button)
        val incomeButton: Button = findViewById(R.id.income_button)
        val summaryButton: Button = findViewById(R.id.summary_button)

        // Set click listeners for each button

        // When the budget button is clicked, navigate to the BudgetActivity
        budgetButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, BudgetActivity::class.java)
            startActivity(intent) // Start the BudgetActivity
        }

        // When the expense button is clicked, navigate to the ExpenseActivity
        expenseButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, ExpenseActivity::class.java)
            startActivity(intent) // Start the ExpenseActivity
        }

        // When the income button is clicked, navigate to the IncomeActivity
        incomeButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, IncomeActivity::class.java)
            startActivity(intent) // Start the IncomeActivity
        }

        // When the summary button is clicked, navigate to the SummaryActivity
        summaryButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, SummaryActivity::class.java)
            startActivity(intent) // Start the SummaryActivity
        }
    }
}
