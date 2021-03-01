package studio.bz_soft.mapkittest.root

object Constants {
    const val DB_NAME = "maps.db"

    const val GEO_CODING_BASE_URL = "https://eu1.locationiq.com/v1/"
    private const val SEARCH_GEO_CODING_API = "search.php"
    private const val REVERSE_GEO_CODING_API = "reverse.php"
    private const val GEO_CODING_API_KEY = "pk.2e6f7779b8d50dcd89a78b34d570854c"
    private const val ANSWER_LANGUAGE = "accept-language=ru"
    private const val FORMAT = "format=json"
    const val SEARCH_API = "$SEARCH_GEO_CODING_API?key=$GEO_CODING_API_KEY&$ANSWER_LANGUAGE&$FORMAT"
//    const val REVERSE_API = "$REVERSE_GEO_CODING_API?key=$GEO_CODING_API_KEY&$ANSWER_LANGUAGE&$FORMAT"
const val REVERSE_API = "$REVERSE_GEO_CODING_API?key=$GEO_CODING_API_KEY"

    const val TEST_API_URL = "http://jsonplaceholder.typicode.com/"
    const val BASE = "posts"
}