
import SwiftUI

#Preview("OnAppear") {
    
  struct _Book: View {
    
    @State var flag = false

    var body: some View {
      ZStack {
        BaseView()
        if flag {
          CoverView()
        }
      }
      .safeAreaInset(edge: .bottom) { 
        Button("Toggle") {
          flag.toggle()
        }
      }
    }
    
    struct BaseView: View {
      
      var body: some View {
        Color.red
          .onAppear {
            print("appear")
          }
      }
    }
    
    struct CoverView: View {
      
      var body: some View {
        Color.purple
          .onAppear {
            print("Cover")
          }
      }
      
    }
    
  }
  
  return _Book()
}
