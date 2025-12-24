package com.name.petmemo.di

import androidx.room.*
import androidx.room.migration.Migration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.name.petmemo.data.model.WeightEntry
import com.name.petmemo.data.dao.ChecklistDao
import com.name.petmemo.data.dao.ContactDao
import com.name.petmemo.data.dao.DocumentDao
import com.name.petmemo.data.dao.ExpenseDao
import com.name.petmemo.data.dao.MedicalRecordDao
import com.name.petmemo.data.dao.MoodDao
import com.name.petmemo.data.dao.PetDao
import com.name.petmemo.data.dao.PetTaskDao
import com.name.petmemo.data.dao.TrainingDao
import com.name.petmemo.data.dao.WeightDao
import com.name.petmemo.data.model.Contact
import com.name.petmemo.data.model.DateConverter
import com.name.petmemo.data.model.Document
import com.name.petmemo.data.model.Expense
import com.name.petmemo.data.model.MedicalRecord
import com.name.petmemo.data.model.MoodEntry
import com.name.petmemo.data.model.Pet
import com.name.petmemo.data.model.PetTask
import com.name.petmemo.data.model.RecordNote
import com.name.petmemo.data.model.StringListConverter
import com.name.petmemo.data.model.TaskCategoryConverter
import com.name.petmemo.data.model.TrainingLog

@Entity(tableName = "user_checklists")
data class UserChecklist(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)

