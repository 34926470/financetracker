package com.ok.financetracker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IncomeAdapter(
    private val incomeList: List<Income>,
    private val onRemove: (Int) -> Unit
) : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    // ViewHolder class to hold references to the views
    class IncomeViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.income_name)
        val amountTextView: TextView = itemView.findViewById(R.id.income_amount)
        val categoryTextView: TextView = itemView.findViewById(R.id.income_category)
        val dateTextView: TextView = itemView.findViewById(R.id.income_date)
    }

    // Inflates the item layout and creates a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.income_item_view, parent, false)
        return IncomeViewHolder(view)
    }

    // Binds data to the ViewHolder at a specific position
    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        val income = incomeList[position]
        holder.nameTextView.text = "Name: ${income.name}"
        holder.amountTextView.text = "Amount: £${income.amount}"
        holder.categoryTextView.text = "Category: ${income.category}"
        holder.dateTextView.text = "Date: ${income.date}"

        // Handle removal of income on long click
        holder.itemView.setOnLongClickListener {
            onRemove(position)
            true
        }
    }

    // Returns the size of the dataset
    override fun getItemCount(): Int = incomeList.size
}
