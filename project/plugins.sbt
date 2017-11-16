// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.6")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")

// web plugins

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.10")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.2")

addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.11")

// Wart remover (Scala linter)
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.1.1")

// Scala style (we care only about the options that affect code quality, not if you use space before parenthesis)
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

// Code coverage
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

// Build info (commit hash, etc.)
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")
