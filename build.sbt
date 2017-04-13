val scalaVer = "2.12.1"

val metaVersion = "1.7.0"

val paradiseVersion = "3.0.0-M8"

val silencerVersion = "0.5"

val logikaVersion = "3.0.1-1-SNAPSHOT"

val sireumScalacVersion = "3.0.0"

lazy val logikaRuntime = Project(
  id = "logika-runtime",
  base = file("native/jvm"),
  settings = Seq(
    organization := "org.sireum",
    name := "logika-runtime",
    incOptions := incOptions.value.withNameHashing(true),
    retrieveManaged := true,
    version := logikaVersion,
    scalaVersion := scalaVer,
    scalacOptions := Seq("-target:jvm-1.8", "-deprecation",
      "-Ydelambdafy:method", "-feature", "-unchecked", "-Xfatal-warnings"),
    parallelExecution in Test := true,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "scalameta" % metaVersion,
      "org.apfloat" % "apfloat" % "1.8.2",
      "org.scala-lang" % "scala-reflect" % scalaVer,
      "org.spire-math" %% "spire" % "0.13.0",
      "com.github.ghik" %% "silencer-lib" % silencerVersion
    ),
    addCompilerPlugin("com.github.ghik" %% "silencer-plugin" % silencerVersion),
    addCompilerPlugin("org.scalameta" % "paradise" % paradiseVersion cross CrossVersion.full),
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra :=
      <url>https://github.com/sireum/v3-logika-runtime/</url>
        <licenses>
          <license>
            <name>Simplified BSD License</name>
            <url>https://github.com/sireum/v3-logika-runtime/blob/master/license.md</url>
          </license>
        </licenses>
        <scm>
          <url>https://github.com/sireum/v3-logika-runtime.git</url>
          <connection>scm:git:https://github.com/sireum/v3-logika-runtime.git</connection>
        </scm>
        <developers>
          <developer>
            <id>robby-phd</id>
            <name>Robby</name>
            <url>http://cs.ksu.edu/~robby</url>
          </developer>
        </developers>
  )
)

lazy val logikaPrelude = Project(
  id = "logika-prelude",
  base = file("api/jvm"),
  settings = Seq(
    organization := "org.sireum",
    name := "logika-prelude",
    incOptions := incOptions.value.withNameHashing(true),
    retrieveManaged := true,
    version := logikaVersion,
    scalaVersion := scalaVer,
    scalacOptions := Seq("-target:jvm-1.8", "-deprecation",
      "-Ydelambdafy:method", "-feature", "-unchecked", "-Xfatal-warnings"),
    parallelExecution in Test := true,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "scalameta" % metaVersion,
      "org.sireum" %% "logika-runtime" % logikaVersion,
      "org.scalatest" %% "scalatest" % "3.0.1" % "test",
      "com.github.ghik" %% "silencer-lib" % silencerVersion
    ),
    unmanagedResourceDirectories in Compile += file("api/jvm/src/main/scala"),
    addCompilerPlugin("com.github.ghik" %% "silencer-plugin" % silencerVersion),
    addCompilerPlugin("org.scalameta" % "paradise" % paradiseVersion cross CrossVersion.full),
    addCompilerPlugin("org.sireum" %% "scalac-plugin" % sireumScalacVersion),
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra :=
      <url>https://github.com/sireum/v3-logika-runtime/</url>
        <licenses>
          <license>
            <name>Simplified BSD License</name>
            <url>https://github.com/sireum/v3-logika-runtime/blob/master/license.md</url>
          </license>
        </licenses>
        <scm>
          <url>https://github.com/sireum/v3-logika-runtime.git</url>
          <connection>scm:git:https://github.com/sireum/v3-logika-runtime.git</connection>
        </scm>
        <developers>
          <developer>
            <id>robby-phd</id>
            <name>Robby</name>
            <url>http://cs.ksu.edu/~robby</url>
          </developer>
        </developers>
  )
)