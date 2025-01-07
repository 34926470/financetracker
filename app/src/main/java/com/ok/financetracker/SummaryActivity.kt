package com.ok.financetracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SummaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.summaryactivity_view)  // Ensure this layout exists (activity_budgets.xml)
    }
}
