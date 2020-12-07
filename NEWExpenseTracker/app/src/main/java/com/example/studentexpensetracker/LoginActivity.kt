package com.example.studentexpensetracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private var dR: DatabaseReference? = null
    private var dB: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    /* Represent the user email and password respectively */
    private var email: EditText? = null
    private var uPwd: EditText? = null

    /* login button */
    private var lBtn: Button? = null

    private var progress: ProgressBar? = null

    /* Initializes all the variables necessary to create the login screen */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dB = FirebaseDatabase.getInstance()
        dR = dB!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        email = findViewById(R.id.email)
        uPwd = findViewById(R.id.password)
        lBtn = findViewById(R.id.login)
        progress = findViewById(R.id.progressBar)

        /* Waits for user to click on login button before running the below method */
        lBtn!!.setOnClickListener { loginUserAccount() }
    }



    private fun loginUserAccount() {
        progress?.visibility ?:  View.VISIBLE
        val email: String = email?.text.toString()
        val uPwd: String = uPwd?.text.toString()

        /* Makes sure that an email is entered */
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(applicationContext, "Please enter email...", Toast.LENGTH_LONG).show()
            return
        }

        /* Makes sure that a password is entered */
        if (TextUtils.isEmpty(uPwd)) {
            Toast.makeText(applicationContext, "Please enter password!", Toast.LENGTH_LONG).show()
            return
        }

        /* Verification of inputted values */
        mAuth!!.signInWithEmailAndPassword(email, uPwd)
            .addOnCompleteListener { task ->
                progress?.visibility ?:  View.GONE
                /* If values are both correct */
                if (task.isSuccessful) {

                    /* hide keyboard here */
                    val inputMethodMng = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodMng.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

                    /* Success Toast & run ExpenseTrackActivity */
                    Toast.makeText(applicationContext, "Login successful!", Toast.LENGTH_LONG).show()

                    startActivity(
                        Intent(this@LoginActivity, ExpenseTrackActivity::class.java).putExtra(
                            USER_ID,  mAuth!!.currentUser?.uid))
                } else {
                    /* Otherwise, just Fail Toast */
                    Toast.makeText(
                        applicationContext,
                        "Login failed! Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    companion object {
        const val USER_ID = "com.example.tesla.myhomelibrary.userid"
    }
}