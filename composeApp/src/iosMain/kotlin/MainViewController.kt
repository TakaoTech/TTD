import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.takaotech.dashboard.App
import com.takaotech.dashboard.ui.platform.LocalTTDUriHandler
import com.takaotech.dashboard.ui.platform.UriHandler
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

fun MainViewController() = ComposeUIViewController {
    CompositionLocalProvider(LocalTTDUriHandler provides object : UriHandler {
        override fun openUrl(url: String) {
            UIApplication.sharedApplication.openURL(
                url = NSURL(string = url),
                options = mapOf<Any?, Any>()
            ) { _ -> }
        }
    }) {
        App()
    }
}
