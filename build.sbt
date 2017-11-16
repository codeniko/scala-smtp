import wartremover.WartRemover.autoImport._

lazy val myResolvers = resolvers := Seq(
  Resolver.bintrayRepo("underscoreio", "training"),
  Resolver.bintrayRepo("lightshed", "maven")
)

lazy val root = (project in file(".")).
  enablePlugins(PlayScala).
  enablePlugins(BuildInfoPlugin).
  settings(myResolvers: _*)

name := "scala-smtp"
version := "0.0-SNAPSHOT"
scalaVersion := "2.12.4"
scalacOptions in(Compile, console) := List(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-unchecked",
  "-deprecation",
  "-Xfuture",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused",
  "-Ywarn-unused-import"
)

// logger option for less verbose test logs
javaOptions in Test += "-Dlogger.resource=logback.test.xml"

// Excluded generated files in the coverage report
coverageExcludedPackages := """controllers\..*Reverse.*;router.Routes.*;"""

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := scalastyle.in(Compile).toTask("").value

(compile in Compile) := ((compile in Compile) dependsOn compileScalastyle).value

// Scala linter: disable unsafe stuff
wartremoverErrors in (Compile, Keys.compile) ++= Seq(
  Wart.FinalCaseClass,
  Wart.StringPlusAny,
  Wart.AsInstanceOf,
  Wart.EitherProjectionPartial,
  Wart.IsInstanceOf,
  Wart.TraversableOps,
  Wart.NonUnitStatements,
  Wart.Null,
  Wart.OptionPartial,
  Wart.Product,
  Wart.Return,
  Wart.Serializable,
  Wart.TryPartial,
  Wart.While,
  Wart.Var
)

// no need to run tests in assembly
test in assembly := {}

// don't package sources and documentation
sources in(Compile, doc) := Seq.empty
publishArtifact in(Compile, packageDoc) := false

assemblyOutputPath in assembly := file("target/package/scala-smtp.jar")

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "io.netty.versions.properties") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

buildInfoKeys := Seq[BuildInfoKey](
  name, version, scalaVersion, sbtVersion,
  "hostname" → java.net.InetAddress.getLocalHost.getHostName,
  "whoami" → System.getProperty("user.name"),
  "buildTimestamp" → new java.util.Date(System.currentTimeMillis()),
  "gitHash" → new java.lang.Object() {
    override def toString: String = {
      try {
        val extracted = new java.io.InputStreamReader(
          java.lang.Runtime.getRuntime.exec("git rev-parse HEAD").getInputStream)
        new java.io.BufferedReader(extracted).readLine()
      } catch {
        case _: Throwable ⇒ "get git hash failed"
      }
    }
  }.toString()
)

libraryDependencies ++= Seq(
  //ehcache,
  ws,
  filters,
  guice,

  // Play json library (now separate project for play 2.6.x)
  "com.typesafe.play" %% "play-json" % "2.6.6",

  // Test frameworks and libraries
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.1" % Test,
  //"org.mockito" % "mockito-all" % "2.0.2-beta" % Test,
  "org.scalacheck" %% "scalacheck" % "1.13.5" % Test,

  "ch.lightshed" %% "courier" % "0.1.4"

)

buildInfoPackage := "buildpkg"

PlayKeys.devSettings := Seq("play.server.http.port" -> "4080")

// exclude generated code from the linter
wartremoverExcluded += crossTarget.value / "routes" / "main" / "router" / "Routes.scala"
wartremoverExcluded += crossTarget.value / "routes" / "main" / "router" / "RoutesPrefix.scala"
wartremoverExcluded += crossTarget.value / "routes" / "main" / "controllers" / "ReverseRoutes.scala"
wartremoverExcluded += crossTarget.value / "routes" / "main" / "controllers" / "javascript" / "JavaScriptReverseRoutes.scala"


// Coverage settings
coverageMinimum := 45
coverageFailOnMinimum := true
