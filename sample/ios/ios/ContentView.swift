import SwiftUI
import Trikot
import TRIKOT_FRAMEWORK_NAME

struct ContentView: View {
    @ObservedObject private var observableViewModel: ObservableViewModelAdapter<ApplicationViewModel>

    init(viewModel: ApplicationViewModel) {
        observableViewModel = viewModel.asObservable()
    }

    var viewModel: RootViewModel {
        observableViewModel.viewModel.rootViewModel
    }

    var body: some View {
        ZStack {
            VStack(alignment: .leading, spacing: 24) {
                Text(viewModel.title)
                    .font(.title)
                DebugPanelView(viewModel.debugPanel)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .padding(16)
    }
}
