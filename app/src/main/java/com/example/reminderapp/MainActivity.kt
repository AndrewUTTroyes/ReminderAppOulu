package com.example.reminderapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var reminderViewModel : ReminderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.list)
        val adapter = ReminderAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        reminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)
        reminderViewModel.allReminders.observe(this, Observer { reminders ->
            reminders?.let {adapter.setReminders(it)}
        })

        var fabOpened = false
        Fab.setOnClickListener{
            if(!fabOpened){
                fabOpened = true
                Fab_Map.animate().translationY(- resources.getDimension(R.dimen.standard_116))
                Fab_Time.animate().translationY(- resources.getDimension(R.dimen.standard_66))
            } else {
                fabOpened = false
                Fab_Map.animate().translationY(0f)
                Fab_Time.animate().translationY(0f)
            }
        }
        Fab_Time.setOnClickListener {
            val intent = Intent(applicationContext, TimeActivity::class.java)
            startActivity(intent)
        }

        Fab_Map.setOnClickListener{
            val intent = Intent(applicationContext, MapActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
    }
}
