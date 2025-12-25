package config

data class ApplicationConfig(
    var title: String = "Covalence Application",
    var maxCacheSize: Int = 1024, // max size of assets in cache before eviction in mb
    var windowConfig: WindowConfig = WindowConfig(),
)

fun appConfig(init: ApplicationConfig.() -> Unit) = ApplicationConfig().apply { init() }
