package com.example.studentexpensetracker

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class ExpenseList(private val context: Activity, private var expenses: List<Expense>) : ArrayAdapter<Expense>(context,
    R.layout.layout_expense_list, expenses) {

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewExpenses = inflater.inflate(R.layout.layout_expense_list, null, true)

        val textViewExpenseValue = listViewExpenses.findViewById<View>(R.id.textViewExpenseValue) as TextView
        val textViewLocationName = listViewExpenses.findViewById<View>(R.id.textViewLocationName) as TextView

        val expense = expenses[position]
        // textViewName.text = author.expenseValue
        textViewExpenseValue.text = "$" + "%.2f".format(expense.expenseValue.toFloat())
        textViewLocationName.text = expense.locationName

        return listViewExpenses
    }
}