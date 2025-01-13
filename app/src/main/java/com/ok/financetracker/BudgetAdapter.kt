package com.ok.financetracker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adapter class to bind budget data to RecyclerView items
class BudgetAdapter(
    private val budgetList: List<Budget>, // List of budget items to display
    private val onBudgetUpdate: (String, Double) -> Unit // Lambda function to handle budget updates
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    // ViewHolder class to hold references to the views in each list item
    class BudgetViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        // TextViews for displaying category and spent budget
        val categoryTextView: TextView = itemView.findViewById(R.id.budget_category)
        val spentTextView: TextView = itemView.findViewById(R.id.budget_spent)

        // EditText for inputting a new budget amount
        val budgetInput: EditText = itemView.findViewById(R.id.budget_input)

        // Button for updating the budget when clicked
        val updateButton: Button = itemView.findViewById(R.id.update_budget_button)
    }

    // Called to create and return a ViewHolder that holds the layout of a budget item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        // Inflate the layout for each individual item in the RecyclerView
        val view = LayoutInflater.from(parent.context).inflate(R.layout.budget_item_view, parent, false)
        return BudgetViewHolder(view) // Return the created ViewHolder
    }

    // Called to bind the data (Budget) to the ViewHolder's views
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgetList[position]

        // Set the category and budget details to their respective TextViews
        holder.categoryTextView.text = "Category: ${budget.category}"
        holder.spentTextView.text = "Spent: £${budget.amountSpent} / Budget: £${budget.totalBudget}"

        // Set an onClickListener for the update button to allow the user to update the budget
        holder.updateButton.setOnClickListener {
            // Get the new budget value from the input field (EditText)
            val newBudget = holder.budgetInput.text.toString().toDoubleOrNull()

            // Validate the new budget input
            if (newBudget != null && newBudget >= budget.amountSpent) {
                // If valid, call the lambda function to update the budget
                onBudgetUpdate(budget.category, newBudget)
                // Update the spent-to-budget text
                holder.spentTextView.text = "Spent: £${budget.amountSpent} / Budget: £${newBudget}"
            } else {
                // If invalid, display an error message on the EditText
                holder.budgetInput.error = "Invalid budget amount"
            }
        }
    }

    // Return the total number of budget items to display in the RecyclerView
    override fun getItemCount(): Int = budgetList.size
}
