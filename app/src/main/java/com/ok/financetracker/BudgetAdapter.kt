package com.ok.financetracker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BudgetAdapter(
    private val budgetList: List<Budget>,
    private val onBudgetUpdate: (String, Double) -> Unit
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    class BudgetViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val categoryTextView: TextView = itemView.findViewById(R.id.budget_category)
        val spentTextView: TextView = itemView.findViewById(R.id.budget_spent)
        val budgetInput: EditText = itemView.findViewById(R.id.budget_input)
        val updateButton: Button = itemView.findViewById(R.id.update_budget_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.budget_item_view, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgetList[position]
        holder.categoryTextView.text = "Category: ${budget.category}"
        holder.spentTextView.text = "Spent: £${budget.amountSpent} / Budget: £${budget.totalBudget}"

        holder.updateButton.setOnClickListener {
            val newBudget = holder.budgetInput.text.toString().toDoubleOrNull()
            if (newBudget != null && newBudget >= budget.amountSpent) {
                onBudgetUpdate(budget.category, newBudget)
                holder.spentTextView.text = "Spent: £${budget.amountSpent} / Budget: £${newBudget}"
            } else {
                holder.budgetInput.error = "Invalid budget amount"
            }
        }
    }

    override fun getItemCount(): Int = budgetList.size
}
