package br.santo.gymly.core.data

import PrepopulateCallback
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import br.santo.gymly.features.routines.data.ExerciseGroup
import br.santo.gymly.features.routines.data.ExerciseGroupDao
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.Exercise
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.ExerciseDao
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.ExerciseTypeConverter
import br.santo.gymly.features.routines.data.Routine
import br.santo.gymly.features.routines.data.RoutineDao
import br.santo.gymly.features.routines.data.RoutineExerciseCrossRef

@Database(
    entities = [
        Exercise::class,
        Routine::class,
        RoutineExerciseCrossRef::class,
        ExerciseGroup::class
    ],
    version = 9,
    exportSchema = false
)


@TypeConverters(ExerciseTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun routineDao(): RoutineDao
    abstract fun exerciseGroupDao(): ExerciseGroupDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new exercise_groups table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `exercise_groups` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `routineId` INTEGER NOT NULL,
                        `name` TEXT NOT NULL,
                        `restTimeSeconds` INTEGER NOT NULL,
                        `order` INTEGER NOT NULL,
                        `type` TEXT NOT NULL
                    )
                """.trimIndent())

                database.execSQL("""
                    ALTER TABLE `RoutineExerciseCrossRef` 
                    ADD COLUMN `groupId` INTEGER
                """.trimIndent())

                database.execSQL("""
                    ALTER TABLE `RoutineExerciseCrossRef` 
                    ADD COLUMN `orderInGroup` INTEGER NOT NULL DEFAULT 0
                """.trimIndent())

            }
        }

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
                                    .addMigrations(MIGRATION_9_10)
                                        .build()
                        INSTANCE = instance
                        instance
                    }
        }
    }
}
