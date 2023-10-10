val Dc10ScalaV = "0.3.0"
val MUnitV = "0.7.29"

inThisBuild(List(
  crossScalaVersions := Seq(scalaVersion.value),
  description := "Library for use with the `dc10-scala` code generator",
  organization := "com.julianpeeters",
  homepage := Some(url("https://github.com/julianpeeters/dc10-cats-effect")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "julianpeeters",
      "Julian Peeters",
      "julianpeeters@gmail.com",
      url("http://github.com/julianpeeters")
    )
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-Werror",
    "-source:future",
    "-Wunused:all",
    "-Wvalue-discard"
  ),
  scalaVersion := "3.3.1",
  versionScheme := Some("semver-spec"),
))

lazy val `dc10-cats-effect` = (project in file("."))
  .settings(
    name := "dc10-cats-effect",
    libraryDependencies ++= Seq(
      // main
      "com.julianpeeters" %% "dc10-scala" % Dc10ScalaV,
      // test
      "org.scalameta" %% "munit" % MUnitV % Test
    )
  )

lazy val docs = project.in(file("docs/gitignored"))
  .settings(
    mdocOut := `dc10-cats-effect`.base,
    mdocVariables := Map(
      "SCALA" -> crossScalaVersions.value.map(e => e.takeWhile(_ != '.')).mkString(", "),
      "VERSION" -> version.value.takeWhile(_ != '+'),
    )
  )
  .dependsOn(`dc10-cats-effect`)
  .enablePlugins(MdocPlugin)
  .enablePlugins(NoPublishPlugin)