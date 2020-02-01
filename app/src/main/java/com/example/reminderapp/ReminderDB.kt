package com.example.reminderapp

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = arrayOf(ReminderEntity::class),
    version = 1,
    exportSchema = false
)
abstract class ReminderDB : RoomDatabase(){
    abstract fun ReminderDao(): ReminderDao

    private class ReminderDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var reminderDao = database.ReminderDao()
                    reminderDao.deleteAll()

                    // Add sample words.
                    var reminder = ReminderEntity(1,12,"Oulu","UNI")
                    reminderDao.insertAll(reminder)
                    reminder = ReminderEntity(2,20,"Oulu","PSOAS")
                    reminderDao.insertAll(reminder)
                }
            }
        }
    }

    companion object {
        @Volatile private var INSTANCE: ReminderDB? = null

        fun invoke(
            context: Context,
            scope: CoroutineScope
        ): ReminderDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReminderDB::class.java,
                    "reminder_database"
                )
                    .addCallback(ReminderDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}