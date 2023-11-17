package com.mirego.debugpanel.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mirego.debugpanel.Settings
import com.mirego.debugpanel.settings
import com.mirego.debugpanel.usecase.DebugPanelItemViewData
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import context
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SpecificDebugPanelRepositoryImplTest {

    @BeforeTest
    fun setup() {
        Settings.initialize(context)
    }

    @Test
    fun `given a repository expect default values to be returned`() = runTest {
        val repository = TestRepositoryDebugPanelRepositoryImpl()

        assertNull(repository.getToggle().first())
        assertEquals(false, repository.getCurrentToggleValue("toggle", false))

        assertNull(repository.getTextField().first())
        assertEquals("", repository.getCurrentTextFieldValue("textField", ""))

        assertNull(repository.getPicker().first())
        assertNull(repository.getCurrentPickerValue("picker"))
    }

    @Test
    fun `given a repository with updated values expect the values to be returned`() = runTest {
        val toggleItemViewData: DebugPanelItemViewData.Toggle = mockk { every { identifier } returns "toggle" }
        val textFieldItemViewData: DebugPanelItemViewData.TextField = mockk { every { identifier } returns "textField" }
        val pickerItemViewData: DebugPanelItemViewData.Picker = mockk { every { identifier } returns "picker" }

        val repository = TestRepositoryDebugPanelRepositoryImpl()

        repository.onToggleUpdated(toggleItemViewData, true)
        repository.onTextFieldUpdated(textFieldItemViewData, "newText")
        repository.onPickerUpdated(pickerItemViewData, "item1")

        advanceUntilIdle()

        assertTrue(repository.getToggle().first() == true)
        assertEquals(true, repository.getCurrentToggleValue("toggle", false))

        assertTrue(repository.getTextField().first() == "newText")
        assertEquals("newText", repository.getCurrentTextFieldValue("textField", ""))

        assertTrue(repository.getPicker().first() == "item1")
        assertEquals("item1", repository.getCurrentPickerValue("picker"))
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
        }
        confirmVerified(settings)
    }

    private fun mockSettings(): Pair<ObservableSettings, FlowSettings> {
        val flowSettings = mockk<FlowSettings>()
        mockkStatic(ObservableSettings::toFlowSettings)

        val observableSettings = mockk<ObservableSettings> {
            every { toFlowSettings(any()) } returns flowSettings
        }
        mockkStatic("com.mirego.debugpanel.SettingsKt")

        every { settings } returns observableSettings

        return observableSettings to flowSettings
    }
}
