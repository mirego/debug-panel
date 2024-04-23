package com.mirego.debugpanel

import com.mirego.debugpanel.usecase.TestEnum
import io.mockk.every
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import mockSettings
import org.junit.Test

class DebugPropertyTest {
    private val repository: DebugPropertyRepository by lazy {
        DebugPropertyRepositoryImpl()
    }

    @Test
    fun `expect default property value to be returned correctly`() =
        runTest {
            val (observableSettings, flowSettings) = mockSettings()
            every { flowSettings.getStringOrNullFlow("stringFlowValueKey") } returns flowOf(null)
            every { observableSettings.getStringOrNull("enumValueKey") } returns null
            every { flowSettings.getStringOrNullFlow("enumFlowValueKey") } returns flowOf(null)
            every { flowSettings.getBooleanOrNullFlow("booleanFlowValueKey") } returns flowOf(null)

            assertEquals("stringValue", repository.stringValue)
            assertEquals("stringFlowValue", repository.stringFlowValue.first())
            assertEquals(TestEnum.VALUE_0, repository.enumValue)
            assertEquals(TestEnum.VALUE_0, repository.enumFlowValue.first())
            assertEquals(false, repository.booleanValue)
            assertEquals(false, repository.booleanFlowValue.first())
        }

    @Test
    fun `expect overridden property value to be returned correctly`() =
        runTest {
            val (observableSettings, flowSettings) = mockSettings()
            every { observableSettings.getString("stringValueKey", "stringValue") } returns "newValue"
            every { flowSettings.getStringOrNullFlow("stringFlowValueKey") } returns flowOf("newValue")
            every { observableSettings.getStringOrNull("enumValueKey") } returns TestEnum.VALUE_1.name
            every { flowSettings.getStringOrNullFlow("enumFlowValueKey") } returns flowOf(TestEnum.VALUE_1.name)
            every { observableSettings.getBoolean("booleanValueKey", false) } returns true
            every { flowSettings.getBooleanOrNullFlow("booleanFlowValueKey") } returns flowOf(true)

            assertEquals("newValue", repository.stringValue)
            assertEquals("newValue", repository.stringFlowValue.first())
            assertEquals(TestEnum.VALUE_1, repository.enumValue)
            assertEquals(TestEnum.VALUE_1, repository.enumFlowValue.first())
            assertEquals(true, repository.booleanValue)
            assertEquals(true, repository.booleanFlowValue.first())
        }
}
