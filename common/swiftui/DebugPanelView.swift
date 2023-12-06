import SwiftUI
import TRIKOT_FRAMEWORK_NAME
import Trikot

public struct DebugPanelView: View {
    @ObservedObject private var observableViewModel: ObservableViewModelAdapter<DebugPanelViewModel>

    public init(_ viewModel: DebugPanelViewModel) {
        observableViewModel = viewModel.asObservable()
    }

    var viewModel: DebugPanelViewModel {
        observableViewModel.viewModel
    }

    public var body: some View {
        ScrollView(showsIndicators: false) {
            VStack(alignment: .leading, spacing: 24) {
                VMDForEach(viewModel.items) { item in
                    if let label = item as? DebugPanelItemViewModelLabel {
                        HStack {
                            VMDText(label.label)
                            VMDText(label.viewModel)
                        }
                    } else if let toggle = item as? DebugPanelItemViewModelToggle {
                        VMDToggle(toggle.viewModel)
                            .padding(.trailing, 2)
                    } else if let textField = item as? DebugPanelItemViewModelTextField {
                        VMDTextField(textField.viewModel) {}
                    } else if let button = item as? DebugPanelItemViewModelButton {
                        VMDButton(button.viewModel) {
                            Text($0.text)
                        }
                    } else if let picker = item as? DebugPanelItemViewModelPicker {
                        HStack {
                            VMDText(picker.label)
                            Spacer()
                            VMDPicker(picker.viewModel, label: picker.selectedItem) {
                                Text($0.text)
                            }
                        }
                    } else if let datePicker = item as? DebugPanelItemViewModelDatePicker {
                        DatePickerView(datePicker: datePicker)
                    }
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
