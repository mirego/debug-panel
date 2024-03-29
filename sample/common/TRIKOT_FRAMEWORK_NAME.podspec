Pod::Spec.new do |spec|
    spec.name                     = 'TRIKOT_FRAMEWORK_NAME'
    spec.version                  = '1.0'
    spec.homepage                 = 'https://github.com/mirego/debug-panel'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = 'MIT license'
    spec.summary                  = 'Sample app for the debug panel library'
    spec.vendored_frameworks      = 'build/cocoapods/framework/TRIKOT_FRAMEWORK_NAME.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '15.0'
                
                
    if !Dir.exist?('build/cocoapods/framework/TRIKOT_FRAMEWORK_NAME.framework') || Dir.empty?('build/cocoapods/framework/TRIKOT_FRAMEWORK_NAME.framework')
        raise "

        Kotlin framework 'TRIKOT_FRAMEWORK_NAME' doesn't exist yet, so a proper Xcode project can't be generated.
        'pod install' should be executed after running ':generateDummyFramework' Gradle task:

            ./gradlew :sample:common:generateDummyFramework

        Alternatively, proper pod installation is performed during Gradle sync in the IDE (if Podfile location is set)"
    end
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':sample:common',
        'PRODUCT_MODULE_NAME' => 'TRIKOT_FRAMEWORK_NAME',
    }
                
    spec.script_phases = [
        {
            :name => 'Build TRIKOT_FRAMEWORK_NAME',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
    spec.prepare_command = <<-CMD
    ../../gradlew :sample:common:generateDummyFramework
CMD
end