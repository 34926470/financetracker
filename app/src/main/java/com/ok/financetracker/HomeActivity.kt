package com.ok.financetracker;

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homeactivity_view) // or whatever your main layout is named

        // Find the buttons
        val budgetButton: Button = findViewById(R.id.budget_button)
        val expenseButton: Button = findViewById(R.id.expense_button)
        val incomeButton: Button = findViewById(R.id.income_button)
        val summaryButton: Button = findViewById(R.id.summary_button)

        // Set click listeners for each button
        budgetButton.setOnClickListener {
            // Open the Budgets Activity
            val intent = Intent(this@HomeActivity, BudgetsActivity::class.java)
            startActivity(intent)
        }

        expenseButton.setOnClickListener {
            // Open the Expense Activity
            val intent = Intent(this@HomeActivity, ExpenseActivity::class.java)
            startActivity(intent)
        }

        incomeButton.setOnClickListener {
            // Open the Income Activity
            val intent = Intent(this@HomeActivity, IncomeActivity::class.java)
            startActivity(intent)
        }

        summaryButton.setOnClickListener {
            // Open the Summary Activity
            val intent = Intent(this@HomeActivity, SummaryActivity::class.java)
            startActivity(intent)
        }
    }
}
