package zinc.doiche.lib.util

import com.mongodb.client.model.Filters
import org.bson.conversions.Bson
import kotlin.reflect.KProperty

internal infix fun <V: Any> KProperty<V>.eq(value: V): Bson = Filters.eq(this.name, value)