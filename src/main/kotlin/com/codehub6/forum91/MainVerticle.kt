package com.codehub6.forum91

import io.vertx.config.ConfigRetriever
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.kotlin.config.configRetrieverOptionsOf
import io.vertx.kotlin.config.configStoreOptionsOf
import io.vertx.kotlin.config.getConfigAwait
import io.vertx.kotlin.core.deploymentOptionsOf
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.CoroutineVerticle

class MainVerticle : CoroutineVerticle() {
  override suspend fun start() {
    val fileStore = configStoreOptionsOf(type = "file",config = jsonObjectOf("path" to "config.json"))
    val options = configRetrieverOptionsOf(stores = listOf(fileStore))
    val retriever = ConfigRetriever.create(vertx, options)
    val config = retriever.getConfigAwait()
    vertx.deployVerticle(SpiderVerticle(), deploymentOptionsOf(config = config))
    vertx.deployVerticle(DowloadImageVerticle(), deploymentOptionsOf(config = config))

  }
}
