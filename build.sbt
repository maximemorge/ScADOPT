name := "ScADOPT"

version := "0.1"

mainClass in (Compile,run) := Some("org.scadopt.util.Main")
mainClass in assembly := Some("org.scadopt.util.Main")

trapExit := false

resolvers += "Artifactory-UCL" at "http://artifactory.info.ucl.ac.be/artifactory/libs-snapshot-local/"
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.20",
  "com.typesafe.akka" %% "akka-remote" % "2.5.20"
)


