package com.wulidanxi.mcenter.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase




@Database(version = 2, entities = [Content::class])
abstract class PasswordDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao

    companion object {
        private var instance: PasswordDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): PasswordDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                PasswordDatabase::class.java, "password_database"
            )
                //.addMigrations(MIGRATION_1_2)
                //.fallbackToDestructiveMigration()
                .build().apply {
                    instance = this
                }
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }
    }
}
