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
import zinc.doiche.lib.init.annotation.*
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.jvm.Throws

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
        run {
            val packageName = "zinc.doiche"

            Reflections(
                ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forPackage(packageName))
                    .setScanners(
                        SubTypesScanner(),
                        TypeAnnotationsScanner(),
                        MethodAnnotationsScanner()
                    )
                    .filterInputsBy(
                        FilterBuilder().includePackage(packageName)
                    )
            )
        }
    )

    val logger: Logger by lazy {
        LoggerFactory.getLogger(javaClass)
    }

    private val injectableMap: MutableMap<String, Any> by lazy {
        HashMap<String, Any>().apply {
            val methodList = mutableListOf<Triple<Int, Method, Any>>()

            this["ApplicationContext"] = this@ApplicationContext

            reflections.getTypesAnnotatedWith(Injectable::class.java).map { clazz ->
                clazz.declaredMethods.filter { method ->
                    method.isAnnotationPresent(Injector::class.java)
                }.forEach { method ->
                    clazz.getDeclaredConstructor().newInstance().let { injectable ->
                        val order = if(method.isAnnotationPresent(InjectionOrder::class.java)) {
                            method.getAnnotation(InjectionOrder::class.java).value
                        } else {
                            0
                        }
                        methodList.add(Triple(order, method, injectable))
                    }
                }
            }

            methodList.sortBy { - it.first }
            methodList.forEach {
                val method = it.second
                val key = method.getAnnotation(Injector::class.java).name.takeIf { name ->
                    name.isNotBlank()
                } ?: method.returnType.simpleName
                val injectable = it.third
                val parameters = method.parameters.map { param ->
                    getInjectable(param)
                }.toTypedArray()

                this[key] = method.invoke(injectable, *parameters)
            }
        }
    }

    init {
        injectableMap["JDA"] = jda
        injectableMap["Logger"] = logger
        injectableMap["Config"] = config

        registerListeners()
        jda.awaitStatus(JDA.Status.CONNECTED)
        registerCommands()
    }

    fun getDatabase() = mongoClient.getDatabase(config.database.getName())

    private fun registerListeners() = reflections.getMethodsAnnotatedWith(Listener::class.java).map { function ->
        function.parameters.map { parameter ->
            getInjectable(parameter)
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
                    getInjectable(parameter)
                }.let {
                    function.invoke(null, *it.toTypedArray()) as? Command
                }?.let {
                    CommandFactory.register(jda, it)
                    logger.info("[SlashCommand] '${it.name}' has been registered.")
                } ?: throw IllegalStateException("No Command found for ${function.name}")
            }
    }

    @Throws(IllegalStateException::class)
    private fun getInjectable(parameter: Parameter): Any {
        val name = parameter.getAnnotation(Inject::class.java)?.name ?: parameter.type.simpleName
        return injectableMap[name]
            ?: throw IllegalStateException("No Injectable found for $name [${parameter.type}].")
    }
}