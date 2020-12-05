package com.example.studentexpensetracker

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class ExpenseList(private val context: Activity, private var expenses: List<Expense>) : ArrayAdapter<Expense>(context,
    R.layout.layout_author_list, expenses) {

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val listViewItem = inflater.inflate(R.layout.layout_author_list, null, true)

        val textViewName = listViewItem.findViewById<View>(R.id.textViewName) as TextView
        val textViewCountry = listViewItem.findViewById<View>(R.id.textViewCountry) as TextView

        val expense = expenses[position]
        textViewName.text = expense.expenseNum
        textViewCountry.text = expense.expenseLoc

        return listViewItem
    }
}