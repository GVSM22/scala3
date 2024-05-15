ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.1"
ThisBuild / semanticdbEnabled := true
ThisBuild / scalacOptions ++= Seq("-Ykind-projector:underscores")

lazy val root = (project in file("."))
  .settings(
    name := "scala3",
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.10.0",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.1",
    libraryDependencies += "org.typelevel" %% "log4cats-slf4j" % "2.7.0",
    libraryDependencies ++= Seq("org.http4s" %% "http4s-core" % "1.0.0-M39",
      "org.http4s" %% "http4s-dsl" % "1.0.0-M39",
      "org.http4s" %% "http4s-ember-server" % "1.0.0-M39",
      "org.http4s" %% "http4s-ember-client" % "1.0.0-M39",
      "org.http4s" %% "http4s-circe" % "1.0.0-M39"),
    libraryDependencies += "co.fs2" %% "fs2-core" % "3.10.2",
    libraryDependencies ++= Seq("io.circe" %% "circe-core" % "0.14.7",
      "io.circe" %% "circe-generic" % "0.14.7",
      "io.circe" %% "circe-parser" % "0.14.7",
      "io.circe" %% "circe-fs2" % "0.14.1"),
    libraryDependencies ++= Seq(
      "dev.optics" %% "monocle-core" % "3.2.0",
      "dev.optics" %% "monocle-macro" % "3.2.0",
    )
  )
