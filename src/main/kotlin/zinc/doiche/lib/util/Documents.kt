package zinc.doiche.lib.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.bson.Document
import zinc.doiche.lib.init.annotation.Injectable
import zinc.doiche.lib.init.annotation.Injector

val objectMapper = ObjectMapper()
    .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    .registerModule(ObjectIdModule())
    .registerKotlinModule()

@Injectable
class ObjectMapperInjection {
    @Injector
    fun getObjectMapper(): ObjectMapper = objectMapper
}

fun <T> Document.toObject(clazz: Class<T>): T {
    return objectMapper.readValue(this.toJson(), clazz)
}

fun Any.toDocument(): Document {
    return Document.parse(objectMapper.writeValueAsString(this))
}

fun Document.toSet(): Document {
    return Document("\$set", this)
}

fun Any.toJson(): String = objectMapper.writeValueAsString(this)

fun Any.toPrettyJson(): String = objectMapper
    .writerWithDefaultPrettyPrinter()
    .writeValueAsString(this)