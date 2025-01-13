package com.ok.financetracker

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BudgetActivity : AppCompatActivity() {

    private lateinit var budgetRecyclerView: RecyclerView
    private lateinit var budgetAdapter: BudgetAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val budgetKey = "budget_list_key"

    private val categoryBudgetList = mutableListOf<Budget>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budgetactivity_view)

        // Initialize RecyclerView
        budgetRecyclerView = findViewById(R.id.budget_recycler_view)
        budgetAdapter = BudgetAdapter(categoryBudgetList) { category, newBudget ->
            updateCategoryBudget(category, newBudget)
        }
        budgetRecyclerView.layoutManager = LinearLayoutManager(this)
        budgetRecyclerView.adapter = budgetAdapter

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("expense_prefs", MODE_PRIVATE)

        // Load saved budgets
        loadBudgets()

        // Notify adapter
        budgetAdapter.notifyDataSetChanged()
    }

    // Function to load budgets from SharedPreferences
    private fun loadBudgets() {
        val gson = Gson()
        val budgetsJson = sharedPreferences.getString(budgetKey, null)

        if (budgetsJson != null) {
            val type = object : TypeToken<List<Budget>>() {}.type
            val savedBudgets: List<Budget> = gson.fromJson(budgetsJson, type)
            categoryBudgetList.clear()
            categoryBudgetList.addAll(savedBudgets)
        }

        Log.d("BudgetActivity", "Loaded Budgets: $categoryBudgetList")
    }

    // Function to save budgets to SharedPreferences
    private fun saveBudgets() {
        val gson = Gson()
        val budgetsJson = gson.toJson(categoryBudgetList)
        sharedPreferences.edit().putString(budgetKey, budgetsJson).apply()
        Log.d("BudgetActivity", "Saved Budgets: $categoryBudgetList")
    }

    // Update the total budget for a category
    private fun updateCategoryBudget(category: String, newBudget: Double) {
        val budget = categoryBudgetList.find { it.category == category }
        budget?.let {
            it.totalBudget = newBudget
            saveBudgets()
            Toast.makeText(this, "Budget updated for $category", Toast.LENGTH_SHORT).show()
        }
    }
}
