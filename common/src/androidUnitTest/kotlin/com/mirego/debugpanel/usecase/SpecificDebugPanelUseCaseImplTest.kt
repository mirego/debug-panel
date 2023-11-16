package com.mirego.debugpanel.usecase

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mirego.debugpanel.config.DebugPanelPickerItem
import com.mirego.debugpanel.repository.TestUseCaseDebugPanelRepository
import com.mirego.debugpanel.repository.TestUseCaseDisplayNameDebugPanelRepository
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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

        val viewData = useCase.createViewData(
            initialToggle = true,
            initialTextField = "textField value",
            initialPicker = "item1",
            initialEnum = TestEnum.VALUE_1,
            button = buttonAction,
            picker = pickerItems,
            label = flowOf("label value")
        )

        assertEquals(6, viewData.items.size)

        val toggle = assertNotNull(viewData.items[0] as? DebugPanelItemViewData.Toggle)
        val button = assertNotNull(viewData.items[1] as? DebugPanelItemViewData.Button)
        val textField = assertNotNull(viewData.items[2] as? DebugPanelItemViewData.TextField)
        val picker = assertNotNull(viewData.items[3] as? DebugPanelItemViewData.Picker)
        val label = assertNotNull(viewData.items[4] as? DebugPanelItemViewData.Label)
        val enumPicker = assertNotNull(viewData.items[5] as? DebugPanelItemViewData.Picker)

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

        verify(exactly = 1) {
            repository.getCurrentToggleValue("toggle", true)
            repository.getCurrentTextFieldValue("textField", "textField value")
            repository.getCurrentPickerValue("picker")
            repository.getCurrentPickerValue("enum")
        }

        confirmVerified(repository)
    }

    @Test
    fun `when creating view data without default values for pickers expect the correct methods to be called on repository and view data is built correctly`() = runTest {
        val repository: TestUseCaseDebugPanelRepository = mockk()
        val useCase = TestUseCaseDebugPanelUseCaseImpl(repository)

        every { repository.getCurrentPickerValue("picker") } returns null
        every { repository.getCurrentPickerValue("enum") } returns null

        val viewData = useCase.createViewData(
            initialToggle = false,
            initialTextField = "",
            initialPicker = "item1",
            initialEnum = TestEnum.VALUE_1,
            button = {},
            picker = emptyList(),
            label = flowOf()
        )

        assertEquals(6, viewData.items.size)

        val picker = assertNotNull(viewData.items[3] as? DebugPanelItemViewData.Picker)
        val enumPicker = assertNotNull(viewData.items[5] as? DebugPanelItemViewData.Picker)

        assertEquals("item1", picker.initialValue)
        assertEquals("VALUE_1", enumPicker.initialValue)

        verify(exactly = 1) {
            repository.getCurrentPickerValue("picker")
            repository.getCurrentPickerValue("enum")
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
            label = flowOf()
        )

        assertEquals(6, viewData.items.size)

        val toggle = assertNotNull(viewData.items[0] as? DebugPanelItemViewData.Toggle)
        val button = assertNotNull(viewData.items[1] as? DebugPanelItemViewData.Button)
        val textField = assertNotNull(viewData.items[2] as? DebugPanelItemViewData.TextField)
        val picker = assertNotNull(viewData.items[3] as? DebugPanelItemViewData.Picker)
        val label = assertNotNull(viewData.items[4] as? DebugPanelItemViewData.Label)
        val enumPicker = assertNotNull(viewData.items[5] as? DebugPanelItemViewData.Picker)

        assertEquals("Test toggle", toggle.label)
        assertEquals("Test button", button.label)
        assertEquals("Test text field", textField.placeholder)
        assertEquals("Test picker", picker.label)
        assertEquals("Test label", label.label)
        assertEquals("Test enum", enumPicker.label)
    }
}
