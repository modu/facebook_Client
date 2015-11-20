import _root_.spray.revolver.RevolverPlugin.Revolver

name := "facebook_Client"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "spray repo" at "http://repo.spray.io/"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")


resolvers ++= Seq(
  "Scala Tools Repo Releases" at "http://scala-tools.org/repo-releases",
  "Typesafe Repo Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "spray" at "http://repo.spray.io/"
)

//resolvers ++= Seq(
//  "spray repo" at "http://repo.spray.io/"
//)

Revolver.settings: Seq[sbt.Def.Setting[_]]

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "io.spray"            %%  "spray-client"  % sprayV,
    "io.spray"            %%  "spray-json"    % "1.3.2",
    "com.typesafe"        % "config" % "1.2.1"
  )
}