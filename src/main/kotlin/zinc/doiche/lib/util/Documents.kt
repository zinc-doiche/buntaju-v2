package zinc.doiche.lib.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.bson.Document

val objectMapper = ObjectMapper()

fun <T> Document.toObject(clazz: Class<T>): T {
    return objectMapper.readValue(this.toJson(), clazz)
}

fun Any.toDocument(): Document {
    return Document.parse(objectMapper.writeValueAsString(this))
}

fun Document.toSet(): Document {
    return Document("\$set", this)
}