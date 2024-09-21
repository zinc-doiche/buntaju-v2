package zinc.doiche.lib.util

import com.mongodb.client.model.Filters
import org.bson.conversions.Bson
import kotlin.reflect.KProperty

private fun String.toSnakeCase() = this.replace(Regex("([a-z])([A-Z]+)"), "$1_$2").lowercase()

internal infix fun <V: Any> KProperty<V>.eq(value: V): Bson = Filters.eq(this.name.toSnakeCase(), value)