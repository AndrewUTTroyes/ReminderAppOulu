package com.example.reminderapp

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ReminderDao {
    @Transaction @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(reminder: ReminderEntity): Long

    @Query("SELECT * FROM reminders")
    fun getAll(): LiveData<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE time LIKE :time")
    fun findByTime(time: String): LiveData<List<ReminderEntity>>

    @Query("DELETE FROM reminders")
    suspend fun deleteAll()

    @Query("DELETE FROM reminders WHERE uid = :id")
    suspend fun delete(id: Int?)

    @Update
    suspend fun updateTodo(vararg reminders: ReminderEntity)
}