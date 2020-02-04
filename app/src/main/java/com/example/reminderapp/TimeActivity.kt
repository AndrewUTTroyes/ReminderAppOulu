package com.example.reminderapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_time.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import java.util.*

class TimeActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)

        timeButton.setOnClickListener{

            val calendar = GregorianCalendar(
                datePicker.year,
                datePicker.month,
                datePicker.dayOfMonth,
                timePicker.hour,
                timePicker.minute)

            if(editText.text.isNotEmpty() && calendar.timeInMillis > System.currentTimeMillis()){
                val reminder = ReminderEntity(null,
                    calendar.timeInMillis,
                    null,
                    editText.text.toString()
                )
                var reminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)
                reminderViewModel.insert(reminder)

                setAlarm(reminder.time!!,reminder.message)
                finish()
            }else{
                toast("Wrong data")
            }


        }
    }

    private fun setAlarm(time: Long, message: String){
        val intent = Intent(this, ReminderReceiver::class.java)
        intent.putExtra("message",message)

        val pendingIntent = PendingIntent.getBroadcast(this, 1,intent,PendingIntent.FLAG_ONE_SHOT)

        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setExact(AlarmManager.RTC,time,pendingIntent)

        runOnUiThread { toast("reminder is created") }
    }
}
