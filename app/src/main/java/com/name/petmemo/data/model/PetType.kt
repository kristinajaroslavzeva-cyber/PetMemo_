package com.name.petmemo.data.model

import com.name.petmemo.R

enum class PetType(val resourceId: Int) {
    DOG(R.string.pet_type_dog),
    CAT(R.string.pet_type_cat),
    BIRD(R.string.pet_type_bird),
    FISH(R.string.pet_type_fish),
    RODENT(R.string.pet_type_rodent),
    REPTILE(R.string.pet_type_reptile),
    OTHER(R.string.pet_type_other),
    UNKNOWN(R.string.pet_type_unknown);
}