package br.santo.gymly.core.data

import PrepopulateCallback
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.santo.gymly.features.exercises.data.Exercise
import br.santo.gymly.features.exercises.data.ExerciseDao
import br.santo.gymly.features.exercises.data.ExerciseTypeConverter
import br.santo.gymly.features.routines.data.Routine
import br.santo.gymly.features.routines.data.RoutineDao

// BUMP THE VERSION NUMBER FROM 1 TO 2
@Database(
    entities = [
    Exercise::class,
    Routine::class
                     ], version = 3, exportSchema = false)

@TypeConverters(ExerciseTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun routineDao(): RoutineDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE
                    ?: synchronized(this) {
                        val instance =
                                Room.databaseBuilder(
                                                context.applicationContext,
                                                AppDatabase::class.java,
                                                "gym_database"
                                        )
                                        .addCallback(PrepopulateCallback(context))
                                        .fallbackToDestructiveMigration()
                                        .build()
                        INSTANCE = instance
                        instance
                    }
        }
    }
}
