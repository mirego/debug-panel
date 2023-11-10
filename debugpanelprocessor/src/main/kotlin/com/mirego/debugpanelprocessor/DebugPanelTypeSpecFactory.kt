package com.mirego.debugpanelprocessor

import com.mirego.debugpanelprocessor.Consts.REPOSITORY_NAME
import com.mirego.debugpanelprocessor.Consts.USE_CASE_IMPL_NAME
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toTypeName

internal object DebugPanelTypeSpecFactory {
    private val FLOW_CLASS_NAME = ClassName("kotlinx.coroutines.flow", "Flow")
    private const val CREATE_VIEW_DATA_FUNCTION_NAME = "createViewData"
    private const val DEBUG_PANEL_VIEW_DATA_NAME = "DebugPanelViewData"
    private val DEBUG_PANEL_VIEW_DATA_CLASS_NAME = ClassName(Consts.USE_CASE_PACKAGE_NAME, DEBUG_PANEL_VIEW_DATA_NAME)

    fun createRepository(className: ClassName, attributes: Sequence<Attribute>) = InterfaceImplementation.create(
        interfaceClassName = className,
        functions = attributes.mapNotNull { attribute ->
            val returnType = when (attribute) {
                is Attribute.Picker, is Attribute.EnumPicker -> STRING
                is Attribute.Function, is Attribute.Label -> return@mapNotNull null
                else -> attribute.type.toTypeName()
            }

            @Suppress("KotlinConstantConditions")
            val baseRepositoryFunctionName = when (attribute) {
                is Attribute.Toggle -> "getToggleValue"
                is Attribute.TextField -> "getTextFieldValue"
                is Attribute.Picker, is Attribute.EnumPicker -> "getPickerValue"
                is Attribute.Function, is Attribute.Label -> return@mapNotNull null
            }

            InterfaceImplementation.Function(
                name = "get${attribute.name.capitalize()}",
                returnType = FLOW_CLASS_NAME.plusParameter(returnType),
                code = "return $baseRepositoryFunctionName(\"${attribute.name}\")"
            )
        }.asIterable(),
        configureInterface = { addSuperinterface(ClassName(Consts.REPOSITORY_PACKAGE_NAME, REPOSITORY_NAME)) },
        configureImplementation = { superclass(ClassName(Consts.REPOSITORY_PACKAGE_NAME, Consts.REPOSITORY_IMPL_NAME)) }
    )

    private fun createUseCaseParams(attributes: Sequence<Attribute>) = attributes
        .mapNotNull { attribute ->
            fun getInitialValueParamName(): String = "initial${attribute.name.capitalize()}"
            fun getParamName(): String = attribute.name

            fun getFlowParamType(): TypeName = FLOW_CLASS_NAME.plusParameter(attribute.type.toTypeName())
            fun getParamType(): TypeName = attribute.type.toTypeName()

            val paramName: String = when (attribute) {
                is Attribute.EnumPicker -> return@mapNotNull null
                is Attribute.Function, is Attribute.Label, is Attribute.Picker -> getParamName()
                is Attribute.TextField, is Attribute.Toggle -> getInitialValueParamName()
            }

            @Suppress("KotlinConstantConditions")
            val paramType: TypeName = when (attribute) {
                is Attribute.EnumPicker -> return@mapNotNull null
                is Attribute.Function -> LambdaTypeName.get(null, emptyList(), Unit::class.asTypeName())
                is Attribute.Label -> getFlowParamType()
                is Attribute.Picker, is Attribute.TextField, is Attribute.Toggle -> getParamType()
            }

            ParameterSpec(paramName, paramType)
        }

    fun createUseCase(className: ClassName, specificRepositoryClassName: ClassName, attributes: Sequence<Attribute>) = InterfaceImplementation.create(
        interfaceClassName = className,
        functions = listOf(
            attributes.map {
                when (it) {
                    is Attribute.Function -> DebugPanelItemViewDataFactory.createButton(it)
                    is Attribute.Label -> DebugPanelItemViewDataFactory.createLabel(it)
                    is Attribute.Picker -> DebugPanelItemViewDataFactory.createPicker(it)
                    is Attribute.TextField -> DebugPanelItemViewDataFactory.createTextField(it)
                    is Attribute.Toggle -> DebugPanelItemViewDataFactory.createToggle(it)
                    is Attribute.EnumPicker -> DebugPanelItemViewDataFactory.createPicker(it)
                }
            }.let { itemViewDataList ->
                InterfaceImplementation.Function(
                    name = CREATE_VIEW_DATA_FUNCTION_NAME,
                    returnType = DEBUG_PANEL_VIEW_DATA_CLASS_NAME,
                    code = """
                        |return $DEBUG_PANEL_VIEW_DATA_NAME(
                        |   listOf(
                        |       ${itemViewDataList.joinToString(", ")}
                        |   )
                        |)
                        |
                    """.trimMargin(),
                    params = createUseCaseParams(attributes).asIterable()
                )
            }
        ),
        configureInterface = { addSuperinterface(ClassName(Consts.USE_CASE_PACKAGE_NAME, Consts.USE_CASE_NAME)).addSuperinterface(specificRepositoryClassName) },
        configureImplementation = {
            val repositoryParamName = "repository"

            superclass(ClassName(Consts.USE_CASE_PACKAGE_NAME, USE_CASE_IMPL_NAME))
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addParameter(repositoryParamName, specificRepositoryClassName)
                        .build()
                )
                .addProperty(
                    PropertySpec.builder(repositoryParamName, specificRepositoryClassName)
                        .initializer(repositoryParamName)
                        .addModifiers(KModifier.PRIVATE)
                        .build()
                )
                .addSuperinterface(specificRepositoryClassName, CodeBlock.of(repositoryParamName))
        }
    )
}
