package com.mirego.debugpanel.sample.repository

import com.mirego.debugpanel.annotations.DebugProperty
import kotlinx.coroutines.flow.flowOf
import repository.SampleRepositoryImplFirstNameInputDelegate
import repository.SampleRepositoryImplIntDelegate
import repository.SampleRepositoryImplIntFlowDelegate
import repository.SampleRepositoryImplLastNameInputDelegate

class SampleRepositoryImpl : SampleRepository {
    @DebugProperty("firstNameInput")
    val firstNameInputInternal = "first name from SampleRepositoryImpl"

    @DebugProperty("lastNameInput")
    val lastNameInputInternal = flowOf("last name from SampleRepositoryImpl")

    @DebugProperty("int")
    val intInternal = 123

    @DebugProperty("intFlow")
    val intFlowInternal = flowOf(123)

    override val firstNameInput by SampleRepositoryImplFirstNameInputDelegate
    override val lastNameInput by SampleRepositoryImplLastNameInputDelegate

    override val int by SampleRepositoryImplIntDelegate
    override val intFlow by SampleRepositoryImplIntFlowDelegate

    override val otherField = "otherField"
}
