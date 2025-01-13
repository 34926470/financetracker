package com.ok.financetracker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private val expensesList: List<Expense>,
    private val onRemove: (Int) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    // ViewHolder class to hold references to the views
    class ExpenseViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.expense_name)
        val priceTextView: TextView = itemView.findViewById(R.id.expense_price)
        val categoryTextView: TextView = itemView.findViewById(R.id.expense_category)
        val dateTextView: TextView = itemView.findViewById(R.id.expense_date)
    }

    // Inflates the item layout and creates a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expense_item_view, parent, false)
        return ExpenseViewHolder(view)
    }

    // Binds data to the ViewHolder at a specific position
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expensesList[position]
        holder.nameTextView.text = "Name: ${expense.name}"
        holder.priceTextView.text = "Price: £${expense.price}"
        holder.categoryTextView.text = "Category: ${expense.category}"
        holder.dateTextView.text = "Date: ${expense.date}"

        // Handle removal of expense on long click
        holder.itemView.setOnLongClickListener {
            onRemove(position)
            true
        }
    }

    // Returns the size of the dataset
    override fun getItemCount(): Int = expensesList.size
}
