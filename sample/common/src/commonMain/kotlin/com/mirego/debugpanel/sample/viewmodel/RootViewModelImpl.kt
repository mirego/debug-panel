package com.mirego.debugpanel.sample.viewmodel

import com.mirego.debugpanel.config.DebugPanelPickerItem
import com.mirego.debugpanel.sample.Language
import com.mirego.debugpanel.usecase.SampleDebugPanelUseCase
import com.mirego.debugpanel.viewmodel.DebugPanelViewModelImpl
import com.mirego.trikot.viewmodels.declarative.viewmodel.VMDViewModelImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf

class RootViewModelImpl(
    coroutineScope: CoroutineScope,
    useCase: SampleDebugPanelUseCase
) : RootViewModel, VMDViewModelImpl(coroutineScope) {
    override val title = "Sample Debug Panel"

    override val debugPanel = DebugPanelViewModelImpl(
        coroutineScope,
        useCase,
        useCase.createViewData(
            initialPreviewMode = false,
            initialLastNameInput = "",
            initialEnvironments = "qa",
            initialLanguage = Language.FRENCH,
            firstName = flowOf("Some name"),
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
