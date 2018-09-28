import java.io.{FileInputStream, FileOutputStream}
import org.scalajs.core.tools.linker.ModuleKind
import Dependencies.deps

name := "api-language-server"

version := deps("version")

scalaVersion := "2.12.2"

publish := {}

jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv()

val settings =  Common.settings ++ Common.publish ++ Seq(
    organization := "test",
    version := deps("version"),

      resolvers ++= List(
        Common.releases,
        Common.snapshots,
        Resolver.mavenLocal/*,
        Resolver.sonatypeRepo("releases"),
        Resolver.sonatypeRepo("snapshots")*/
    ),
    resolvers += "jitpack" at "https://jitpack.io",
    
    credentials ++= Common.credentials(),
    
    libraryDependencies ++= Seq(
        "com.github.amlorg" %%% "amf-webapi" % deps("amf"),
        "com.github.amlorg" %%% "amf-core" % deps("amf"),
        "com.github.amlorg" %%% "amf-client" % deps("amf"),
        "com.github.amlorg" %%% "amf-aml" % deps("amf"),
        "org.mule.common" %%% "scala-common" % deps("common"),
        "org.mule.syaml" %%% "syaml" % deps("syaml"),
        "org.scalatest"    %%% "scalatest" % "3.0.0" % Test,
        "com.chuusai" %% "shapeless" % "2.3.3",
        "org.mule.amf" %%% "typesystem-project" % deps("hl"),
        "org.mule.amf" %%% "als-suggestions" % deps("suggestions"),
        "org.mule.amf" %%% "als-outline" % deps("outline"),

        "com.lihaoyi" %%% "upickle" % "0.5.1" % Test
    )
)

lazy val url = sys.env.getOrElse("SONAR_SERVER_URL", "Not found url.")
lazy val token = sys.env.getOrElse("SONAR_SERVER_TOKEN", "Not found token.")


lazy val root = project.in(file("."))
    .aggregate(coreJVM, coreJS)
    .enablePlugins(SonarRunnerPlugin)
    .settings(
        sonarProperties := {
            Map(
                "sonar.host.url" -> url,
                "sonar.login" -> token,
                "sonar.projectKey" -> "mulesoft.als",
                "sonar.projectName" -> "ALS",
                "sonar.github.repository" -> "mulesoft/als",
                "sonar.projectVersion" -> deps("version"),
                "sonar.sourceEncoding" -> "UTF-8",
                "sonar.modules" -> ".",
                "..sonar.sources" -> "shared/src/main/scala",
                "..sonar.exclusions" -> "shared/src/test/resources/**",
                "..sonar.tests" -> "shared/src/test/scala",
                "..sonar.scoverage.reportPath" -> "jvm/target/scala-2.12/scoverage-report/scoverage.xml"
            )
        }
    )

lazy val sharedProject = (project in file("shared")).settings(settings: _*)
  .settings(
      name := "api-language-server-shared-project"
  )

lazy val core = crossProject.settings(
    Seq(
        name := "api-language-server"
    )
).in(file(".")).settings(settings: _*).jvmSettings(
    assemblyMergeStrategy in assembly := {
        case x if x.toString.endsWith("JS_DEPENDENCIES")             => MergeStrategy.discard
        case PathList(ps @ _*) if ps.last endsWith "JS_DEPENDENCIES" => MergeStrategy.discard
        case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
        case x => {
            MergeStrategy.first
        }
    },
    assemblyJarName in assembly := "server.jar"
).jsSettings(
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
    scalaJSModuleKind := ModuleKind.CommonJSModule,
    scalaJSUseMainModuleInitializer := true,
    mainClass in Compile := Some("org.mulesoft.language.client.js.ServerProcess"),
    artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"serverProcess.js"
)

//lazy val core_mslsp = crossProject.settings(
//    Seq(
//        name := "api-language-server-mslsp",
//
//        libraryDependencies += "org.mule.syaml" %%% "syaml" % "0.1.3"
//    )
//).in(file(".")).settings(settings: _*).jvmSettings(
//    libraryDependencies += "org.scala-js"           %% "scalajs-stubs"          % scalaJSVersion % "provided"
//).jsSettings(
//    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
//    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
//    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
//    scalaJSModuleKind := ModuleKind.CommonJSModule,
//    scalaJSUseMainModuleInitializer := true,
//    mainClass in Compile := Some("org.mulesoft.language.server.js.Main"),
//    artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"serverProcess.js"
//)

