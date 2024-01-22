<div align="center">
  <img src="logo.png" width="563" />
  <p><strong>Debug Panel</strong> is a Kotlin Multiplatform library built by <a href="https://www.mirego.com">Mirego</a> that allows mobile developers to generate<br /> boilerplate code to display a debug panel with different component types.</p>
  <br />
  <a href="https://github.com/mirego/debug-panel/actions/workflows/ci.yaml"><img alt="" src="https://github.com/mirego/debug-panel/actions/workflows/ci.yaml/badge.svg"/></a>
  <a href="https://kotlinlang.org/"><img alt="" src="https://img.shields.io/badge/kotlin-1.9.21-blue.svg?logo=kotlin"/></a>
  <a href="https://opensource.org/licenses/BSD-3-Clause"><img alt="" src="https://img.shields.io/badge/License-BSD_3--Clause-blue.svg"/></a>
</div>

# Table of contents

1. [How it works](#how-it-works)
2. [Setup](#setup)
    1. [Common module](#common-module)
    2. [Android](#android)
    3. [iOS](#ios)
3. [Usage](#usage)
    1. [Components](#components)
    2. [Annotations](#annotations)
4. [Architecture](#architecture)
5. [License](#license)
6. [About Mirego](#about-mirego)

<a name="how-it-works"></a>

## How it works

The main goal of this library is to have a class definition in your common code that specifies how the debug panel should be built. Using this definition, the library generates:

* a repository with typed getters
* a use case with a typed parameters function that creates a list of item view data
* property delegates that can be used directly in existing code to reduce the friction from reading debug values

The view data list created by the use case can be passed to a builtin view model that handles the user interactions. You have the choice to either use the default UI that comes with the library or to build your own.

<a name="setup"></a>

## Setup

<a name="common-module"></a>

### Common module

The library is published to Mirego's public Maven repository, so make sure you have it included in your settings.gradle.kts `dependencyResolutionManagement` block.

```kotlin
dependencyResolutionManagement {
    repositories {
        // ...
        maven("https://s3.amazonaws.com/mirego-maven/public")
    }
}
```

In your top-level build.gradle.kts file add the reference to the KSP plugin:

```kotlin
plugins {
    // ...
    id("com.google.devtools.ksp") version "1.9.21-1.0.15" apply false
}
```

In your common module's build.gradle.kts file add the reference to the KSP plugin:

```kotlin
plugins {
    // ...
    id("com.google.devtools.ksp")
}
```

Also add the `core` and `annotations` dependencies along with the KSP generated source directory:

```kotlin
val commonMain by getting {
    dependencies {
        // ...
        api("com.mirego.trikot:viewmodels-declarative-flow:x.y.z")
        api("com.mirego.debugpanel:core:x.y.z")
        implementation("com.mirego.debugpanel:annotations:x.y.z")
    }
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}
```

Don't forget to export the core dependency to the iOS framework:

```kotlin
kotlin {
    cocoapods {
        framework {
            // ...
            export("com.mirego.trikot:viewmodels-declarative-flow:x.y.z")
            export("com.mirego.debugpanel:core:x.y.z")
        }
    }
}
```

You also need to add the compiler's reference to the dependencies block:

```kotlin
dependencies {
    add("kspCommonMainMetadata", "com.mirego.debugpanel:compiler:x.y.z")
}
```

<a name="android"></a>

### Android

The sample UI is resolved automatically from the common module since we include the library with the `api()` function.

If you have some issues with a duplicated `META-INF/versions/9/previous-compilation-data.bin` file during compilation, you can add it to the excluded resources inside the Android app's build.gradle.kts file:

```kotlin
android {
    packaging {
        resources {
            excludes += listOf(
                "META-INF/versions/9/previous-compilation-data.bin"
            )
        }
    }
}
```

<a name="ios"></a>

### iOS

If you want to use the sample UI on iOS, include the pod in the application's Podfile:

```
pod 'Trikot/viewmodels.declarative.SwiftUI.flow', :git => 'git@github.com:mirego/trikot.git', :tag => 'x.y.z', :inhibit_warnings => true
pod 'DebugPanel', :git => 'git@github.com:mirego/debug-panel.git', :tag => 'x.y.z', :inhibit_warnings => true
```

<a name="usage"></a>

## Usage

In your common's module, create a class with the @DebugPanel annotation. You can find the different components that are available in the table below.

```kotlin
@DebugPanel(prefix = "MyProject", packageName = "com.myproject.app.generated")
data class DebugPanelConfig(
    val toggle: DebugPanelToggle,
    val label: DebugPanelLabel,
    val textField: DebugPanelTextField,
    val button: DebugPanelButton,
    val picker: DebugPanelPicker,
    val datePicker: DebugPanelDatePicker,
    val enumPicker: SomeEnum
)
```

Once the configuration is done, you can run the `kspCommonMainMetadata` Gradle task to generate the specific files for your project.<br>
You should now have `MyProjectDebugPanelUseCase.kt`, `MyProjectDebugPanelUseCaseImpl.kt`, `MyProjectDebugPanelRepository.kt` and `MyProjectDebugPanelRepositoryImpl.kt` available in your classpath.<br>

All you need to do now is to instantiate the implementations:

```kotlin

private val repository: MyProjectDebugPanelRepository = MyProjectDebugPanelRepositoryImpl()
private val useCase: MyProjectDebugPanelUseCase = MyProjectDebugPanelUseCaseImpl(repository)

/* ... */

class ParentViewModelImpl(
    coroutineScope: CoroutineScope,
    useCase: MyProjectDebugPanelUseCase
) : ParentViewModel, VMDViewModelImpl(coroutineScope) {
    override val debugPanel = DebugPanelViewModelImpl(
        coroutineScope,
        useCase,
        useCase.createViewData( /* Configure the components here */)
    )
}
```

<a name="components"></a>

### Components

| Name                 | Persisted data type | Configuration                                                    |
|----------------------|---------------------|------------------------------------------------------------------|
| DebugPanelToggle     | `Boolean`           | Initial `Boolean` value                                          |
| DebugPanelLabel      | -                   | `Flow<String>`                                                   |
| DebugPanelTextField  | `String`            | Initial `String` value                                           |
| DebugPanelButton     | -                   | Initial `() -> Unit` value                                       |
| DebugPanelPicker     | `String`            | Initial `String` value representing the selected item identifier |
| DebugPanelDatePicker | `Long`              | Initial `Long` value representing the epoch in milliseconds      |
| Enum                 | `String`            | Initial enum value                                               |

<a name="annotations"></a>

### Annotations

#### @DebugPanel

The debug panel is configured using the `@DebugPanel(val prefix: String, val packageName: String)` annotation.<br><br>

* The `prefix` is included in the generated use case and repository classes.<br><br>
* The `packageName` is where the files will be output inside the `generated` folder.<br><br>

#### @Identifier

By default the values are saved in the settings using their field name as identifier. However this behaviour can be overridden using the `@Identifier(val value: String)` annotation.<br>
For exemple, this is useful in the case where you would want to replace an old debug panel with this one and keep the original keys.

Example:

```kotlin
@Identifier("PREVIEW_MODE")
val preview: DebugPanelToggle
```

#### @DisplayName

By default the components are displayed beside a label with the field name as value. You can use the `@DisplayName(val value: String)` annotation to give the components a more meaningful label.

Example:

```kotlin
@DisplayName("Preview Mode")
val preview: DebugPanelToggle
```

#### @DebugProperty

You can use the `@DebugProperty(val name: String)` annotation to generate a component that is bound to a class property.<br>
For example, you can have a repository with a `String` or `Flow<String>` property, and by putting the annotation on the field the library will generate a delegate property.<br>
You can then expose this delegate field in the interface and the caller will either receive your internal value or the one from the debug panel (in the case where it is overridden).<br>
Please note that this annotation can only be used with the types: `String`, `Boolean`, `Enum`, `Flow<String>`, `Flow<Boolean>` and `Flow<Enum>`.

Example:

`Repository.kt`

```kotlin
interface Repository {
    val value: Flow<String>
}
```

`RepositoryImpl.kt`

```kotlin
class RepositoryImpl : Repository {
    @Identifier("custom_value_identifier")
    @DebugProperty("value")
    val internalValue = flowOf("String value")

    override val value by RepositoryImplValueDelegate
}
```

Caller:

```kotlin
val repository: Repository = RepositoryImpl()

repository.value.map {
    println("Repository value: $it")
}
```

This will either print<br>
`Repository value: String value` or `Repository value: Overridden value` depending if `Overridden value` has been input inside the generated text field.

<a name="architecture"></a>

### Clearing the component values

The generated repository comes with a `resetSettings()` method that you can call in order to clear the persisted component values.
Please be aware that the debug panel view models are not bound to these values, so you will need to either exit the debug panel screen or kill the application to make sure the values are reset properly
(see `RootViewModelImpl.kt` in the sample application folder).

## Architecture

The generated use case and repository implementations have the `open` modifier, which means you can extend them to add more functionalities if you need.<br>
If your project has a dependency injection library like [Koin](https://insert-koin.io/) and you have your own extended classes, you can annotate them with either `@Factory` or `@Single`.<br>
If you don't need to override these classes, you can just put them manually in the dependencies injection modules.

<a name="license"></a>

## License

Debug Panel is © 2013-2023 [Mirego](https://www.mirego.com) and may be freely distributed under the [New BSD license](http://opensource.org/licenses/BSD-3-Clause). See the [`LICENSE.md`](./LICENSE.md) file.

<a name="about-mirego"></a>

## About Mirego

[Mirego](https://www.mirego.com) is a team of passionate people who believe that work is a place where you can innovate and have fun. We’re a team of [talented people](https://life.mirego.com) who imagine and build beautiful Web and mobile applications. We come together to share ideas
and [change the world](http://www.mirego.org).

We also [love open-source software](https://open.mirego.com) and we try to give back to the community as much as we can.
