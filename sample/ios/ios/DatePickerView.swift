import SwiftUI
import common
import Trikot
import Popovers

struct DatePickerView: View {

    @ObservedObject private var observableViewModel: ObservableViewModelAdapter<DatePickerViewModel>

    init(_ viewModel: DatePickerViewModel) {
        observableViewModel = viewModel.asObservable()
    }

    var viewModel: DatePickerViewModel {
        observableViewModel.viewModel
    }
    
    @State private var selectedDate: Foundation.Date = Foundation.Date()

    var body: some View {
        VStack(spacing: 16) {
            DatePicker("Select now:", selection: $selectedDate, displayedComponents: [.date])
                .environment(\.timeZone, TimeZone(abbreviation: "GMT")!)
        }
        .padding(.horizontal, 16)
        .onAppear {
            selectedDate = Date(timeIntervalSince1970: (Double(truncating: viewModel.date ?? 0) / 1000.0))
        }
    }
}
