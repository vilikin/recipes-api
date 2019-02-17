package `in`.vilik

import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

annotation class CsvField(
    val value: String
)

object CsvParser {
    inline fun <reified T : Any> parseCsv(
        csv: String,
        delimiter: String = ","
    ): List<T> {
        val lines = csv.lines()
        val header = lines.first().split(delimiter)
        val contentRows = lines
            .subList(1, lines.size)
            .filter { it.isNotBlank() }
            .map { it.split(delimiter) }

        val csvParamNameToClassParam: MutableMap<String, KParameter> = mutableMapOf()

        T::class.primaryConstructor!!.parameters
            .forEach {
                val annotation = it.findAnnotation<CsvField>()
                val csvParamName = annotation?.value ?: it.name!!
                csvParamNameToClassParam[csvParamName] = it
            }

        val indexToCsvParamName: MutableMap<Int, String> = mutableMapOf()

        header.forEachIndexed { index: Int, csvParamName: String ->
            indexToCsvParamName[index] = csvParamName
        }

        return contentRows.map { row ->
            val csvParamNameToValue: MutableMap<String, String> = mutableMapOf()

            indexToCsvParamName.forEach {
                val csvParamIndex = it.key
                val csvParamName = it.value
                csvParamNameToValue[csvParamName] = row[csvParamIndex]
            }

            val classParamToValue: Map<KParameter, String> = csvParamNameToValue
                .mapKeys { csvParamNameToClassParam[it.key]!! }

            T::class.primaryConstructor!!.callBy(classParamToValue)
        }
    }
}