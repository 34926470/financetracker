package com.ok.financetracker

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SummaryActivity : AppCompatActivity() {

    // Define the views that will display the summary data
    private lateinit var monthSpinner: Spinner
    private lateinit var yearSpinner: Spinner
    private lateinit var totalIncomeTextView: TextView
    private lateinit var totalExpensesTextView: TextView
    private lateinit var netBalanceTextView: TextView

    // SharedPreferences to retrieve saved income and expenses data
    private lateinit var sharedPreferences: SharedPreferences
    private val expensesKey = "expenses_list_key"
    private val incomeKey = "income_list_key"  // Shared preferences key for income data

    // Lists to hold expenses and income data
    private val expensesList = mutableListOf<Expense>()
    private val incomeList = mutableListOf<Income>() // List to hold income data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.summary_activity)  // Set the layout for the summary screen

        // Initialize the views from the layout
        monthSpinner = findViewById(R.id.month_spinner)
        yearSpinner = findViewById(R.id.year_spinner)
        totalIncomeTextView = findViewById(R.id.total_income_text_view)
        totalExpensesTextView = findViewById(R.id.total_expenses_text_view)
        netBalanceTextView = findViewById(R.id.net_balance_text_view)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("expense_prefs", MODE_PRIVATE)

        // Load saved expenses and income data from SharedPreferences
        loadExpenses()
        loadIncome()

        // Set up the month spinner with months
        val months = arrayOf("All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter

        // Set up the year spinner with available years
        val years = getAvailableYears() // Function to get available years from income and expenses
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter

        // Set listeners for the month and year spinners
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: android.view.View?, position: Int, id: Long) {
                // Filter data when a month is selected
                filterDataByMonthAndYear()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: android.view.View?, position: Int, id: Long) {
                // Filter data when a year is selected
                filterDataByMonthAndYear()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        // Initially display all data (all months and all years)
        filterDataByMonthAndYear()
    }

    // Function to load expenses from SharedPreferences
    private fun loadExpenses() {
        val gson = Gson()
        val expensesJson = sharedPreferences.getString(expensesKey, null)

        if (expensesJson != null) {
            // Deserialize the expenses JSON string into a list of Expense objects
            val type = object : TypeToken<List<Expense>>() {}.type
            val savedExpenses: List<Expense> = gson.fromJson(expensesJson, type)
            expensesList.addAll(savedExpenses)
        }
    }

    // Function to load income from SharedPreferences
    private fun loadIncome() {
        val gson = Gson()
        val incomeJson = sharedPreferences.getString(incomeKey, null)

        if (incomeJson != null) {
            // Deserialize the income JSON string into a list of Income objects
            val type = object : TypeToken<List<Income>>() {}.type
            val savedIncome: List<Income> = gson.fromJson(incomeJson, type)
            incomeList.addAll(savedIncome)
        }
    }

    // Function to filter and update the data based on the selected month and year
    @SuppressLint("SetTextI18n")
    private fun filterDataByMonthAndYear() {
        // Get the selected month and year from spinners
        val selectedMonthPosition = monthSpinner.selectedItemPosition
        val selectedYear = yearSpinner.selectedItem.toString()

        // Filter expenses based on the selected month
        val filteredExpenses = if (selectedMonthPosition == 0) {
            expensesList // "All" months selected, no filtering
        } else {
            expensesList.filter { expense ->
                // Extract the month from the expense date and compare
                val expenseMonth = expense.date.split("-")[1].toInt()
                expenseMonth == selectedMonthPosition
            }
        }

        // Filter income based on the selected month
        val filteredIncome = if (selectedMonthPosition == 0) {
            incomeList // "All" months selected, no filtering
        } else {
            incomeList.filter { income ->
                // Extract the month from the income date and compare
                val incomeMonth = income.date.split("-")[1].toInt()
                incomeMonth == selectedMonthPosition
            }
        }

        // Filter by the selected year
        val filteredByYearExpenses = filteredExpenses.filter { expense ->
            val expenseYear = expense.date.split("-")[0].toInt()
            expenseYear == selectedYear.toInt()
        }

        val filteredByYearIncome = filteredIncome.filter { income ->
            val incomeYear = income.date.split("-")[0].toInt()
            incomeYear == selectedYear.toInt()
        }

        // Calculate totals for the filtered data
        val totalExpenses = filteredByYearExpenses.sumOf { it.price }
        val totalIncome = filteredByYearIncome.sumOf { it.amount }

        // Display totals on the screen
        totalIncomeTextView.text = "Total Income: £${"%.2f".format(totalIncome)}"
        totalExpensesTextView.text = "Total Expenses: £${"%.2f".format(totalExpenses)}"

        // Calculate and display net balance (Income - Expenses)
        val netBalance = totalIncome - totalExpenses
        netBalanceTextView.text = "Net Balance: £${"%.2f".format(netBalance)}"
    }

    // Function to get a list of available years from expenses and income data
    private fun getAvailableYears(): List<String> {
        val allYears = mutableSetOf<String>() // Use a set to avoid duplicates

        // Add years from the expenses list
        expensesList.forEach {
            allYears.add(it.date.split("-")[0]) // Extract and add the year part of the date
        }

        // Add years from the income list
        incomeList.forEach {
            allYears.add(it.date.split("-")[0]) // Extract and add the year part of the date
        }

        // Return the sorted list of unique years
        return allYears.sorted().toList()
    }
}
