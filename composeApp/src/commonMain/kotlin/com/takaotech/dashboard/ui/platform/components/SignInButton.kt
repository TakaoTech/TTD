package com.takaotech.dashboard.ui.platform.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SignInButton(
    text: String,
    loadingText: String = "Signing in...",
    icon: Painter,
    isLoading: Boolean = false,
    shape: Shape = RoundedCornerShape(100),
    borderColor: Color = Color.LightGray,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    progressIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    Surface(
        modifier =
            Modifier
                .clip(shape)
                .clickable(
                    enabled = !isLoading,
                    onClick = onClick,
                ),
        shape = shape,
        border = BorderStroke(width = 1.dp, color = borderColor),
        color = backgroundColor,
    ) {
        Row(
            modifier =
                Modifier
                    .padding(
                        start = 12.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 12.dp,
                    )
                    .animateContentSize(
                        animationSpec =
                            tween(
                                durationMillis = 300,
                                easing = LinearOutSlowInEasing,
                            ),
                    ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = icon,
                contentDescription = "SignInButton",
                tint = Color.Unspecified,
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(text = if (isLoading) loadingText else text)
            if (isLoading) {
                Spacer(modifier = Modifier.width(16.dp))
                CircularProgressIndicator(
                    modifier =
                        Modifier
                            .height(16.dp)
                            .width(16.dp),
                    strokeWidth = 2.dp,
                    color = progressIndicatorColor,
                )
            }
        }
    }
}

@Composable
@Preview
fun SignInButtonPreview() {
//    SignInButton(
//        text = "Sign in with Google",
//        loadingText = "Signing in...",
//        isLoading = false,
//        icon = painterResource(id = R.drawable.ic_google_logo),
//        onClick = { }
//    )
}
