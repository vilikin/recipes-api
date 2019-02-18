package `in`.vilik

import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

data class CsvFile(
    val headerRow: List<String>,
    val contentRows: List<List<String>>
) {
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

        val indexToCsvParamName: MutableMap<Int, String> = mutableMapOf()

        csvFile.headerRow.forEachIndexed { index: Int, csvParamName: String ->
            indexToCsvParamName[index] = csvParamName
        }

        return csvFile.contentRows.map { row ->
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