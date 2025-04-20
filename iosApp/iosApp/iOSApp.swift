import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init(){
        TakaoApplicationKt.startTakaoApplicationKoin(
            cryptoManager: SymmetricCryptoManagerImpl()
        )
    }
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
