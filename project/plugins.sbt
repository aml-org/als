addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.2.0")
addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "0.6.33")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"            % "2.0.4")
addSbtPlugin("com.github.mwz"     % "sbt-sonar"                % "2.1.0")
addSbtPlugin("com.eed3si9n"       % "sbt-sriracha"             % "0.1.0")

ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)
