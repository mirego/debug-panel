package com.mirego.debugpanelprocessor

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.WildcardTypeName

internal object Consts {
    fun getRepositoryPackageName(packageName: String) = "$packageName.repository"
    fun getUseCasePackageName(packageName: String) = "$packageName.usecase"

    private const val BASE_PACKAGE_NAME = "com.mirego.debugpanel"
    const val CONFIG_PACKAGE_NAME = "$BASE_PACKAGE_NAME.config"

    val REPOSITORY_PACKAGE_NAME = getRepositoryPackageName(BASE_PACKAGE_NAME)
    const val REPOSITORY_NAME = "DebugPanelRepository"
    const val REPOSITORY_IMPL_NAME = "DebugPanelRepositoryImpl"

    val USE_CASE_PACKAGE_NAME = getUseCasePackageName(BASE_PACKAGE_NAME)
    const val USE_CASE_NAME = "DebugPanelUseCase"
    const val USE_CASE_IMPL_NAME = "DebugPanelUseCaseImpl"

    const val SERVICE_PACKAGE_NAME = "$BASE_PACKAGE_NAME.service"

    const val FLOW_PACKAGE_NAME = "kotlinx.coroutines.flow"

    val FLOW = ClassName(FLOW_PACKAGE_NAME, "Flow")
    val WILDCARD = WildcardTypeName.producerOf(ANY.copy(nullable = true))
}
