package com.mirego.debugpanel.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mirego.debugpanel.service.settings
import com.mirego.debugpanel.usecase.DebugPanelItemViewData
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DebugPanelRepositoryImplTest {

    @Test
    fun `given a repository expect null values to be returned`() = runTest {
        val (settings, flowSettings) = mockSettings()

        every { settings.getBooleanOrNull("toggle") } returns null
        every { flowSettings.getBooleanOrNullFlow("toggle") } returns flowOf(null)

        every { settings.getStringOrNull("textField") } returns null
        every { flowSettings.getStringOrNullFlow("textField") } returns flowOf(null)

        every { settings.getStringOrNull("picker") } returns null
        every { flowSettings.getStringOrNullFlow("picker") } returns flowOf(null)

        every { settings.getLongOrNull("datePicker") } returns null
        every { flowSettings.getLongOrNullFlow("datePicker") } returns flowOf(null)

        val repository = TestRepositoryDebugPanelRepositoryImpl()

        assertNull(repository.getToggle().first())
        assertNull(repository.getCurrentToggleValue("toggle"))

        assertNull(repository.getTextField().first())
        assertNull(repository.getCurrentTextFieldValue("textField"))

        assertNull(repository.getPicker().first())
        assertNull(repository.getCurrentPickerValue("picker"))

        assertNull(repository.getDatePicker().first())
        assertNull(repository.getCurrentDatePickerValue("datePicker"))

        verify(exactly = 1) {
            settings.getBooleanOrNull("toggle")
            flowSettings.getBooleanOrNullFlow("toggle")

            settings.getStringOrNull("textField")
            flowSettings.getStringOrNullFlow("textField")

            settings.getStringOrNull("picker")
            flowSettings.getStringOrNullFlow("picker")

            settings.getLongOrNull("datePicker")
            flowSettings.getLongOrNullFlow("datePicker")
        }
    }

    @Test
    fun `given a repository with updated values expect the values to be returned`() = runTest {
        val (settings, flowSettings) = mockSettings()

        every { settings.getBooleanOrNull("toggle") } returns true
        every { flowSettings.getBooleanOrNullFlow("toggle") } returns flowOf(true)

        every { settings.getStringOrNull("textField") } returns "newText"
        every { flowSettings.getStringOrNullFlow("textField") } returns flowOf("newText")

        every { settings.getStringOrNull("picker") } returns "item1"
        every { flowSettings.getStringOrNullFlow("picker") } returns flowOf("item1")

        every { settings.getLongOrNull("datePicker") } returns 123
        every { flowSettings.getLongOrNullFlow("datePicker") } returns flowOf(123)

        val toggleItemViewData: DebugPanelItemViewData.Toggle = mockk { every { identifier } returns "toggle" }
        val textFieldItemViewData: DebugPanelItemViewData.TextField = mockk { every { identifier } returns "textField" }
        val pickerItemViewData: DebugPanelItemViewData.Picker = mockk { every { identifier } returns "picker" }
        val datePickerItemViewData: DebugPanelItemViewData.DatePicker = mockk { every { identifier } returns "datePicker" }

        val repository = TestRepositoryDebugPanelRepositoryImpl()

        repository.onToggleUpdated(toggleItemViewData, true)
        repository.onTextFieldUpdated(textFieldItemViewData, "newText")
        repository.onPickerUpdated(pickerItemViewData, "item1")
        repository.onDatePickerUpdated(datePickerItemViewData, 123)

        advanceUntilIdle()

        assertTrue(repository.getToggle().first() == true)
        assertEquals(true, repository.getCurrentToggleValue("toggle"))

        assertTrue(repository.getTextField().first() == "newText")
        assertEquals("newText", repository.getCurrentTextFieldValue("textField"))

        assertTrue(repository.getPicker().first() == "item1")
        assertEquals("item1", repository.getCurrentPickerValue("picker"))

        assertTrue(repository.getDatePicker().first() == 123L)
        assertEquals(123L, repository.getCurrentDatePickerValue("datePicker"))

        verify(exactly = 1) {
            settings.getBooleanOrNull("toggle")
            settings.putBoolean("toggle", true)
            flowSettings.getBooleanOrNullFlow("toggle")

            settings.getStringOrNull("textField")
            settings.putString("textField", "newText")
            flowSettings.getStringOrNullFlow("textField")

            settings.getStringOrNull("picker")
            settings.putString("picker", "item1")
            flowSettings.getStringOrNullFlow("picker")

            settings.getLongOrNull("datePicker")
            settings.putLong("datePicker", 123)
            flowSettings.getLongOrNullFlow("datePicker")
        }
    }

    @Test
    fun `given a default identifier expect it to be passed to settings`() = runTest {
        val (_, flowSettings) = mockSettings()

        val repository = TestRepositoryDebugPanelRepositoryImpl()

        repository.getToggle().firstOrNull()

        verify(exactly = 1) {
            flowSettings.getBooleanOrNullFlow("toggle")
        }
        confirmVerified(flowSettings)
    }

    @Test
    fun `given a custom identifier expect it to be passed to settings`() = runTest {
        val (_, flowSettings) = mockSettings()

        val repository = TestRepositoryIdentifierDebugPanelRepositoryImpl()

        repository.getToggle().firstOrNull()

        verify(exactly = 1) {
            flowSettings.getBooleanOrNullFlow("TOGGLE_IDENTIFIER")
        }
        confirmVerified(flowSettings)
    }

    @Test
    fun `given a config with reset button expect it to be created correctly`() = runTest {
        val (settings, _) = mockSettings()

        val repository = TestRepositoryResetButtonDebugPanelRepositoryImpl()

        repository.resetSettings()

        verify(exactly = 1) {
            settings.remove("TOGGLE_IDENTIFIER")
            settings.remove("textField")
            settings.remove("picker")
            settings.remove("enum")
            settings.remove("datePicker")
        }
        confirmVerified(settings)
    }

    private fun mockSettings(): Pair<ObservableSettings, FlowSettings> {
        val flowSettings = mockk<FlowSettings>()
        mockkStatic(ObservableSettings::toFlowSettings)

        val observableSettings = mockk<ObservableSettings> {
            every { toFlowSettings(any()) } returns flowSettings
        }
        mockkStatic("com.mirego.debugpanel.service.SettingsKt")

        every { settings } returns observableSettings

        return observableSettings to flowSettings
    }
}
