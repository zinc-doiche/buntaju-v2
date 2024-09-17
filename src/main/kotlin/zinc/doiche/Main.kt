package zinc.doiche.zinc.doiche

import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.AnnotatedEventManager
import net.dv8tion.jda.api.requests.GatewayIntent
import okhttp3.OkHttpClient
import zinc.doiche.lib.init.ApplicationContext
import java.time.Duration

internal lateinit var applicationContext: ApplicationContext

fun main(): Unit = runBlocking {
    applicationContext = ApplicationContext(this)

    applicationContext.apply {
        jda = JDABuilder
            .createDefault(config.discordToken)
            .setEventManager(AnnotatedEventManager())
            .addEventListeners(listenerList)
            .setHttpClientBuilder(
                OkHttpClient.Builder()
                    .callTimeout(Duration.ofMinutes(1))
                    .connectTimeout(Duration.ofMinutes(1))
                    .readTimeout(Duration.ofMinutes(1))
                    .writeTimeout(Duration.ofMinutes(1)))
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .build()
            .awaitReady()
    }
}