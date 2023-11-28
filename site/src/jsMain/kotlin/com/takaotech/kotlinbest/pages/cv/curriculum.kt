package com.takaotech.kotlinbest.pages.cv

import androidx.compose.runtime.Composable
import com.takaotech.kotlinbest.components.TakaoNavBar
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.classNames
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.graphics.Image
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Text

@Page("/cv")
@Composable
fun Curriculum() {
    val ctx = rememberPageContext()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TakaoNavBar(
            modifier = Modifier.fillMaxWidth()
        ) {
            ctx.router.navigateTo(it)
        }

        Row(modifier = Modifier.fillMaxWidth().maxWidth(100.percent)) {
            Image(
                modifier = Modifier
                    .maxWidth(100.percent)
                    .classNames("img-thumbnail", "img-fluid"),
                src = "https://avatars.githubusercontent.com/u/26302503"
            )

//            val painter = asyncPainterResource(data ="https://avatars.githubusercontent.com/u/26302503?v=4")
//
//            KamelImage(
//                resource = painter,
//                contentDescription = "Profile",
//            )

            H2 {
                Text("Samuele Bruschi")
            }
        }

        //TODO Immagine, Nome Cognome
        //TODO Nascita
        //TODO Links Social


        //TODO Esperienza
    }
}