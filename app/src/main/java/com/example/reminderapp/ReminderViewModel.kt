package com.example.reminderapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// Class extends AndroidViewModel and requires application as a parameter.
class ReminderViewModel(application: Application) : AndroidViewModel(application) {


    // The ViewModel maintains a reference to the repository to get data.
    private val repository: ReminderRepository
    val allReminders: LiveData<List<ReminderEntity>>

    init {
        val reminderDao = ReminderDB.invoke(application, viewModelScope).ReminderDao()
        repository = ReminderRepository(reminderDao)
        allReminders = repository.allReminder
    }
    fun insert(reminder: ReminderEntity) = viewModelScope.launch {
        repository.insert(reminder)
    }
}
