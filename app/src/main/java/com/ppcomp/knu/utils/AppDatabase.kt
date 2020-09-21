package com.ppcomp.knu.utils

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ppcomp.knu.`object`.noticeData.Notice
import com.ppcomp.knu.`object`.noticeData.NoticeDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room Database 클래스
 * @author 정준
 */
@Database(entities = [Notice::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noticeDao(): NoticeDao

    companion object {
        private val DB_NAME = "room-db"
        private var instance: AppDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
            return instance ?: synchronized(this) {
                val instanceDB = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(NoticeDatabaseCallback(scope))
                    .build()
                instance = instanceDB

                instanceDB
            }
        }
    }

    private class NoticeDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            instance?.let { database ->
                scope.launch(Dispatchers.IO) {
                    var noticeDao = database.noticeDao()

                }
            }
        }
    }


}