package com.mirego.debugpanel.sample

import com.mirego.trikot.viewmodels.declarative.viewmodel.VMDViewModelImpl
import kotlinx.coroutines.CoroutineScope

class RootViewModelImpl(coroutineScope: CoroutineScope) : RootViewModel, VMDViewModelImpl(coroutineScope)
