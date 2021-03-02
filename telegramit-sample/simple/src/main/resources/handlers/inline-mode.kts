package handlers

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.botlaxy.telegramit.core.client.model.inline.ChosenInlineQuery
import org.botlaxy.telegramit.core.client.model.inline.InlineQueryResultArticle
import org.botlaxy.telegramit.core.client.model.inline.InputTextMessageContent
import org.botlaxy.telegramit.core.handler.dsl.inlineHandler

data class News(
    @JsonProperty("status")
    val status: String,
    @JsonProperty("totalResults")
    val totalResults: Int,
    @JsonProperty("articles")
    val articles: List<Article>
)

data class Article(
    @JsonProperty("author")
    val author: String?,
    @JsonProperty("title")
    val title: String,
    @JsonProperty("description")
    val description: String?,
    @JsonProperty("url")
    val url: String,
    @JsonProperty("urlToImage")
    val urlToImage: String?,
    @JsonProperty("content")
    val content: String?
)

val availableCountrySet = setOf<String>("us")

val getNews: (country: String) -> News = { country ->
    jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .readValue<News>("""
            {
               "status":"ok",
               "totalResults":38,
               "articles":[
                  {
                     "source":{
                        "id":"techradar",
                        "name":"TechRadar"
                     },
                     "author":"Tom Bedford",
                     "title":"Samsung acknowledges Galaxy Note 20 Ultra flaws raised by some owners - TechRadar",
                     "description":"But you shouldn't worry",
                     "url":"https://www.techradar.com/news/samsung-acknowledges-galaxy-note-20-ultra-flaws-raised-by-some-owners",
                     "urlToImage":"https://cdn.mos.cms.futurecdn.net/sfwFiN6h8SXM8WZcGFNLBh-1200-80.jpg",
                     "publishedAt":"2020-08-21T09:41:00Z",
                     "content":"The Samsung Galaxy Note 20 Ultra release date has just passed, and some users have already reported issues with the smartphone in the form of camera bump deformities.\r\nAs reported by Sammobile, over … [+2654 chars]"
                  },
                  {
                     "source":{
                        "id":"the-verge",
                        "name":"The Verge"
                     },
                     "author":"Tom Warren",
                     "title":"An innocent typo led to a giant 212-story obelisk in Microsoft Flight Simulator - The Verge",
                     "description":"A university student accidentally created a giant 212-story obelisk inside Microsoft Flight Simulator. An innocent typo in OpenStreetMap data changed a building from two stories to 212 stories.",
                     "url":"https://www.theverge.com/2020/8/21/21395084/microsoft-flight-simulator-melbourne-obelish-openstreetmap-bing-maps-data-glitch",
                     "urlToImage":"https://cdn.vox-cdn.com/thumbor/IGNB7YGvv63_3WxbDQ5Pkqizwh0=/0x100:2560x1440/fit-in/1200x630/cdn.vox-cdn.com/uploads/chorus_asset/file/21805503/kjbJabx.jpg",
                     "publishedAt":"2020-08-21T09:17:02Z",
                     "content":"It was only supposed to be two stories high\r\nMelbournes giant virtual obelisk.\r\nMicrosoft Flight Simulator players spotted a giant mountain-high obelisk in Australia this week. While Flight Simulator… [+2046 chars]"
                  }
               ]
            }
        """.trimIndent())
}

inlineHandler {

    option {
        cacheTime = 60
    }

    answer {
        result { inlineQuery ->
            val country = if (!availableCountrySet.contains(inlineQuery.query)) "us" else inlineQuery.query
            getNews(country).articles.mapIndexed { index, article ->
                InlineQueryResultArticle(
                    id = index.toString(),
                    title = article.title,
                    description = article.description,
                    inputMessageContent = InputTextMessageContent(article.url),
                    url = article.url,
                    thumbUrl = article.urlToImage
                )
            }
        }
    }

}