@Entity(
    tableName = "user_checklist_items",
    indices = [Index(value = ["checklistId"])],
    foreignKeys = [ForeignKey(
        entity = UserChecklist::class,
        parentColumns = ["id"],
        childColumns = ["checklistId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class UserChecklistItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val checklistId: Int,
    val text: String,
    val isChecked: Boolean = false
)

@Database(
    entities = [
        UserChecklist::class, UserChecklistItem::class, MoodEntry::class, Contact::class,
        TrainingLog::class, Pet::class, PetTask::class, Expense::class,
        WeightEntry::class, MedicalRecord::class, RecordNote::class, Document::class
    ],
    version = 18,
    exportSchema = false
)
@TypeConverters(TaskCategoryConverter::class, DateConverter::class, StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao
    abstract fun petTaskDao(): PetTaskDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun weightDao(): WeightDao
    abstract fun medicalRecordDao(): MedicalRecordDao
    abstract fun documentDao(): DocumentDao
    abstract fun trainingDao(): TrainingDao
    abstract fun contactDao(): ContactDao
    abstract fun moodDao(): MoodDao
    abstract fun checklistDao(): ChecklistDao

    companion object {


        val MIGRATION_1_2 = object : Migration(1, 2) { override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `tasks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `petId` INTEGER NOT NULL, `date` INTEGER NOT NULL, `category` TEXT NOT NULL, `title` TEXT NOT NULL, `note` TEXT, `isCompleted` INTEGER NOT NULL DEFAULT 0)")
            database.execSQL("DROP TABLE IF EXISTS `vaccinations`")
            database.execSQL("DROP TABLE IF EXISTS `vet_visits`")
        } }
        val MIGRATION_2_3 = object : Migration(2, 3) { override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE expenses_new (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `petId` INTEGER NOT NULL, `date` INTEGER NOT NULL, `amount` REAL NOT NULL, `category` TEXT NOT NULL, `description` TEXT)")
            database.execSQL("INSERT INTO expenses_new (id, petId, date, amount, category, description) SELECT id, petId, date, amount, CASE category WHEN 'FOOD' THEN 'Корм и лакомства' WHEN 'TOYS' THEN 'Игрушки и амуниция' WHEN 'VET' THEN 'Ветеринар' WHEN 'HYGIENE' THEN 'Гигиена и уход' ELSE 'Другое' END, description FROM expenses")
            database.execSQL("DROP TABLE expenses")
            database.execSQL("ALTER TABLE expenses_new RENAME TO expenses")
        } }
        val MIGRATION_3_4 = object : Migration(3, 4) { override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `tasks` ADD COLUMN `reminderDateTime` INTEGER")
        } }
        val MIGRATION_4_5 = object : Migration(4, 5) { override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE `pets` ADD COLUMN `type_new` TEXT NOT NULL DEFAULT 'Собака'")
            database.execSQL("UPDATE `pets` SET `type_new` = CASE `type` WHEN 'DOG' THEN 'Собака' WHEN 'CAT' THEN 'Кошка' ELSE 'Другое' END")
            database.execSQL("CREATE TABLE `pets_new` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `photoUri` TEXT, `type` TEXT NOT NULL, `gender` TEXT NOT NULL, `breed` TEXT, `color` TEXT, `birthDate` INTEGER, `familyDate` INTEGER, `isCastrated` INTEGER NOT NULL, `hasMicrochip` INTEGER NOT NULL, `note` TEXT)")
            database.execSQL("INSERT INTO `pets_new` (id, name, photoUri, type, gender, breed, color, birthDate, familyDate, isCastrated, hasMicrochip, note) SELECT id, name, photoUri, `type_new`, gender, breed, color, birthDate, familyDate, isCastrated, hasMicrochip, note FROM `pets`")
            database.execSQL("DROP TABLE `pets`")
            database.execSQL("ALTER TABLE `pets_new` RENAME TO `pets`")
        } }
        val MIGRATION_5_6 = object : Migration(5, 6) { override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `weight_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `petId` INTEGER NOT NULL, `date` INTEGER NOT NULL, `weightKg` REAL NOT NULL)")
        } }


        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `medical_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `petId` INTEGER NOT NULL, `date` INTEGER NOT NULL, `place` TEXT, `specialist` TEXT, `symptoms` TEXT NOT NULL, `diagnosis` TEXT, `comment` TEXT)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `record_notes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `recordId` INTEGER NOT NULL, `date` INTEGER NOT NULL, `note` TEXT NOT NULL, FOREIGN KEY(`recordId`) REFERENCES `medical_records`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)")
            }
        }
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `documents` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `petId` INTEGER NOT NULL, 
                        `name` TEXT NOT NULL, 
                        `filePath` TEXT NOT NULL, 
                        `dateAdded` INTEGER NOT NULL
                    )
                """)
            }
        }
        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
            }
        }
        val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `training_logs` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `petId` INTEGER NOT NULL, 
                        `date` INTEGER NOT NULL, 
                        `commandName` TEXT NOT NULL, 
                        `status` TEXT NOT NULL, 
                        `notes` TEXT
                    )
                """)
            }
        }

        val MIGRATION_11_12 = object : Migration(11, 12) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `contacts` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `name` TEXT NOT NULL, 
                        `phone` TEXT NOT NULL, 
                        `type` TEXT NOT NULL, 
                        `notes` TEXT
                    )
                """)
            }
        }
        val MIGRATION_12_13 = object : Migration(12, 13) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `mood_entries` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `petId` INTEGER NOT NULL, 
                        `date` INTEGER NOT NULL, 
                        `mood` TEXT NOT NULL
                    )
                """)
            }
        }
        val MIGRATION_13_14 = object : Migration(13, 14) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `pets` ADD COLUMN `showTrainingLog` INTEGER NOT NULL DEFAULT 1")
            }
        }
        val MIGRATION_14_15 = object : Migration(14, 15) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }
        val MIGRATION_15_16 = object : Migration(15, 16) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            CREATE TABLE medical_records_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                petId INTEGER NOT NULL,
                date INTEGER NOT NULL,
                title TEXT NOT NULL,         -- НОВОЕ ПОЛЕ
                symptoms TEXT NOT NULL,
                diagnosis TEXT,
                treatment TEXT,
                vetName TEXT,                -- НОВОЕ ПОЛЕ
                clinicName TEXT,             -- НОВОЕ ПОЛЕ
                notes TEXT,
                attachments TEXT NOT NULL DEFAULT '[]' -- НОВОЕ ПОЛЕ
            )
        """)

                database.execSQL("""
            INSERT INTO medical_records_new (
                id, petId, date, title, symptoms, diagnosis, treatment, 
                vetName, clinicName, notes, attachments
            )
            SELECT
                id,
                petId,
                date,
                'Медицинская запись',  -- ✅ ИСПРАВЛЕНИЕ: Заглушка для нового поля title
                symptoms,
                diagnosis,
                'Не указано',          -- ✅ ИСПРАВЛЕНИЕ: Заглушка для treatment
                specialist,            -- ✅ ИСПРАВЛЕНИЕ: Старый specialist становится vetName
                place,                 -- ✅ ИСПРАВЛЕНИЕ: Старый place становится clinicName
                comment,               -- ✅ ИСПРАВЛЕНИЕ: Старый comment становится notes
                '[]'                   -- ✅ ИСПРАВЛЕНИЕ: Заглушка для attachments
            FROM medical_records
        """)
                database.execSQL("DROP TABLE medical_records")
                database.execSQL("ALTER TABLE medical_records_new RENAME TO medical_records")
            }
        }
        val MIGRATION_16_17 = object : Migration(16, 17) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            CREATE INDEX IF NOT EXISTS `index_user_checklist_items_checklistId` 
            ON `user_checklist_items` (`checklistId`)
        """)
            }
        }

    }
}