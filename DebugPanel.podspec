require_relative 'podspec_versions.rb'

properties = load_properties('gradle.properties')

Pod::Spec.new do |spec|
  spec.name          = "DebugPanel"
  spec.version       = "#{properties['version']}"
  spec.summary       = "Generic debug panel views for Swift UI applications"
  spec.homepage      = "https://github.com/mirego/debug-panel"
  spec.license       = "MIT license"
  spec.author        = { "Mathieu Larue" => "mlarue@mirego.com" }
  spec.source        = { :git => 'https://github.com/mirego/debug-panel' }
  spec.source_files = 'common/swiftui/**/*.swift'
  spec.ios.deployment_target = '15.0'

  spec.dependency 'Popovers'
  spec.dependency 'Trikot/viewmodels.declarative.SwiftUI.flow'
  spec.dependency ENV['TRIKOT_FRAMEWORK_NAME']

  spec.static_framework = true

  spec.prepare_command = <<-CMD
    find . -type f -name "*.swift" -exec sed -i '' -e "s/TRIKOT_FRAMEWORK_NAME/${TRIKOT_FRAMEWORK_NAME}/g" {} +
  CMD
end
