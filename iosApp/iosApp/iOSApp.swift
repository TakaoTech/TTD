import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init(){
        TakaoApplicationKt.startTakaoApplicationKoin()
    }
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
