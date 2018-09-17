val dottyVersion = "0.10.0-bin-20180914-0a2734b-NIGHTLY"

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
