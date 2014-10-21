logLevel := Level.Info

name := "ReactiveMongoLifecycle"

version := "1.0"

scalaVersion := "2.10.4"

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Sonatype respository" at "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies += "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.6"

libraryDependencies += "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.0.2"

