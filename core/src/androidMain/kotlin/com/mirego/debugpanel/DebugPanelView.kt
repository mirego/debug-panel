package com.mirego.debugpanel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mirego.compose.utils.SpacerHorizontal
import com.mirego.compose.utils.extensions.clickable
import com.mirego.debugpanel.usecase.DebugPanelUseCasePreview
import com.mirego.debugpanel.viewmodel.DebugPanelItemViewModel
import com.mirego.debugpanel.viewmodel.DebugPanelViewModel
import com.mirego.debugpanel.viewmodel.DebugPanelViewModelImpl
import com.mirego.trikot.viewmodels.declarative.compose.extensions.observeAsState
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.VMDLazyColumn
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.material3.VMDButton
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.material3.VMDDropDownMenu
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.material3.VMDDropDownMenuItem
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.material3.VMDSwitch
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.material3.VMDText
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.material3.VMDTextField
import com.mirego.trikot.viewmodels.declarative.util.CoroutineScopeProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.flowOf

@Composable
fun DebugPanelView(
    viewModel: DebugPanelViewModel,
    modifier: Modifier = Modifier,
) {
    VMDLazyColumn(
        viewModel = viewModel.items,
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) { item ->
        when (item) {
            is DebugPanelItemViewModel.TextField -> TextFieldItem(item)
            is DebugPanelItemViewModel.Toggle -> ToggleItem(item)
            is DebugPanelItemViewModel.Button -> ButtonItem(item)
            is DebugPanelItemViewModel.Label -> LabelItem(item)
            is DebugPanelItemViewModel.Picker -> PickerItem(item)
            is DebugPanelItemViewModel.DatePicker -> DatePickerItem(item)
        }
    }
}

@Composable
private fun TextFieldItem(item: DebugPanelItemViewModel.TextField) {
    VMDTextField(viewModel = item.viewModel, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun ToggleItem(item: DebugPanelItemViewModel.Toggle) {
    VMDSwitch(viewModel = item.viewModel, label = { content ->
        Text(content.text, modifier = Modifier.weight(1f))
    })
}

@Composable
private fun ButtonItem(item: DebugPanelItemViewModel.Button) {
    VMDButton(
        viewModel = item.viewModel,
        modifier = Modifier.fillMaxWidth(),
        content = { content ->
            Text(
                content.text,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onPrimary,
            )
        },
    )
}

@Composable
private fun LabelItem(item: DebugPanelItemViewModel.Label) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        VMDText(viewModel = item.label)
        SpacerHorizontal(32.dp)
        SelectionContainer {
            VMDText(viewModel = item.viewModel)
        }
    }
}

@Composable
private fun PickerItem(item: DebugPanelItemViewModel.Picker) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    Box {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VMDText(viewModel = item.label)

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { isExpanded = true },
                contentAlignment = Alignment.CenterStart,
            ) {
                VMDText(
                    viewModel = item.selectedItem,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        VMDDropDownMenu(
            viewModel = item.viewModel,
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            },
        ) { dropdownItem, index ->
            VMDDropDownMenuItem(
                pickerViewModel = item.viewModel,
                viewModel = dropdownItem,
                index = index,
                onClick = { isExpanded = false },
                text = {
                    Text(text = dropdownItem.content.text)
                },
            )
        }
    }
}

@Composable
private fun DatePickerItem(item: DebugPanelItemViewModel.DatePicker) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        VMDText(viewModel = item.label)

        val dialogVisible = rememberSaveable { mutableStateOf(false) }

        val datePickerViewModel by item.viewModel.observeAsState()
        LaunchedEffect(datePickerViewModel) {
            datePickerViewModel.showPicker = { dialogVisible.value = true }
        }

        VMDTextField(
            viewModel = datePickerViewModel,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(datePickerViewModel.action),
            textFieldColors = TextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface),
        )
        DatePickerView(dialogVisible.value, datePickerViewModel.date) { date ->
            date?.let { datePickerViewModel.date = it }
            dialogVisible.value = false
        }
    }
}

@Composable
private fun DatePickerView(
    visible: Boolean,
    initialSelectedDate: Long?,
    onDismissed: (Long?) -> Unit,
) {
    val datePickerState = rememberDatePickerState(initialSelectedDate)

    if (!visible) return

    DatePickerDialog(
        onDismissRequest = { onDismissed(null) },
        confirmButton = {
            TextButton(onClick = { onDismissed(datePickerState.selectedDateMillis) }) {
                Text("Ok")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissed(null) }) {
                Text("Cancel")
            }
        },
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
private fun Preview() {
    DebugPanelTheme {
        val useCase = DebugPanelUseCasePreview()

        DebugPanelView(
            DebugPanelViewModelImpl(
                CoroutineScopeProvider.provideMainWithSuperviserJob(
                    CoroutineExceptionHandler { _, exception ->
                        println("CoroutineExceptionHandler got $exception")
                    },
                ),
                useCase,
                flowOf(useCase.createViewData()),
            ),
        )
    }
}
