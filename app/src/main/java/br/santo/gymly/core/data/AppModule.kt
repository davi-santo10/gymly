package br.santo.gymly.core.data

import android.content.Context
import br.santo.gymly.features.routines.data.RoutineDao
import br.santo.gymly.features.routines.data.RoutinesRepository
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.ExerciseDao
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.ExerciseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // This means dependencies live as long as the app
object AppModule {

    @Provides
    @Singleton // Use @Singleton to ensure only one instance of the database is created
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideRoutineDao(appDatabase: AppDatabase): RoutineDao {
        return appDatabase.routineDao()
    }

    @Provides
    fun provideExerciseDao(appDatabase: AppDatabase): ExerciseDao {
        return appDatabase.exerciseDao()
    }

    @Provides
    @Singleton // Only one instance of the repository
    fun provideRoutinesRepository(routineDao: RoutineDao): RoutinesRepository {
        return RoutinesRepository(routineDao)
    }

    @Provides
    @Singleton // Only one instance of the repository
    fun provideExerciseRepository(exerciseDao: ExerciseDao): ExerciseRepository {
        return ExerciseRepository(exerciseDao)
    }
}