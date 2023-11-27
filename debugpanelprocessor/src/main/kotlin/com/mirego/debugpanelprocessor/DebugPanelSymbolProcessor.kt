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
import com.mirego.debugpanelprocessor.Attribute
import com.mirego.debugpanelprocessor.Consts
import com.mirego.debugpanelprocessor.Consts.CONFIG_PACKAGE_NAME
import com.mirego.debugpanelprocessor.Consts.FLOW
import com.mirego.debugpanelprocessor.Consts.REPOSITORY_IMPL_NAME
import com.mirego.debugpanelprocessor.Consts.REPOSITORY_NAME
import com.mirego.debugpanelprocessor.Consts.USE_CASE_IMPL_NAME
import com.mirego.debugpanelprocessor.Consts.USE_CASE_NAME
import com.mirego.debugpanelprocessor.ResolvedConfiguration
import com.mirego.debugpanelprocessor.TypeSpecWithImports
import com.mirego.debugpanelprocessor.capitalize
import com.mirego.debugpanelprocessor.typespec.DebugPanelObservablePropertyTypeSpec
import com.mirego.debugpanelprocessor.typespec.DebugPanelPropertyTypeSpec
import com.mirego.debugpanelprocessor.typespec.DebugPanelRepositoryTypeSpec
import com.mirego.debugpanelprocessor.typespec.DebugPanelUseCaseTypeSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.reflect.KClass

class DebugPanelSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private var invoked = false

    private fun KSAnnotated.findAnnotation(clazz: KClass<*>): KSAnnotation? =
        annotations.find { it.annotationType.toString() == clazz.simpleName }

    private fun KSAnnotation.findArgument(name: String): Any? =
        arguments.find { it.name?.getShortName() == name }?.value

    private fun createAttributes(declaration: KSClassDeclaration): Sequence<Attribute> = declaration.getAllProperties()
        .mapNotNull { property ->
            val identifier = property.findAnnotation(Identifier::class)?.arguments?.first()?.value as String?
            val type = property.type.resolve()
            val className = type.toClassName()
            val displayName = property.findAnnotation(DisplayName::class)?.arguments?.first()?.value as String?
            val name = property.simpleName.getShortName()

            when {
                className == TOGGLE_CLASS_NAME -> Attribute.Toggle(identifier, displayName, name)
                className == TEXT_FIELD_CLASS_NAME -> Attribute.TextField(identifier, displayName, name)
                className == LABEL_CLASS_NAME -> Attribute.Label(identifier, displayName, name)
                className == PICKER_CLASS_NAME -> Attribute.Picker(identifier, displayName, name)
                className == DATE_PICKER_CLASS_NAME -> Attribute.DatePicker(identifier, displayName, name)
                (type.declaration as? KSClassDeclaration)?.classKind == ClassKind.ENUM_CLASS -> Attribute.EnumPicker(identifier, displayName, name, type)
                className == BUTTON_CLASS_NAME -> Attribute.Function(identifier, displayName, name)
                else -> null
            }
        }

    private fun getConfigurations(resolver: Resolver): Sequence<ResolvedConfiguration> =
        resolver.getSymbolsWithAnnotation(DebugPanel::class.qualifiedName.toString())
            .filterIsInstance<KSClassDeclaration>()
            .map { declaration ->
                val annotation = declaration.findAnnotation(DebugPanel::class)!!
                val prefix = (annotation.findArgument("prefix") as String).capitalize()
                val packageName = annotation.findArgument("packageName") as String
                val includeResetButton = annotation.findArgument("includeResetButton") as Boolean

                ResolvedConfiguration(
                    declaration = declaration,
                    annotation = annotation,
                    attributes = createAttributes(declaration),
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

    private fun writeDebugProperties(resolver: Resolver) {
        resolver.getSymbolsWithAnnotation(DebugProperty::class.qualifiedName.toString())
            .filterIsInstance<KSPropertyDeclaration>()
            .forEach {
                val name = it.simpleName.getShortName()
                val parent = it.parent as KSClassDeclaration
                val parentName = parent.simpleName.getShortName()
                val packageName = it.packageName.getShortName()
                val propertyName = it.findAnnotation(DebugProperty::class)!!.findArgument("name") as String
                val fileName = parentName + propertyName.capitalize() + "Delegate"
                val returnType = it.type.resolve()
                val isFlow = returnType.declaration.simpleName.getShortName() == FLOW.simpleName

                if (isFlow) {
                    writeFile(packageName, fileName, DebugPanelObservablePropertyTypeSpec.create(fileName, parent, returnType, propertyName, name))
                } else {
                    writeFile(packageName, fileName, DebugPanelPropertyTypeSpec.create(fileName, parent, returnType, propertyName, name))
                }
            }
    }

    private fun writeClasses(resolver: Resolver) {
        getConfigurations(resolver).forEach { configuration ->
            val repositoryPackageName = Consts.getRepositoryPackageName(configuration.packageName)
            val specificRepositoryName = "${configuration.prefix}$REPOSITORY_NAME"
            val specificRepositoryClassName = ClassName(repositoryPackageName, specificRepositoryName)
            val specificRepositoryImplName = "${configuration.prefix}$REPOSITORY_IMPL_NAME"

            val useCasePackageName = Consts.getUseCasePackageName(configuration.packageName)
            val specificUseCaseName = "${configuration.prefix}$USE_CASE_NAME"
            val specificUseCaseClassName = ClassName(useCasePackageName, specificUseCaseName)
            val specificUseCaseImplName = "${configuration.prefix}$USE_CASE_IMPL_NAME"

            val (repositoryInterface, repositoryImplementation) = DebugPanelRepositoryTypeSpec.create(specificRepositoryClassName, configuration.attributes)
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

        writeDebugProperties(resolver)
        writeClasses(resolver)

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
