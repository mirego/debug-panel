import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.mirego.debugpanel.annotations.DebugPanel
import com.mirego.debugpanel.annotations.DisplayName
import com.mirego.debugpanel.annotations.Identifier
import com.mirego.debugpanelprocessor.Attribute
import com.mirego.debugpanelprocessor.Consts
import com.mirego.debugpanelprocessor.Consts.CONFIG_PACKAGE_NAME
import com.mirego.debugpanelprocessor.Consts.REPOSITORY_IMPL_NAME
import com.mirego.debugpanelprocessor.Consts.REPOSITORY_NAME
import com.mirego.debugpanelprocessor.Consts.USE_CASE_IMPL_NAME
import com.mirego.debugpanelprocessor.Consts.USE_CASE_NAME
import com.mirego.debugpanelprocessor.Consts.USE_CASE_PACKAGE_NAME
import com.mirego.debugpanelprocessor.DebugPanelTypeSpecFactory
import com.mirego.debugpanelprocessor.capitalize
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.reflect.KClass

class DebugPanelSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private var invoked = false

    private data class Import(
        val packageName: String,
        val name: String
    )

    private data class ResolvedConfiguration(
        val declaration: KSClassDeclaration,
        val annotation: KSAnnotation
    )

    private fun KSAnnotated.findAnnotation(clazz: KClass<*>): KSAnnotation? =
        annotations.find { it.annotationType.toString() == clazz.simpleName }

    private fun KSAnnotation.findArgument(name: String): Any? =
        arguments.find { it.name?.getShortName() == name }?.value

    private fun getConfigurations(resolver: Resolver): Sequence<ResolvedConfiguration> =
        resolver.getSymbolsWithAnnotation(DebugPanel::class.qualifiedName.toString())
            .filterIsInstance<KSClassDeclaration>()
            .map { ResolvedConfiguration(it, it.findAnnotation(DebugPanel::class)!!) }

    private fun createAttributes(config: ResolvedConfiguration): Sequence<Attribute> = config.declaration.getAllProperties()
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
                (type.declaration as? KSClassDeclaration)?.classKind == ClassKind.ENUM_CLASS -> Attribute.EnumPicker(identifier, displayName, name, type)
                className == BUTTON_CLASS_NAME -> Attribute.Function(identifier, displayName, name)
                else -> null
            }
        }

    private fun writeFile(packageName: String, name: String, type: TypeSpec, vararg imports: Import) {
        FileSpec.builder(packageName, name)
            .addType(type)
            .run {
                imports.fold(this) { acc, element ->
                    acc.addImport(element.packageName, element.name)
                }
            }
            .build()
            .writeTo(environment.codeGenerator, aggregating = false)
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }

        getConfigurations(resolver).forEach { configuration ->
            val prefix = (configuration.annotation.findArgument("prefix") as String).capitalize()
            val packageName = configuration.annotation.findArgument("packageName") as String

            val repositoryPackageName = Consts.getRepositoryPackageName(packageName)
            val specificRepositoryName = "$prefix$REPOSITORY_NAME"
            val specificRepositoryClassName = ClassName(repositoryPackageName, specificRepositoryName)
            val specificRepositoryImplName = "$prefix$REPOSITORY_IMPL_NAME"

            val useCasePackageName = Consts.getUseCasePackageName(packageName)
            val specificUseCaseName = "$prefix$USE_CASE_NAME"
            val specificUseCaseClassName = ClassName(useCasePackageName, specificUseCaseName)
            val specificUseCaseImplName = "$prefix$USE_CASE_IMPL_NAME"

            val attributes = createAttributes(configuration)

            val (repositoryInterface, repositoryImplementation) = DebugPanelTypeSpecFactory.createRepository(specificRepositoryClassName, attributes)
            val (useCaseInterface, useCaseImplementation) = DebugPanelTypeSpecFactory.createUseCase(specificUseCaseClassName, specificRepositoryClassName, attributes)

            writeFile(repositoryPackageName, specificRepositoryName, repositoryInterface)
            writeFile(repositoryPackageName, specificRepositoryImplName, repositoryImplementation)
            writeFile(useCasePackageName, specificUseCaseName, useCaseInterface)
            writeFile(
                useCasePackageName,
                specificUseCaseImplName,
                useCaseImplementation,
                Import(CONFIG_PACKAGE_NAME, "DebugPanelPickerItem"),
                Import(USE_CASE_PACKAGE_NAME, "DebugPanelItemViewData")
            )
        }

        invoked = true
        return emptyList()
    }

    companion object {
        private val LABEL_CLASS_NAME = ClassName(CONFIG_PACKAGE_NAME, "DebugPanelLabel")
        private val PICKER_CLASS_NAME = ClassName(CONFIG_PACKAGE_NAME, "DebugPanelPicker")
        private val BUTTON_CLASS_NAME = ClassName(CONFIG_PACKAGE_NAME, "DebugPanelButton")
        private val TOGGLE_CLASS_NAME = ClassName(CONFIG_PACKAGE_NAME, "DebugPanelToggle")
        private val TEXT_FIELD_CLASS_NAME = ClassName(CONFIG_PACKAGE_NAME, "DebugPanelTextField")
    }
}
