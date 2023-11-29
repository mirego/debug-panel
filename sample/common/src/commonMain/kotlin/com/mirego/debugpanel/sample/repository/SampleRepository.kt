package com.mirego.debugpanel.sample.repository

import com.mirego.debugpanel.sample.Language
import kotlinx.coroutines.flow.Flow

interface SampleRepository {
    val firstNameInput: String

    val lastNameInput: Flow<String>

    val int: Int

    val intFlow: Flow<Int>

    val language: Flow<Language>

    val toggle: Boolean

    val toggleFlow: Flow<Boolean>

    val otherField: String
}
