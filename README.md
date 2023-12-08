<div align="center">
  <p><strong>Debug Panel</strong> is a Kotlin Multiplatform library built by <a href="https://www.mirego.com">Mirego</a> that allows mobile developers to generate<br /> boilerplate code to display a debug panel with different component types.</p>
  <br />
  <a href="https://github.com/mirego/debug-panel/actions/workflows/ci.yaml"><img src="https://github.com/mirego/debug-panel/actions/workflows/ci.yaml/badge.svg"/></a>
  <a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/kotlin-1.9.21-blue.svg?logo=kotlin"/></a>
  <a href="https://opensource.org/licenses/BSD-3-Clause"><img src="https://img.shields.io/badge/License-BSD_3--Clause-blue.svg"/></a>
</div>

## Setup

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

Also add the core and annotations dependencies with the KSP generated source directory:
```kotlin
val commonMain by getting {
    dependencies {
        // ...
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

### Android

The sample UI is resolved automatically from the common module since we include the library with the `api()` function.

If you have som issues with a duplicated `META-INF/versions/9/previous-compilation-data.bin` file during compilation, you can add it to the excluded resources inside the Android app's build.gradle.kts file:

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

### iOS

In your Podfile include the library's pod:

```
pod 'DebugPanel', :git => 'git@github.com:mirego/debug-panel.git', :tag => 'x.y.z', :inhibit_warnings => true
```

## Usage

In your common's module, create a class with the @DebugPanel annotation:

```kotlin
@DebugPanel(prefix = "MyProject", packageName = "com.myproject.app.generated", includeResetButton = true)
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

### Components

There are several components available:

#### DebugPanelToggle

This type generates a `DebugPanelItemViewModel.Toggle`. The toggle is configured with an initial value and is stored in the form of a `Boolean`.

#### DebugPanelLabel

This type generates a `DebugPanelItemViewModel.Label`. The label is configured with a `Flow` of `String`.

#### DebugPanelTextField

This type generates a `DebugPanelItemViewModel.TextField`. The text field is configured with an initial value and is stored in the form of a `String`.

#### DebugPanelButton

This type generates a `DebugPanelItemViewModel.Button`. The button is configured with a lambda `() -> Unit`.

#### DebugPanelPicker

This type generates a `DebugPanelItemViewModel.Picker`. The picker is configured with an initial selected identifier and a list of `DebugPanelPickerItem` and is stored in the form of a `String`.

#### DebugPanelDatePicker

This type generates a `DebugPanelItemViewModel.DatePicker`. The picker is configured with an initial value representing the epoch in milliseconds and is stored in the form of a `Long`.

#### Enum

This type generates a `DebugPanelItemViewModel.Picker`. The picker is configured with an initial selected enum value and is stored in the form of a `String`. The items are generated using the enum values and their name is used as the identifier.

### Annotations

#### @DebugPanel

The debug panel is configured using the `@DebugPanel(val prefix: String, val packageName: String, val includeResetButton: Boolean)` annotation.<br><br>
* The `prefix` is included in the generated use case and repository classes.<br><br>
* The `packageName` is where the files will be output inside the `generated` folder.<br><br>
* The `includeResetButton` indicates the library to include a "reset" button at the end of the component list. When tapping on that button all the overridden values will be cleared from the settings.

#### @Identifier

By default the values are saved in the settings using their field name as identifier. However this behaviour can be overridden using the `@Identifier(val value: String)` annotation.<br>
For exemple, this is useful in the case where you would want to replace an old debug panel with this one and use the original keys.

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

## License

Debug Panel is © 2013-2023 [Mirego](https://www.mirego.com) and may be freely distributed under the [New BSD license](http://opensource.org/licenses/BSD-3-Clause). See the [`LICENSE.md`](./LICENSE.md) file.

## About Mirego

[Mirego](https://www.mirego.com) is a team of passionate people who believe that work is a place where you can innovate and have fun. We’re a team of [talented people](https://life.mirego.com) who imagine and build beautiful Web and mobile applications. We come together to share ideas and [change the world](http://www.mirego.org).

We also [love open-source software](https://open.mirego.com) and we try to give back to the community as much as we can.
