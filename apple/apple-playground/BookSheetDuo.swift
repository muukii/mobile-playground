import SwiftUI
import UIKit

/// Compares system sheet presentations without overriding their width or position.
///
/// Open this book on iPhone Duo and change the device pose while a sheet is
/// visible. The form preserves its input, count, and session identifier so that
/// a resize can be distinguished from a new presentation.
private struct SheetDuoBook: View {
  @State private var swiftUISheet: SheetDuoMode?
  @State private var uiKitSheet: SheetDuoMode?

  var body: some View {
    NavigationStack {
      List {
        Section("SwiftUI .sheet") {
          Button("SwiftUI: Default") { swiftUISheet = .defaultSheet }
            .accessibilityIdentifier("sheet-duo-swiftui-default")
          Button("SwiftUI: Medium / Large") { swiftUISheet = .detents }
            .accessibilityIdentifier("sheet-duo-swiftui-detents")
        }
        Section("UIKit .pageSheet") {
          Button("UIKit: Default") { uiKitSheet = .defaultSheet }
            .accessibilityIdentifier("sheet-duo-uikit-default")
          Button("UIKit: Medium / Large") { uiKitSheet = .detents }
            .accessibilityIdentifier("sheet-duo-uikit-detents")
        }
        Section("Compare poses") {
          Text("Present a sheet, then try Closed, Open, Partially Open, and Rotate Right in Device Hub.")
          Text("No custom width, presentation sizing, or fold avoidance is applied.")
        }
      }
      .navigationTitle("Sheet on iPhone Duo")
    }
    .sheet(item: $swiftUISheet) { mode in
      SheetDuoSwiftUISheet(mode: mode)
    }
    .background {
      SheetDuoUIKitPresenter(selection: $uiKitSheet)
        .frame(width: 0, height: 0)
    }
  }
}

/// Selects either unmodified system defaults or explicit medium/large detents.
private enum SheetDuoMode: String, Identifiable {
  case defaultSheet
  case detents

  var id: String { rawValue }
  var title: String { self == .defaultSheet ? "Default" : "Medium / Large" }
}

/// Owns the native SwiftUI presentation and its current detent.
private struct SheetDuoSwiftUISheet: View {
  let mode: SheetDuoMode
  @Environment(\.dismiss) private var dismiss
  @State private var detent: PresentationDetent = .medium

  var body: some View {
    NavigationStack {
      SheetDuoPayload(label: "SwiftUI / \(mode.title)")
        .navigationTitle(mode.title)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
          ToolbarItem(placement: .confirmationAction) {
            Button("Done") { dismiss() }
          }
          if mode == .detents {
            ToolbarItem(placement: .bottomBar) {
              Button(detent == .medium ? "Expand" : "Collapse") {
                detent = detent == .medium ? .large : .medium
              }
              .accessibilityIdentifier("sheet-duo-change-detent")
            }
          }
        }
    }
    .modifier(SheetDuoDetentModifier(mode: mode, detent: $detent))
  }
}

/// Leaves default sheets untouched and applies detents only to the explicit case.
private struct SheetDuoDetentModifier: ViewModifier {
  let mode: SheetDuoMode
  @Binding var detent: PresentationDetent

  @ViewBuilder
  func body(content: Content) -> some View {
    switch mode {
    case .defaultSheet:
      content
    case .detents:
      content
        .presentationDetents([.medium, .large], selection: $detent)
        .presentationDragIndicator(.visible)
    }
  }
}

/// The same scrollable content is used by both frameworks.
private struct SheetDuoPayload: View {
  let label: String
  @State private var input = "Keep this text"
  @State private var count = 0
  @State private var session = String(UUID().uuidString.prefix(8))
  @State private var geometry = "Waiting for layout"

  var body: some View {
    Form {
      Section(label) {
        Text(geometry)
          .font(.system(.caption, design: .monospaced))
          .accessibilityIdentifier("sheet-duo-geometry")
        Text("Session: \(session)")
          .font(.caption)
          .accessibilityIdentifier("sheet-duo-session")
      }
      Section("State retention") {
        TextField("Text", text: $input)
          .accessibilityIdentifier("sheet-duo-input")
        Stepper("Count: \(count)", value: $count)
          .accessibilityIdentifier("sheet-duo-count")
      }
      Section("Scrollable content") {
        ForEach(1...24, id: \.self) { index in
          Text("Row \(index)")
        }
      }
    }
    .background {
      SheetDuoGeometryProbe(label: label) { geometry = $0 }
    }
  }
}

/// Presents a real UIKit navigation controller instead of a SwiftUI sheet wrapper.
private struct SheetDuoUIKitPresenter: UIViewControllerRepresentable {
  @Binding var selection: SheetDuoMode?

  func makeUIViewController(context: Context) -> UIViewController {
    UIViewController()
  }

