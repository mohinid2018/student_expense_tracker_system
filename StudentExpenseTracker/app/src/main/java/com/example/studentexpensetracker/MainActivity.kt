package com.example.studentexpensetracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var totalExpensesValue: Int = 0
        var totalExpensesString: String = "Total Expenses: $" + totalExpensesValue
        var expenseView = findViewById<TextView>(R.id.totalExpenses);
        expenseView.text = totalExpensesString
    }

}