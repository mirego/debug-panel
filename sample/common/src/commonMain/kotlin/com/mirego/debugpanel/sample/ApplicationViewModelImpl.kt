package com.mirego.debugpanel.sample

import com.mirego.trikot.viewmodels.declarative.viewmodel.VMDViewModelImpl
import kotlinx.coroutines.CoroutineScope

class ApplicationViewModelImpl(
    coroutineScope: CoroutineScope
) : ApplicationViewModel, VMDViewModelImpl(coroutineScope) {

    override val rootViewModel: RootViewModel = RootViewModelImpl(coroutineScope)
}
