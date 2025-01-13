package com.ok.financetracker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private val expensesList: List<Expense>,  // List of expenses to display in the RecyclerView
    private val onRemove: (Int) -> Unit        // Lambda function to handle the removal of an expense
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    // ViewHolder class to hold references to the views of each item in the list
    class ExpenseViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        // Views that represent the expense data in the layout
        val nameTextView: TextView = itemView.findViewById(R.id.expense_name)
        val priceTextView: TextView = itemView.findViewById(R.id.expense_price)
        val categoryTextView: TextView = itemView.findViewById(R.id.expense_category)
        val dateTextView: TextView = itemView.findViewById(R.id.expense_date)
    }

    // Inflates the item layout (expense_item_view) and creates a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_item_view, parent, false)
        return ExpenseViewHolder(view)
    }

    // Binds the data of each expense to the corresponding views in the ViewHolder
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expensesList[position]  // Get the expense object at the current position
        holder.nameTextView.text = "Name: ${expense.name}"           // Display the name of the expense
        holder.priceTextView.text = "Price: £${expense.price}"       // Display the price of the expense
        holder.categoryTextView.text = "Category: ${expense.category}" // Display the category of the expense
        holder.dateTextView.text = "Date: ${expense.date}"           // Display the date of the expense

        // Set up a long-click listener on the itemView to remove the expense
        holder.itemView.setOnLongClickListener {
            onRemove(position) // Call the onRemove function (passed as a lambda) with the position
            true  // Return true to indicate the click was handled
        }
    }

    // Returns the size of the dataset (number of expenses)
    override fun getItemCount(): Int = expensesList.size
}
