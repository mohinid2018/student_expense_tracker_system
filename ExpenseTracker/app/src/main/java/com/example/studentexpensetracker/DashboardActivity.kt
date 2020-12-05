package com.example.studentexpensetracker

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.lang.Exception
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editLocationName: EditText
    private lateinit var buttonAddExpense: Button
    internal lateinit var listOfExpenses: ListView

    internal lateinit var expenses: MutableList<Expense>

    private lateinit var database: DatabaseReference

    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        database = FirebaseDatabase.getInstance().getReference("authors")

        editTextName = findViewById<View>(R.id.editTextName) as EditText
        editLocationName = findViewById<View>(R.id.editLocationName) as EditText
        listOfExpenses = findViewById<View>(R.id.listExpense) as ListView
        buttonAddExpense = findViewById<View>(R.id.buttonAddExpense) as Button

        expenses = ArrayList()
        uid = intent.getStringExtra(USER_ID)!!

        buttonAddExpense.setOnClickListener {
            addExpense()
        }


       listOfExpenses.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->

            val expense = expenses[i]


            val intent = Intent(applicationContext, ExpenseActivity::class.java)

            intent.putExtra(EXPENSE_ID, expense.expenseId)
            intent.putExtra(EXPENSE_NUM, expense.expenseNum)
            intent.putExtra(USER_ID, USER_ID)
            startActivity(intent)
        }

        listOfExpenses.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
            val expense = expenses[i]
            showUpdateDeleteDialog(expense.expenseId, expense.expenseNum)
            true
        }
    }

    private fun showUpdateDeleteDialog(expenseId: String, expenseNum: String) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.update_dialog, null)
        dialogBuilder.setView(dialogView)

        val editTextName = dialogView.findViewById<View>(R.id.editTextName) as EditText
        val editLocationName = dialogView.findViewById<View>(R.id.editLocationName) as EditText
        val buttonUpdate = dialogView.findViewById<View>(R.id.buttonUpdateAuthor) as Button
        val buttonDelete = dialogView.findViewById<View>(R.id.buttonDeleteAuthor) as Button

        dialogBuilder.setTitle(expenseNum)
        val b = dialogBuilder.create()
        b.show()


        buttonUpdate.setOnClickListener {
            val expense = editTextName.text.toString().trim{it <= ' '}
            val location = editLocationName.text.toString().trim{it <= ' '}
            if (!TextUtils.isEmpty(expense)){
                updateExpense(expenseId, uid, expense, location)
                b.dismiss()
            }
        }


        buttonDelete.setOnClickListener {
            deleteExpense(expenseId)
            b.dismiss()
        }
    }


    private fun addExpense() {
        val expense = editTextName.text.toString().trim { it <= ' ' }
        val location = editLocationName.text.toString().trim { it <= ' ' }
        if (!TextUtils.isEmpty(expense)) {
            val id = database.push().key
            val expense = Expense(id!!, expense, location)
            database.child(uid).child(id).setValue(expense)
            editTextName.setText("")
            Toast.makeText(this, "Thank you. Your expense has been successfully added", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Please enter an expense", Toast.LENGTH_LONG).show()
        }
    }


    private fun updateExpense(id: String, uid: String, expense: String, loc: String): Boolean {
        val dR = FirebaseDatabase.getInstance().getReference("authors").child(uid).child(id)
        val expense = Expense(id, expense, loc)
        dR.setValue(expense)
        Toast.makeText(applicationContext, "Your expense has become updated", Toast.LENGTH_LONG).show()
        return true
    }


    private fun deleteExpense(id: String): Boolean {
        val dR = FirebaseDatabase.getInstance().getReference("authors").child(uid).child(id)
        dR.removeValue()
        val drTitles = FirebaseDatabase.getInstance().getReference("titles").child(uid).child(id)
        drTitles.removeValue()
        Toast.makeText(applicationContext, "Success! This expense has been deleted", Toast.LENGTH_LONG).show()
        return true
    }

    override fun onStart() {
        super.onStart()

        database.addValueEventListener(object : ValueEventListener {
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

                val adapter = ExpenseList(this@DashboardActivity, expenses)
                listOfExpenses.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    companion object {
        const val TAG = "Lab-Firebase"
        const val EXPENSE_NUM = "com.example.tesla.myhomelibrary.authorname"
        const val EXPENSE_ID = "com.example.tesla.myhomelibrary.authorid"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
    }
}