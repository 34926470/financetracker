package com.ok.financetracker

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
import java.util.Calendar

class BudgetActivity : AppCompatActivity() {

    private lateinit var budgetRecyclerView: RecyclerView
    private lateinit var monthSpinner: Spinner
    private lateinit var yearSpinner: Spinner

    private val expensesList = mutableListOf<Expense>()
    private val categoryBudgetList = mutableListOf<Budget>()
    private lateinit var budgetAdapter: BudgetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budgetactivity_view)

        // Initialize views
        budgetRecyclerView = findViewById(R.id.budget_recycler_view)
        monthSpinner = findViewById(R.id.month_spinner)
        yearSpinner = findViewById(R.id.year_spinner)

        // Load data from SharedPreferences
        loadExpenses()
        loadBudgets()

        // Set up RecyclerView
        budgetAdapter = BudgetAdapter(categoryBudgetList) { category, amountSpent ->
            updateBudget(category, amountSpent)
        }
        budgetRecyclerView.layoutManager = LinearLayoutManager(this)
        budgetRecyclerView.adapter = budgetAdapter

        // Set up the month spinner
        val months = arrayOf("All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter

        // Set up the year spinner
        val years = getAvailableYears()
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter

        // Set up listeners for both spinners
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                filterDataByMonthAndYear()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                filterDataByMonthAndYear()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        // Initially display all data
        filterDataByMonthAndYear()
    }

    private fun updateBudget(category: String, amountSpent: Double) {
        val index = categoryBudgetList.indexOfFirst { it.category == category }
        if (index != -1) {
            val updatedBudget = categoryBudgetList[index]
            updatedBudget.amountSpent = amountSpent
            categoryBudgetList[index] = updatedBudget
            saveBudgets()
            budgetAdapter.notifyItemChanged(index)
        }
    }

    private fun loadExpenses() {
        val gson = Gson()
        val expensesJson = getSharedPreferences("expense_prefs", MODE_PRIVATE)
            .getString("expenses_list_key", null)
        if (expensesJson != null) {
            val type = object : TypeToken<List<Expense>>() {}.type
            expensesList.clear() // Clear existing list to avoid duplicates
            expensesList.addAll(gson.fromJson(expensesJson, type))
        }
    }

    private fun loadBudgets() {
        val gson = Gson()
        val budgetsJson = getSharedPreferences("expense_prefs", MODE_PRIVATE)
            .getString("budget_list_key", null)
        if (budgetsJson != null) {
            val type = object : TypeToken<List<Budget>>() {}.type
            categoryBudgetList.clear() // Clear existing budgets
            categoryBudgetList.addAll(gson.fromJson(budgetsJson, type))
        }
    }

    private fun saveBudgets() {
        val gson = Gson()
        val budgetsJson = gson.toJson(categoryBudgetList)
        getSharedPreferences("expense_prefs", MODE_PRIVATE)
            .edit()
            .putString("budget_list_key", budgetsJson)
            .apply()
    }

    private fun filterDataByMonthAndYear() {
        val selectedMonthPosition = monthSpinner.selectedItemPosition
        val selectedYear = yearSpinner.selectedItem.toString()

        val filteredExpenses = if (selectedMonthPosition == 0) {
            expensesList
        } else {
            expensesList.filter { expense ->
                val expenseMonth = expense.date.split("-")[1].toInt()
                expenseMonth == selectedMonthPosition
            }
        }

        val filteredByYearExpenses = filteredExpenses.filter { expense ->
            val expenseYear = expense.date.split("-")[0].toInt()
            expenseYear == selectedYear.toInt()
        }

        // Update the budget list based on filtered expenses
        categoryBudgetList.forEach { budget ->
            val totalSpent = filteredByYearExpenses.filter { it.category == budget.category }
                .sumOf { it.price }
            budget.amountSpent = totalSpent
        }

        // Notify the adapter about the data change
        budgetAdapter.notifyDataSetChanged()
    }

    private fun getAvailableYears(): List<String> {
        val allYears = mutableSetOf<String>()

        // Add years from expenses data
        expensesList.forEach {
            allYears.add(it.date.split("-")[0])
        }

        return allYears.sorted().toList()
    }
}
