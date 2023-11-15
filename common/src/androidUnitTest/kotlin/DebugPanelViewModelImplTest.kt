import com.mirego.debugpanel.DebugPanelPickerItem
import com.mirego.debugpanel.usecase.DebugPanelItemViewData
import com.mirego.debugpanel.usecase.DebugPanelUseCase
import com.mirego.debugpanel.usecase.DebugPanelViewData
import com.mirego.debugpanel.viewmodel.DebugPanelItemViewModel
import com.mirego.debugpanel.viewmodel.DebugPanelViewModelImpl
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle

class DebugPanelViewModelImplTest {
    private val useCase: DebugPanelUseCase = mockk()

    private fun createEveryItems(buttonAction: () -> Unit = {}): List<DebugPanelItemViewData> = listOf(
        DebugPanelItemViewData.Toggle(
            identifier = "toggleId",
            label = "toggle",
            initialValue = false
        ),
        DebugPanelItemViewData.TextField(
            identifier = "textFieldId",
            placeholder = "textField",
            initialValue = "text"
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
            )
        ),
        DebugPanelItemViewData.Picker(
            identifier = "pickerId2",
            label = "picker2",
            initialValue = "item1",
            items = listOf(
                DebugPanelPickerItem("item0", "Item 0"),
                DebugPanelPickerItem("item1", "Item 1")
            )
        )
    )

    @Test
    fun `given debug panel items expect the view models to be configured properly`() = runTestWithPendingCoroutines {
        var tapped = false

        val viewModel = createViewModel(
            DebugPanelViewData(
                createEveryItems {
                    tapped = true
                }
            )
        )

        advanceUntilIdle()

        assertEquals(6, viewModel.items.elements.size)

        val toggle = viewModel.items.elements[0] as DebugPanelItemViewModel.Toggle
        val textField = viewModel.items.elements[1] as DebugPanelItemViewModel.TextField
        val action = viewModel.items.elements[2] as DebugPanelItemViewModel.Button
        val label = viewModel.items.elements[3] as DebugPanelItemViewModel.Label
        val picker = viewModel.items.elements[4] as DebugPanelItemViewModel.Picker
        val pickerWithInitialValue = viewModel.items.elements[5] as DebugPanelItemViewModel.Picker

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
        assertEquals(-1, picker.viewModel.selectedIndex)
        assertEquals(2, picker.viewModel.elements.size)
        assertEquals("item0", picker.viewModel.elements[0].identifier)
        assertEquals("Item 0", picker.viewModel.elements[0].content.text)
        assertEquals("item1", picker.viewModel.elements[1].identifier)
        assertEquals("Item 1", picker.viewModel.elements[1].content.text)

        assertEquals(1, pickerWithInitialValue.viewModel.selectedIndex)

        confirmVerified(useCase)
    }

    @Test
    fun `when updating the view model items expect the correct methods to be called on the use case`() = runTestWithPendingCoroutines {
        val viewData = DebugPanelViewData(createEveryItems())
        val toggleItemViewData = viewData.items[0] as DebugPanelItemViewData.Toggle
        val textFieldItemViewData = viewData.items[1] as DebugPanelItemViewData.TextField
        val pickerItemViewData = viewData.items[4] as DebugPanelItemViewData.Picker

        val viewModel = createViewModel(viewData)

        val toggle = viewModel.items.elements[0] as DebugPanelItemViewModel.Toggle
        val textField = viewModel.items.elements[1] as DebugPanelItemViewModel.TextField
        val picker = viewModel.items.elements[4] as DebugPanelItemViewModel.Picker

        advanceUntilIdle()

        toggle.viewModel.onValueChange(true)
        textField.viewModel.onValueChange("newValue")
        picker.viewModel.selectedIndex = 1

        advanceUntilIdle()

        verify(exactly = 1) {
            useCase.onToggleUpdated(toggleItemViewData, true)
            useCase.onTextFieldUpdated(textFieldItemViewData, "newValue")
            useCase.onPickerUpdated(pickerItemViewData, "item1")
        }

        confirmVerified(useCase)
    }

    private fun TestScope.createViewModel(viewData: DebugPanelViewData) = DebugPanelViewModelImpl(
        coroutineScope = this,
        useCase = useCase,
        viewData = viewData
    )
}
