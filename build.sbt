val dottyVersion = "0.11.0-bin-20181009-9d12693-NIGHTLY"

lazy val root = project
  .in(file("."))
  .settings(
    name := "jsx-interpolator",
    version := "0.1.0",
    scalaVersion := dottyVersion,
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-Xfatal-warnings",
      "-encoding", "UTF8",
      "-language:implicitConversions"
    ),
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test
  )
