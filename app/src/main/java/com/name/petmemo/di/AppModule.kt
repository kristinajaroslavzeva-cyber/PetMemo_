package com.name.petmemo.di

import android.content.Context
import androidx.room.Room
import com.name.petmemo.billing.BillingClientWrapper
import com.name.petmemo.billing.GooglePlayBillingClientWrapper
import com.name.petmemo.data.*
import com.name.petmemo.data.repository.PetRepository
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_10_11
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_11_12
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_12_13
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_13_14
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_14_15
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_15_16
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_16_17
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_1_2
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_2_3
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_3_4
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_4_5
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_5_6
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_6_7
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_7_8
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_8_9
import com.name.petmemo.di.AppDatabase.Companion.MIGRATION_9_10
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindBillingClientWrapper(
        impl: GooglePlayBillingClientWrapper
    ): BillingClientWrapper

    companion object {

        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "pet_memo_database"
            )
                .addMigrations(
                    MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4,
                    MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7,
                    MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10,
                    MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13,
                    MIGRATION_13_14, MIGRATION_14_15, MIGRATION_15_16,
                    MIGRATION_16_17
                )
                .fallbackToDestructiveMigration()
                .build()
        }

        @Provides
        @Singleton
        fun providePetRepository(db: AppDatabase): PetRepository {
            return PetRepository(
                petDao = db.petDao(),
                petTaskDao = db.petTaskDao(),
                expenseDao = db.expenseDao(),
                weightDao = db.weightDao(),
                medicalRecordDao = db.medicalRecordDao(),
                documentDao = db.documentDao(),
                trainingDao = db.trainingDao(),
                contactDao = db.contactDao(),
                moodDao = db.moodDao(),
                checklistDao = db.checklistDao()
            )
        }
    }
}