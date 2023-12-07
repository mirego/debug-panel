package com.mirego.debugpanel

import com.mirego.debugpanel.usecase.TestEnum
import kotlinx.coroutines.flow.Flow

interface DebugPropertyRepository {
    val stringValue: String
    val stringFlowValue: Flow<String>

    val enumValue: TestEnum
    val enumFlowValue: Flow<TestEnum>

    val booleanValue: Boolean
    val booleanFlowValue: Flow<Boolean>
}
