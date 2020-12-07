package com.example.studentexpensetracker

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_expense_track.view.*
import java.util.*

class ExpenseTrackActivity : AppCompatActivity() {

    private lateinit var editTextExpenseValue: EditText
    private lateinit var editTextLocationName: EditText
    private lateinit var buttonAddExpense: Button
    internal lateinit var listViewExpenses: ListView
    //NEW
    private lateinit var editTotalExpenses: TextView
    private var totalExpenses: Float = 0F
    //NEW
    internal lateinit var expenses: MutableList<Expense>

    private lateinit var databaseExpenses: DatabaseReference

    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_track)

        //getting the reference of artists node
        databaseExpenses = FirebaseDatabase.getInstance().getReference("expenses")
        totalExpenses = 0F
        editTextExpenseValue = findViewById<View>(R.id.editTextExpenseValue) as EditText
        editTextLocationName = findViewById<View>(R.id.editTextLocationName) as EditText
        listViewExpenses = findViewById<View>(R.id.listViewExpenses) as ListView
        buttonAddExpense = findViewById<View>(R.id.buttonAddExpense) as Button
        editTotalExpenses = findViewById<View>(R.id.totalExpensesView) as TextView

        expenses = ArrayList()
        uid = intent.getStringExtra(USER_ID)!!

        buttonAddExpense.setOnClickListener {
            addExpense()
        }

        listViewExpenses.onItemClickListener = AdapterView.OnItemClickListener {  _, _, i, _ ->
            val author = expenses[i]
            showUpdateDeleteDialog(author.expenseID, author.expenseValue, author.locationName)
            true
        }
    }

    private fun showUpdateDeleteDialog(expenseID: String, expenseValue: String, locationName: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.layout_update_delete, null)
        dialogBuilder.setView(dialogView)

        val editTextUpdateExpenseValue = dialogView.findViewById<View>(R.id.editTextUpdateExpenseValue) as EditText
        editTextUpdateExpenseValue.setText(expenseValue)
        val editTextUpdateLocationName = dialogView.findViewById<View>(R.id.editTextUpdateLocationName) as EditText
        editTextUpdateLocationName.setText(locationName)
        val buttonUpdate = dialogView.findViewById<View>(R.id.buttonUpdateExpense) as Button
        val buttonDelete = dialogView.findViewById<View>(R.id.buttonDeleteExpense) as Button


        dialogBuilder.setTitle("$$expenseValue at $locationName")

        val b = dialogBuilder.create()
        b.show()

        var oldExpenseValue = expenseValue.toFloat()

        buttonUpdate.setOnClickListener {
            val expense = editTextUpdateExpenseValue.text.toString().trim{it <= ' '}
            val location = editTextUpdateLocationName.text.toString().trim{it <= ' '}

            val expenseRegex = Regex("\\d+" + "(\\.\\d{1,2})?")

            if (!TextUtils.isEmpty(expense) && !TextUtils.isEmpty(location)) {
                if (expenseRegex.matches((expense))) {
                    updateExpense(expenseID, uid, expense, location, oldExpenseValue)
                    b.dismiss()
                } else {
                    Toast.makeText(
                        this,
                        "Enter the expense as a whole number or with 1-2 decimal places",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }


        }

        buttonDelete.setOnClickListener {
            deleteExpense(expenseID, oldExpenseValue)
            b.dismiss()
        }

        // hide keyboard here
        val inputMethodMng = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodMng.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    private fun addExpense() {
        var expense = editTextExpenseValue.text.toString().trim { it <= ' ' }
        if (expense.isNotEmpty()) {
            expense = expense.substring(1)
        }

        val location =  editTextLocationName.text.toString().trim { it <= ' ' }
        if (expense != "$" && !TextUtils.isEmpty(expense) && !TextUtils.isEmpty((location))) {
            val expenseRegex = Regex("\\d+" + "(\\.\\d{1,2})?")

            if (expenseRegex.matches((expense))) {
                val id = databaseExpenses.push().key
                val expenseObj = Expense(id!!, expense, location)
                val newExpense = expense.toFloat()
                totalExpenses += newExpense
                editTotalExpenses.text = "Total Expenses: $$totalExpenses"
                databaseExpenses.child(uid).child(id).setValue(expenseObj)
                editTextExpenseValue.setText("")
                editTextLocationName.setText("")



                Toast.makeText(this, "Expense added", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this,
                    "Enter the expense as a whole number or with 1-2 decimal places",
                    Toast.LENGTH_LONG
                ).show()
            }

        } else {
            Toast.makeText(this, "Please provide an expense and a location!", Toast.LENGTH_LONG).show()
        }

        // hide keyboard here
        val inputMethodMng = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodMng.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    private fun updateExpense(id: String, uid: String, expense: String, location: String, oldExpenseValue: Float): Boolean {
        val dR = FirebaseDatabase.getInstance().getReference("expenses").child(uid).child(id)
        val expenseObject = Expense(id, expense, location)
        var newExpenseValue = expense.toFloat()
        totalExpenses -= oldExpenseValue
        totalExpenses += newExpenseValue
        editTotalExpenses.text = "Total Expenses: $$totalExpenses"
        dR.setValue(expenseObject)
        Toast.makeText(applicationContext, "Expense Updated", Toast.LENGTH_LONG).show()
        return true
    }

    private fun deleteExpense(id: String, oldExpenseValue: Float): Boolean {
        val dR = FirebaseDatabase.getInstance().getReference("expenses").child(uid).child(id)
        totalExpenses -= oldExpenseValue
        editTotalExpenses.text = "Total Expenses: $$totalExpenses"
        dR.removeValue()
        Toast.makeText(applicationContext, "Expense Deleted", Toast.LENGTH_LONG).show()
        return true
    }

    override fun onStart() {
        super.onStart()

        editTextExpenseValue.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editTextExpenseValue.setText("$")
                editTextExpenseValue.setSelection(1)
            }
        }

        databaseExpenses.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                expenses.clear()

                var expense: Expense? = null
                for (postSnapshot in dataSnapshot.child(uid).children) {
                    try {
                        expense = postSnapshot.getValue(Expense::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    } finally {
                        expenses.add(expense!!)
                    }
                }

                val expenseAdapter = ExpenseList(this@ExpenseTrackActivity, expenses)
                listViewExpenses.adapter = expenseAdapter
                var expenseSum = 0F

                for (expense in expenses) {
                    Log.i("TAG", expense.expenseValue)

                    expenseSum += expense.expenseValue.toFloat()
                }
                val expenseSumStr = "%.2f".format(expenseSum)
                editTotalExpenses.text = "Total Expenses: $$expenseSumStr"
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    companion object {
        const val TAG = "Student Expense Tracker"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
    }
}