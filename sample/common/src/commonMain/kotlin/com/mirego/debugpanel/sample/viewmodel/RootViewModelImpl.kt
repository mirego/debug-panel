package com.mirego.debugpanel.sample.viewmodel

import com.mirego.debugpanel.config.DebugPanelPickerItem
import com.mirego.debugpanel.sample.Language
import com.mirego.debugpanel.usecase.SampleDebugPanelUseCase
import com.mirego.debugpanel.viewmodel.DebugPanelViewModelImpl
import com.mirego.trikot.viewmodels.declarative.viewmodel.VMDViewModelImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.datetime.Clock

class RootViewModelImpl(
    coroutineScope: CoroutineScope,
    useCase: SampleDebugPanelUseCase
) : RootViewModel, VMDViewModelImpl(coroutineScope) {
    override val title = "Sample Debug Panel"

    override val debugPanel = DebugPanelViewModelImpl(
        coroutineScope,
        useCase,
        useCase.createViewData(
            initialPreviewMode = true,
            initialEnvironments = "qa",
            initialDate = Clock.System.now().toEpochMilliseconds(),
            environments = listOf(
                DebugPanelPickerItem("dev", "Dev"),
                DebugPanelPickerItem("qa", "QA"),
                DebugPanelPickerItem("prod", "Production")
            )
        ) {
            println("Reset onboarding")
        }
    )
}
