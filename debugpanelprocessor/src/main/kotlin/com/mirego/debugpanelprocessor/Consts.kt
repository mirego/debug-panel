package com.mirego.debugpanelprocessor

internal object Consts {
    const val REPOSITORY_NAME = "DebugPanelRepository"
    const val REPOSITORY_IMPL_NAME = "DebugPanelRepositoryImpl"

    const val USE_CASE_NAME = "DebugPanelUseCase"
    const val USE_CASE_IMPL_NAME = "DebugPanelUseCaseImpl"

    fun getConfigPackageName(packageName: String) = "$packageName.config"
    fun getRepositoryPackageName(packageName: String) = "$packageName.repository"
    fun getUseCasePackageName(packageName: String) = "$packageName.usecase"
}
