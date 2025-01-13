package com.ok.financetracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// BudgetActivity class to manage the budget display and interaction logic
class BudgetActivity : AppCompatActivity() {

    // Declare views for the RecyclerView and spinners
    private lateinit var budgetRecyclerView: RecyclerView
    private lateinit var monthSpinner: Spinner
    private lateinit var yearSpinner: Spinner

    // Lists to store expenses and category budgets
    private val expensesList = mutableListOf<Expense>()
    private val categoryBudgetList = mutableListOf<Budget>()
    private lateinit var budgetAdapter: BudgetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budgetactivity_view)

        // Initialize the views
        budgetRecyclerView = findViewById(R.id.budget_recycler_view)
        monthSpinner = findViewById(R.id.month_spinner)
        yearSpinner = findViewById(R.id.year_spinner)

        // Load saved data (expenses and budgets) from SharedPreferences
        loadExpenses()
        loadBudgets()

        // Set up RecyclerView to display budgets, with an adapter and layout manager
        budgetAdapter = BudgetAdapter(categoryBudgetList) { category, amountSpent ->
            // Update budget when a category's spent amount is modified
            updateBudget(category, amountSpent)
        }
        budgetRecyclerView.layoutManager = LinearLayoutManager(this)
        budgetRecyclerView.adapter = budgetAdapter

        // Set up the month spinner with a list of months
        val months = arrayOf("All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter

        // Set up the year spinner with a list of available years
        val years = getAvailableYears()
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter

        // Set listeners for both spinners to trigger filtering when an item is selected
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                // Filter data based on selected month and year
                filterDataByMonthAndYear()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                // Filter data based on selected month and year
                filterDataByMonthAndYear()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        // Initially display all data by calling the filter method
        filterDataByMonthAndYear()
    }

    // Function to update the budget when an amount is spent in a specific category
    private fun updateBudget(category: String, amountSpent: Double) {
        val index = categoryBudgetList.indexOfFirst { it.category == category }
        if (index != -1) {
            val updatedBudget = categoryBudgetList[index]
            updatedBudget.amountSpent = amountSpent
            categoryBudgetList[index] = updatedBudget
            saveBudgets()  // Save updated budget data
            budgetAdapter.notifyItemChanged(index)  // Notify the adapter about the change
        }
    }

    // Load expenses from SharedPreferences using Gson to deserialize JSON data
    private fun loadExpenses() {
        val gson = Gson()
        val expensesJson = getSharedPreferences("expense_prefs", MODE_PRIVATE)
            .getString("expenses_list_key", null)
        if (expensesJson != null) {
            val type = object : TypeToken<List<Expense>>() {}.type
            expensesList.clear() // Clear any existing data to avoid duplicates
            expensesList.addAll(gson.fromJson(expensesJson, type))
        }
    }

    // Load budget data from SharedPreferences using Gson
    private fun loadBudgets() {
        val gson = Gson()
        val budgetsJson = getSharedPreferences("expense_prefs", MODE_PRIVATE)
            .getString("budget_list_key", null)
        if (budgetsJson != null) {
            val type = object : TypeToken<List<Budget>>() {}.type
            categoryBudgetList.clear()  // Clear existing budgets
            categoryBudgetList.addAll(gson.fromJson(budgetsJson, type))
        }
    }

    // Save updated budget data to SharedPreferences
    private fun saveBudgets() {
        val gson = Gson()
        val budgetsJson = gson.toJson(categoryBudgetList)
        getSharedPreferences("expense_prefs", MODE_PRIVATE)
            .edit()
            .putString("budget_list_key", budgetsJson)
            .apply()
    }

    // Filter expenses based on selected month and year
    @SuppressLint("NotifyDataSetChanged")
    private fun filterDataByMonthAndYear() {
        val selectedMonthPosition = monthSpinner.selectedItemPosition
        val selectedYear = yearSpinner.selectedItem.toString()

        // Filter expenses based on the selected month
        val filteredExpenses = if (selectedMonthPosition == 0) {
            expensesList  // If "All" is selected, show all expenses
        } else {
            expensesList.filter { expense ->
                val expenseMonth = expense.date.split("-")[1].toInt()
                expenseMonth == selectedMonthPosition
            }
        }

        // Further filter expenses based on the selected year
        val filteredByYearExpenses = filteredExpenses.filter { expense ->
            val expenseYear = expense.date.split("-")[0].toInt()
            expenseYear == selectedYear.toInt()
        }

        // Update the budget list with the total spent for each category
        categoryBudgetList.forEach { budget ->
            val totalSpent = filteredByYearExpenses.filter { it.category == budget.category }
                .sumOf { it.price }
            budget.amountSpent = totalSpent
        }

        // Notify the adapter about the data change
        budgetAdapter.notifyDataSetChanged()
    }

    // Retrieve the list of years available in the expense data
    private fun getAvailableYears(): List<String> {
        val allYears = mutableSetOf<String>()

        // Collect all unique years from the expense data
        expensesList.forEach {
            allYears.add(it.date.split("-")[0])
        }

        // Return a sorted list of years
        return allYears.sorted().toList()
    }
}
