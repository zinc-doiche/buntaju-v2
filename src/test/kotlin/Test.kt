
fun main() {
    "messageIdObject".replace(Regex("([a-z])([A-Z]+)"), "$1_$2").lowercase().let {
        println(it)
    }
}
