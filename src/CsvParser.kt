package `in`.vilik

import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

data class CsvFile(
    private val headerRow: List<String>,
    private val contentRows: List<List<String>>
) {
    val rows: List<Map<String, String>>
        get() = contentRows.map { row ->
            val pairs = row.mapIndexed { index: Int, value: String ->
                val fieldName = headerRow[index]
                fieldName to value
            }

            pairs.toMap()
        }

    companion object {
        fun fromString(csv: String, delimiter: String = ","): CsvFile {
            val lines = csv.lines()
            val headerRow: List<String> = lines.first().split(delimiter)
            val contentRows: List<List<String>> = lines
                .subList(1, lines.size)
                .filter { it.isNotBlank() }
                .map { it.split(delimiter) }

            return CsvFile(headerRow, contentRows)
        }
    }
}

annotation class CsvField(
    val value: String
)

object CsvParser {
    inline fun <reified T : Any> parseCsv(
        csv: String,
        delimiter: String = ","
    ): List<T> {
        val csvFile = CsvFile.fromString(csv)

        val csvParamNameToClassParam: MutableMap<String, KParameter> = mutableMapOf()

        T::class.primaryConstructor!!.parameters
            .forEach {
                val annotation = it.findAnnotation<CsvField>()
                val csvParamName = annotation?.value ?: it.name!!
                csvParamNameToClassParam[csvParamName] = it
            }

        return csvFile.rows.map { row ->
            val classParamToValue: Map<KParameter, String> = row
                .mapKeys { csvParamNameToClassParam[it.key]!! }

            T::class.primaryConstructor!!.callBy(classParamToValue)
        }
    }
}