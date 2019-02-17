import `in`.vilik.CsvField
import `in`.vilik.CsvParser
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class CsvParserTest {
    data class Person(val id: String, val name: String)
    data class Name(
        @CsvField("first_name") val firstName: String,
        @CsvField("last_name") val lastName: String
    )

    val personCsv = File("test-resources/persons.csv").readText()
    val namesCsv = File("test-resources/names.csv").readText()

    @Test
    fun `should parse csv to plain data class`() {
        val rows = CsvParser.parseCsv<Person>(personCsv)
        val expectedRows = listOf(
            Person("1", "Vili"),
            Person("2", "Erika")
        )

        assertEquals(expectedRows, rows)
    }

    @Test
    fun `should parse csv to annotated data class`() {
        val rows = CsvParser.parseCsv<Name>(namesCsv)
        val expectedRows = listOf(
            Name("Vili", "Kinnunen"),
            Name("Erika", "Sankari")
        )

        assertEquals(expectedRows, rows)
    }
}