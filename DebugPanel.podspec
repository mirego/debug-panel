require_relative 'podspec_versions.rb'

properties = load_properties('gradle.properties')

Pod::Spec.new do |spec|
  spec.name          = "DebugPanel"
  spec.version       = "#{properties['version']}"
  spec.summary       = "Plugins for DebugPanel"
  spec.description   = "Plugins for DebugPanel"
  spec.homepage      = "https://github.com/mirego/debug-panel"
  spec.license       = "MIT license"
  spec.author        = { "Mathieu Larue" => "mlarue@mirego.com" }
  spec.source        = { :git => 'https://github.com/mirego/debug-panel', :branch => 'feature/ios-ui' }
  spec.source_files = 'common/swiftui/**/*.swift'
  spec.dependency 'Popovers'
  spec.dependency 'Trikot/viewmodels.declarative.SwiftUI.flow'

  spec.static_framework = true

  spec.dependency ENV['TRIKOT_FRAMEWORK_NAME']

  spec.prepare_command = <<-CMD
    find . -type f -name "*.swift" -exec sed -i '' -e "s/TRIKOT_FRAMEWORK_NAME/${TRIKOT_FRAMEWORK_NAME}/g" {} +
  CMD
end
