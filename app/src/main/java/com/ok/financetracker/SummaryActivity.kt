package com.ok.financetracker

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

    private lateinit var monthSpinner: Spinner
    private lateinit var yearSpinner: Spinner
    private lateinit var totalIncomeTextView: TextView
    private lateinit var totalExpensesTextView: TextView
    private lateinit var netBalanceTextView: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private val expensesKey = "expenses_list_key"
    private val incomeKey = "income_list_key"  // Assuming you have income data saved similarly to expenses

    private val expensesList = mutableListOf<Expense>()
    private val incomeList = mutableListOf<Income>() // Assuming you have a similar Income data class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.summary_activity)  // Use your layout XML for the summary screen

        // Initialize views
        monthSpinner = findViewById(R.id.month_spinner)
        yearSpinner = findViewById(R.id.year_spinner)
        totalIncomeTextView = findViewById(R.id.total_income_text_view)
        totalExpensesTextView = findViewById(R.id.total_expenses_text_view)
        netBalanceTextView = findViewById(R.id.net_balance_text_view)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("expense_prefs", MODE_PRIVATE)

        // Load saved expenses and income
        loadExpenses()
        loadIncome()

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
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: android.view.View?, position: Int, id: Long) {
                filterDataByMonthAndYear()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: android.view.View?, position: Int, id: Long) {
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
            val type = object : TypeToken<List<Income>>() {}.type
            val savedIncome: List<Income> = gson.fromJson(incomeJson, type)
            incomeList.addAll(savedIncome)
        }
    }

    // Function to filter and update the data based on the selected month and year
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

        val filteredIncome = if (selectedMonthPosition == 0) {
            incomeList
        } else {
            incomeList.filter { income ->
                val incomeMonth = income.date.split("-")[1].toInt()
                incomeMonth == selectedMonthPosition
            }
        }

        val filteredByYearExpenses = filteredExpenses.filter { expense ->
            val expenseYear = expense.date.split("-")[0].toInt()
            expenseYear == selectedYear.toInt()
        }

        val filteredByYearIncome = filteredIncome.filter { income ->
            val incomeYear = income.date.split("-")[0].toInt()
            incomeYear == selectedYear.toInt()
        }

        // Calculate totals for filtered data
        val totalExpenses = filteredByYearExpenses.sumOf { it.price }
        val totalIncome = filteredByYearIncome.sumOf { it.amount }

        // Display totals
        totalIncomeTextView.text = "Total Income: £${"%.2f".format(totalIncome)}"
        totalExpensesTextView.text = "Total Expenses: £${"%.2f".format(totalExpenses)}"

        // Display net balance (income - expenses)
        val netBalance = totalIncome - totalExpenses
        netBalanceTextView.text = "Net Balance: £${"%.2f".format(netBalance)}"
    }

    // Function to get a list of available years from expenses and income data
    private fun getAvailableYears(): List<String> {
        val allYears = mutableSetOf<String>()

        // Add years from expenses data
        expensesList.forEach {
            allYears.add(it.date.split("-")[0])
        }

        // Add years from income data
        incomeList.forEach {
            allYears.add(it.date.split("-")[0])
        }

        return allYears.sorted().toList()
    }
}
