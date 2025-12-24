package com.name.petmemo.ui.components

import androidx.compose.runtime.Composable
import com.name.petmemo.R
import androidx.compose.ui.res.stringResource
import com.name.petmemo.data.model.Gender
@Composable
fun getGenderString(gender: Gender): String {
    return when (gender) {
        Gender.MALE -> stringResource(R.string.gender_male)
        Gender.FEMALE -> stringResource(R.string.gender_female)
        Gender.UNKNOWN -> stringResource(R.string.gender_unknown)
    }
}

fun stringToGender(genderString: String?): Gender {
    if (genderString == null) return Gender.UNKNOWN
    return try {
        Gender.valueOf(genderString.uppercase())
    } catch (e: IllegalArgumentException) {
        Gender.UNKNOWN
    }
}