package com.ok.financetracker

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class ExpenseActivity : AppCompatActivity() {

    private lateinit var expenseNameInput: EditText
    private lateinit var expensePriceInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var expenseDateInput: TextView
    private lateinit var submitButton: Button
    private lateinit var expensesRecyclerView: RecyclerView
    private lateinit var selectDateButton: Button

    private val expensesList = mutableListOf<Expense>()
    private lateinit var expensesAdapter: ExpenseAdapter

    private lateinit var sharedPreferences: SharedPreferences
    private val expensesKey = "expenses_list_key"
    private val budgetKey = "budget_list_key"

    private var selectedDate: String = ""

    private val categoryBudgetList = mutableListOf<Budget>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expenseactivity_view)

        // Initialize views
        expenseNameInput = findViewById(R.id.expense_name_input)
        expensePriceInput = findViewById(R.id.expense_price_input)
        categorySpinner = findViewById(R.id.category_spinner)
        expenseDateInput = findViewById(R.id.expense_date_input)
        submitButton = findViewById(R.id.submit_button)
        expensesRecyclerView = findViewById(R.id.expenses_recycler_view)
        selectDateButton = findViewById(R.id.select_date_button)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("expense_prefs", MODE_PRIVATE)

        // Load saved expenses and budgets
        loadExpenses()
        loadBudgets()

        // Set up category spinner
        val categories = arrayOf("Food", "Transport", "Entertainment", "Bills")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        // Set up RecyclerView
        expensesAdapter = ExpenseAdapter(expensesList) { position ->
            removeExpense(position)
        }
        expensesRecyclerView.layoutManager = LinearLayoutManager(this)
        expensesRecyclerView.adapter = expensesAdapter

        // Date picker button functionality
        selectDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    selectedDate = dateFormat.format(selectedCalendar.time)
                    expenseDateInput.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        // Handle submit button click
        submitButton.setOnClickListener {
            val name = expenseNameInput.text.toString().trim()
            val priceText = expensePriceInput.text.toString().trim()
            val category = categorySpinner.selectedItem.toString()

            if (name.isEmpty() || priceText.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceText.toDoubleOrNull()
            if (price == null || price <= 0) {
                Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Add expense to list
            val expense = Expense(name, price, category, selectedDate)
            expensesList.add(expense)
            expensesAdapter.notifyItemInserted(expensesList.size - 1)

            // Update budget after adding expense
            updateCategoryBudget(category, price)

            // Save updated list of expenses and budgets
            saveExpenses()
            saveBudgets()

            // Clear input fields
            expenseNameInput.text.clear()
            expensePriceInput.text.clear()
            categorySpinner.setSelection(0)
            expenseDateInput.text = "Select Date"
            selectedDate = ""
        }
    }

    // Function to save expenses to SharedPreferences
    private fun saveExpenses() {
        val gson = Gson()
        val expensesJson = gson.toJson(expensesList)
        sharedPreferences.edit().putString(expensesKey, expensesJson).apply()
        Log.d("ExpenseActivity", "Saved Expenses: $expensesList")
    }

    // Function to load expenses from SharedPreferences
    private fun loadExpenses() {
        val gson = Gson()
        val expensesJson = sharedPreferences.getString(expensesKey, null)

        if (expensesJson != null) {
            val type = object : TypeToken<List<Expense>>() {}.type
            val savedExpenses: List<Expense> = gson.fromJson(expensesJson, type)
            expensesList.addAll(savedExpenses)
        }
        Log.d("ExpenseActivity", "Loaded Expenses: $expensesList")
    }

    // Function to save budgets to SharedPreferences
    private fun saveBudgets() {
        val gson = Gson()
        val budgetsJson = gson.toJson(categoryBudgetList)
        sharedPreferences.edit().putString(budgetKey, budgetsJson).apply()
        Log.d("ExpenseActivity", "Saved Budgets: $categoryBudgetList")
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
        Log.d("ExpenseActivity", "Loaded Budgets: $categoryBudgetList")
    }

    // Update the amount spent for the selected category
    private fun updateCategoryBudget(category: String, price: Double) {
        val existingBudget = categoryBudgetList.find { it.category == category }

        if (existingBudget != null) {
            existingBudget.amountSpent += price
            if (existingBudget.amountSpent > existingBudget.totalBudget) {
                Toast.makeText(
                    this,
                    "Warning: Spending for $category exceeds the budget!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Log.d("ExpenseActivity", "Updated budget for $category: £${existingBudget.amountSpent}")
        } else {
            val newBudget = Budget(category, 0.0, price)
            categoryBudgetList.add(newBudget)
            Log.d("ExpenseActivity", "Created new budget for $category with amount: £$price")
        }
    }

    // Remove an expense entry from the list and update budget
    private fun removeExpense(position: Int) {
        val expense = expensesList[position]
        expensesList.removeAt(position)
        expensesAdapter.notifyItemRemoved(position)

        val categoryBudget = categoryBudgetList.find { it.category == expense.category }
        categoryBudget?.let {
            it.amountSpent -= expense.price
            Log.d("ExpenseActivity", "Removed expense from ${expense.category}. Updated amountSpent: £${it.amountSpent}")
        }

        saveExpenses()
        saveBudgets()
    }
}
