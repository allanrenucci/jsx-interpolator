val dottyVersion = "0.12.0-bin-20181208-303bfcc-NIGHTLY"
// val dottyVersion = dottyLatestNightlyBuild.get

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
