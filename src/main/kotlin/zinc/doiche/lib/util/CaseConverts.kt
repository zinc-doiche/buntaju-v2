package zinc.doiche.lib.util

import java.util.*

fun String.toCamelCase(): String {
    return this.split("_").joinToString("") {
        it.replaceFirstChar { char ->
            if (char.isLowerCase()) {
                char.titlecase(Locale.getDefault())
            } else {
                char.toString()
            }
        }
    }.replaceFirstChar {
        it.lowercase(Locale.getDefault())
    }
}