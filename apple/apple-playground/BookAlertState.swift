import SwiftUI

/// View-local alert presentation state backed by a presentation flag and a value.
///
/// Assigning a value presents it. Assignments while presented are ignored;
/// assigning `nil` dismisses it. The last value remains available to SwiftUI
/// while dismissal is in progress.
@propertyWrapper
public struct AlertState<Value>: DynamicProperty {

  @State fileprivate var isPresented = false
  @State fileprivate var value: Value?

  public var wrappedValue: Value? {
    get { value }   
  }

  public init(wrappedValue: Value? = nil) {
    self.value = value
  }

  public var projectedValue: AlertState {
    self
  }
  
  public func present(_ value: Value) {
    
    assert(self.isPresented == false, "AlertState is already presented. Assigning a new value while presented will be ignored.")
    
    self.value = value
    self.isPresented = true
  }

}

extension View {
  /// Presents an alert using view-local alert state without erasing content types.
  public func alert<Value, Actions: View, Message: View>(
    _ title: LocalizedStringKey,
    state: AlertState<Value>,
    @ContentBuilder actions: (Value) -> Actions,
    @ContentBuilder message: (Value) -> Message
  ) -> some View {
    self.alert(
      title,
      isPresented: state.$isPresented,
      presenting: state.value,
      actions: actions,
      message: message
    )
  }

  public func alert<Value, Actions: View, Message: View>(
    _ title: LocalizedStringResource,
    state: AlertState<Value>,
    @ContentBuilder actions: (Value) -> Actions,
    @ContentBuilder message: (Value) -> Message
  ) -> some View {
    self.alert(
      title,
      isPresented: state.$isPresented,
      presenting: state.value,
      actions: actions,
      message: message
    )
  }

  public func alert<S: StringProtocol, Value, Actions: View, Message: View>(
    _ title: S,
    state: AlertState<Value>,
    @ContentBuilder actions: (Value) -> Actions,
    @ContentBuilder message: (Value) -> Message
  ) -> some View {
    self.alert(
      title,
      isPresented: state.$isPresented,
      presenting: state.value,
      actions: actions,
      message: message
    )
  }

  public func alert<Value, Actions: View, Message: View>(
    _ title: Text,
    state: AlertState<Value>,
    @ContentBuilder actions: (Value) -> Actions,
    @ContentBuilder message: (Value) -> Message
  ) -> some View {
    self.alert(
      title,
      isPresented: state.$isPresented,
      presenting: state.value,
      actions: actions,
      message: message
    )
  }
}

/// Interactive cases for the state and content supplied to a SwiftUI alert.
private struct AlertStateExample: View {
  @AlertState private var alert: Item?
  @State private var lastAction = "None"

  var body: some View {
    NavigationStack {
      Form {
        Section("Presentation") {
          Button("Present First") {
            $alert.present(.first)
          }
          Button("Present Second") {
            $alert.present(.second)
          }
          Button("Assign First, then Second") {
            $alert.present(.first)
            $alert.present(.second)
          }
          Text(
            "The first assignment should be shown when two values are assigned together."
          )
          .font(.footnote)
          .foregroundStyle(.secondary)
        }

        Section("Observation") {
          LabeledContent("Current value", value: alert?.name ?? "nil")
          LabeledContent("Last action", value: lastAction)
        }
      }
      .navigationTitle("Alert State")
      .alert(
        "Hello",
        state: $alert,
        actions: { item in
          Button("Confirm") {
            lastAction = "Confirmed \(item.name)"
          }
          Button("Cancel", role: .cancel) {
            lastAction = "Cancelled \(item.name)"
          }
        },
        message: { item in
          Text("This alert was created for \(item.name).")
        }
      )
    }
  }

  private enum Item {
    case first
    case second

    var name: String {
      switch self {
      case .first: "First"
      case .second: "Second"
      }
    }
  }
}

#Preview("Alert State") {
  AlertStateExample()
}

#Preview("Plain") {

  struct Book: View {

    @State var isPresented: Bool = false
    @State var data: String?

    var body: some View {

      VStack {
        Button("Set isPresented") {
          self.isPresented = true
        }
        Button("Set Data") {
          self.data = "Hello"
        }
        HStack {
          Text("isPresented: \(isPresented.description)")
          Text("data: \(data ?? "nil")")
        }
      }

      .alert(
        "Alert",
        isPresented: $isPresented,
        presenting: data,
        actions: { item in
          Button("Confirm") {
            // Handle confirm action
          }
          Button("Cancel", role: .cancel) {
            // Handle cancel action
          }
        },
        message: { item in
          Text("This alert was created for \(item ?? "Unknown").")
        }
      )

    }
  }

  return Book()

}
