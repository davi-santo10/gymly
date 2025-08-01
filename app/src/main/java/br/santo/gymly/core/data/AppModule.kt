package br.santo.gymly.core.data

import android.content.Context
import br.santo.gymly.features.routines.data.RoutineDao
import br.santo.gymly.features.routines.data.RoutinesRepository
import br.santo.gymly.features.routines.data.ExerciseGroupDao
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.ExerciseDao
import br.santo.gymly.features.routines.ui.createroutine.exercisesList.data.ExerciseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
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
    fun provideExerciseGroupDao(appDatabase: AppDatabase): ExerciseGroupDao {
        return appDatabase.exerciseGroupDao()
    }

    @Provides
    @Singleton
    fun provideRoutinesRepository(
        routineDao: RoutineDao,
        exerciseGroupDao: ExerciseGroupDao,
        exerciseDao: ExerciseDao
    ): RoutinesRepository {
        return RoutinesRepository(routineDao, exerciseGroupDao, exerciseDao)
    }

    @Provides
    @Singleton
    fun provideExerciseRepository(exerciseDao: ExerciseDao): ExerciseRepository {
        return ExerciseRepository(exerciseDao)
    }
}