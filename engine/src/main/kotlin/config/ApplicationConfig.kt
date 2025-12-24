package config

data class ApplicationConfig (
    var title: String = "Covalence Application",
    var windowConfig: WindowConfig = WindowConfig(),
)

fun appConfig(init: ApplicationConfig.() -> Unit) = ApplicationConfig().apply { init() }
