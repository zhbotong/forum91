package com.codehub6.forum91

import io.vertx.core.json.JsonArray
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.web.client.sendAwait
import io.vertx.kotlin.ext.web.client.webClientOptionsOf
import org.jsoup.Jsoup

class SpiderVerticle:CoroutineVerticle(){
  lateinit var webClient: WebClient
  private val forumUrl  = "/forumdisplay.php"
  private val articleUrl = "/viewthread.php"
  lateinit var host:String;
  private val log = LoggerFactory.getLogger(this::class.java)

  override suspend fun start() {
    val option = webClientOptionsOf()
    webClient = WebClient.create(vertx, option)
    val websiteConfig = config.getJsonObject("website")
    host = websiteConfig.getString("host")
    val fid = "19"

    for (index in 1..1000){
      val content = webClient.getAbs(host+forumUrl)
        .addQueryParam("fid", fid)
        .addQueryParam("orderby", "dateline")
        .addQueryParam("page",index.toString())
        .sendAwait()
        .bodyAsString()
      val body = Jsoup.parse(content).body()
      val normalthread = body.getElementsByAttributeValueMatching("id","normalthread_*")

      for (element in normalthread) {
        val tid = element.attr("id").split("_")[1]
        articleContent(tid)
      }
    }

  }

  suspend fun articleContent(tid:String){
    log.info("开始处理文章：$tid")
    val content = webClient.getAbs(host+articleUrl)
      .addQueryParam("tid",tid)
      .addQueryParam("page","1")
      .sendAwait()
      .bodyAsString()
    val body = Jsoup.parse(content).body()
    //标题
    val title = body
      .getElementsByAttributeValue("id","threadtitle")
      .first().getElementsByTag("h1")
      .text()

    //图片处理
     val imageList = body
      .getElementsByTag("img")
      .filter{it.hasAttr("file")}
    var dowloadParamList = JsonArray()
    for (image in imageList) {
        val dowloadParam = jsonObjectOf("url" to image.attr("file")
          ,"filename" to image.attr("alt"),"title" to title)
      dowloadParamList.add(dowloadParam)
    }
    vertx.eventBus().send(DowloadImageVerticle::class.java.name,dowloadParamList)
    //内容+评论
   /* val contentParam = jsonObjectOf("body" to content,"tid" to tid)
    vertx.eventBus().send(ContentVerticle::class.java.name,contentParam)*/
  }
}
