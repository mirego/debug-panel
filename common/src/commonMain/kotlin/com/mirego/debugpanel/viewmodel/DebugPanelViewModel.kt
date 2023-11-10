package com.mirego.debugpanel.viewmodel

import com.mirego.trikot.viewmodels.declarative.components.VMDListViewModel
import com.mirego.trikot.viewmodels.declarative.viewmodel.VMDViewModel

interface DebugPanelViewModel : VMDViewModel {
    val items: VMDListViewModel<DebugPanelItemViewModel>
}
