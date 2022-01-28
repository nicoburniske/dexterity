import sbt.Compile

name         := "dexterity"
version      := "0.1"
scalaVersion := "2.13.8"
val classMain = "nicoburniske.dexterity.Main"

enablePlugins(CalibanPlugin, ScalaJSPlugin, ScalaJSBundlerPlugin)

scalaJSUseMainModuleInitializer := true
ThisBuild / evictionErrorLevel  := Level.Info

val calibanVersionSbt = "1.3.2"

libraryDependencies ++= Seq(
  "org.scala-js"          %%% "scalajs-dom"             % "2.1.0",
  "com.raquo"             %%% "laminar"                 % "0.13.1",
  "com.github.ghostdogpr"  %% "caliban-client"          % calibanVersionSbt,
  "com.github.ghostdogpr" %%% "caliban-client-laminext" % calibanVersionSbt
)

Compile / npmDependencies ++= Seq(
  "@material/mwc-linear-progress"   -> "0.18.0",
  "@material/mwc-slider"            -> "0.18.0",
  "@material/mwc-list"              -> "0.25.3",
  "@material/mwc-circular-progress" -> "0.25.3",
  "@material/select"                -> "0.25.3"
)

scalaJSLinkerConfig ~= {
  // Producing source maps throws warnings on material web components complaining about missing .ts files. Not sure why.
  _.withSourceMap(false)
}

//scalacOptions ++= Seq(
//  "-Vclasspath"
//)

// fastOptJS::webpack

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
