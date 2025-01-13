package com.ok.financetracker

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homeactivity_view) // Set the layout for this activity

        // Prompt for passkey before showing the main content
        promptForPasskey()

        // Find the buttons by their ID
        val budgetButton: Button = findViewById(R.id.budget_button)
        val expenseButton: Button = findViewById(R.id.expense_button)
        val incomeButton: Button = findViewById(R.id.income_button)
        val summaryButton: Button = findViewById(R.id.summary_button)

        // Set click listeners for each button
        budgetButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, BudgetActivity::class.java)
            startActivity(intent)
        }

        expenseButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, ExpenseActivity::class.java)
            startActivity(intent)
        }

        incomeButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, IncomeActivity::class.java)
            startActivity(intent)
        }

        summaryButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, SummaryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun promptForPasskey() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.passkey_dialog, null)

        // Access the views inside the custom dialog
        val passkeyInput: EditText = dialogView.findViewById(R.id.passkey_input)
        val submitButton: Button = dialogView.findViewById(R.id.submit_button)

        builder.setView(dialogView)
        builder.setCancelable(false) // Prevent closing the dialog without entering the passkey
        val dialog = builder.create()

        submitButton.setOnClickListener {
            val enteredPasskey = passkeyInput.text.toString()
            val correctPasskey = "1234"

            if (enteredPasskey == correctPasskey) {
                dialog.dismiss() // Close the dialog
            } else {
                Toast.makeText(this, "Incorrect passkey. App will close.", Toast.LENGTH_SHORT).show()
                finishAffinity() // Close the app
            }
        }

        dialog.show()
    }
}
