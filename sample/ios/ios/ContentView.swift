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
        VStack {
            Text(viewModel.title)

            LazyVStack(spacing: 0) {
                VMDForEach(viewModel.debugPanel.items) { item in
                    if let label = item as? DebugPanelItemViewModelLabel {
                        HStack {
                            VMDText(label.label)
                            VMDText(label.viewModel)
                        }
                    } else if let toggle = item as? DebugPanelItemViewModelToggle {

                    } else if let textField = item as? DebugPanelItemViewModelTextField {

                    } else if let button = item as? DebugPanelItemViewModelButton {

                    } else if let picker = item as? DebugPanelItemViewModelPicker {

                    } else if let datePicker = item as? DebugPanelItemViewModelDatePicker {

                    }
                }
            }
        }
	}
}
