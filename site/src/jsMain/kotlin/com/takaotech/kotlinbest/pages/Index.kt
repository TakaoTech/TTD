package com.takaotech.kotlinbest.pages

import androidx.compose.runtime.Composable
import com.stevdza.san.kotlinbs.components.BSNavBar
import com.stevdza.san.kotlinbs.models.BackgroundStyle
import com.stevdza.san.kotlinbs.models.navbar.NavBarBrand
import com.stevdza.san.kotlinbs.models.navbar.NavLink
import com.takaotech.kotlinbest.components.TakaoNavBar
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.rememberPageContext

@Page
@Composable
fun HomePage() {
    val ctx = rememberPageContext()

   Column(modifier = Modifier.fillMaxWidth()) {
       TakaoNavBar(
           modifier = Modifier.fillMaxWidth()
       ){
           ctx.router.navigateTo(it)
       }

       //TODO Home page


//       SimpleGrid(numColumns(2)){
//           Box {
//               Text("Ciao 1")
//           }
//
//           Box {
//               Text("Ciao 1")
//           }
//
//           Box {
//               Text("Ciao 1")
//           }
//
//           Box {
//               Text("Ciao 1")
//           }
//       }

   }
}
