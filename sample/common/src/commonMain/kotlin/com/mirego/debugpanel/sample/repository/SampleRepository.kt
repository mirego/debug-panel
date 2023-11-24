package com.mirego.debugpanel.sample.repository

import kotlinx.coroutines.flow.Flow

interface SampleRepository {
    val firstNameInput: String

    val lastNameInput: Flow<String>

    val otherField: String
}
