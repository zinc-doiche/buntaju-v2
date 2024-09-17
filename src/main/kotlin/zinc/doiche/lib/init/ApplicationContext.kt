package zinc.doiche.lib.init

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.CoroutineScope
import net.dv8tion.jda.api.JDA
import org.reflections.Reflections
import zinc.doiche.lib.init.annotation.Injectable
import zinc.doiche.lib.init.annotation.Injector
import zinc.doiche.lib.init.annotation.Listener
import java.io.File

class ApplicationContext(
    val mainScope: CoroutineScope
) {

    var jda: JDA? = null
        set(value) {
            if(field != null) {
                throw IllegalStateException("JDA is already initialized.")
            }
            field = value
        }

    val config: Config = YAMLMapper().let { mapper ->
        val filePath = "config.yaml"
        val contextClassLoader = Thread.currentThread().contextClassLoader
        val file = File(filePath).apply {
            if(!exists()) {
                createNewFile()
                contextClassLoader.getResource(filePath)?.let {
                    val objectMapper = ObjectMapper()
                    val default = objectMapper.readValue<Config>(File(it.path))
                    objectMapper.writeValue(this, default)
                }
            }
        }

        mapper.readValue(file, Config::class.java)
    }

    val mongoClient: MongoClient by lazy {
        MongoClient.create(config.database.getConnectionString())
    }

    val listenerList: List<Any>
        get() {
            val injectableMap = mutableMapOf<String, Any>()
            val reflections = Reflections()

            reflections.getTypesAnnotatedWith(Injectable::class.java).map { clazz ->
                clazz.declaredMethods.filter { method ->
                    method.isAnnotationPresent(Injector::class.java)
                }.forEach { method ->
                    clazz.getDeclaredConstructor().newInstance().let { injectable ->
                        injectableMap[method.returnType.simpleName] = method.invoke(injectable)
                    }
                }
            }

            return reflections.getTypesAnnotatedWith(Listener::class.java).map { clazz ->
                clazz.declaredConstructors.firstOrNull()?.let { constructor ->
                    constructor.parameters.map { parameter ->
                        val simpleName = parameter.type.simpleName
                        injectableMap[simpleName] ?: throw IllegalStateException("No Injectable found for $simpleName")
                    }.let {
                        constructor.newInstance(*it.toTypedArray())
                    }
                } ?: throw IllegalStateException("No constructor found for ${clazz.simpleName}")
            }
        }

    fun launch(coroutineBlock: CoroutineScope.() -> Unit) {
        mainScope.coroutineBlock()
    }
}