package com.name.petmemo.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import androidx.room.TypeConverter
import com.name.petmemo.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

enum class TaskCategory(val resourceId: Int) {
    VET_VISIT(R.string.task_category_vet_visit),
    VACCINATION(R.string.task_category_vaccination),
    GROOMING(R.string.task_category_grooming),
    MEDICATION(R.string.task_category_medication),
    TRAINING(R.string.task_category_training),
    SHOPPING(R.string.task_category_shopping),
    CUSTOM(R.string.task_category_custom)
}

@Composable
fun TaskCategory.getDisplayName(): String {
    return stringResource(this.resourceId)
}

class TaskCategoryConverter {
    @TypeConverter
    fun fromTaskCategory(category: TaskCategory): String {
        return category.name
    }

    @TypeConverter
    fun toTaskCategory(categoryName: String): TaskCategory {
        return TaskCategory.valueOf(categoryName)
    }
}
class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

class StringListConverter {
    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        // Преобразует List<String> в JSON-строку
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toStringList(data: String?): List<String>? {
        // Преобразует JSON-строку обратно в List<String>
        if (data == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(data, listType)
    }
}

@Entity(tableName = "tasks")
@TypeConverters(TaskCategoryConverter::class, DateConverter::class)
data class PetTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val petId: Int,
    val date: Date,
    var category: TaskCategory,
    var title: String? = null,
    var note: String? = null,
    var isCompleted: Boolean = false,
    var reminderDateTime: Date? = null,
    val dueDate: Date?
)