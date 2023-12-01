import SwiftUI
import common
import Trikot

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
            VStack(alignment: .leading) {
                Text(viewModel.title)
                DebugPanelView(viewModel.debugPanel)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .padding(16)
	}
}
