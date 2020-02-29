package com.example.reminderapp

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random


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

    companion object {
        val CHANNEL_ID = "REMINDER_NOTIFICATION_CHANNEL"
        var notificationId = 1589
        fun showNotification(context: Context, message: String){
            var notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm_24px)
                .setContentTitle("Reminder")
                .setContentText(message)
                .setStyle( NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context?.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context?.getString(R.string.app_name)
                }
                notificationManager.createNotificationChannel(channel)
            }
            val notification = notificationId+ Random(notificationId).nextInt(1,30)
            notificationManager.notify(notification, notificationBuilder.build())


        }
    }
}
