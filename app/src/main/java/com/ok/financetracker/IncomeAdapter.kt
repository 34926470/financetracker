package com.ok.financetracker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// The adapter class for binding the list of incomes to the RecyclerView
class IncomeAdapter(
    private val incomeList: List<Income>, // List of income items
    private val onRemove: (Int) -> Unit // Callback for removing an income item
) : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    // ViewHolder class to hold references to the views in each item of the RecyclerView
    class IncomeViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.income_name)
        val amountTextView: TextView = itemView.findViewById(R.id.income_amount)
        val categoryTextView: TextView = itemView.findViewById(R.id.income_category)
        val dateTextView: TextView = itemView.findViewById(R.id.income_date)
    }

    // This function is called to create a new ViewHolder for a list item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        // Inflate the item layout for an individual income
        val view = LayoutInflater.from(parent.context).inflate(R.layout.income_item_view, parent, false)
        return IncomeViewHolder(view) // Return the newly created ViewHolder
    }

    // This function binds the data to the ViewHolder at a specific position in the list
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        // Get the income at the current position
        val income = incomeList[position]

        // Set the text for each TextView in the ViewHolder
        holder.nameTextView.text = "Name: ${income.name}"
        holder.amountTextView.text = "Amount: £${income.amount}"
        holder.categoryTextView.text = "Category: ${income.category}"
        holder.dateTextView.text = "Date: ${income.date}"

        // Set a long-click listener to remove an income item
        holder.itemView.setOnLongClickListener {
            // Trigger the onRemove callback with the position of the item
            onRemove(position)
            true // Return true to indicate the event was handled
        }
    }

    // This function returns the size of the income list (number of items)
    override fun getItemCount(): Int = incomeList.size
}
