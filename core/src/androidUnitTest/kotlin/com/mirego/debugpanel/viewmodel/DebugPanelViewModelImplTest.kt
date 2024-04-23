package com.mirego.debugpanel.viewmodel

import com.mirego.debugpanel.config.DebugPanelPickerItem
import com.mirego.debugpanel.extensions.datePicker
import com.mirego.debugpanel.extensions.picker
import com.mirego.debugpanel.extensions.textField
import com.mirego.debugpanel.extensions.toggle
import com.mirego.debugpanel.service.DateFormatter
import com.mirego.debugpanel.service.dateFormatter
import com.mirego.debugpanel.usecase.DebugPanelItemViewData
import com.mirego.debugpanel.usecase.DebugPanelUseCase
import com.mirego.debugpanel.usecase.DebugPanelViewData
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import runTestWithPendingCoroutines

class DebugPanelViewModelImplTest {
    private val useCase: DebugPanelUseCase = mockk()

    private fun createEveryItems(isDirty: Flow<Boolean> = flowOf(false), buttonAction: () -> Unit = {}): List<DebugPanelItemViewData> = listOf(
        DebugPanelItemViewData.Toggle(
            identifier = "toggleId",
            label = "toggle",
            initialValue = false,
            isDirty = isDirty
        ),
        DebugPanelItemViewData.TextField(
            identifier = "textFieldId",
            label = "textField",
            initialValue = "text",
            isDirty = isDirty
        ),
        DebugPanelItemViewData.Button(
            identifier = "actionId",
            label = "action",
            action = buttonAction
        ),
        DebugPanelItemViewData.Label(
            identifier = "labelId",
            label = "label",
            value = flowOf("value")
        ),
        DebugPanelItemViewData.Picker(
            identifier = "pickerId",
            label = "picker",
            initialValue = null,
            items = listOf(
                DebugPanelPickerItem("item0", "Item 0"),
                DebugPanelPickerItem("item1", "Item 1")
            ),
            isDirty = isDirty
        ),
        DebugPanelItemViewData.Picker(
            identifier = "pickerId2",
            label = "picker2",
            initialValue = "item1",
            items = listOf(
                DebugPanelPickerItem("item0", "Item 0"),
                DebugPanelPickerItem("item1", "Item 1")
            ),
            isDirty = isDirty
        ),
        DebugPanelItemViewData.DatePicker(
            identifier = "datePickerId",
            label = "datePicker",
            initialValue = 123,
            isDirty = isDirty
        )
    )

    @Test
    fun `given debug panel items expect the view models to be configured properly`() = runTestWithPendingCoroutines {
        val dateFormatter = mockDateFormatter()

        var tapped = false

        val viewDataList = DebugPanelViewData(
            createEveryItems {
                tapped = true
            }
        )
        val datePickerViewData = viewDataList.items[6].datePicker
        val viewModel = createViewModel(viewDataList)

        advanceUntilIdle()

        assertEquals(7, viewModel.items.elements.size)

        val toggle = viewModel.items.elements[0] as DebugPanelItemViewModel.Toggle
        val textField = viewModel.items.elements[1] as DebugPanelItemViewModel.TextField
        val action = viewModel.items.elements[2] as DebugPanelItemViewModel.Button
        val label = viewModel.items.elements[3] as DebugPanelItemViewModel.Label
        val picker = viewModel.items.elements[4] as DebugPanelItemViewModel.Picker
        val pickerWithInitialValue = viewModel.items.elements[5] as DebugPanelItemViewModel.Picker
        val datePicker = viewModel.items.elements[6] as DebugPanelItemViewModel.DatePicker

        assertEquals("toggleId", toggle.identifier)
        assertEquals("toggle", toggle.viewModel.label.text)

        assertEquals("textFieldId", textField.identifier)
        assertEquals("text", textField.viewModel.text)
        assertEquals("textField", textField.viewModel.placeholder)

        assertEquals("actionId", action.identifier)
        assertEquals("action", action.viewModel.content.text)

        action.viewModel.actionBlock()
        assertTrue(tapped)

        assertEquals("labelId", label.identifier)
        assertEquals("label", label.label.text)
        assertEquals("value", label.viewModel.text)

        assertEquals("pickerId", picker.identifier)
        assertEquals("picker", picker.label.text)
        assertEquals("", picker.selectedItem.text)
        assertEquals(-1, picker.viewModel.selectedIndex)
        assertEquals(2, picker.viewModel.elements.size)
        assertEquals("item0", picker.viewModel.elements[0].identifier)
        assertEquals("Item 0", picker.viewModel.elements[0].content.text)
        assertEquals("item1", picker.viewModel.elements[1].identifier)
        assertEquals("Item 1", picker.viewModel.elements[1].content.text)

        assertEquals(1, pickerWithInitialValue.viewModel.selectedIndex)
        assertEquals("Item 1", pickerWithInitialValue.selectedItem.text)

        assertEquals("datePickerId", datePicker.identifier)
        assertEquals("datePicker", datePicker.label.text)
        assertEquals("123", datePicker.viewModel.text)

        datePicker.viewModel.date = 456

        var showPickerCalled = false
        datePicker.viewModel.showPicker = {
            showPickerCalled = true
        }

        datePicker.viewModel.action()
        assertTrue(showPickerCalled)

        advanceUntilIdle()

        verify(exactly = 1) {
            dateFormatter.format(123)
            useCase.onDatePickerUpdated(datePickerViewData, 456)
            dateFormatter.format(456)
        }

        confirmVerified(useCase)
        confirmVerified(dateFormatter)
    }

