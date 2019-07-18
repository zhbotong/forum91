package com.codehub6.forum91

import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.web.client.webClientOptionsOf
import org.jsoup.Jsoup

/**
 * 文章+评论
 */
class ContentVerticle : CoroutineVerticle(){
  private val log = LoggerFactory.getLogger(this::class.java)
  lateinit var webClient: WebClient
  lateinit var host:String;


  override suspend fun start() {
    val option = webClientOptionsOf()
    webClient = WebClient.create(vertx, option)
    val websiteConfig = config.getJsonObject("website")
    host = websiteConfig.getString("host")

    vertx.eventBus().consumer<JsonObject>(DowloadImageVerticle::class.java.name) {
        val body  = it.body()
        val tid = body.getString("tid")
        val content = body.getString("body")
        log.info("开始处理文章id=$tid,内容和评论")
    }
  }

  fun dd(content:String){
     val body =  Jsoup.parse(content)
     val contentList = body.getElementsByAttributeValueMatching("id", "postmessage_*")
    contentList.first()
  }
}
