package com.mirego.debugpanel.usecase

import com.mirego.debugpanel.Consts.NUMBER_OF_DEBUG_PROPERTIES
import com.mirego.debugpanel.config.DebugPanelPickerItem
import com.mirego.debugpanel.extensions.button
import com.mirego.debugpanel.extensions.datePicker
import com.mirego.debugpanel.extensions.labelItem
import com.mirego.debugpanel.extensions.picker
import com.mirego.debugpanel.extensions.textField
import com.mirego.debugpanel.extensions.toggle
import com.mirego.debugpanel.repository.TestUseCaseDebugPanelRepository
import com.mirego.debugpanel.repository.TestUseCaseDisplayNameDebugPanelRepository
import com.mirego.debugpanel.repository.TestUseCaseIdentifierDebugPanelRepository
import com.mirego.debugpanel.repository.TestUseCaseVisibilityDebugPanelRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

enum class TestEnum {
    VALUE_0,
    VALUE_1
}

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
        ).first()

        assertEquals(7 + NUMBER_OF_DEBUG_PROPERTIES, viewData.items.size)

        val toggle = viewData.items[0].toggle
        val button = viewData.items[1].button
        val textField = viewData.items[2].textField
        val picker = viewData.items[3].picker
        val label = viewData.items[4].labelItem
        val enumPicker = viewData.items[5].picker
        val datePicker = viewData.items[6].datePicker
        val stringValueTextField = viewData.items[7].textField
        val stringFlowValueTextField = viewData.items[8].textField
        val enumValuePicker = viewData.items[9].picker
        val enumFlowValuePicker = viewData.items[10].picker
        val booleanValueToggle = viewData.items[11].toggle
        val booleanFlowValueToggle = viewData.items[12].toggle

        assertEquals("toggle", toggle.identifier)
        assertEquals("toggle", toggle.label)

        assertEquals("button", button.identifier)
        assertEquals("button", button.label)
        assertEquals(buttonAction, button.action)

        assertEquals("textField", textField.identifier)
        assertEquals("textField", textField.label)

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

        assertEquals("stringValueKey", stringValueTextField.identifier)
        assertEquals("stringValue", stringValueTextField.label)

        assertEquals("stringFlowValueKey", stringFlowValueTextField.identifier)
        assertEquals("stringFlowValue", stringFlowValueTextField.label)

        assertEquals("enumValueKey", enumValuePicker.identifier)
        assertEquals("enumValue", enumValuePicker.label)
        assertEquals(expectedEnumPickerItems, enumValuePicker.items)

        assertEquals("enumFlowValueKey", enumFlowValuePicker.identifier)
        assertEquals("enumFlowValue", enumFlowValuePicker.label)
        assertEquals(expectedEnumPickerItems, enumFlowValuePicker.items)

        assertEquals("booleanValueKey", booleanValueToggle.identifier)
        assertEquals("booleanValue", booleanValueToggle.label)

        assertEquals("booleanFlowValueKey", booleanFlowValueToggle.identifier)
        assertEquals("booleanFlowValue", booleanFlowValueToggle.label)
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
        ).first()

        assertEquals(7 + NUMBER_OF_DEBUG_PROPERTIES, viewData.items.size)

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
        ).first()

        assertEquals(7 + NUMBER_OF_DEBUG_PROPERTIES, viewData.items.size)

        assertEquals("Test toggle", viewData.items[0].toggle.label)
        assertEquals("Test button", viewData.items[1].button.label)
        assertEquals("Test text field", viewData.items[2].textField.label)
        assertEquals("Test picker", viewData.items[3].picker.label)
        assertEquals("Test label", viewData.items[4].labelItem.label)
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
        ).first()

        assertEquals(7 + NUMBER_OF_DEBUG_PROPERTIES, viewData.items.size)

        val toggle = viewData.items[0].toggle
        val button = viewData.items[1].button
        val textField = viewData.items[2].textField
        val picker = viewData.items[3].picker
        val label = viewData.items[4].labelItem
        val enumPicker = viewData.items[5].picker
        val datePicker = viewData.items[6].datePicker

        assertEquals("TOGGLE_KEY", toggle.identifier)
        assertEquals("BUTTON_KEY", button.identifier)
        assertEquals("TEXT_FIELD_KEY", textField.identifier)
        assertEquals("PICKER_KEY", picker.identifier)
        assertEquals("LABEL_KEY", label.identifier)
        assertEquals("ENUM_KEY", enumPicker.identifier)
        assertEquals("DATE_PICKER_KEY", datePicker.identifier)
    }

    @Test
    fun `expect isDirty to be false when no values are returned`() = runTest {
        val repository: TestUseCaseDebugPanelRepository = mockk()
        val useCase = TestUseCaseDebugPanelUseCaseImpl(repository)

        val pickerItems: List<DebugPanelPickerItem> = listOf(mockk(), mockk())
        val buttonAction = {}

        every { repository.getToggle() } returns flowOf(null)
        every { repository.getTextField() } returns flowOf(null)
        every { repository.getPicker() } returns flowOf(null)
        every { repository.getEnum() } returns flowOf(null)
        every { repository.getDatePicker() } returns flowOf(null)

        val viewData = useCase.createViewData(
            initialToggle = true,
            initialTextField = "textField value",
            initialPicker = "item1",
            initialEnum = TestEnum.VALUE_1,
            button = buttonAction,
            picker = pickerItems,
            label = flowOf("label value"),
            initialDatePicker = 123L
        ).first()

        assertEquals(7 + NUMBER_OF_DEBUG_PROPERTIES, viewData.items.size)

        val toggle = viewData.items[0].toggle
        val button = viewData.items[1].button
        val textField = viewData.items[2].textField
        val picker = viewData.items[3].picker
        val label = viewData.items[4].labelItem
        val enumPicker = viewData.items[5].picker
        val datePicker = viewData.items[6].datePicker

        assertFalse(toggle.isDirty.first())
        assertFalse(button.isDirty.first())
        assertFalse(textField.isDirty.first())
        assertFalse(picker.isDirty.first())
        assertFalse(label.isDirty.first())
        assertFalse(enumPicker.isDirty.first())
        assertFalse(datePicker.isDirty.first())
    }

    @Test
    fun `expect isDirty to be true when values are returned`() = runTest {
        val repository: TestUseCaseDebugPanelRepository = mockk()
        val useCase = TestUseCaseDebugPanelUseCaseImpl(repository)

        val pickerItems: List<DebugPanelPickerItem> = listOf(mockk(), mockk())
        val buttonAction = {}

        every { repository.getToggle() } returns flowOf(false)
        every { repository.getTextField() } returns flowOf("")
        every { repository.getPicker() } returns flowOf("")
        every { repository.getEnum() } returns flowOf("")
        every { repository.getDatePicker() } returns flowOf(0)

        val viewData = useCase.createViewData(
            initialToggle = true,
            initialTextField = "textField value",
            initialPicker = "item1",
            initialEnum = TestEnum.VALUE_1,
            button = buttonAction,
            picker = pickerItems,
            label = flowOf("label value"),
            initialDatePicker = 123L
        ).first()

        assertEquals(7 + NUMBER_OF_DEBUG_PROPERTIES, viewData.items.size)

        val toggle = viewData.items[0].toggle
        val button = viewData.items[1].button
        val textField = viewData.items[2].textField
        val picker = viewData.items[3].picker
        val label = viewData.items[4].labelItem
        val enumPicker = viewData.items[5].picker
        val datePicker = viewData.items[6].datePicker

        assertTrue(toggle.isDirty.first())
        assertFalse(button.isDirty.first())
        assertTrue(textField.isDirty.first())
        assertTrue(picker.isDirty.first())
        assertFalse(label.isDirty.first())
        assertTrue(enumPicker.isDirty.first())
        assertTrue(datePicker.isDirty.first())
    }

    @Test
    fun `expect component to be excluded from list if visibility is set to false`() = runTest {
        val repository: TestUseCaseVisibilityDebugPanelRepository = mockk()
        val useCase = TestUseCaseVisibilityDebugPanelUseCaseImpl(repository)

        val viewData = useCase.createViewData(
            button1 = {},
            button2 = {},
            componentsVisibility = flowOf(
                TestUseCaseVisibilityDebugPanelComponentsVisibility(
                    button1 = false
                )
            )
        ).first()

        assertEquals(1 + NUMBER_OF_DEBUG_PROPERTIES, viewData.items.size)

        val button = viewData.items[0].button

        assertEquals("button2", button.identifier)
    }
}
