package com.example.data.di

import com.example.data.repository.MealRepository
import com.example.data.repository.MealRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton


@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepository(
        repository: MealRepositoryImpl
    ): MealRepository
}