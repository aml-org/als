import org.scalajs.core.tools.linker.ModuleKind
import sbt.Keys.{libraryDependencies, resolvers}

name := "api-language-server"

version := "0.1"

scalaVersion := "2.12.2"

publish := {}

jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv()

val settings =  Common.settings ++ Common.publish ++ Seq(
    organization := "test",
    resolvers ++= List(
      Common.releases,
      Common.snapshots,
      Resolver.mavenLocal,
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    ),
    credentials ++= Common.credentials(),
    libraryDependencies ++= Seq(
//        "org.mule.amf" %%% "amf-core" % "1.2.0-SNAPSHOT",
//        "org.mule.amf" %%% "amf-client" % "1.2.0-SNAPSHOT",
//      "org.mule.amf" %%% "amf-validation" % "1.2.0-SNAPSHOT",

//      "org.mule.amf" %%% "amf-webapi" % "1.2.0-SNAPSHOT",
//      "org.mule.amf" %%% "amf-core" % "1.2.0-SNAPSHOT",
//      "org.mule.amf" %%% "amf-client" % "1.2.0-SNAPSHOT",
      "org.mule.amf" %%% "amf-core" % "1.3.0-SNAPSHOT",
      "org.mule.amf" %%% "amf-client" % "1.3.0-SNAPSHOT",
      "org.mule.amf" %%% "typesystem-project" % "0.1-SNAPSHOT",
      "org.mule.amf" %%% "als-suggestions" % "0.1-SNAPSHOT",
      "org.mule.amf" %%% "als-outline" % "0.1-SNAPSHOT",
//      "org.mule.amf" %%% "amf-vocabularies" % "1.2.0-SNAPSHOT",
      "org.mule.common" %%% "scala-common" % "0.1.2",

      "org.scalatest"    %%% "scalatest" % "3.0.0" % Test,
      "com.chuusai" %% "shapeless" % "2.3.3"
        //"com.github.scopt" %%% "scopt"     % "3.7.0"
    )
)

lazy val sharedProject = (project in file("shared"))
  .settings(settings: _*)
  .settings(
      name := "api-language-server-shared-project"
  )

//run in Compile <<= (run in Compile in sharedProject)

lazy val core = crossProject
    .settings(
        Seq(
            name := "api-language-server",
            libraryDependencies += "org.mule.syaml" %%% "syaml" % "0.0.10"
        ))
    .in(file("."))
    .settings(settings: _*)
    .jvmSettings(

        libraryDependencies += "org.scala-js"           %% "scalajs-stubs"          % scalaJSVersion % "provided",
        //libraryDependencies += "org.scala-lang.modules" % "scala-java8-compat_2.12" % "0.8.0",
        //libraryDependencies += "org.json4s"             %% "json4s-jackson"         % "3.5.2",
        artifactPath in (Compile, packageDoc) := baseDirectory.value / "target" / "artifact" / "amf-core-javadoc.jar"
    )
    .jsSettings(
        libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
        libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
        scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
        scalaJSModuleKind := ModuleKind.CommonJSModule,
        scalaJSUseMainModuleInitializer := true,
        mainClass in Compile := Some("org.mulesoft.language.client.js.Main"),
        artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"serverProcess.js"


)

lazy val coreJVM = core.jvm.in(file("./jvm")).dependsOn(sharedProject)
lazy val coreJS  = core.js.in(file("./js")).dependsOn(sharedProject)
val buildJS = TaskKey[Unit](
    "buildJS")
buildJS := {
    val _ = (fastOptJS in Compile in coreJS).value
}

mainClass in Compile := Some("org.mulesoft.language.client.js.Main")
//mainClass in (Compile, run) := Some("org.mulesoft.language.server.Test")
//mainClass in (Compile, run) := Some("org.test.java.TestClass")
//mainClass in (Compile, run) := Some("org.test.java.Main")
//scalaSource in Compile := baseDirectory.value / "shared/src"
//libraryDependencies ++= Seq(
//
//    "org.scalatest"    %%% "scalatest" % "3.0.0" % Test
//    //"com.github.scopt" %%% "scopt"     % "3.7.0"
//)

