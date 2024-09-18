package zinc.doiche

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLFactoryBuilder
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dev.minn.jda.ktx.events.CoroutineEventManager
import dev.minn.jda.ktx.jdabuilder.intents
import dev.minn.jda.ktx.jdabuilder.light
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.AnnotatedEventManager
import net.dv8tion.jda.api.requests.GatewayIntent
import okhttp3.OkHttpClient
import org.yaml.snakeyaml.Yaml
import zinc.doiche.lib.init.ApplicationContext
import zinc.doiche.lib.init.Config
import java.io.File
import java.time.Duration

internal lateinit var applicationContext: ApplicationContext

fun main(): Unit {
    val config = readConfig()

    applicationContext = ApplicationContext(config)
    applicationContext.apply {
        jda = light(
            config.discordToken,
            true
        ) {
            setEventManager(CoroutineEventManager())
            setHttpClientBuilder(
                OkHttpClient.Builder()
                    .callTimeout(Duration.ofMinutes(1))
                    .connectTimeout(Duration.ofMinutes(1))
                    .readTimeout(Duration.ofMinutes(1))
                    .writeTimeout(Duration.ofMinutes(1))
            )
            intents += listOf(
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES
            )
        }
    }
    applicationContext.postInit()
    applicationContext.registerCommands()
}

fun readConfig(): Config {
    val objectMapper = ObjectMapper(YAMLFactory()).registerKotlinModule()
    val filePath = "config.yaml"
    val contextClassLoader = Thread.currentThread().contextClassLoader
    val file = File(filePath).apply {
        if(!exists()) {
            createNewFile()
            contextClassLoader.getResource(filePath)?.let {
                val default = objectMapper.readValue<Config>(it.openStream())
                objectMapper.writeValue(outputStream(), default)
            }
        }
    }
    return objectMapper.readValue(file.inputStream(), Config::class.java)
}