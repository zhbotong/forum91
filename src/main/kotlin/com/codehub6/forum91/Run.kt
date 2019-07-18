package com.codehub6.forum91

import io.vertx.core.Vertx
import io.vertx.core.spi.resolver.ResolverProvider.DISABLE_DNS_RESOLVER_PROP_NAME


fun main() {
  System.getProperties().setProperty(DISABLE_DNS_RESOLVER_PROP_NAME, "true")
  val vertx = Vertx.vertx()
  vertx.deployVerticle(MainVerticle())
}
