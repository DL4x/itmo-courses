import java.io.InputStream
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Config(fileName: String) {
    private val config: Map<String, String> = extractContent(fileName)

    operator fun provideDelegate(
        thisRef: Any?,
        property: KProperty<*>,
    ): ReadOnlyProperty<String?, String> {
        require(property.name in config) {
            "No value '${property.name}' in config"
        }
        return ReadOnlyProperty { _, _ -> config.getValue(property.name) }
    }

    companion object {
        private fun extractContent(fileName: String): Map<String, String> {
            val inputStream = getResource(fileName)
            requireNotNull(inputStream) {
                "File '$fileName' not found. Try to specify existing file"
            }
            val values: MutableMap<String, String> = mutableMapOf()
            inputStream.bufferedReader().use {
                it.forEachLine { line ->
                    val elements = line.split("=")
                    require(elements.size == 2) {
                        "Incorrect entry in the configuration: '$line'"
                    }
                    val (key, value) = elements.map(String::trim)
                    values[key] = value
                }
            }
            return values
        }
    }
}

@Suppress(
    "RedundantNullableReturnType",
    "UNUSED_PARAMETER",
)
fun getResource(fileName: String): InputStream? {
    // do not touch this function
    val content =
        """
        |valueKey = 10
        |otherValueKey = stringValue 
        """.trimMargin()

    return content.byteInputStream()
}
