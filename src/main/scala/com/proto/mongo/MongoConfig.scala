package com.proto.mongo

import com.typesafe.config.{Config, ConfigFactory}

object MongoConfig {
  lazy val url = rootConfig.getString("mongo.url")

  lazy val databaseName = rootConfig.getString("mongo.name")

  private val rootConfig: Config = ConfigFactory.load()
}
