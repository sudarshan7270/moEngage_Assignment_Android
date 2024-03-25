package com.example.moengage_assignment_android.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moengage_assignment_android.R
import com.example.moengage_assignment_android.adapter.NewsItemAdapter

class HeadingBasedOnSourceActivity : AppCompatActivity() {

    private lateinit var rvNewsTitle: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_heading_based_on_source)
        rvNewsTitle=findViewById<RecyclerView>(R.id.rvNewsTitle)
//        val adapter = NewsItemAdapter(context, articles)
//        rvNewsTitle.adapter = adapter

    }
}