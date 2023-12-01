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
                        VMDToggle(toggle.viewModel)
                    } else if let textField = item as? DebugPanelItemViewModelTextField {
                        VMDTextField(textField.viewModel) {}
                    } else if let button = item as? DebugPanelItemViewModelButton {
                        VMDButton(button.viewModel) {
                            Text($0.text)
                        }
                    } else if let picker = item as? DebugPanelItemViewModelPicker {
                        VMDPicker(picker.viewModel, label: picker.label) {
                            Text($0.text)
                        }
                    } else if let datePicker = item as? DebugPanelItemViewModelDatePicker {
                        DatePickerView(datePicker: datePicker)
                    }
                }
            }
        }
	}
}
