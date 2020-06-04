enablePlugins(JavaAppPackaging, AshScriptPlugin)

name := "akkahttp-proto"

version := "0.2"

scalaVersion := "2.13.1"
dockerBaseImage := "openjdk:8-jre-alpine"

val akkaVersion = "2.6.5"
val akkaHttpVersion = "10.1.12"
val circeVersion =  "0.13.0"

val scalaTestVersion = "3.1.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "de.heikoseeberger" %% "akka-http-circe" % "1.32.0",

  "org.scalatest" %% "scalatest" % scalaTestVersion % Test
)
