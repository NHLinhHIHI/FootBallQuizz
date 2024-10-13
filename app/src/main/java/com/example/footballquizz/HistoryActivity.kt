package com.example.footballquizz

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var listViewHistory: ListView
    private lateinit var historyAdapter: HistoryAdapter
    private var historyList = mutableListOf<ScoreModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        listViewHistory = findViewById(R.id.listViewHistory)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_history -> {
                    // Chuyển đến trang DifficultySelectionActivity
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                else -> false
            }
        }
        // Get the current logged-in user's email
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email

        if (userEmail == null) {
            Toast.makeText(this, "User is not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch scores from Firestore
        db.collection("score")
            .whereEqualTo("e-mail", userEmail)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val score = document.getString("point") ?: ""
                    val name = document.getString("name") ?: ""
                    val difficulty = document.getString("difficulty") ?: ""
                    val timeTaken = document.getString("timeTaken") ?: ""
                    val time = document.getString("time") ?: ""

                    val scoreModel = ScoreModel(
                        name = name,
                        score = score,
                        difficulty = difficulty,
                        timeTaken = timeTaken,
                        time = time
                    )
                    historyList.add(scoreModel)
                }

                // Set up adapter
                historyAdapter = HistoryAdapter(this, historyList)
                listViewHistory.adapter = historyAdapter
            }

            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to fetch history: $e", Toast.LENGTH_SHORT).show()
            }
    }
}