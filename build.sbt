import sbt.Compile

name         := "dexterity"
version      := "0.1"
scalaVersion := "2.13.8"

enablePlugins(CalibanPlugin, ScalaJSPlugin)
scalaJSUseMainModuleInitializer := true
ThisBuild / evictionErrorLevel  := Level.Info

val classMain         = "nicoburniske.dexterity.Main"
val sttpVersion       = "3.3.18"
val calibanVersionSbt = "1.3.2"

libraryDependencies ++= Seq(
  "org.scala-js"                 %%% "scalajs-dom"                    % "1.1.0",
  "com.raquo"                    %%% "laminar"                        % "0.13.1",
  "org.typelevel"                %%% "cats-effect"                    % "3.3.4",
  "com.github.ghostdogpr"         %% "caliban-client"                 % calibanVersionSbt,
  "com.github.ghostdogpr"        %%% "caliban-client-laminext"        % calibanVersionSbt,
  "com.softwaremill.sttp.client3" %% "core"                           % sttpVersion,
  "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % sttpVersion
)

lazy val app = (project in file(".")).settings(
  mainClass := Some(classMain),
  Compile / caliban / calibanSettings += calibanSetting(
    file("SushiExchangeSchema.graphql")
  )(cs =>
    cs.splitFiles(true)
      .packageName("generated.sushi.exchange")
      .genView(true)
      .imports("nicoburniske.dexterity.utils.Implicits._")
      .scalarMapping("Bytes" -> "String"))
)
