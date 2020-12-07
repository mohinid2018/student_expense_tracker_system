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
import kotlin.collections.HashMap


class ExpenseTrackActivity : AppCompatActivity() {

    /* Keeps track of the total expense of current list of expenses
       inputted by user */
    private var totExp = 0F
    private lateinit var str: String
    private lateinit var dBExp: DatabaseReference
    internal lateinit var expenses: MutableList<Expense>

    /* Represents the add button by user to input general expense as well as
       location */
    private lateinit var bAdd: Button
    private lateinit var bDetails: Button

    /* Views that keep track of list of expenses as well as the view
     containing its total */
    internal lateinit var expListView: ListView
    private lateinit var totExpView: TextView

    /* EditTexts that represent where user inputs values for both expense
     & location */
    private lateinit var editTextExpenseValue: EditText
    private lateinit var editTextLocationName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_track)

        /* initializes all class variables according */
        dBExp = FirebaseDatabase.getInstance().getReference("expenses")
        totExp = 0F
        editTextExpenseValue = findViewById<View>(R.id.editTextExpenseValue) as EditText
        editTextLocationName = findViewById<View>(R.id.editTextLocationName) as EditText
        expListView = findViewById<View>(R.id.listViewExpenses) as ListView
        bAdd = findViewById<View>(R.id.buttonAddExpense) as Button
        bDetails = findViewById<View>(R.id.buttonCheckSort) as Button
        totExpView = findViewById<View>(R.id.totalExpensesView) as TextView

        expenses = ArrayList()
        str = intent.getStringExtra(USER_ID)!!

        /* If user clicks the add button --> go to the add Expense method */
        bAdd.setOnClickListener {
            addExpense()
        }

        bDetails.setOnClickListener {
            checkDetails()
        }

        expListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            val expense = expenses[i]
            showUpdateDeleteDialog(expense.expenseID, expense.expenseValue, expense.locationName)
            true
        }

    }

    private fun showUpdateDeleteDialog(
        expenseID: String,
        expenseValue: String,
        locationName: String
    ) {
        // hide keyboard here
        val inputMethodMng2 = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodMng2.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

        /* Creates the screen in the form of an AlertDialog */
        val builder = AlertDialog.Builder(this)
        val layoutInflater = layoutInflater
        val dView = layoutInflater.inflate(R.layout.layout_update_delete, null)
        builder.setView(dView)

        /* Update and delete buttons */
        val bUpdate = dView.findViewById<View>(R.id.buttonUpdateExpense) as Button
        val bDelete = dView.findViewById<View>(R.id.buttonDeleteExpense) as Button

        /* Sets up the two field values, expense and location, within this particular
        * screen --> Its default will be the last expenseValue and last locationName
        * inputted by user*/
        val editTextUpdateExpenseValue = dView.findViewById<View>(R.id.editTextUpdateExpenseValue) as EditText
        var expenseStr = "%.2f".format(expenseValue.toFloat())
        editTextUpdateExpenseValue.setText("$expenseStr")

        val editTextUpdateLocationName = dView.findViewById<View>(R.id.editTextUpdateLocationName) as EditText
        editTextUpdateLocationName.setText(locationName)

        /* Sets the title of the screen to something like $10.10 (current expense)
          at Lowes (location name)*/
        builder.setTitle("$$expenseStr at $locationName")


        val b = builder.create()
        b.show()

        var oldExpenseValue = expenseValue.toFloat()

        /* If update is clicked, the updateExpense method is called and the two fields
               * will be reset to reflect one or both of the changed values by user*/
        bUpdate.setOnClickListener {
            val expense = editTextUpdateExpenseValue.text.toString().trim{it <= ' '}
            val location = editTextUpdateLocationName.text.toString().trim{it <= ' '}

            /* Regex -- Allows for text to contain a number with 0, 1, or 2 decimal points */
            val expenseRegex = Regex("\\d+" + "(\\.\\d{1,2})?")


            if (!TextUtils.isEmpty(expense) && !TextUtils.isEmpty(location)) {

                /* Uses regex as condition before update */
                if (expenseRegex.matches((expense))) {
                    updateExpense(expenseID, str, expense, location, oldExpenseValue)
                    b.dismiss()
                } else {
                    /* If user fails to meet the regex requirements, this toast is shown */
                    Toast.makeText(
                        this,
                        "Enter the expense as a whole number or with 1-2 decimal places",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }

        /* If delete is clicked, the deleteExpense method is called and the view with the two fields
       * will be deleted*/
        bDelete.setOnClickListener {
            deleteExpense(expenseID, oldExpenseValue)
            b.dismiss()
        }

        // hide keyboard here
        val inputMethodMng = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodMng.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    private fun addExpense() {
        var expense = editTextExpenseValue.text.toString().trim { it <= ' ' }

        /* Ignore the first character ($) */
        if (expense.isNotEmpty()) {
            expense = expense.substring(1)
        }

        val loc =  editTextLocationName.text.toString().trim { it <= ' ' }

        /* Checks if not empty before using regex to properly add the value */
        if (expense != "$" && !TextUtils.isEmpty(expense) && !TextUtils.isEmpty((loc))) {
            val expenseRegex = Regex("\\d+" + "(\\.\\d{1,2})?")

            if (expenseRegex.matches((expense))) {

                val id = dBExp.push().key
                val expenseObj = Expense(id!!, expense, loc)
                val newExpense = expense.toFloat()

                /* Accommodates the new expense into the total sum accounted by app */
                totExp += newExpense
                //if (totExp != newExpense)
                    //totExpView.text = "Total Expenses: $$totExp"
                dBExp.child(str).child(id).setValue(expenseObj)
                editTextExpenseValue.setText("")
                editTextLocationName.setText("")

                /* Success Toast */
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

    /* Is called in the dialog method above; used to update a certain expense and/or location */
    private fun updateExpense(
        id: String,
        uid: String,
        expense: String,
        location: String,
        oldExpenseValue: Float
    ): Boolean {
        /* goes to particular place */
        val dB = FirebaseDatabase.getInstance().getReference("expenses").child(uid).child(id)
        /* creates new expense object */
        val expObj = Expense(id, expense, location)
        var newExpenseValue = expense.toFloat()

        /* Updates the total expense value */
        totExp -= oldExpenseValue
        totExp += newExpenseValue
        //totExpView.text = "Total Expenses: $$totExp"

        /* places new object in our database */
        dB.setValue(expObj)

        /* Success Toast */
        Toast.makeText(applicationContext, "Expense Updated", Toast.LENGTH_LONG).show()
        return true
    }

    private fun deleteExpense(id: String, oldExpenseValue: Float): Boolean {
        /* goes to particular place in database */
        val dB = FirebaseDatabase.getInstance().getReference("expenses").child(str).child(id)

        /* updates the total value in both the total expense and deletes instance in database*/
        totExp -= oldExpenseValue
        totExpView.text = "Total Expenses: $$totExp"
        dB.removeValue()

        /* Success Toast */
        Toast.makeText(applicationContext, "Expense Deleted", Toast.LENGTH_LONG).show()
        return true
    }

    private fun checkDetails() {
        // create map
        var finalList: ArrayList<String> = ArrayList()
        var finalMap: HashMap<String, Float> = HashMap()
        for (expense in expenses) {
            if (finalMap.containsKey(expense.locationName.toUpperCase())) {
                Log.i("TAG","Entering if 1")
                if (finalMap[expense.locationName.toUpperCase()] != null) {
                    Log.i("TAG","Entering if 2")
                    var oldVal = finalMap[expense.locationName.toUpperCase()]
                    var newVal = oldVal?.plus(expense.expenseValue.toFloat()) as Float

                    finalMap[expense.locationName.toUpperCase()] = newVal
                }

            } else {
                Log.i("TAG","Entering else")
                finalMap[expense.locationName.toUpperCase()] = expense.expenseValue.toFloat()
            }
            //finalList.add(expense.locationName + " " + expense.expenseValue)
        }

        // loop thru map for size of map
        for (idx in 0..finalMap.size-1) {
            // find largest key val
            var maxKey = findMaxKey(finalMap)

            // add key val to list
            finalList.add(maxKey + ": $" + ("%.2f".format(finalMap[maxKey])))

            // remove key val from map
            finalMap.remove((maxKey))
        }




        //for (key in finalMap.keys) {
         //   finalList.add(key + " $" + (finalMap[key].toString()))
       // }


        //val names = arrayOf("A", "B", "C", "D")
        val finalArr = finalList.toTypedArray()
        val alertDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val convertView = inflater.inflate(R.layout.layout_check_details, null) as View
        alertDialog.setView(convertView)
        alertDialog.setTitle("Where You Spend The Most")
        val lv = convertView.findViewById<View>(R.id.listViewExpenses) as ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, finalArr)
        lv.adapter = adapter
        alertDialog.show()
    }

    private fun findMaxKey(finalMap : HashMap<String, Float>) : String {
        var currMaxKey = ""
        var currMaxExpense = 0F

        for (key in finalMap.keys) {
            if (finalMap[key]!! > currMaxExpense) {
                currMaxKey = key
                currMaxExpense = finalMap[key]!!
            }
        }

        return currMaxKey
    }

    override fun onStart() {
        super.onStart()
        /* Once user clicks on first box -- an automatic '$' sign will appear */
        editTextExpenseValue.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editTextExpenseValue.setText("$")
                editTextExpenseValue.setSelection(1)
            }
        }

        /* Lets database keep track of adds of expense */
        dBExp.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                expenses.clear()

                var expense: Expense? = null
                for (postSnapshot in dataSnapshot.child(str).children) {
                    try {
                        expense = postSnapshot.getValue(Expense::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    } finally {
                        /* Incorporates the Expense object into list */
                        expenses.add(expense!!)
                    }
                }

                val expenseAdapter = ExpenseList(this@ExpenseTrackActivity, expenses)
                expListView.adapter = expenseAdapter


                /* Adds up all the current expenses together into one number */
                var expenseSum = 0F

                for (expense in expenses) {
                    Log.i("TAG", expense.expenseValue)

                    expenseSum += expense.expenseValue.toFloat()
                }

                /* Incorporates this total into the text of the total expense view */
                val expenseSumStr = "%.2f".format(expenseSum)
                totExpView.text = "Total Expenses: $$expenseSumStr"
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