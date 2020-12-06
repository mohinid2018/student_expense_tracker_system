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
import kotlinx.android.synthetic.main.activity_dashboard.view.*
import java.lang.Exception
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editLocationName: EditText
    private lateinit var buttonAddExpense: Button
    internal lateinit var listViewAuthors: ListView
    //NEW
    private lateinit var editTotalExpenses: TextView
    internal var totalExpenses: Float = 0F
    //NEW
    internal lateinit var expenses: MutableList<Expense>

    private lateinit var databaseExpenses: DatabaseReference

    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        //getting the reference of artists node
        databaseExpenses = FirebaseDatabase.getInstance().getReference("expenses")
        totalExpenses = 0F
        editTextName = findViewById<View>(R.id.editTextName) as EditText
        editLocationName = findViewById<View>(R.id.editLocationName) as EditText
        listViewAuthors = findViewById<View>(R.id.listViewAuthors) as ListView
        buttonAddExpense = findViewById<View>(R.id.buttonAddExpense) as Button
        editTotalExpenses = findViewById<View>(R.id.totalExpensesView) as TextView

        expenses = ArrayList()
        uid = intent.getStringExtra(USER_ID)!!

        buttonAddExpense.setOnClickListener {
            addExpense()
        }

        //attaching listener to ListView
        listViewAuthors.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            //getting the selected artist
            val author = expenses[i]

            //creating an intent
            val intent = Intent(applicationContext, AuthorActivity::class.java)

            intent.putExtra(AUTHOR_ID, author.expenseID)
            intent.putExtra(AUTHOR_NAME, author.expenseValue)
            intent.putExtra(USER_ID, USER_ID)
            startActivity(intent)
        }

        listViewAuthors.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
            val author = expenses[i]
            showUpdateDeleteDialog(author.expenseID, author.expenseValue)
            true
        }
    }

    private fun showUpdateDeleteDialog(expenseID: String, expenseValue: String) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.update_dialog, null)
        dialogBuilder.setView(dialogView)

        val editTextName = dialogView.findViewById<View>(R.id.editTextName) as EditText
        val editLocationName = dialogView.findViewById<View>(R.id.editLocationName) as EditText
        val buttonUpdate = dialogView.findViewById<View>(R.id.buttonUpdateAuthor) as Button
        val buttonDelete = dialogView.findViewById<View>(R.id.buttonDeleteAuthor) as Button

        dialogBuilder.setTitle(expenseValue)
        val b = dialogBuilder.create()
        b.show()

        var oldExpenseValue = expenseValue.toFloat()

        // TODO: Set update listener
        buttonUpdate.setOnClickListener {
            val expense = editTextName.text.toString().trim{it <= ' '}
            // val location = spinnerCountry.selectedItem.toString()
            val location = editLocationName.text.toString().trim{it <= ' '}
            if (!TextUtils.isEmpty(expense) && !TextUtils.isEmpty(location)){
                updateAuthor(expenseID, uid, expense, location, oldExpenseValue)
                b.dismiss()
            }
        }

        // TODO: Set delete listener
        buttonDelete.setOnClickListener {
            deleteAuthor(expenseID, oldExpenseValue)
            b.dismiss()
        }
    }

    // TODO: Add an author
    private fun addExpense() {
        val expense = editTextName.text.toString().trim { it <= ' ' }
        // val location = editLocationName.selectedItem.toString()
        val location =  editLocationName.text.toString().trim { it <= ' ' }
        if (!TextUtils.isEmpty(expense)) {
            val id = databaseExpenses.push().key
            val author = Expense(id!!, expense, location)
            val newExpense = expense.toFloat()
            totalExpenses += newExpense
            editTotalExpenses.text = "Total Expenses: $" + totalExpenses
            databaseExpenses.child(uid).child(id).setValue(author)
            editTextName.setText("")
            Toast.makeText(this, "Expense added", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Please enter an expense", Toast.LENGTH_LONG).show()
        }
    }

    // TODO: Update an author
    private fun updateAuthor(id: String, uid: String, expense: String, location: String, oldExpenseValue: Float): Boolean {
        val dR = FirebaseDatabase.getInstance().getReference("expenses").child(uid).child(id)
        val author = Expense(id, expense, location)
        var newExpenseValue = expense.toFloat()
        totalExpenses -= oldExpenseValue
        totalExpenses += newExpenseValue
        editTotalExpenses.text = "Total Expenses: $" + totalExpenses
        dR.setValue(author)
        Toast.makeText(applicationContext, "Author Updated", Toast.LENGTH_LONG).show()
        return true
    }

    // TODO: Delete an author
    private fun deleteAuthor(id: String, oldExpenseValue: Float): Boolean {
        val dR = FirebaseDatabase.getInstance().getReference("expenses").child(uid).child(id)
        totalExpenses -= oldExpenseValue
        editTotalExpenses.text = "Total Expenses: $" + totalExpenses
        dR.removeValue()
        val drTitles = FirebaseDatabase.getInstance().getReference("titles").child(uid).child(id)
        drTitles.removeValue()
        Toast.makeText(applicationContext, "Author Deleted", Toast.LENGTH_LONG).show()
        return true
    }

    override fun onStart() {
        super.onStart()

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
                
                val authorAdapter = AuthorList(this@DashboardActivity, expenses)
                listViewAuthors.adapter = authorAdapter
                var tempSum = 0F
                for(expense in expenses){
                    Log.i("TAG", expense.expenseValue)
                    tempSum += expense.expenseValue.toFloat()
                }
                editTotalExpenses.text = "Total Expenses: $" + tempSum
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    companion object {
        const val TAG = "Lab-Firebase"
        const val AUTHOR_NAME = "com.example.tesla.myhomelibrary.authorname"
        const val AUTHOR_ID = "com.example.tesla.myhomelibrary.authorid"
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
    }
}