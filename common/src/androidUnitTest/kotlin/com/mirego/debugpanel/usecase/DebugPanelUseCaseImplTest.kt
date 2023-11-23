package com.mirego.debugpanel.usecase

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mirego.debugpanel.config.DebugPanelPickerItem
import com.mirego.debugpanel.extensions.button
import com.mirego.debugpanel.extensions.datePicker
import com.mirego.debugpanel.extensions.label
import com.mirego.debugpanel.extensions.picker
import com.mirego.debugpanel.extensions.textField
import com.mirego.debugpanel.extensions.toggle
import com.mirego.debugpanel.repository.TestUseCaseDebugPanelRepository
import com.mirego.debugpanel.repository.TestUseCaseDisplayNameDebugPanelRepository
import com.mirego.debugpanel.repository.TestUseCaseIdentifierDebugPanelRepository
import com.mirego.debugpanel.repository.TestUseCaseResetButtonDebugPanelRepository
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

enum class TestEnum {
    VALUE_0,
    VALUE_1
}

@RunWith(AndroidJUnit4::class)
class SpecificDebugPanelUseCaseImplTest {

    @Test
    fun `when creating view data with default values expect the correct methods to be called on repository and view data is built correctly`() = runTest {
        val repository: TestUseCaseDebugPanelRepository = mockk()
        val useCase = TestUseCaseDebugPanelUseCaseImpl(repository)

        val pickerItems: List<DebugPanelPickerItem> = listOf(mockk(), mockk())
        val buttonAction = {}

        every { repository.getCurrentPickerValue("picker") } returns "initial picker"
        every { repository.getCurrentPickerValue("enum") } returns "initial enum"
        every { repository.getCurrentDatePickerValue("datePicker") } returns 123

        val viewData = useCase.createViewData(
            initialToggle = true,
            initialTextField = "textField value",
            initialPicker = "item1",
            initialEnum = TestEnum.VALUE_1,
            button = buttonAction,
            picker = pickerItems,
            label = flowOf("label value"),
            initialDatePicker = 123L
        )

        assertEquals(7, viewData.items.size)

        val toggle = viewData.items[0].toggle
        val button = viewData.items[1].button
        val textField = viewData.items[2].textField
        val picker = viewData.items[3].picker
        val label = viewData.items[4].label
        val enumPicker = viewData.items[5].picker
        val datePicker = viewData.items[6].datePicker

        assertEquals("toggle", toggle.identifier)
        assertEquals("toggle", toggle.label)

        assertEquals("button", button.identifier)
        assertEquals("button", button.label)
        assertEquals(buttonAction, button.action)

        assertEquals("textField", textField.identifier)
        assertEquals("textField", textField.placeholder)

        assertEquals("picker", picker.identifier)
        assertEquals("picker", picker.label)
        assertEquals("initial picker", picker.initialValue)
        assertEquals(pickerItems, picker.items)

        assertEquals("label", label.identifier)
        assertEquals("label", label.label)
        assertEquals("label value", label.value.first())

        assertEquals("enum", enumPicker.identifier)
        assertEquals("enum", enumPicker.label)
        assertEquals("initial enum", enumPicker.initialValue)
        val expectedEnumPickerItems = listOf(
            DebugPanelPickerItem("VALUE_0", "Value_0"),
            DebugPanelPickerItem("VALUE_1", "Value_1")
        )
        assertEquals(expectedEnumPickerItems, enumPicker.items)

        assertEquals("datePicker", datePicker.identifier)
        assertEquals("datePicker", datePicker.label)
        assertEquals(123, datePicker.initialValue)

        verify(exactly = 1) {
            repository.getCurrentToggleValue("toggle")
            repository.getCurrentTextFieldValue("textField")
            repository.getCurrentPickerValue("picker")
            repository.getCurrentPickerValue("enum")
            repository.getCurrentDatePickerValue("datePicker")
        }

        confirmVerified(repository)
    }

