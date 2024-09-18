package zinc.doiche.lib.init

import com.mongodb.kotlin.client.coroutine.MongoClient
import dev.minn.jda.ktx.jdabuilder.light
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import org.reflections8.Reflections
import org.reflections8.scanners.MethodAnnotationsScanner
import org.reflections8.scanners.SubTypesScanner
import org.reflections8.scanners.TypeAnnotationsScanner
import org.reflections8.util.ClasspathHelper
import org.reflections8.util.ConfigurationBuilder
import org.reflections8.util.FilterBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import zinc.doiche.lib.command.Command
import zinc.doiche.lib.command.CommandFactory
import zinc.doiche.lib.init.annotation.Injectable
import zinc.doiche.lib.init.annotation.Injector
import zinc.doiche.lib.init.annotation.Listener
import zinc.doiche.lib.init.annotation.SlashCommand

class ApplicationContext(
    val config: Config,
    private val jdaBuilderConsumer: JDABuilder.() -> Unit
) {
    val jda: JDA by lazy {
        light(
            token = config.discordToken,
            enableCoroutines = true,
            builder = jdaBuilderConsumer
        )
    }

    val mongoClient: MongoClient by lazy {
        MongoClient.create(config.database.getConnectionString())
    }

    val reflections: Reflections by lazyOf(
        Reflections(
            ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("zinc.doiche"))
                .setScanners(
                    SubTypesScanner(),
                    TypeAnnotationsScanner(),
                    MethodAnnotationsScanner()
                )
                .filterInputsBy(
                    FilterBuilder().includePackage("zinc.doiche")
                )
        )
    )

    val logger: Logger by lazy {
        LoggerFactory.getLogger(javaClass)
    }

    private val injectableMap: MutableMap<String, Any> by lazy {
        mutableMapOf<String, Any>().apply {
            reflections.getTypesAnnotatedWith(Injectable::class.java).map { clazz ->
                clazz.declaredMethods.filter { method ->
                    method.isAnnotationPresent(Injector::class.java)
                }.forEach { method ->
                    clazz.getDeclaredConstructor().newInstance().let { injectable ->
                        this[method.returnType.simpleName] = if(method.parameters.isNotEmpty()) {
                            method.invoke(injectable, this@ApplicationContext)
                        } else {
                            method.invoke(injectable)
                        }
                    }
                }
            }
        }
    }

    init {
        injectableMap["JDA"] = jda
        injectableMap["Logger"] = logger

        registerListeners()
        jda.awaitStatus(JDA.Status.CONNECTED)
        registerCommands()
    }

    private fun registerListeners() = reflections.getMethodsAnnotatedWith(Listener::class.java).map { function ->
        function.parameters.map { parameter ->
            val simpleName = parameter.type.simpleName
            injectableMap[simpleName] ?: throw IllegalStateException("No Injectable found for $simpleName")
        }.let {
            logger.info("[Listener] ${function.name} has been registered.")
            function.invoke(null, *it.toTypedArray())
        }
    }

    private fun registerCommands() {
        reflections.getMethodsAnnotatedWith(SlashCommand::class.java)
            .forEach { function ->
                if(function.returnType != Command::class.java) {
                    throw IllegalStateException("Return type of ${function.name} must be Command.")
                }
                function.parameters.map { parameter ->
                    val simpleName = parameter.type.simpleName
                    injectableMap[simpleName] ?: throw IllegalStateException("No Injectable found for $simpleName")
                }.let {
                    function.invoke(null, *it.toTypedArray()) as? Command
                }?.let {
                    CommandFactory.register(jda, it)
                    logger.info("[Command] '/${it.name}' has been registered.")
                } ?: throw IllegalStateException("No Command found for ${function.name}")
            }
    }
}