  func updateUIViewController(_ controller: UIViewController, context: Context) {
    guard let mode = selection, controller.presentedViewController == nil else { return }
    let selection = $selection
    DispatchQueue.main.async {
      guard controller.view.window != nil, controller.presentedViewController == nil else { return }
      selection.wrappedValue = nil

      let body = UIHostingController(rootView: SheetDuoPayload(label: "UIKit / \(mode.title)"))
      body.title = mode.title
      let navigation = UINavigationController(rootViewController: body)
      navigation.modalPresentationStyle = .pageSheet
      body.navigationItem.rightBarButtonItem = UIBarButtonItem(
        systemItem: .done,
        primaryAction: UIAction { [weak navigation] _ in navigation?.dismiss(animated: true) }
      )

      if mode == .detents, let sheet = navigation.sheetPresentationController {
        sheet.detents = [.medium(), .large()]
        sheet.selectedDetentIdentifier = .medium
        sheet.prefersGrabberVisible = true
        let changeDetent = UIBarButtonItem(
          title: "Resize",
          primaryAction: UIAction { [weak sheet] _ in
            sheet?.animateChanges {
              sheet?.selectedDetentIdentifier = sheet?.selectedDetentIdentifier == .medium ? .large : .medium
            }
          }
        )
        changeDetent.accessibilityIdentifier = "sheet-duo-uikit-change-detent"
        body.navigationItem.leftBarButtonItem = changeDetent
      }
      controller.present(navigation, animated: true)
    }
  }
}

/// Observes the containing presentation in window coordinates without changing it.
private struct SheetDuoGeometryProbe: UIViewRepresentable {
  let label: String
  let onUpdate: (String) -> Void

  func makeUIView(context: Context) -> SheetDuoProbeView {
    let view = SheetDuoProbeView()
    view.isUserInteractionEnabled = false
    view.label = label
    view.onUpdate = onUpdate
    return view
  }

  func updateUIView(_ view: SheetDuoProbeView, context: Context) {
    view.onUpdate = onUpdate
  }
}

/// Records geometry changes to the screen and the app's Documents directory.
///
/// `sheet-duo-measurements.jsonl` is diagnostic output, not configuration. Its
/// frames come from the actual presented view and the active window. Each
/// distinct layout is appended once so transitions can be inspected later.
private final class SheetDuoProbeView: UIView {
  var label = ""
  var onUpdate: (String) -> Void = { _ in }
  private var previousDescription = ""

  override func didMoveToWindow() {
    super.didMoveToWindow()
    scheduleMeasurement()
  }

  override func layoutSubviews() {
    super.layoutSubviews()
    scheduleMeasurement()
  }

  override func safeAreaInsetsDidChange() {
    super.safeAreaInsetsDidChange()
    scheduleMeasurement()
  }

  private func scheduleMeasurement() {
    // Read after the current layout pass and never mutate SwiftUI state inside it.
    DispatchQueue.main.async { [weak self] in self?.measure() }
  }

  private func measure() {
    guard let window else { return }
    var responder: UIResponder? = self
    while responder != nil, !(responder is UIViewController) { responder = responder?.next }
    guard var controller = responder as? UIViewController else { return }
    while let parent = controller.parent { controller = parent }

    let presentedView = controller.presentationController?.presentedView ?? controller.view!
    let frame = presentedView.convert(presentedView.bounds, to: window)
    let insets = presentedView.safeAreaInsets
    let detent = controller.sheetPresentationController?.selectedDetentIdentifier?.rawValue ?? "default"
    let sizeClass = "\(controller.traitCollection.horizontalSizeClass.rawValue)/\(controller.traitCollection.verticalSizeClass.rawValue)"
    let description = "Window \(rect(window.bounds))\nSheet  \(rect(frame))\nSafe   T\(Int(insets.top)) L\(Int(insets.left)) B\(Int(insets.bottom)) R\(Int(insets.right))\nTraits \(sizeClass)  Detent \(detent)"
    guard description != previousDescription else { return }
    previousDescription = description
    onUpdate(description)

    let measurement: [String: Any] = [
      "time": Date().timeIntervalSince1970,
      "label": label,
      "window": values(window.bounds),
      "sheet": values(frame),
      "safeArea": [insets.top, insets.left, insets.bottom, insets.right],
      "sizeClasses": sizeClass,
      "detent": detent,
    ]
    guard let data = try? JSONSerialization.data(withJSONObject: measurement, options: [.sortedKeys]),
      let directory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first
    else { return }
    let file = directory.appendingPathComponent("sheet-duo-measurements.jsonl")
    if !FileManager.default.fileExists(atPath: file.path) {
      FileManager.default.createFile(atPath: file.path, contents: nil)
    }
    if let handle = try? FileHandle(forWritingTo: file) {
      defer { try? handle.close() }
      do {
        try handle.seekToEnd()
        try handle.write(contentsOf: data + Data([0x0A]))
      } catch {
        print("Sheet Duo measurement: \(error)")
      }
    }
  }

  private func rect(_ rect: CGRect) -> String {
    "\(Int(rect.minX)),\(Int(rect.minY)) \(Int(rect.width))×\(Int(rect.height))"
  }

  private func values(_ rect: CGRect) -> [CGFloat] {
    [rect.minX, rect.minY, rect.width, rect.height]
  }
}

#Preview("Sheet on iPhone Duo") {
  SheetDuoBook()
}
