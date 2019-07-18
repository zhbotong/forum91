package com.codehub6.forum91

import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.web.client.webClientOptionsOf

class ArticleVerticle : CoroutineVerticle() {
  lateinit var webClient: WebClient

  override suspend fun start() {
    val option = webClientOptionsOf()
    webClient = WebClient.create(vertx, option)
    val websiteConfig = config.getJsonObject("website")
    val host = websiteConfig.getString("host")
    val articleUri = websiteConfig.getString("articleUri")
    vertx.eventBus().consumer<JsonObject>(ArticleVerticle::class.java.name) {
      val body = it.body()
      val tid = body.getString("tid")
      val page = body.getString("page")
    }
  }
}
