package `in`.vilik

import com.google.gson.annotations.SerializedName
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get

data class ContentItem(
    val name: String,
    val path: String,
    val type: String,
    @SerializedName("download_url")
    val downloadUrl: String?
)

open class GithubRepository(
    userName: String,
    repositoryName: String
) {
    private val rootApiUrl = "https://api.github.com/repos/$userName/$repositoryName"
    private val rootRawApiUrl = "https://raw.githubusercontent.com/$userName/$repositoryName/master"

    suspend fun getContentsFrom(path: String): List<ContentItem> {
        return get("$rootApiUrl/contents/$path")
    }

    suspend fun getRawFileContent(path: String): String =
        get("$rootRawApiUrl/$path")

    fun getRawFileUrl(path: String) = "$rootRawApiUrl/$path"

    suspend inline fun <reified T> get(path: String): T {
        val client = HttpClient(Apache) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }

        val response: T = client.get(path)

        client.close()

        return response
    }
}

