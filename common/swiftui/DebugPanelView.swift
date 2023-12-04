import SwiftUI
import TRIKOT_FRAMEWORK_NAME
import Trikot

struct DebugPanelView: View {
    @ObservedObject private var observableViewModel: ObservableViewModelAdapter<DebugPanelViewModel>

    private let itemHeight = 48.0

    init(_ viewModel: DebugPanelViewModel) {
        observableViewModel = viewModel.asObservable()
    }

    var viewModel: DebugPanelViewModel {
        observableViewModel.viewModel
    }

    var body: some View {
        LazyVStack(alignment: .leading) {
            VMDForEach(viewModel.items) { item in
                if let label = item as? DebugPanelItemViewModelLabel {
                    HStack {
                        VMDText(label.label)
                        VMDText(label.viewModel)
                    }
                    .height(itemHeight)
                } else if let toggle = item as? DebugPanelItemViewModelToggle {
                    VMDToggle(toggle.viewModel)
                        .height(itemHeight)
                } else if let textField = item as? DebugPanelItemViewModelTextField {
                    VMDTextField(textField.viewModel) {}
                        .height(itemHeight)
                } else if let button = item as? DebugPanelItemViewModelButton {
                    VMDButton(button.viewModel) {
                        Text($0.text)
                    }
                    .height(itemHeight)
                } else if let picker = item as? DebugPanelItemViewModelPicker {
                    HStack {
                        VMDText(picker.label)
                        Spacer()
                        VMDPicker(picker.viewModel, label: picker.selectedItem) {
                            Text($0.text)
                        }
                    }
                    .frame(maxWidth: .infinity, minHeight: itemHeight)
                } else if let datePicker = item as? DebugPanelItemViewModelDatePicker {
                    DatePickerView(datePicker: datePicker)
                        .height(itemHeight)
                }
            }
        }
    }
}

struct DebugPanelView_Previews: PreviewProvider {
    static var previews: some View {
        let useCase = DebugPanelUseCasePreview()
        let viewModel = DebugPanelViewModelImpl(coroutineScope: CoroutineScopeProvider().provideMainWithSuperviserJob(), useCase: useCase, viewData: useCase.createViewData()
        )
        DebugPanelView(viewModel)
    }
}