package com.mirego.debugpanelprocessor.typespec

import com.mirego.debugpanelprocessor.Attribute
import com.mirego.debugpanelprocessor.Consts
import com.mirego.debugpanelprocessor.Consts.FLOW
import com.mirego.debugpanelprocessor.Import
import com.mirego.debugpanelprocessor.ResolvedConfiguration
import com.mirego.debugpanelprocessor.capitalize
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
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toTypeName

internal object DebugPanelUseCaseTypeSpec {
    fun create(className: ClassName, specificRepositoryClassName: ClassName, configuration: ResolvedConfiguration) = InterfaceImplementation.create(
        interfaceClassName = className,
        functions = createFunctions(configuration),
        configureInterface = { configureInterface(specificRepositoryClassName) },
        configureImplementation = { configureImplementation(specificRepositoryClassName) },
        implementationImports = listOf(
            Import(Consts.CONFIG_PACKAGE_NAME, "DebugPanelPickerItem"),
            Import(Consts.USE_CASE_PACKAGE_NAME, "DebugPanelItemViewData")
        )
    )

    private fun createFunctions(configuration: ResolvedConfiguration): Iterable<InterfaceImplementation.Function> = listOf(
        (createAttributeItemViewDataList(configuration.attributes) + createExtraItemViewDataList(configuration)).let { itemViewDataList ->
            val viewDataName = "DebugPanelViewData"

            InterfaceImplementation.Function(
                name = "createViewData",
                returnType = ClassName(Consts.USE_CASE_PACKAGE_NAME, viewDataName),
                code = """
                        |return $viewDataName(
                        |   listOf(
                        |       ${itemViewDataList.joinToString(", ")}
                        |   )
                        |)
                        |
                """.trimMargin(),
                params = createParams(configuration.attributes).asIterable()
            )
        }
    )

    private fun TypeSpec.Builder.configureInterface(specificRepositoryClassName: ClassName): TypeSpec.Builder =
        addSuperinterface(ClassName(Consts.USE_CASE_PACKAGE_NAME, Consts.USE_CASE_NAME))
            .addSuperinterface(specificRepositoryClassName)

    private fun TypeSpec.Builder.configureImplementation(specificRepositoryClassName: ClassName): TypeSpec.Builder {
        val repositoryParamName = "repository"

        return superclass(ClassName(Consts.USE_CASE_PACKAGE_NAME, Consts.USE_CASE_IMPL_NAME))
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

    private fun createAttributeItemViewDataList(attributes: Sequence<Attribute>): Sequence<String> = attributes.map {
        when (it) {
            is Attribute.Function -> DebugPanelItemViewDataFactory.createButton(it)
            is Attribute.Label -> DebugPanelItemViewDataFactory.createLabel(it)
            is Attribute.Picker -> DebugPanelItemViewDataFactory.createPicker(it)
            is Attribute.DatePicker -> DebugPanelItemViewDataFactory.createDatePicker(it)
            is Attribute.TextField -> DebugPanelItemViewDataFactory.createTextField(it)
            is Attribute.Toggle -> DebugPanelItemViewDataFactory.createToggle(it)
            is Attribute.EnumPicker -> DebugPanelItemViewDataFactory.createPicker(it)
        }
    }

    private fun createExtraItemViewDataList(configuration: ResolvedConfiguration): Sequence<String> = if (configuration.includeResetButton) {
        sequenceOf(
            DebugPanelItemViewDataFactory.createButton("_reset", "Reset", "::resetSettings")
        )
    } else {
        emptySequence()
    }

    private fun createParams(attributes: Sequence<Attribute>): Sequence<ParameterSpec> {
        val initialValueParams = attributes
            .mapNotNull { attribute ->
                val paramName: String = "initial${attribute.name.capitalize()}".takeIf { attribute.persistedType != null } ?: return@mapNotNull null

                val paramType: TypeName = when (attribute) {
                    is Attribute.EnumPicker -> attribute.type.toTypeName().copy(nullable = true)
                    is Attribute.Picker -> STRING.copy(nullable = true)
                    else -> attribute.persistedType?.asTypeName()
                } ?: return@mapNotNull null

                ParameterSpec(paramName, paramType)
            }

        val valueParams = attributes
            .mapNotNull { attribute ->
                val paramName: String = when (attribute) {
                    is Attribute.TextField, is Attribute.Toggle, is Attribute.EnumPicker -> return@mapNotNull null
                    is Attribute.Function, is Attribute.Label, is Attribute.Picker, is Attribute.DatePicker -> attribute.name
                }

                @Suppress("KotlinConstantConditions")
                val paramType: TypeName = when (attribute) {
                    is Attribute.TextField, is Attribute.Toggle, is Attribute.EnumPicker, is Attribute.DatePicker -> return@mapNotNull null
                    is Attribute.Function -> LambdaTypeName.get(null, emptyList(), UNIT)
                    is Attribute.Label -> FLOW.plusParameter(STRING)
                    is Attribute.Picker -> List::class.asTypeName().plusParameter(ClassName(Consts.CONFIG_PACKAGE_NAME, "DebugPanelPickerItem"))
                }

                ParameterSpec(paramName, paramType)
            }

        return initialValueParams + valueParams
    }
}
