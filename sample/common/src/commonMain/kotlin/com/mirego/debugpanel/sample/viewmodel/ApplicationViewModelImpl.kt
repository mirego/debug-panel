package com.mirego.debugpanel.sample.viewmodel

import com.mirego.debugpanel.repository.SampleDebugPanelRepository
import com.mirego.debugpanel.repository.SampleDebugPanelRepositoryImpl
import com.mirego.debugpanel.usecase.SampleDebugPanelUseCase
import com.mirego.debugpanel.usecase.SampleDebugPanelUseCaseImpl
import com.mirego.trikot.viewmodels.declarative.viewmodel.VMDViewModelImpl
import kotlinx.coroutines.CoroutineScope

class ApplicationViewModelImpl(
    coroutineScope: CoroutineScope,
) : ApplicationViewModel, VMDViewModelImpl(coroutineScope) {
    private val repository: SampleDebugPanelRepository = SampleDebugPanelRepositoryImpl()
    private val useCase: SampleDebugPanelUseCase = SampleDebugPanelUseCaseImpl(repository)

    override val rootViewModel: RootViewModel = RootViewModelImpl(coroutineScope, useCase)
}
