import SwiftUI
import common
import Trikot
import Popovers

struct DatePickerView: View {

    @ObservedObject private var observableViewModel: ObservableViewModelAdapter<DatePickerViewModel>
    @ObservedObject private var observableLabelViewModel: ObservableViewModelAdapter<VMDTextViewModel>

    init(datePicker: DebugPanelItemViewModelDatePicker) {
        observableViewModel = datePicker.viewModel.asObservable()
        observableLabelViewModel = datePicker.label.asObservable()
    }

    var viewModel: DatePickerViewModel {
        observableViewModel.viewModel
    }

    var labelViewModel: VMDTextViewModel {
        observableLabelViewModel.viewModel
    }
    
    @State private var selectedDate: Foundation.Date = Foundation.Date()

    var body: some View {
        DatePicker(labelViewModel.text, selection: $selectedDate, displayedComponents: [.date])
            .environment(\.timeZone, TimeZone(abbreviation: "GMT")!)
            .onAppear {
                selectedDate = Date(timeIntervalSince1970: (Double(truncating: viewModel.date ?? 0) / 1000.0))
            }
            .onChange(of: selectedDate) { newValue in
                viewModel.date = KotlinLong(longLong: Int64(newValue.timeIntervalSince1970 * 1000.0))
            }
    }
}
