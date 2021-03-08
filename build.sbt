name := "fp-scala-quick-notes"

version := "0.1"

scalaVersion := "2.13.5"


version := "0.1"

scalaVersion := "2.13.5"




// cats effect
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.2.0" withSources() withJavadoc()


// scalaz
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.31" withSources() withJavadoc()
libraryDependencies += "org.scalaz" %% "scalaz-concurrent" % "7.2.31" withSources() withJavadoc()


// scalatest
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.5" withSources() withJavadoc()
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.5" % "test" withSources() withJavadoc()
libraryDependencies += "org.scalatest" %% "scalatest-freespec" % "3.2.5" % "test" withSources() withJavadoc()


scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds")


