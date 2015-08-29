organization := "me.shadaj"

name := "spotify-scala"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.6"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.11.3"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.3.4"

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.12" % Test
