package config

data class WindowConfig(
    var width: Int = 800,
    var height: Int = 600,
)


fun windowConfig(init: WindowConfig.() -> Unit) = WindowConfig().apply { init() }