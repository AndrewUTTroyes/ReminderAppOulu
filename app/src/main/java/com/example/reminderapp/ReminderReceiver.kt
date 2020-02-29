package com.example.reminderapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast

class ReminderReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val uid = intent?.getIntExtra("uid",0)
        val text = intent?.getStringExtra("message")
        context?.toast(text.toString())

        if (context != null) {
            MainActivity.showNotification(context,text!!)
        }

        GlobalScope.launch {
            val db = context?.let { ReminderDB.invoke(it,this).ReminderDao()
            }
            db?.delete(uid)
        }


    }
}