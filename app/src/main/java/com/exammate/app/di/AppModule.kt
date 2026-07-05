package com.exammate.app.di

import com.exammate.app.data.remote.firebase.FirebaseAuthSource
import com.exammate.app.data.remote.firebase.FirebaseRealtimeSource
import com.exammate.app.data.repository.AuthRepositoryImpl
import com.exammate.app.data.repository.DummyDataRepository
import com.exammate.app.data.repository.ExamRepositoryImpl
import com.exammate.app.domain.repository.AuthRepository
import com.exammate.app.domain.repository.ExamRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindExamRepository(
        impl: ExamRepositoryImpl
    ): ExamRepository
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuthSource(): FirebaseAuthSource = FirebaseAuthSource()

    @Provides
    @Singleton
    fun provideFirebaseRealtimeSource(): FirebaseRealtimeSource = FirebaseRealtimeSource()

    @Provides
    @Singleton
    fun provideDummyDataRepository(): DummyDataRepository = DummyDataRepository()
}
