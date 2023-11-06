package com.takaotech.kotlinbest.pages.social

import androidx.compose.runtime.Composable
import com.stevdza.san.kotlinbs.components.BSIcon
import com.stevdza.san.kotlinbs.icons.BSIcons
import com.takaotech.kotlinbest.components.TakaoNavBar
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Iframe
import org.jetbrains.compose.web.dom.Text

@Composable
@Page("/social")
fun SocialPage() {
    val ctx = rememberPageContext()
    Column(Modifier.fillMaxWidth()) {
        TakaoNavBar(
            modifier = Modifier.fillMaxWidth()
        ) {
            ctx.router.navigateTo(it)
        }


        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BSIcon(
                    icon = BSIcons.YOUTUBE,
                    size = 4.cssRem,
                    color = Colors.Red
                )

                H2 {
                    Text("Youtube")
                }
            }
            Iframe(
                attrs = {
                    attr("src", "https://www.youtube.com/embed/qgi0IohVilk?si=n9xnhngFnm8xUdkT")
                    attr("width", "560")
                    attr("height", "315")
                }
            )
        }

        //TODO Instagram

        //TODO Discord
    }
}