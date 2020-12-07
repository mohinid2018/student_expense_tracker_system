package com.example.studentexpensetracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegistrationActivity : AppCompatActivity() {


    private var mAuth: FirebaseAuth? = null

    /* the email and password of the register screen */
    private var email: EditText? = null
    private var uPwd: EditText? = null

    /* This variable represents the register button */
    private var rBtn: Button? = null

    private var progress: ProgressBar? = null

    /* Initializes above variables appropriately */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        mAuth = FirebaseAuth.getInstance()

        email = findViewById(R.id.email)
        uPwd = findViewById(R.id.password)
        rBtn = findViewById(R.id.register)
        progress = findViewById(R.id.progressBar)

        /* If user clicks register button, it runs the method below */
        rBtn!!.setOnClickListener { registerNewUser() }
    }

    /* Like login, it serves as a checkpoint
       Except instead of with existing users, this
       is for the new users
     */
    private fun registerNewUser() {
        progress!!.visibility = View.VISIBLE

        val email: String = email!!.text.toString()
        val password: String = uPwd!!.text.toString()

        /* Invalid email */
        if (!validEmail(email)) {
            /* Fail Toast & doesn't register */
            Toast.makeText(applicationContext, "Please enter a valid email...", Toast.LENGTH_LONG).show()
            return
        }

        /* Invalid password */
        if (validPassword(password) <= 0) {

            /* There are many ways to have a failed password. Therefore, the fail toasts here
               provides detailed failure messages & again, this does not register
             */
            var ret = validPassword(password);
            when (ret) {
                0 -> {
                    Toast.makeText(applicationContext, "Password field appears to be empty. Please reenter your password.", Toast.LENGTH_LONG).show()
                }
                -1 -> {
                    Toast.makeText(applicationContext, "Passwords must be between 6 to 12 characters inclusive. Please try again.", Toast.LENGTH_LONG).show()
                }
                else -> {
                    Toast.makeText(applicationContext, "Passwords must be a combination of letters, numbers, and symbols. Please try again.", Toast.LENGTH_LONG).show()
                }
            }
            return
        }

        /* Final Check */
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                /* If user doesn't exist yet */
                if (task.isSuccessful) {

                    /* hide keyboard here */
                    val inputMethodMng = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodMng.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

                    /* Success Toast */
                    Toast.makeText(applicationContext, "Registration successful!", Toast.LENGTH_LONG).show()
                    progress!!.visibility = View.GONE

                    val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    /* Otherwise, Fail Toast */
                    Toast.makeText(applicationContext, "Registration failed! Please try again later", Toast.LENGTH_LONG).show()
                    progress!!.visibility = View.GONE
                }
            }
    }

    /* Verifies the email using RFC 5322 Official Standard */
    private fun validEmail(email: String?) : Boolean {
        if (email.isNullOrEmpty()) {
            return false
        }

        val emailRegex = Regex("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'" +
                "*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x" +
                "5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z" +
                "0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4" +
                "][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z" +
                "0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|" +
                "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])")

        return emailRegex.matches(email)
    }

    /* Checks if password is between 6 to 12 (inclusive) and is a combination of letters, numbers
       and symbols
     */
    private fun validPassword(password: String?) : Int {
        if (password == null)
            return 0
        if(password.length < 6 || password.length > 12)
            return -1
        var ind = 0
        var hasLetter = false
        var hasNumber = false
        var hasSymbol = false
        while(ind < password.length){
            if( password[ind] in 'a'..'z' || password[ind] in 'A'..'Z') {
                hasLetter = true
            } else if (password[ind] in '0'..'9') {
                hasNumber = true
            } else {
                hasSymbol = true
            }
            ind++
            if(hasLetter && hasNumber && hasSymbol)
                return 1
        }
        return -2
    }

}
