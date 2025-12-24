package com.name.petmemo.data.model
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.name.petmemo.R

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date


enum class Mood(val stringResId: Int, val emoji: String, val score: Int) {
    HAPPY(R.string.mood_happy, "😄", 5),
    PLAYFUL(R.string.mood_playful, "🥳", 4),
    CALM(R.string.mood_calm, "🙂", 3),
    ANXIOUS(R.string.mood_anxious, "😟", 2),
    SAD(R.string.mood_sad, "😔", 1);

    @Composable
    fun getDisplayName(): String {
        return stringResource(id = this.stringResId)
    }

    companion object {
        fun fromScore(score: Int): Mood? {
            return values().find { it.score == score }
        }
    }
}


@Entity(
    tableName = "mood_entries",
    foreignKeys = [ForeignKey(
        entity = Pet::class,
        parentColumns = ["id"],
        childColumns = ["petId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["petId"])]
)
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val petId: Int,
    val mood: Mood,
    val date: Date,
    val note: String?
)
