package com.mirego.debugpanel.sample.viewmodel

import com.mirego.debugpanel.repository.SampleDebugPanelRepository
import com.mirego.debugpanel.repository.SampleDebugPanelRepositoryImpl
import com.mirego.debugpanel.sample.repository.SampleRepository
import com.mirego.debugpanel.sample.repository.SampleRepositoryImpl
import com.mirego.debugpanel.usecase.SampleDebugPanelUseCase
import com.mirego.debugpanel.usecase.SampleDebugPanelUseCaseImpl
import com.mirego.trikot.viewmodels.declarative.viewmodel.VMDViewModelImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ApplicationViewModelImpl(
    coroutineScope: CoroutineScope
) : ApplicationViewModel, VMDViewModelImpl(coroutineScope) {
    private val repository: SampleDebugPanelRepository = SampleDebugPanelRepositoryImpl()
    private val useCase: SampleDebugPanelUseCase = SampleDebugPanelUseCaseImpl(repository)

    private val testRepository: SampleRepository = SampleRepositoryImpl()

    override val rootViewModel: RootViewModel = RootViewModelImpl(coroutineScope, useCase)

    init {
        println("tsst123 first name input: " + testRepository.firstNameInput)

        coroutineScope.launch {
            testRepository.lastNameInput.collect {
                println("tsst123 last name input: " + it)
            }
        }
    }
}
