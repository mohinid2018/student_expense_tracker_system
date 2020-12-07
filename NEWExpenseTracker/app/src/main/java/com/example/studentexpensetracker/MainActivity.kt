package com.example.studentexpensetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    /* Represent the login and register buttons respectively */
    private var rBtn: Button? = null
    private var lBtn: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()

        /* If register is clicked, then run the RegistrationActivity file */
        rBtn!!.setOnClickListener {
            val intent = Intent(this@MainActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }

        /* If login is clicked, then run the LoginActivity file */
        lBtn!!.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeViews() {
        rBtn = findViewById(R.id.register)
        lBtn = findViewById(R.id.login)
    }
}