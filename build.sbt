val scala3Version = "3.0.0-RC1"

testFrameworks += new TestFramework("munit.Framework")

lazy val root = project
  .in(file("."))
  .settings(
    name := "opaque-type-sample",
    version := "0.1.0",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalameta" %% "munit" % "0.7.22"

)
