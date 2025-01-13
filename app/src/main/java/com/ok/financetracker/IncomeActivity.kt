package com.ok.financetracker

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class IncomeActivity : AppCompatActivity() {

    // Declare UI elements
    private lateinit var incomeNameInput: EditText
    private lateinit var incomeAmountInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var incomeDateInput: TextView
    private lateinit var submitButton: Button
    private lateinit var incomesRecyclerView: RecyclerView
    private lateinit var selectDateButton: Button

    // Declare the list of incomes and its adapter
    private val incomeList = mutableListOf<Income>()
    private lateinit var incomeAdapter: IncomeAdapter

    // SharedPreferences to save and load incomes
    private lateinit var sharedPreferences: SharedPreferences
    private val incomeKey = "income_list_key"

    // Store the selected date for the income
    private var selectedDate: String = ""

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.incomeactivity_view) // Set the layout for the activity

        // Initialize the UI views
        incomeNameInput = findViewById(R.id.income_name_input)
        incomeAmountInput = findViewById(R.id.income_amount_input)
        categorySpinner = findViewById(R.id.category_spinner)
        incomeDateInput = findViewById(R.id.income_date_input)
        submitButton = findViewById(R.id.submit_button)
        incomesRecyclerView = findViewById(R.id.incomes_recycler_view)
        selectDateButton = findViewById(R.id.select_date_button)

        // Initialize SharedPreferences to store income data
        sharedPreferences = getSharedPreferences("finance_prefs", MODE_PRIVATE)

        // Load previously saved incomes from SharedPreferences
        loadIncomes()

        // Set up category spinner with predefined categories
        val categories = arrayOf("Salary", "Investment", "Business", "Gift", "Other")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        // Set up RecyclerView to display the list of incomes
        incomeAdapter = IncomeAdapter(incomeList) { position ->
            removeIncome(position)
        }
        incomesRecyclerView.layoutManager = LinearLayoutManager(this)
        incomesRecyclerView.adapter = incomeAdapter

        // Set up date picker for selecting income date
        selectDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    selectedDate = dateFormat.format(selectedCalendar.time)
                    incomeDateInput.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        // Handle submit button click to save new income
        submitButton.setOnClickListener {
            val name = incomeNameInput.text.toString().trim()
            val amountText = incomeAmountInput.text.toString().trim()
            val category = categorySpinner.selectedItem.toString()

            // Validate input fields
            if (name.isEmpty() || amountText.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a new income object and add it to the list
            val income = Income(name, amount, category, selectedDate)
            incomeList.add(income)
            incomeAdapter.notifyDataSetChanged()

            // Save the updated list of incomes
            saveIncomes()

            // Clear the input fields after submission
            incomeNameInput.text.clear()
            incomeAmountInput.text.clear()
            categorySpinner.setSelection(0)
            incomeDateInput.text = "Select Date"
            selectedDate = ""
        }
    }

    // Function to save the list of incomes to SharedPreferences
    private fun saveIncomes() {
        val gson = Gson()
        val incomesJson = gson.toJson(incomeList)
        sharedPreferences.edit().putString(incomeKey, incomesJson).apply()

        // Log the incomes being saved
        println("Saved Incomes: $incomeList")
    }

    // Function to load the list of incomes from SharedPreferences
    private fun loadIncomes() {
        val gson = Gson()
        val incomesJson = sharedPreferences.getString(incomeKey, null)

        if (incomesJson != null) {
            val type = object : TypeToken<List<Income>>() {}.type
            val savedIncomes: List<Income> = gson.fromJson(incomesJson, type)
            incomeList.addAll(savedIncomes)

            // Log each income and its date
            incomeList.forEach {
                println("Income: ${it.name}, Date: ${it.date}")
            }
        }
    }

    // Function to remove an income from the list
    @SuppressLint("NotifyDataSetChanged")
    private fun removeIncome(position: Int) {
        incomeList.removeAt(position)
        incomeAdapter.notifyDataSetChanged()

        // Save the updated list after removal
        saveIncomes()
    }
}
