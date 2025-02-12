ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.1"
ThisBuild / semanticdbEnabled := true
ThisBuild / scalacOptions ++= Seq("-Ykind-projector:underscores")

lazy val root = (project in file("."))
  .settings(
    name := "scala3",
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.13.0",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.7",
    libraryDependencies += "org.typelevel" %% "log4cats-slf4j" % "2.7.0",
    libraryDependencies ++= Seq("org.http4s" %% "http4s-core" % "1.0.0-M44",
      "org.http4s" %% "http4s-dsl" % "1.0.0-M44",
      "org.http4s" %% "http4s-ember-server" % "1.0.0-M44",
      "org.http4s" %% "http4s-ember-client" % "1.0.0-M44",
      "org.http4s" %% "http4s-circe" % "1.0.0-M44"),
    libraryDependencies += "co.fs2" %% "fs2-core" % "3.11.0",
    libraryDependencies ++= Seq("io.circe" %% "circe-core" % "0.14.10",
      "io.circe" %% "circe-generic" % "0.14.10",
      "io.circe" %% "circe-parser" % "0.14.10",
      "io.circe" %% "circe-fs2" % "0.14.1"),
    libraryDependencies ++= Seq(
      "dev.optics" %% "monocle-core" % "3.3.0",
      "dev.optics" %% "monocle-macro" % "3.3.0",
    ),
    libraryDependencies += "org.mockito" % "mockito-core" % "5.15.2" % Test,
    libraryDependencies += "com.disneystreaming" %% "weaver-cats" % "0.8.4" % Test,
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-laws" % "2.13.0" % Test,
    ),
    libraryDependencies +=
      "org.typelevel" %% "discipline-core" % "1.7.0",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "discipline-scalatest" % "2.3.0"
    ),
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.19",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test",
    //testFrameworks += new TestFramework("weaver.framework.CatsEffect")
    libraryDependencies += "org.tpolecat" %% "skunk-core" % "0.6.4"
  )
