package com.mirego.debugpanelprocessor

import com.squareup.kotlinpoet.ClassName

internal object Consts {
    fun getRepositoryPackageName(packageName: String) = "$packageName.repository"
    fun getUseCasePackageName(packageName: String) = "$packageName.usecase"

    const val BASE_PACKAGE_NAME = "com.mirego.debugpanel"
    const val CONFIG_PACKAGE_NAME = "$BASE_PACKAGE_NAME.config"

    val REPOSITORY_PACKAGE_NAME = getRepositoryPackageName(BASE_PACKAGE_NAME)
    const val REPOSITORY_NAME = "DebugPanelRepository"
    const val REPOSITORY_IMPL_NAME = "DebugPanelRepositoryImpl"

    val USE_CASE_PACKAGE_NAME = getUseCasePackageName(BASE_PACKAGE_NAME)
    const val USE_CASE_NAME = "DebugPanelUseCase"
    const val USE_CASE_IMPL_NAME = "DebugPanelUseCaseImpl"

    val FLOW = ClassName("kotlinx.coroutines.flow", "Flow")
}
