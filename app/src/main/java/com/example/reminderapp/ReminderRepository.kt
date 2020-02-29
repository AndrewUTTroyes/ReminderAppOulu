package com.example.reminderapp

import androidx.lifecycle.LiveData

class ReminderRepository(private val reminderDao: ReminderDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allReminder: LiveData<List<ReminderEntity>> = reminderDao.getAll()

    suspend fun insert(reminder: ReminderEntity): Int {
        return reminderDao.insertAll(reminder).toInt()
    }
}