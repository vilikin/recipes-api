package `in`.vilik

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import java.time.LocalDateTime

object Cache {
    private data class CacheItem(
        val cachedAt: LocalDateTime,
        val content: String
    )

    private val cache: MutableMap<String, CacheItem> = mutableMapOf()
    private val cacheEntryInvalidIfOlderThan: LocalDateTime
        get() = LocalDateTime.now().minusMinutes(5)

    fun put(path: String, content: String) {
        cache[path] = CacheItem(LocalDateTime.now(), content)
    }

    fun put(pair: Pair<String, String>) {
        cache[pair.first] = CacheItem(LocalDateTime.now(), pair.second)
    }

    fun clear() {
        cache.clear()
    }

    operator fun get(path: String): String? =
        cache[path]?.content

    operator fun contains(path: String): Boolean =
        path in cache && isCacheStillValid(path)

    private fun isCacheStillValid(path: String): Boolean =
        cache[path]?.cachedAt?.isAfter(cacheEntryInvalidIfOlderThan) ?: false
}

open class GithubRepository(
    userName: String,
    repositoryName: String
) {
    private val rootRawApiUrl = "https://raw.githubusercontent.com/$userName/$repositoryName/master"

    suspend fun getRawFileContent(path: String): String =
        if (path in Cache) {
            Cache[path]!!
        } else {
            val rawFileContent = get("$rootRawApiUrl/$path")
            Cache.put(path to rawFileContent)
            rawFileContent
        }

    fun getRawFileUrl(path: String) = "$rootRawApiUrl/$path"

    suspend fun get(path: String): String {
        val client = HttpClient(Apache)
        val response: String = client.get(path)
        client.close()

        return response
    }
}

