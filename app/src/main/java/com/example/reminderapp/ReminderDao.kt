package com.example.reminderapp

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg reminder: ReminderEntity)

    @Query("SELECT * FROM reminders")
    fun getAll(): LiveData<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE time LIKE :time")
    fun findByTime(time: String): LiveData<List<ReminderEntity>>

    @Query("DELETE FROM reminders")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Update
    suspend fun updateTodo(vararg reminders: ReminderEntity)
}