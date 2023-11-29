package com.mirego.debugpanel.sample.repository

import com.mirego.debugpanel.annotations.DebugProperty
import com.mirego.debugpanel.annotations.DisplayName
import com.mirego.debugpanel.annotations.Identifier
import com.mirego.debugpanel.sample.Language
import kotlinx.coroutines.flow.flowOf
import repository.SampleRepositoryImplFirstNameInputDelegate
import repository.SampleRepositoryImplIntDelegate
import repository.SampleRepositoryImplIntFlowDelegate
import repository.SampleRepositoryImplLanguageDelegate
import repository.SampleRepositoryImplLastNameInputDelegate
import repository.SampleRepositoryImplToggleDelegate
import repository.SampleRepositoryImplToggleFlowDelegate

class SampleRepositoryImpl : SampleRepository {
    @DisplayName("First name input")
    @DebugProperty("firstNameInput")
    val firstNameInputInternal = "first name from SampleRepositoryImpl"

    @DisplayName("Last name input")
    @DebugProperty("lastNameInput")
    val lastNameInputInternal = flowOf("last name from SampleRepositoryImpl")

    @DebugProperty("int")
    val intInternal = 123

    @DebugProperty("intFlow")
    val intFlowInternal = flowOf(123)

    @Identifier("languageKey")
    @DisplayName("Language")
    @DebugProperty("language")
    val languageInternal = flowOf(Language.FRENCH)

    override val firstNameInput by SampleRepositoryImplFirstNameInputDelegate
    override val lastNameInput by SampleRepositoryImplLastNameInputDelegate

    override val int by SampleRepositoryImplIntDelegate
    override val intFlow by SampleRepositoryImplIntFlowDelegate

    override val language by SampleRepositoryImplLanguageDelegate

    override val otherField = "otherField"

    @DebugProperty("toggle")
    val toggleInternal = true

    @DebugProperty("toggleFlow")
    val toggleFlowInternal = flowOf(true)

    override val toggle by SampleRepositoryImplToggleDelegate
    override val toggleFlow by SampleRepositoryImplToggleFlowDelegate
}
