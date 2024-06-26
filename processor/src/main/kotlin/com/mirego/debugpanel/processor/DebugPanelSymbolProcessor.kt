package com.mirego.debugpanel.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.mirego.debugpanel.annotations.DebugPanel
import com.mirego.debugpanel.annotations.DebugProperty
import com.mirego.debugpanel.annotations.Identifier
import com.mirego.debugpanel.processor.Consts.COMPONENTS_VISIBILITY_NAME
import com.mirego.debugpanel.processor.Consts.FLOW
import com.mirego.debugpanel.processor.Consts.REPOSITORY_IMPL_NAME
import com.mirego.debugpanel.processor.Consts.REPOSITORY_NAME
import com.mirego.debugpanel.processor.Consts.USE_CASE_IMPL_NAME
import com.mirego.debugpanel.processor.Consts.USE_CASE_NAME
import com.mirego.debugpanel.processor.typespec.DebugPanelComponentsVisibilityTypeSpec
import com.mirego.debugpanel.processor.typespec.DebugPanelObservablePropertyTypeSpec
import com.mirego.debugpanel.processor.typespec.DebugPanelPropertyTypeSpec
import com.mirego.debugpanel.processor.typespec.DebugPanelRepositoryTypeSpec
import com.mirego.debugpanel.processor.typespec.DebugPanelUseCaseTypeSpec
import com.mirego.debugpanel.processor.typespec.TypeSpecWithImports
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.writeTo

class DebugPanelSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private var invoked = false

    private fun getConfigurations(
        declarations: Sequence<KSClassDeclaration>,
        debugProperties: Sequence<KSPropertyDeclaration>,
    ): Sequence<ResolvedConfiguration> =
        declarations
            .map { declaration ->
                val annotation = declaration.findAnnotation(DebugPanel::class)!!
                val prefix = (annotation.findArgument("prefix") as String).capitalize()
                val packageName = annotation.findArgument("packageName") as String

                ResolvedConfiguration(
                    declaration = declaration,
                    annotation = annotation,
                    components = ComponentFactory.createAllComponents(declaration, debugProperties),
                    prefix = prefix,
                    packageName = packageName,
                )
            }

    private fun writeFile(
        packageName: String,
        name: String,
        type: TypeSpecWithImports,
    ) {
        FileSpec.builder(packageName, name)
            .addType(type.typeSpec)
            .run {
                type.imports.fold(this) { acc, element ->
                    acc.addImport(element.packageName, element.name)
                }
            }
            .build()
            .writeTo(environment.codeGenerator, aggregating = false)
    }

    private fun writeDebugProperties(debugProperties: Sequence<KSPropertyDeclaration>) {
        debugProperties
            .forEach { property ->
                val declarationName = property.simpleName.getShortName()
                val parentDeclaration = property.parent as KSClassDeclaration
                val packageName = property.packageName.getShortName()
                val propertyName = property.findAnnotation(DebugProperty::class)!!.findArgument("name") as String
                val fileName = parentDeclaration.simpleName.getShortName() + propertyName.capitalize() + "Delegate"
                val returnType = property.type.resolve()
                val safeIdentifier = property.findAnnotation(Identifier::class)?.arguments?.first()?.value as String? ?: propertyName

                if (returnType.declaration.simpleName.getShortName() == FLOW.simpleName) {
                    writeFile(packageName, fileName, DebugPanelObservablePropertyTypeSpec.create(fileName, parentDeclaration, returnType, safeIdentifier, declarationName))
                } else {
                    writeFile(packageName, fileName, DebugPanelPropertyTypeSpec.create(fileName, parentDeclaration, returnType, safeIdentifier, declarationName))
                }
            }
    }

    private fun writeClasses(
        declarations: Sequence<KSClassDeclaration>,
        debugProperties: Sequence<KSPropertyDeclaration>,
    ) {
        getConfigurations(declarations, debugProperties).forEach { configuration ->
            val repositoryPackageName = Consts.getRepositoryPackageName(configuration.packageName)
            val specificRepositoryName = "${configuration.prefix}$REPOSITORY_NAME"
            val specificRepositoryClassName = ClassName(repositoryPackageName, specificRepositoryName)
            val specificRepositoryImplName = "${configuration.prefix}$REPOSITORY_IMPL_NAME"

            val useCasePackageName = Consts.getUseCasePackageName(configuration.packageName)

            val componentsVisibilityName = "${configuration.prefix}$COMPONENTS_VISIBILITY_NAME"
            val componentsVisibilityClassName = ClassName(useCasePackageName, componentsVisibilityName)

            val specificUseCaseName = "${configuration.prefix}$USE_CASE_NAME"
            val specificUseCaseClassName = ClassName(useCasePackageName, specificUseCaseName)
            val specificUseCaseImplName = "${configuration.prefix}$USE_CASE_IMPL_NAME"

            val (repositoryInterface, repositoryImplementation) = DebugPanelRepositoryTypeSpec.create(specificRepositoryClassName, configuration.components)
            val componentsVisibility = DebugPanelComponentsVisibilityTypeSpec.create(componentsVisibilityName, configuration.components)
            val (useCaseInterface, useCaseImplementation) = DebugPanelUseCaseTypeSpec.create(specificUseCaseClassName, specificRepositoryClassName, componentsVisibilityClassName, configuration)

            writeFile(repositoryPackageName, specificRepositoryName, repositoryInterface)
            writeFile(repositoryPackageName, specificRepositoryImplName, repositoryImplementation)
            writeFile(useCasePackageName, componentsVisibilityName, TypeSpecWithImports(componentsVisibility))
            writeFile(useCasePackageName, specificUseCaseName, useCaseInterface)
            writeFile(useCasePackageName, specificUseCaseImplName, useCaseImplementation)
        }
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }

        val declarations = resolver.getSymbolsWithAnnotation(DebugPanel::class.qualifiedName.toString())
            .filterIsInstance<KSClassDeclaration>()

        val debugProperties = resolver.getSymbolsWithAnnotation(DebugProperty::class.qualifiedName.toString())
            .filterIsInstance<KSPropertyDeclaration>()

        writeDebugProperties(debugProperties)
        writeClasses(declarations, debugProperties)

        invoked = true
        return emptyList()
    }
}
