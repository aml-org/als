import java.io.{FileInputStream, FileOutputStream}

import org.scalajs.core.tools.linker.ModuleKind

name := "api-language-server"

val VERSION = "0.3.1-SNAPSHOT"
val hlVersion = "0.2.1-SNAPSHOT"
val suggestionsVersion = "0.4.1-SNAPSHOT"
val outlineVersion = "0.1.1-SNAPSHOT"
val amfVersion = "1.7.0"
val syamlVersion = "0.2.7"
val scalaCommonVersion = "0.1.3"

scalaVersion := "2.12.2"

publish := {}

jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv()

val settings =  Common.settings ++ Common.publish ++ Seq(
    organization := "test",
    
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
        "com.github.amlorg" %%% "amf-webapi" % amfVersion,
        "com.github.amlorg" %%% "amf-core" % amfVersion,
        "com.github.amlorg" %%% "amf-client" % amfVersion,
        "com.github.amlorg" %%% "amf-aml" % amfVersion,
        "org.mule.common" %%% "scala-common" % scalaCommonVersion,
        "org.mule.syaml" %%% "syaml" % syamlVersion,
        "org.scalatest"    %%% "scalatest" % "3.0.0" % Test,
        "com.chuusai" %% "shapeless" % "2.3.3",
        "org.mule.amf" %%% "typesystem-project" % hlVersion,
        "org.mule.amf" %%% "als-suggestions" % suggestionsVersion,
        "org.mule.amf" %%% "als-outline" % outlineVersion
    )
)

lazy val sharedProject = (project in file("shared")).settings(settings: _*).settings(name := "api-language-server-shared-project",
    version := VERSION)

lazy val core = crossProject.settings(
    Seq(
        name := "api-language-server",
        version := VERSION
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
        
        var srcDir = baseDir / "static" / "raml-language-server";
        var dstDir = baseDir / "target" / "raml-language-server";
        
        dstDir.delete();
        dstDir.mkdir();
        
        copyDir(srcDir, dstDir);
        
        var srcDir1 = baseDir / "target/artifact";
        var dstDir1 = baseDir / "target" / "raml-language-server" / "dist/entryPoints/node/server";
        
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

