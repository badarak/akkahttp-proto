package com.proto

import com.typesafe.config.{Config, ConfigFactory}

trait ServerConfig {
  private val rootConfig : Config  = ConfigFactory.load()
  lazy val host                  = rootConfig.getString("server.host")
  lazy val port                  = rootConfig.getInt("server.port")
}