//lazy val core_weblsp = crossProject.settings(
//    Seq(
//        name := "api-language-server-weblsp",
//
//        libraryDependencies += "org.mule.syaml" %%% "syaml" % "0.1.3"
//    )
//).in(file(".")).settings(settings: _*).jvmSettings(
//    libraryDependencies += "org.scala-js"           %% "scalajs-stubs"          % scalaJSVersion % "provided"
//).jsSettings(
//    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
//    libraryDependencies += "com.lihaoyi" %%% "upickle" % "0.5.1",
//    scalaJSOutputMode := org.scalajs.core.tools.linker.backend.OutputMode.ECMAScript6,
//    scalaJSModuleKind := ModuleKind.CommonJSModule,
//    scalaJSUseMainModuleInitializer := true,
//    mainClass in Compile := Some("org.mulesoft.language.server.js.Main"),
//    artifactPath in (Compile, fastOptJS) := baseDirectory.value / "target" / "artifact" /"serverProcess.js"
//)

lazy val coreJVM = core.jvm.in(file("./jvm")).dependsOn(sharedProject)
lazy val coreJS  = core.js.in(file("./js")).dependsOn(sharedProject)

//lazy val coreJVM_MSLSP = core_mslsp.jvm.in(file("./jvm_mslsp")).dependsOn(sharedProject)
//lazy val coreJS_MSLSP  = core_mslsp.js.in(file("./js_mslsp")).dependsOn(sharedProject)
//
//lazy val coreJVM_WEBLSP = core_weblsp.jvm.in(file("./jvm_weblsp")).dependsOn(sharedProject)
//lazy val coreJS_WEBLSP  = core_weblsp.js.in(file("./js_weblsp")).dependsOn(sharedProject)

val buildJS = TaskKey[Unit]("buildJS")
val buildJS_MSLSP = TaskKey[Unit]("buildJSMSLSP")
val buildJS_WEBLSP = TaskKey[Unit]("buildJSWEBLSP")

buildJS := {
    val _ = (fastOptJS in Compile in coreJS).map((value) => {
        var copyDir1: Function2[File, File, Unit] = null;
        
        var copyDir: Function2[File, File, Unit] = (src: File, dst: File) => {
            src.list().foreach(name => {
                var srcf = src / name;
                var dstf = dst / name;
                
                if(srcf.isFile) {
                    dstf.createNewFile();
    
                    var is = new FileInputStream(srcf);
                    var os = new FileOutputStream(dstf);
                    
                    var bytes = new Array[Byte](is.available());
    
                    is.read(bytes);
                    
                    os.write(bytes);
    
                    is.close();
                    os.close();
                } else if(srcf.isDirectory) {
                    dstf.mkdir();
                    
                    copyDir1(srcf, dstf);
                }
            });
        };
    
        copyDir1 = copyDir;
        
        var baseDir = value.data.getParentFile.getParentFile.getParentFile;
        
        var srcDir = baseDir / "static" / "api-language-server";
        var dstDir = baseDir / "target" / "api-language-server";
        
        dstDir.delete();
        dstDir.mkdir();
        
        copyDir(srcDir, dstDir);
        
        var srcDir1 = baseDir / "target/artifact";
        var dstDir1 = baseDir / "target" / "api-language-server" / "dist/entryPoints/node/server";
        
        dstDir1.mkdir();
        
        copyDir(srcDir1, dstDir1);
        
        value;
    }).value;
}

//buildJS_MSLSP := {
//    val _ = (fastOptJS in Compile in coreJS_MSLSP).value
//}
//
//buildJS_WEBLSP := {
//    val _ = (fastOptJS in Compile in coreJS_WEBLSP).value
//}

mainClass in Compile := Some("org.mulesoft.language.client.js.Main")

