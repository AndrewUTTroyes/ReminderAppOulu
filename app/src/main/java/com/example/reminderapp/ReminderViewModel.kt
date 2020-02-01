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
    // LiveData gives us updated words when they change.
    val allReminders: LiveData<List<ReminderEntity>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val reminderDao = ReminderDB.invoke(application, viewModelScope).ReminderDao()
        repository = ReminderRepository(reminderDao)
        allReminders = repository.allReminder
    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(reminder: ReminderEntity) = viewModelScope.launch {
        repository.insert(reminder)
    }
}