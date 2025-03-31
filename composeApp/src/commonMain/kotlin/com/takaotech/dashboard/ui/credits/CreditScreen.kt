package com.takaotech.dashboard.ui.credits

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.rememberLibraries
import org.jetbrains.compose.resources.ExperimentalResourceApi
import ttd.composeapp.generated.resources.Res

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CreditPage(modifier: Modifier) {
    val libraries by rememberLibraries {
        Res.readBytes("files/aboutlibraries.json").decodeToString()
    }

    LibrariesContainer(
        libraries = libraries,
        modifier = modifier
    )
}
