package com.ok.financetracker

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpensesAdapter(
    private val expenses: MutableList<Expense>,
    private val onRemove: (Int) -> Unit  // Pass position instead of Expense object
    ) : RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder>() {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ExpenseViewHolder {
        val itemView = android.view.LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ExpenseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.nameTextView.text = "${expense.name} (${expense.category})"
        holder.priceTextView.text = "$${"%.2f".format(expense.price)}"
        holder.dateTextView.text = expense.date

        // Long click listener to remove the expense
        holder.itemView.setOnLongClickListener {
            onRemove(position)  // Pass position to the onRemove lambda
            true
        }
    }

    override fun getItemCount(): Int = expenses.size

    class ExpenseViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(android.R.id.text1)
        val priceTextView: TextView = itemView.findViewById(android.R.id.text2)
        val dateTextView: TextView = itemView.findViewById(android.R.id.text1)  // Assuming date is shown here
    }
}