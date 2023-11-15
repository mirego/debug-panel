package com.mirego.debugpanel.sample.viewmodel

import com.mirego.debugpanel.viewmodel.DebugPanelViewModel
import com.mirego.trikot.viewmodels.declarative.viewmodel.VMDViewModel

interface RootViewModel : VMDViewModel {
    val debugPanel: DebugPanelViewModel
}
