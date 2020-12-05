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
    private lateinit var buttonAddAuthor: Button
    internal lateinit var listViewAuthors: ListView

    internal lateinit var authors: MutableList<Author>

    private lateinit var databaseAuthors: DatabaseReference

    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        //getting the reference of artists node
        databaseAuthors = FirebaseDatabase.getInstance().getReference("authors")

        editTextName = findViewById<View>(R.id.editTextName) as EditText
        editLocationName = findViewById<View>(R.id.editLocationName) as EditText
        listViewAuthors = findViewById<View>(R.id.listViewAuthors) as ListView
        buttonAddAuthor = findViewById<View>(R.id.buttonAddAuthor) as Button

        authors = ArrayList()
        uid = intent.getStringExtra(USER_ID)!!

        buttonAddAuthor.setOnClickListener {
            addAuthor()
        }

        //attaching listener to ListView
        listViewAuthors.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            //getting the selected artist
            val author = authors[i]

            //creating an intent
            val intent = Intent(applicationContext, AuthorActivity::class.java)

            intent.putExtra(AUTHOR_ID, author.authorId)
            intent.putExtra(AUTHOR_NAME, author.authorName)
            intent.putExtra(USER_ID, USER_ID)
            startActivity(intent)
        }

        listViewAuthors.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
            val author = authors[i]
            showUpdateDeleteDialog(author.authorId, author.authorName)
            true
        }
    }

    private fun showUpdateDeleteDialog(authorId: String, authorName: String) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.update_dialog, null)
        dialogBuilder.setView(dialogView)

        val editTextName = dialogView.findViewById<View>(R.id.editTextName) as EditText
        val editLocationName = dialogView.findViewById<View>(R.id.editLocationName) as EditText
        val buttonUpdate = dialogView.findViewById<View>(R.id.buttonUpdateAuthor) as Button
        val buttonDelete = dialogView.findViewById<View>(R.id.buttonDeleteAuthor) as Button

        dialogBuilder.setTitle(authorName)
        val b = dialogBuilder.create()
        b.show()

        // TODO: Set update listener
        buttonUpdate.setOnClickListener {
            val name = editTextName.text.toString().trim{it <= ' '}
            // val country = spinnerCountry.selectedItem.toString()
            val country = editLocationName.text.toString().trim{it <= ' '}
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(country)){
                updateAuthor(authorId, uid, name, country)
                b.dismiss()
            }
        }

        // TODO: Set delete listener
        buttonDelete.setOnClickListener {
            deleteAuthor(authorId)
            b.dismiss()
        }
    }

    // TODO: Add an author
    private fun addAuthor() {
        val name = editTextName.text.toString().trim { it <= ' ' }
        // val country = editLocationName.selectedItem.toString()
        val country =  editLocationName.text.toString().trim { it <= ' ' }
        if (!TextUtils.isEmpty(name)) {
            val id = databaseAuthors.push().key
            val author = Author(id!!, name, country)
            databaseAuthors.child(uid).child(id).setValue(author)
            editTextName.setText("")
            Toast.makeText(this, "Author added", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show()
        }
    }

    // TODO: Update an author
    private fun updateAuthor(id: String, uid: String, name: String, country: String): Boolean {
        val dR = FirebaseDatabase.getInstance().getReference("authors").child(uid).child(id)
        val author = Author(id, name, country)
        dR.setValue(author)
        Toast.makeText(applicationContext, "Author Updated", Toast.LENGTH_LONG).show()
        return true
    }

    // TODO: Delete an author
    private fun deleteAuthor(id: String): Boolean {
        val dR = FirebaseDatabase.getInstance().getReference("authors").child(uid).child(id)
        dR.removeValue()
        val drTitles = FirebaseDatabase.getInstance().getReference("titles").child(uid).child(id)
        drTitles.removeValue()
        Toast.makeText(applicationContext, "Author Deleted", Toast.LENGTH_LONG).show()
        return true
    }

    override fun onStart() {
        super.onStart()

        databaseAuthors.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                authors.clear()

                var author: Author? = null
                for (postSnapshot in dataSnapshot.child(uid).children) {
                    try {
                        author = postSnapshot.getValue(Author::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    } finally {
                        authors.add(author!!)
                    }
                }

                val authorAdapter = AuthorList(this@DashboardActivity, authors)
                listViewAuthors.adapter = authorAdapter
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