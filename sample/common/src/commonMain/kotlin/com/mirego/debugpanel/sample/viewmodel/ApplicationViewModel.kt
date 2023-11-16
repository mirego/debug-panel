package com.mirego.debugpanel.sample.viewmodel

import com.mirego.trikot.viewmodels.declarative.viewmodel.VMDViewModel

interface ApplicationViewModel : VMDViewModel {
    val rootViewModel: RootViewModel
}
