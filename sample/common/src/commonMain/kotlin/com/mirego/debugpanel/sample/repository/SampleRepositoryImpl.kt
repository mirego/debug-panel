package com.mirego.debugpanel.sample.repository

import com.mirego.debugpanel.annotations.DebugProperty
import kotlinx.coroutines.flow.flowOf

class SampleRepositoryImpl : SampleRepository {
    @DebugProperty("firstNameInput")
    val firstNameInputInternal = "first name from SampleRepositoryImpl"

    @DebugProperty("lastNameInput")
    val lastNameInputInternal = flowOf("last name from SampleRepositoryImpl")

    override val firstNameInput by SampleRepositoryProperties

    override val lastNameInput by SampleRepositoryObservableProperties

    override val otherField = "otherField"
}
