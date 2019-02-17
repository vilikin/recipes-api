package `in`.vilik

object RecipesRepository : GithubRepository(
    "vilikin",
    "recipes"
)

data class Category(
    val id: String,
    val name: String
)

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
    fun findAll(): List<RecipeWithoutContent> {
        return emptyList()
    }

    fun findById(id: String): RecipeWithContent {
        return RecipeWithContent(
            "id",
            "name",
            "image.png",
            Category(id = "soups", name = "Soups"),
            "wheuhqweuhruywhruyw"
        )
    }
}