    @Test
    fun `when creating view data without default values for pickers expect the correct methods to be called on repository and view data is built correctly`() = runTest {
        val repository: TestUseCaseDebugPanelRepository = mockk()
        val useCase = TestUseCaseDebugPanelUseCaseImpl(repository)

        every { repository.getCurrentPickerValue("picker") } returns null
        every { repository.getCurrentPickerValue("enum") } returns null
        every { repository.getCurrentDatePickerValue("datePicker") } returns null

        val viewData = useCase.createViewData(
            initialToggle = false,
            initialTextField = "",
            initialPicker = "item1",
            initialEnum = TestEnum.VALUE_1,
            button = {},
            picker = emptyList(),
            label = flowOf(),
            initialDatePicker = 123
        )

        assertEquals(7, viewData.items.size)

        val picker = viewData.items[3].picker
        val enumPicker = viewData.items[5].picker
        val datePicker = viewData.items[6].datePicker

        assertEquals("item1", picker.initialValue)
        assertEquals("VALUE_1", enumPicker.initialValue)
        assertEquals(123, datePicker.initialValue)

        verify(exactly = 1) {
            repository.getCurrentPickerValue("picker")
            repository.getCurrentPickerValue("enum")
            repository.getCurrentDatePickerValue("datePicker")
        }
    }

    @Test
    fun `expect display name to be used correctly`() = runTest {
        val repository: TestUseCaseDisplayNameDebugPanelRepository = mockk()
        val useCase = TestUseCaseDisplayNameDebugPanelUseCaseImpl(repository)

        val viewData = useCase.createViewData(
            initialToggle = false,
            initialTextField = "",
            initialPicker = "item1",
            initialEnum = TestEnum.VALUE_1,
            button = {},
            picker = emptyList(),
            label = flowOf(),
            initialDatePicker = 123
        )

        assertEquals(7, viewData.items.size)

        assertEquals("Test toggle", viewData.items[0].toggle.label)
        assertEquals("Test button", viewData.items[1].button.label)
        assertEquals("Test text field", viewData.items[2].textField.placeholder)
        assertEquals("Test picker", viewData.items[3].picker.label)
        assertEquals("Test label", viewData.items[4].label.label)
        assertEquals("Test enum", viewData.items[5].picker.label)
        assertEquals("Test date picker", viewData.items[6].datePicker.label)
    }

    @Test
    fun `expect custom identifier to be used correctly`() = runTest {
        val repository: TestUseCaseIdentifierDebugPanelRepository = mockk()
        val useCase = TestUseCaseIdentifierDebugPanelUseCaseImpl(repository)

        val viewData = useCase.createViewData(
            initialToggle = false,
            initialTextField = "",
            initialPicker = "item1",
            initialEnum = TestEnum.VALUE_1,
            button = {},
            picker = emptyList(),
            label = flowOf("label value"),
            initialDatePicker = 123
        )

        assertEquals(7, viewData.items.size)

        val toggle = viewData.items[0].toggle
        val button = viewData.items[1].button
        val textField = viewData.items[2].textField
        val picker = viewData.items[3].picker
        val label = viewData.items[4].label
        val enumPicker = viewData.items[5].picker
        val datePicker = viewData.items[6].datePicker

        assertEquals("TOGGLE_KEY", toggle.identifier)
        assertEquals("BUTTON_KEY", button.identifier)
        assertEquals("TEXT_FIELD_KEY", textField.identifier)
        assertEquals("PICKER_KEY", picker.identifier)
        assertEquals("LABEL_KEY", label.identifier)
        assertEquals("ENUM_KEY", enumPicker.identifier)
        assertEquals("DATE_PICKER_KEY", datePicker.identifier)

        verify(exactly = 1) {
            repository.getCurrentToggleValue("TOGGLE_KEY")
            repository.getCurrentTextFieldValue("TEXT_FIELD_KEY")
            repository.getCurrentPickerValue("PICKER_KEY")
            repository.getCurrentPickerValue("ENUM_KEY")
            repository.getCurrentDatePickerValue("DATE_PICKER_KEY")
        }

        confirmVerified(repository)
    }

    @Test
    fun `expect reset button to be created correctly`() = runTest {
        val repository: TestUseCaseResetButtonDebugPanelRepository = mockk()
        val useCase = TestUseCaseResetButtonDebugPanelUseCaseImpl(repository)

        val viewData = useCase.createViewData(initialToggle = false)

        assertEquals(2, viewData.items.size)

        val resetButton = viewData.items[1].button

        assertEquals("_reset", resetButton.identifier)

        resetButton.action()

        verify(exactly = 1) {
            repository.resetSettings()
        }
    }
}
