import SwiftUI
import common

@main
struct iOSApp: App {
    let applicationViewModel = ApplicationViewModelImpl(coroutineScope: CoroutineScopeProvider().provideMainWithSuperviserJob())

    var body: some Scene {
        WindowGroup {
            ContentView(viewModel: applicationViewModel)
        }
    }
}
