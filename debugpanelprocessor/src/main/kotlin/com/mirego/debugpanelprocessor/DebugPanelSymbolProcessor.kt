import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.mirego.debugpanel.annotations.DebugPanel
import com.mirego.debugpanel.annotations.DebugProperty
import com.mirego.debugpanel.annotations.DisplayName
import com.mirego.debugpanel.annotations.Identifier
import com.mirego.debugpanelprocessor.Component
import com.mirego.debugpanelprocessor.Consts
import com.mirego.debugpanelprocessor.Consts.CONFIG_PACKAGE_NAME
import com.mirego.debugpanelprocessor.Consts.FLOW
import com.mirego.debugpanelprocessor.Consts.REPOSITORY_IMPL_NAME
import com.mirego.debugpanelprocessor.Consts.REPOSITORY_NAME
import com.mirego.debugpanelprocessor.Consts.USE_CASE_IMPL_NAME
import com.mirego.debugpanelprocessor.Consts.USE_CASE_NAME
import com.mirego.debugpanelprocessor.Property
import com.mirego.debugpanelprocessor.ResolvedConfiguration
import com.mirego.debugpanelprocessor.TypeSpecWithImports
import com.mirego.debugpanelprocessor.capitalize
import com.mirego.debugpanelprocessor.typespec.DebugPanelObservablePropertyTypeSpec
import com.mirego.debugpanelprocessor.typespec.DebugPanelPropertyTypeSpec
import com.mirego.debugpanelprocessor.typespec.DebugPanelRepositoryTypeSpec
import com.mirego.debugpanelprocessor.typespec.DebugPanelUseCaseTypeSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.reflect.KClass

class DebugPanelSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private var invoked = false

    private fun KSAnnotated.findAnnotation(clazz: KClass<*>): KSAnnotation? =
        annotations.find { it.annotationType.toString() == clazz.simpleName }

    private fun KSAnnotation.findArgument(name: String): Any? =
        arguments.find { it.name?.getShortName() == name }?.value

    private fun createComponents(declaration: KSClassDeclaration, debugProperties: Sequence<KSPropertyDeclaration>) = createComponents(
        declaration.getAllProperties()
            .mapNotNull {
                val type = it.type.resolve()
                val className = type.toClassName()
                val propertyType: Property.Component = when {
                    className == TOGGLE_CLASS_NAME -> Property.Component.Toggle
                    className == TEXT_FIELD_CLASS_NAME -> Property.Component.TextField
                    className == LABEL_CLASS_NAME -> Property.Component.Label
                    className == PICKER_CLASS_NAME -> Property.Component.Picker
                    className == DATE_PICKER_CLASS_NAME -> Property.Component.DatePicker
                    (type.declaration as? KSClassDeclaration)?.classKind == ClassKind.ENUM_CLASS -> Property.Component.EnumPicker(type)
                    className == BUTTON_CLASS_NAME -> Property.Component.Button
                    else -> return@mapNotNull null
                }

                Property(it, propertyType, it.simpleName.getShortName())
            },
        isFromDebugProperty = false
    ) + createComponents(
        debugProperties.mapNotNull {
            val propertyName = it.findAnnotation(DebugProperty::class)!!.findArgument("name") as String
            val type = it.type.resolve()
            val typeToUse = if (type.declaration.simpleName.getShortName() == FLOW.simpleName) {
                type.arguments.first().type!!.resolve()
            } else {
                type
            }
            val className = typeToUse.toClassName()
            val propertyType = when {
                className == STRING -> Property.Component.TextField
                (typeToUse.declaration as? KSClassDeclaration)?.classKind == ClassKind.ENUM_CLASS -> Property.Component.EnumPicker(typeToUse)
                else -> return@mapNotNull null
            }
            Property(it, propertyType, propertyName)
        },
        isFromDebugProperty = true
    )

    private fun createComponents(properties: Sequence<Property>, isFromDebugProperty: Boolean): Sequence<Component> =
        properties.map { property ->
            val identifier = property.declaration.findAnnotation(Identifier::class)?.arguments?.first()?.value as String?
            val displayName = property.declaration.findAnnotation(DisplayName::class)?.arguments?.first()?.value as String?
            val name = property.name
            val requiresInitialValue = !isFromDebugProperty

            when (property.component) {
                Property.Component.DatePicker -> Component.DatePicker(identifier, displayName, name, requiresInitialValue)
                is Property.Component.EnumPicker -> Component.EnumPicker(identifier, displayName, name, requiresInitialValue, property.component.type)
                Property.Component.Button -> Component.Button(identifier, displayName, name, requiresInitialValue)
                Property.Component.Label -> Component.Label(identifier, displayName, name, requiresInitialValue)
                Property.Component.Picker -> Component.Picker(identifier, displayName, name, requiresInitialValue)
                Property.Component.TextField -> Component.TextField(identifier, displayName, name, requiresInitialValue)
                Property.Component.Toggle -> Component.Toggle(identifier, displayName, name, requiresInitialValue)
            }
        }

    private fun getConfigurations(declarations: Sequence<KSClassDeclaration>, debugProperties: Sequence<KSPropertyDeclaration>): Sequence<ResolvedConfiguration> =
        declarations
            .map { declaration ->
                val annotation = declaration.findAnnotation(DebugPanel::class)!!
                val prefix = (annotation.findArgument("prefix") as String).capitalize()
                val packageName = annotation.findArgument("packageName") as String
                val includeResetButton = annotation.findArgument("includeResetButton") as Boolean

                ResolvedConfiguration(
                    declaration = declaration,
                    annotation = annotation,
                    components = createComponents(declaration, debugProperties),
                    prefix = prefix,
                    packageName = packageName,
                    includeResetButton = includeResetButton
                )
            }

    private fun writeFile(packageName: String, name: String, type: TypeSpecWithImports) {
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
                val name = property.simpleName.getShortName()
                val parent = property.parent as KSClassDeclaration
                val packageName = property.packageName.getShortName()
                val propertyName = property.findAnnotation(DebugProperty::class)!!.findArgument("name") as String
                val fileName = parent.simpleName.getShortName() + propertyName.capitalize() + "Delegate"
                val returnType = property.type.resolve()

                if (returnType.declaration.simpleName.getShortName() == FLOW.simpleName) {
                    writeFile(packageName, fileName, DebugPanelObservablePropertyTypeSpec.create(fileName, parent, returnType, propertyName, name))
                } else {
                    writeFile(packageName, fileName, DebugPanelPropertyTypeSpec.create(fileName, parent, returnType, propertyName, name))
                }
            }
    }

    private fun writeClasses(declarations: Sequence<KSClassDeclaration>, debugProperties: Sequence<KSPropertyDeclaration>) {
        getConfigurations(declarations, debugProperties).forEach { configuration ->
            val repositoryPackageName = Consts.getRepositoryPackageName(configuration.packageName)
            val specificRepositoryName = "${configuration.prefix}$REPOSITORY_NAME"
            val specificRepositoryClassName = ClassName(repositoryPackageName, specificRepositoryName)
            val specificRepositoryImplName = "${configuration.prefix}$REPOSITORY_IMPL_NAME"

            val useCasePackageName = Consts.getUseCasePackageName(configuration.packageName)
            val specificUseCaseName = "${configuration.prefix}$USE_CASE_NAME"
            val specificUseCaseClassName = ClassName(useCasePackageName, specificUseCaseName)
            val specificUseCaseImplName = "${configuration.prefix}$USE_CASE_IMPL_NAME"

            val (repositoryInterface, repositoryImplementation) = DebugPanelRepositoryTypeSpec.create(specificRepositoryClassName, configuration.components)
            val (useCaseInterface, useCaseImplementation) = DebugPanelUseCaseTypeSpec.create(specificUseCaseClassName, specificRepositoryClassName, configuration)

            writeFile(repositoryPackageName, specificRepositoryName, repositoryInterface)
            writeFile(repositoryPackageName, specificRepositoryImplName, repositoryImplementation)
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

    companion object {
        private val LABEL_CLASS_NAME = ClassName(CONFIG_PACKAGE_NAME, "DebugPanelLabel")
        private val PICKER_CLASS_NAME = ClassName(CONFIG_PACKAGE_NAME, "DebugPanelPicker")
        private val DATE_PICKER_CLASS_NAME = ClassName(CONFIG_PACKAGE_NAME, "DebugPanelDatePicker")
        private val BUTTON_CLASS_NAME = ClassName(CONFIG_PACKAGE_NAME, "DebugPanelButton")
        private val TOGGLE_CLASS_NAME = ClassName(CONFIG_PACKAGE_NAME, "DebugPanelToggle")
        private val TEXT_FIELD_CLASS_NAME = ClassName(CONFIG_PACKAGE_NAME, "DebugPanelTextField")
    }
}