    @Test
    fun `when updating the view model items expect the correct methods to be called on the use case`() = runTestWithPendingCoroutines {
        val viewData = DebugPanelViewData(createEveryItems())
        val toggleItemViewData = viewData.items[0].toggle
        val textFieldItemViewData = viewData.items[1].textField
        val pickerItemViewData = viewData.items[4].picker
        val datePickerItemViewData = viewData.items[6].datePicker

        val viewModel = createViewModel(viewData)

        advanceUntilIdle()

        val toggle = viewModel.items.elements[0] as DebugPanelItemViewModel.Toggle
        val textField = viewModel.items.elements[1] as DebugPanelItemViewModel.TextField
        val picker = viewModel.items.elements[4] as DebugPanelItemViewModel.Picker
        val datePicker = viewModel.items.elements[6] as DebugPanelItemViewModel.DatePicker

        toggle.viewModel.onValueChange(true)
        textField.viewModel.onValueChange("newValue")
        picker.viewModel.selectedIndex = 1
        datePicker.viewModel.date = 456

        advanceUntilIdle()

        verify(exactly = 1) {
            useCase.onToggleUpdated(toggleItemViewData, true)
            useCase.onTextFieldUpdated(textFieldItemViewData, "newValue")
            useCase.onPickerUpdated(pickerItemViewData, "item1")
            useCase.onDatePickerUpdated(datePickerItemViewData, 456)
        }

        confirmVerified(useCase)
    }

    @Test
    fun `when isDirty returns true expect the label to be updated`() = runTestWithPendingCoroutines {
        val isDirty = MutableStateFlow(false)

        val viewDataList = DebugPanelViewData(createEveryItems(isDirty))
        val viewModel = createViewModel(viewDataList)

        advanceUntilIdle()

        assertEquals(7, viewModel.items.elements.size)

        val toggle = viewModel.items.elements[0] as DebugPanelItemViewModel.Toggle
        val picker = viewModel.items.elements[4] as DebugPanelItemViewModel.Picker
        val datePicker = viewModel.items.elements[6] as DebugPanelItemViewModel.DatePicker

        assertEquals("toggle", toggle.viewModel.label.text)
        assertEquals("picker", picker.label.text)
        assertEquals("datePicker", datePicker.label.text)

        isDirty.value = true

        advanceUntilIdle()

        assertEquals("toggle*", toggle.viewModel.label.text)
        assertEquals("picker*", picker.label.text)
        assertEquals("datePicker*", datePicker.label.text)
    }

    private fun TestScope.createViewModel(viewData: DebugPanelViewData) = DebugPanelViewModelImpl(
        coroutineScope = this,
        useCase = useCase,
        viewDataFlow = flowOf(viewData)
    )

    private fun mockDateFormatter(): DateFormatter {
        val dateFormatterMock = mockk<DateFormatter> {
            every { format(any()) } answers { arg<Long>(0).toString() }
        }
        mockkStatic("com.mirego.debugpanel.service.DateFormatterKt")

        every { dateFormatter } returns dateFormatterMock

        return dateFormatterMock
    }
}
