package com.example.chooser

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType

fun MainViewController() = ComposeUIViewController {
    App(
        onVibrate = { type ->
            when (type) {
                VibrationType.TICK -> {
                    val generator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy)
                    generator.prepare()
                    generator.impactOccurred()
                }
                VibrationType.WINNER -> {
                    val generator = UINotificationFeedbackGenerator()
                    generator.prepare()
                    generator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)
                }
            }
        }
    )
}