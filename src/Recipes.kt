package `in`.vilik

import java.nio.charset.Charset
import java.util.*

class NoMatchingRecipeException : Exception("Could not find recipe with the specified id")
class NoMatchingCategoryException : Exception("Category of a recipe does not exist in categories.csv")

object RecipesRepository : GithubRepository(
    "vilikin",
    "recipes"
)

data class Category(
    val id: String,
    val name: String,
    val description: String,
    @CsvField("image_file_name") val imageFileName: String
)

data class RecipeMetadata(
    val id: String,
    val name: String,
    @CsvField("category_id") val categoryId: String,
    @CsvField("image_file_name") val imageFileName: String,
    @CsvField("recipe_file_name") val recipeFileName: String
) {
    fun toRecipeWithoutContent(categories: List<Category>) =
            RecipeWithoutContent(
                id,
                name,
                RecipesRepository.getRawFileUrl("images/$imageFileName"),
                categories.find { it.id == categoryId } ?: throw NoMatchingCategoryException()
            )

    suspend fun toRecipeWithContent(categories: List<Category>) =
            RecipeWithContent(
                id,
                name,
                RecipesRepository.getRawFileUrl("images/$imageFileName"),
                categories.find { it.id == categoryId } ?: throw NoMatchingCategoryException(),
                RecipesRepository.getRawFileContent("recipes/$recipeFileName").toBase64()
            )
}

private fun String.toBase64(): String {
    val encoder = Base64.getEncoder()
    val base64Bytes = encoder.encode(this.toByteArray())
    return base64Bytes.toString(Charset.forName("UTF-8"))
}

data class RecipeWithoutContent(
    val id: String,
    val name: String,
    val imageUrl: String,
    val category: Category
)

data class RecipeWithContent(
    val id: String,
    val name: String,
    val imageUrl: String,
    val category: Category,
    val recipeBodyBase64: String
)

object Recipes {
    suspend fun findAll(): List<RecipeWithoutContent> {
        val categories = getAllCategories()
        val recipes = getAllRecipeMetadata()

        return recipes.map { it.toRecipeWithoutContent(categories) }
    }

    suspend fun findById(id: String): RecipeWithContent {
        val categories = getAllCategories()
        val recipes = getAllRecipeMetadata()

        return recipes
            .find { it.id == id }
            ?.toRecipeWithContent(categories)
            ?: throw NoMatchingRecipeException()
    }

    private suspend fun getAllRecipeMetadata(): List<RecipeMetadata> {
        val recipesCsv = RecipesRepository.getRawFileContent("recipes.csv")
        return CsvParser.parseCsv(recipesCsv)
    }

    private suspend fun getAllCategories(): List<Category> {
        val categoriesCsv = RecipesRepository.getRawFileContent("categories.csv")
        return CsvParser.parseCsv(categoriesCsv)
    }
}
