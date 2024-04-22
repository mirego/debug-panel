package com.mirego.debugpanel.processor.typespec

import com.mirego.debugpanel.processor.Component
import com.mirego.debugpanel.processor.Consts
import com.mirego.debugpanel.processor.Consts.FLOW
import com.mirego.debugpanel.processor.ResolvedConfiguration
import com.mirego.debugpanel.processor.capitalize
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
    fun create(className: ClassName, specificRepositoryClassName: ClassName, componentsVisibilityClassName: ClassName, configuration: ResolvedConfiguration) = InterfaceImplementation.create(
        interfaceClassName = className,
        functions = createFunctions(componentsVisibilityClassName, configuration),
        configureInterface = { configureInterface(specificRepositoryClassName) },
        configureImplementation = { configureImplementation(specificRepositoryClassName) },
        interfaceImports = listOf(
            Import(Consts.FLOW_PACKAGE_NAME, "flowOf"),
        ),
        implementationImports = listOf(
            Import(Consts.CONFIG_PACKAGE_NAME, "DebugPanelPickerItem"),
            Import(Consts.USE_CASE_PACKAGE_NAME, "DebugPanelItemViewData"),
            Import(Consts.FLOW_PACKAGE_NAME, "map"),
        )
    )

    private fun createFunctions(componentsVisibilityClassName: ClassName, configuration: ResolvedConfiguration): Iterable<InterfaceImplementation.Function> = listOf(
        createComponentItemViewDataList(configuration.components).let { itemViewDataList ->
            val viewDataName = "DebugPanelViewData"

            InterfaceImplementation.Function(
                name = "createViewData",
                returnType = FLOW.plusParameter(ClassName(Consts.USE_CASE_PACKAGE_NAME, viewDataName)),
                code = """
                    |return componentsVisibility.map {
                    |$viewDataName(
                    |⇥listOf(
                    |⇥${itemViewDataList.joinToString(",\n")}
                    |⇤)
                    |⇤)
                    |}
                """.trimMargin(),
                params = createParams(componentsVisibilityClassName, configuration.components).asIterable()
            )
        }
    )

    private fun TypeSpec.Builder.configureInterface(specificRepositoryClassName: ClassName): TypeSpec.Builder =
        addSuperinterface(ClassName(Consts.USE_CASE_PACKAGE_NAME, Consts.USE_CASE_NAME))
            .addSuperinterface(specificRepositoryClassName)

    private fun TypeSpec.Builder.configureImplementation(specificRepositoryClassName: ClassName): TypeSpec.Builder {
        val repositoryParamName = "repository"

        return addModifiers(KModifier.OPEN)
            .superclass(ClassName(Consts.USE_CASE_PACKAGE_NAME, Consts.USE_CASE_IMPL_NAME))
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

    private fun createComponentItemViewDataList(components: Sequence<Component>): Sequence<String> = components.map {
        when (it) {
            is Component.Button -> DebugPanelItemViewDataFactory.createButton(it)
            is Component.Label -> DebugPanelItemViewDataFactory.createLabel(it)
            is Component.Picker -> DebugPanelItemViewDataFactory.createPicker(it)
            is Component.DatePicker -> DebugPanelItemViewDataFactory.createDatePicker(it)
            is Component.TextField -> DebugPanelItemViewDataFactory.createTextField(it)
            is Component.Toggle -> DebugPanelItemViewDataFactory.createToggle(it)
            is Component.EnumPicker -> DebugPanelItemViewDataFactory.createPicker(it)
        }
    }

    private fun createParams(componentsVisibilityClassName: ClassName, components: Sequence<Component>): Sequence<ParameterSpec> {
        val initialValueParams = components
            .filter { it.requiresInitialValue }
            .mapNotNull { component ->
                val paramName: String = "initial${component.name.capitalize()}".takeIf { component.persistedType != null } ?: return@mapNotNull null

                val paramType: TypeName = when (component) {
                    is Component.EnumPicker -> component.type.toTypeName().copy(nullable = true)
                    is Component.DatePicker -> component.persistedType.asTypeName().copy(nullable = true)
                    is Component.Picker -> STRING.copy(nullable = true)
                    else -> component.persistedType?.asTypeName()
                } ?: return@mapNotNull null

                ParameterSpec(paramName, paramType)
            }

        val valueParams = components
            .mapNotNull { component ->
                val paramName: String = when (component) {
                    is Component.TextField, is Component.Toggle, is Component.EnumPicker -> return@mapNotNull null
                    is Component.Button, is Component.Label, is Component.Picker, is Component.DatePicker -> component.name
                }

                @Suppress("KotlinConstantConditions")
                val paramType: TypeName = when (component) {
                    is Component.TextField, is Component.Toggle, is Component.EnumPicker, is Component.DatePicker -> return@mapNotNull null
                    is Component.Button -> LambdaTypeName.get(null, emptyList(), UNIT)
                    is Component.Label -> FLOW.plusParameter(STRING)
                    is Component.Picker -> List::class.asTypeName().plusParameter(ClassName(Consts.CONFIG_PACKAGE_NAME, "DebugPanelPickerItem"))
                }

                ParameterSpec(paramName, paramType)
            }

        val visibilityParam = ParameterSpec.builder("componentsVisibility", FLOW.plusParameter(componentsVisibilityClassName))
            .defaultValue("flowOf(${componentsVisibilityClassName.simpleName}())")
            .build()

        return initialValueParams + valueParams + sequenceOf(visibilityParam)
    }
}
