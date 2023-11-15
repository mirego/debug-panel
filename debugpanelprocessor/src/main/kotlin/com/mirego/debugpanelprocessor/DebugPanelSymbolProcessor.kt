import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.mirego.debugpanel.annotations.DebugPanel
import com.mirego.debugpanel.annotations.DisplayName
import com.mirego.debugpanelprocessor.Attribute
import com.mirego.debugpanelprocessor.Consts.REPOSITORY_IMPL_NAME
import com.mirego.debugpanelprocessor.Consts.REPOSITORY_NAME
import com.mirego.debugpanelprocessor.Consts.REPOSITORY_PACKAGE_NAME
import com.mirego.debugpanelprocessor.Consts.USE_CASE_IMPL_NAME
import com.mirego.debugpanelprocessor.Consts.USE_CASE_NAME
import com.mirego.debugpanelprocessor.Consts.USE_CASE_PACKAGE_NAME
import com.mirego.debugpanelprocessor.DebugPanelTypeSpecFactory
import com.mirego.debugpanelprocessor.capitalize
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.LIST
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.reflect.KClass

class DebugPanelSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private var invoked = false

    private data class ResolvedConfiguration(
        val declaration: KSClassDeclaration,
        val annotation: KSAnnotation
    )

    private fun KSAnnotated.findAnnotation(clazz: KClass<*>): KSAnnotation? =
        annotations.find { it.annotationType.toString() == clazz.simpleName }

    private fun getConfigurations(resolver: Resolver): Sequence<ResolvedConfiguration> =
        resolver.getSymbolsWithAnnotation(DebugPanel::class.qualifiedName.toString())
            .filterIsInstance<KSClassDeclaration>()
            .map { ResolvedConfiguration(it, it.findAnnotation(DebugPanel::class)!!) }

    private fun createAttributes(config: ResolvedConfiguration): Sequence<Attribute> = config.declaration.getAllProperties()
        .mapNotNull { property ->
            val type = property.type.resolve()
            val className = type.toClassName()
            val displayName = property.findAnnotation(DisplayName::class)?.arguments?.first()?.value as String?
            val name = property.simpleName.getShortName()

            when {
                className == BOOLEAN -> Attribute.Toggle(displayName, name, type)
                className == STRING && property.isMutable -> Attribute.TextField(displayName, name, type)
                className == STRING && !property.isMutable -> Attribute.Label(displayName, name, type)
                className == LIST -> Attribute.Picker(displayName, name, type)
                (type.declaration as? KSClassDeclaration)?.classKind == ClassKind.ENUM_CLASS -> Attribute.EnumPicker(displayName, name, type)
                className == FUNCTION_0_CLASS_NAME -> Attribute.Function(displayName, name, type)
                else -> null
            }
        }

    private fun writeFile(packageName: String, name: String, type: TypeSpec) {
        FileSpec.builder(packageName, name)
            .addType(type)
            .build()
            .writeTo(environment.codeGenerator, aggregating = false)
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }

        getConfigurations(resolver).forEach { configuration ->
            val prefix = (configuration.annotation.arguments.first().value as String).capitalize()

            val specificRepositoryName = "$prefix$REPOSITORY_NAME"
            val specificRepositoryClassName = ClassName(REPOSITORY_PACKAGE_NAME, specificRepositoryName)
            val specificRepositoryImplName = "$prefix$REPOSITORY_IMPL_NAME"

            val specificUseCaseName = "$prefix$USE_CASE_NAME"
            val specificUseCaseClassName = ClassName(USE_CASE_PACKAGE_NAME, specificUseCaseName)
            val specificUseCaseImplName = "$prefix$USE_CASE_IMPL_NAME"

            val attributes = createAttributes(configuration)

            val (repositoryInterface, repositoryImplementation) = DebugPanelTypeSpecFactory.createRepository(specificRepositoryClassName, attributes)
            val (useCaseInterface, useCaseImplementation) = DebugPanelTypeSpecFactory.createUseCase(specificUseCaseClassName, specificRepositoryClassName, attributes)

            writeFile(REPOSITORY_PACKAGE_NAME, specificRepositoryName, repositoryInterface)
            writeFile(REPOSITORY_PACKAGE_NAME, specificRepositoryImplName, repositoryImplementation)
            writeFile(USE_CASE_PACKAGE_NAME, specificUseCaseName, useCaseInterface)
            writeFile(USE_CASE_PACKAGE_NAME, specificUseCaseImplName, useCaseImplementation)
        }

        invoked = true
        return emptyList()
    }

    companion object {
        private val FUNCTION_0_CLASS_NAME = ClassName("kotlin", "Function0")
    }
}
