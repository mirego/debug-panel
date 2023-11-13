package com.mirego.debugpanel.sample

import com.mirego.debugpanel.DebugPanelPickerItem
import com.mirego.debugpanel.repository.SampleDebugPanelRepository
import com.mirego.debugpanel.repository.SampleDebugPanelRepositoryImpl
import com.mirego.debugpanel.usecase.SampleDebugPanelUseCase
import com.mirego.debugpanel.usecase.SampleDebugPanelUseCaseImpl
import com.mirego.debugpanel.viewmodel.DebugPanelViewModelImpl
import com.mirego.trikot.viewmodels.declarative.viewmodel.VMDViewModelImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf

class RootViewModelImpl(coroutineScope: CoroutineScope) : RootViewModel, VMDViewModelImpl(coroutineScope) {
    private val repository: SampleDebugPanelRepository = SampleDebugPanelRepositoryImpl()
    private val useCase: SampleDebugPanelUseCase = SampleDebugPanelUseCaseImpl(repository)

    override val debugPanel = DebugPanelViewModelImpl(
        coroutineScope,
        useCase,
        useCase.createViewData(
            initialPreviewMode = true,
            initialLastNameInput = "",
            initialEnvironments = "qa",
            initialLanguage = Language.FRENCH,
            firstName = flowOf("First name"),
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
