package com.drspaceman.atomicio.di

import android.content.Context
import com.drspaceman.atomicio.db.AtomicIoDao
import com.drspaceman.atomicio.db.AtomicIoDatabase
import com.drspaceman.atomicio.repository.AtomicIoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ApplicationComponent::class)
@Module
object DbModule {
    @Provides
    fun provideAtomicIoDao(@ApplicationContext appContext: Context) : AtomicIoDao {
        return AtomicIoDatabase.getInstance(appContext).atomicIoDao()
    }

    @Provides
    fun provideAtomicIoRepository(dao: AtomicIoDao) = AtomicIoRepository(dao)
}