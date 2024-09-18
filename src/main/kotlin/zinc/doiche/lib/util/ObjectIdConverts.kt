package zinc.doiche.lib.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import org.bson.types.ObjectId

fun ObjectIdModule() = SimpleModule().apply {
    addSerializer(ObjectId::class.java, ObjectIdSerializer())
    addDeserializer(ObjectId::class.java, ObjectIdDeserializer())
}

class ObjectIdSerializer: JsonSerializer<ObjectId>() {
    override fun serialize(value: ObjectId, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.toHexString())
    }
}

class ObjectIdDeserializer: JsonDeserializer<ObjectId>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ObjectId {
        return ObjectId(p.valueAsString)
    }
}