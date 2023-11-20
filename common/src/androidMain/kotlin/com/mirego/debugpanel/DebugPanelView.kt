package com.mirego.debugpanel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mirego.debugpanel.usecase.DebugPanelUseCasePreview
import com.mirego.debugpanel.viewmodel.DebugPanelItemViewModel
import com.mirego.debugpanel.viewmodel.DebugPanelViewModel
import com.mirego.debugpanel.viewmodel.DebugPanelViewModelImpl
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.VMDLazyColumn
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.material3.VMDButton
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.material3.VMDDropDownMenu
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.material3.VMDDropDownMenuItem
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.material3.VMDSwitch
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.material3.VMDText
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.material3.VMDTextField
import com.mirego.trikot.viewmodels.declarative.util.CoroutineScopeProvider
import kotlinx.coroutines.CoroutineExceptionHandler

@Composable
fun DebugPanelView(viewModel: DebugPanelViewModel, modifier: Modifier = Modifier) {
    VMDLazyColumn(
        viewModel = viewModel.items,
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) { item ->
        when (item) {
            is DebugPanelItemViewModel.TextField -> TextFieldItem(item)
            is DebugPanelItemViewModel.Toggle -> ToggleItem(item)
            is DebugPanelItemViewModel.Button -> ButtonItem(item)
            is DebugPanelItemViewModel.Label -> LabelItem(item)
            is DebugPanelItemViewModel.Picker -> PickerItem(item)
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
        Text(content.text)
        Spacer(Modifier.width(8.dp))
    })
}

@Composable
private fun ButtonItem(item: DebugPanelItemViewModel.Button) {
    VMDButton(
        viewModel = item.viewModel,
        content = { content ->
            Text(
                content.text,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    )
}

@Composable
private fun LabelItem(item: DebugPanelItemViewModel.Label) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        VMDText(viewModel = item.label)
        VMDText(viewModel = item.viewModel)
    }
}

@Composable
private fun PickerItem(item: DebugPanelItemViewModel.Picker) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { isExpanded = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        VMDText(
            viewModel = item.label,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    VMDDropDownMenu(
        viewModel = item.viewModel,
        expanded = isExpanded,
        onDismissRequest = {
            isExpanded = false
        }
    ) { dropdownItem, index ->
        VMDDropDownMenuItem(
            pickerViewModel = item.viewModel,
            viewModel = dropdownItem,
            index = index,
            onClick = { isExpanded = false },
            text = {
                Text(text = dropdownItem.content.text)
            }
        )
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
                    }
                ),
                useCase,
                useCase.createViewData()
            )
        )
    }
}
