package com.mirego.debugpanel

import com.mirego.debugpanel.annotations.DebugProperty
import com.mirego.debugpanel.annotations.Identifier
import com.mirego.debugpanel.usecase.TestEnum
import debugpanel.DebugPropertyRepositoryImplBooleanFlowValueDelegate
import debugpanel.DebugPropertyRepositoryImplBooleanValueDelegate
import debugpanel.DebugPropertyRepositoryImplEnumFlowValueDelegate
import debugpanel.DebugPropertyRepositoryImplEnumValueDelegate
import debugpanel.DebugPropertyRepositoryImplStringFlowValueDelegate
import debugpanel.DebugPropertyRepositoryImplStringValueDelegate
import kotlinx.coroutines.flow.flowOf

class DebugPropertyRepositoryImpl : DebugPropertyRepository {
    @Identifier("stringValueKey")
    @DebugProperty("stringValue")
    val internalStringValue = "stringValue"

    @Identifier("stringFlowValueKey")
    @DebugProperty("stringFlowValue")
    val internalStringFlowValue = flowOf("stringFlowValue")

    @Identifier("enumValueKey")
    @DebugProperty("enumValue")
    val internalEnumValue = TestEnum.VALUE_0

    @Identifier("enumFlowValueKey")
    @DebugProperty("enumFlowValue")
    val internalEnumFlowValue = flowOf(TestEnum.VALUE_0)

    @Identifier("booleanValueKey")
    @DebugProperty("booleanValue")
    val internalBooleanValue = false

    @Identifier("booleanFlowValueKey")
    @DebugProperty("booleanFlowValue")
    val internalFlowBooleanValue = flowOf(false)

    override val stringValue by DebugPropertyRepositoryImplStringValueDelegate
    override val stringFlowValue by DebugPropertyRepositoryImplStringFlowValueDelegate
    override val enumValue by DebugPropertyRepositoryImplEnumValueDelegate
    override val enumFlowValue by DebugPropertyRepositoryImplEnumFlowValueDelegate
    override val booleanValue by DebugPropertyRepositoryImplBooleanValueDelegate
    override val booleanFlowValue by DebugPropertyRepositoryImplBooleanFlowValueDelegate
}
