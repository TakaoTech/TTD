package com.takaotech.kotlinbest.components

import androidx.compose.runtime.Composable
import com.stevdza.san.kotlinbs.components.BSNavBar
import com.stevdza.san.kotlinbs.models.BackgroundStyle
import com.stevdza.san.kotlinbs.models.navbar.NavBarBrand
import com.stevdza.san.kotlinbs.models.navbar.NavDropdown
import com.stevdza.san.kotlinbs.models.navbar.NavDropdownItem
import com.stevdza.san.kotlinbs.models.navbar.NavLink
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth

@Composable
fun TakaoNavBar(modifier: Modifier = Modifier, onNavClicked: (destination: String) -> Unit) {
    BSNavBar(
        modifier = modifier,
        items = listOf(
            NavLink(
                id = "social",
                title = "Social",
                onClick = {
                    onNavClicked("/social")
                }
            ),
            NavLink(
                id = "kotlin",
                title = "Kotlin",
                onClick = {

                }
            ),
            NavLink(
                id = "cv",
                title = "CV",
                onClick = {

                }
            ),
            NavDropdown(
                placeholder = "Job",
                items = listOf(
                    NavDropdownItem(
                        "job-list",
                        "List",
                        onClick = {

                        }
                    ),
                    NavDropdownItem(
                        "job-add",
                        "Request Add",
                        onClick = {

                        }
                    )
                )
            )
        ),
        backgroundStyle = BackgroundStyle.Dark,
        brand = NavBarBrand(
            title = "TakaoTech",
            image = "https://avatars.githubusercontent.com/u/125040015",
            href = "/"
        ),
        stickyTop = true
    )
}