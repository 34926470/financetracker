package com.ok.financetracker

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

    private lateinit var incomeNameInput: EditText
    private lateinit var incomeAmountInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var incomeDateInput: TextView
    private lateinit var submitButton: Button
    private lateinit var incomesRecyclerView: RecyclerView
    private lateinit var selectDateButton: Button

    private val incomeList = mutableListOf<Income>()
    private lateinit var incomeAdapter: IncomeAdapter

    private lateinit var sharedPreferences: SharedPreferences
    private val incomeKey = "income_list_key"

    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.incomeactivity_view)

        // Initialize views
        incomeNameInput = findViewById(R.id.income_name_input)
        incomeAmountInput = findViewById(R.id.income_amount_input)
        categorySpinner = findViewById(R.id.category_spinner)
        incomeDateInput = findViewById(R.id.income_date_input)
        submitButton = findViewById(R.id.submit_button)
        incomesRecyclerView = findViewById(R.id.incomes_recycler_view)
        selectDateButton = findViewById(R.id.select_date_button)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("income_prefs", MODE_PRIVATE)

        // Load saved incomes
        loadIncomes()

        // Set up category spinner
        val categories = arrayOf("Salary", "Investment", "Business", "Gift", "Other")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        // Set up RecyclerView
        incomeAdapter = IncomeAdapter(incomeList) { position ->
            removeIncome(position)
        }
        incomesRecyclerView.layoutManager = LinearLayoutManager(this)
        incomesRecyclerView.adapter = incomeAdapter

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
                    incomeDateInput.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        // Handle submit button click
        submitButton.setOnClickListener {
            val name = incomeNameInput.text.toString().trim()
            val amountText = incomeAmountInput.text.toString().trim()
            val category = categorySpinner.selectedItem.toString()

            if (name.isEmpty() || amountText.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Add income to list
            val income = Income(name, amount, category, selectedDate)
            incomeList.add(income)
            incomeAdapter.notifyDataSetChanged()

            // Save updated list of incomes
            saveIncomes()

            // Clear input fields
            incomeNameInput.text.clear()
            incomeAmountInput.text.clear()
            categorySpinner.setSelection(0)
            incomeDateInput.text = "Select Date"
            selectedDate = ""
        }
    }

    // Function to save incomes to SharedPreferences
    private fun saveIncomes() {
        val gson = Gson()
        val incomesJson = gson.toJson(incomeList)
        sharedPreferences.edit().putString(incomeKey, incomesJson).apply()
    }

    // Function to load incomes from SharedPreferences
    private fun loadIncomes() {
        val gson = Gson()
        val incomesJson = sharedPreferences.getString(incomeKey, null)

        if (incomesJson != null) {
            val type = object : TypeToken<List<Income>>() {}.type
            val savedIncomes: List<Income> = gson.fromJson(incomesJson, type)
            incomeList.addAll(savedIncomes)
        }
    }

    // Remove an income entry from the list
    private fun removeIncome(position: Int) {
        incomeList.removeAt(position)
        incomeAdapter.notifyDataSetChanged()
        saveIncomes()
    }
}
