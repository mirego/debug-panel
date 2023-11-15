package com.mirego.debugpanel.sample.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.mirego.debugpanel.sample.usecase.SampleDebugPanelUseCasePreview
import com.mirego.debugpanel.sample.viewmodel.ApplicationViewModelImpl
import com.mirego.debugpanel.sample.viewmodel.RootViewModel
import com.mirego.debugpanel.sample.viewmodel.RootViewModelImpl
import com.mirego.debugpanel.viewmodel.DebugPanelItemViewModel
import com.mirego.debugpanel.viewmodel.DebugPanelViewModel
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.VMDButton
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.VMDDropDownMenu
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.VMDDropDownMenuItem
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.VMDLazyColumn
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.VMDSwitch
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.VMDText
import com.mirego.trikot.viewmodels.declarative.compose.viewmodel.VMDTextField
import com.mirego.trikot.viewmodels.declarative.util.CoroutineScopeProvider
import kotlinx.coroutines.CoroutineExceptionHandler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val applicationViewModel = getInitialViewModel {
            ApplicationViewModelImpl(lifecycleScope)
        }

        setContent {
            DebugPanelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RootView(applicationViewModel.rootViewModel)
                }
            }
        }
    }
}

@Composable
fun RootView(viewModel: RootViewModel) {
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Sample Debug Panel",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )

        Items(viewModel.debugPanel)
    }
}

@Composable
private fun Items(viewModel: DebugPanelViewModel) {
    VMDLazyColumn(
        viewModel = viewModel.items,
        modifier = Modifier.fillMaxWidth(),
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
        modifier = Modifier
            .height(48.dp)
            .background(Color.LightGray)
            .padding(horizontal = 16.dp),
        viewModel = item.viewModel,
        content = { content ->
            Text(content.text)
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
            .clickable { isExpanded = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        VMDText(
            viewModel = item.label,
            modifier = Modifier
                .background(Color(0xFFEEEEEE))
                .padding(16.dp)
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
            onClick = { isExpanded = false }
        ) { pickerItem, _ ->
            Text(text = pickerItem.content.text)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun DefaultPreview() {
    DebugPanelTheme {
        RootView(
            RootViewModelImpl(
                CoroutineScopeProvider.provideMainWithSuperviserJob(
                    CoroutineExceptionHandler { _, exception ->
                        println("CoroutineExceptionHandler got $exception")
                    }
                ),
                SampleDebugPanelUseCasePreview()
            )
        )
    }
}
