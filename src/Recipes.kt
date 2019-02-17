package `in`.vilik

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
    val name: String,
    @CsvField("category_id") val categoryId: String,
    @CsvField("image_file_name") val imageFileName: String,
    @CsvField("recipe_file_name") val recipeFileName: String
) {
    fun toRecipeWithoutContent(categories: List<Category>) =
            RecipeWithoutContent(
                name,
                RecipesRepository.getRawFileUrl("images/$imageFileName"),
                categories.find { it.id == categoryId } ?: throw NoMatchingCategoryException()
            )
}

data class RecipeWithoutContent(
    val name: String,
    val imageUrl: String,
    val category: Category
)

data class RecipeWithContent(
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
        return RecipeWithContent(
            "name",
            "image.png",
            Category(id = "soups", name = "Soups", description = "test", imageFileName = "test"),
            "wheuhqweuhruywhruyw"
        )
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
