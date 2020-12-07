package com.example.studentexpensetracker

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

// This file contains a class that stores the content of the second parameter and stores them into
// their rightful place in the ListView shown in the .xml file
class ExpenseList(private val mContext: Activity, private var expenses: List<Expense>) : ArrayAdapter<Expense>(mContext,
    R.layout.layout_expense_list, expenses) {

    @SuppressLint("InflateParams", "ViewHolder")
    override fun getView(pos: Int, v: View?, par: ViewGroup): View {

        val layoutInflater = mContext.layoutInflater
        val listViewExp : View = layoutInflater.inflate(R.layout.layout_expense_list, null, true)

        // initiates a view for each of the fields represented in our app
        val expenseValueVw = listViewExp.findViewById<View>(R.id.textViewExpenseValue) as TextView
        val locationNameVw = listViewExp.findViewById<View>(R.id.textViewLocationName) as TextView

        // Gets a certain position of the expenses list (the second
        // parameter of the class -- position determined by first parameter
        val exp = expenses[pos]

        // Stores the values appropriately based on their views
        // NOTE: The value for the expense is stored as you would see with money.
        // It has a '$' sign (in front) as well as a float that goes only to 2 decimal places
        expenseValueVw.text = "$" + "%.2f".format(exp.expenseValue.toFloat())
        locationNameVw.text = exp.locationName

        return listViewExp
    }
}