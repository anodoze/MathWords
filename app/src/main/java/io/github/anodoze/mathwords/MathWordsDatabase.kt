package io.github.anodoze.mathwords

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Card::class, Answer::class], version = 1)
@TypeConverters(Converters::class)
abstract class MathWordsDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun answerDao(): AnswerDao

    companion object {
        @Volatile private var INSTANCE: MathWordsDatabase? = null

        fun getInstance(context: Context): MathWordsDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MathWordsDatabase::class.java,
                    "mathwords.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}