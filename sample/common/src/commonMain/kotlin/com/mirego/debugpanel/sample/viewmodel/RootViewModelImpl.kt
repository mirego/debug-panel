package com.mirego.debugpanel.sample.viewmodel

import com.mirego.debugpanel.config.DebugPanelPickerItem
import com.mirego.debugpanel.sample.Language
import com.mirego.debugpanel.sample.killApp
import com.mirego.debugpanel.usecase.SampleDebugPanelUseCase
import com.mirego.debugpanel.viewmodel.DebugPanelViewModelImpl
import com.mirego.trikot.viewmodels.declarative.viewmodel.VMDViewModelImpl
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayIn

class RootViewModelImpl(
    coroutineScope: CoroutineScope,
    useCase: SampleDebugPanelUseCase,
) : RootViewModel, VMDViewModelImpl(coroutineScope) {
    override val title = "Sample Debug Panel"

    override val debugPanel = DebugPanelViewModelImpl(
        coroutineScope,
        useCase,
        useCase.createViewData(
            initialPreviewMode = true,
            initialLastNameInput = "",
            initialEnvironments = "qa",
            initialLanguage = Language.FRENCH,
            initialDate = Clock.System
                .todayIn(TimeZone.currentSystemDefault())
                .atStartOfDayIn(TimeZone.currentSystemDefault())
                .toEpochMilliseconds(),
            firstName = flowOf("Some name"),
            environments = listOf(
                DebugPanelPickerItem("dev", "Dev"),
                DebugPanelPickerItem("qa", "QA"),
                DebugPanelPickerItem("prod", "Production"),
            ),
            reset = {
                coroutineScope.launch {
                    useCase.resetSettings()
                    delay(500.milliseconds) // The shared preferences are async on Android so we make sure to wait enough time before killing the app
                    killApp()
                }
            },
        ),
    )
}
