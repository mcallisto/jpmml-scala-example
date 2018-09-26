name := "jpmml-scala-example"

version := "1.2"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "org.jpmml" % "pmml-evaluator" % "1.4.3",
  "com.github.tototoshi" %% "scala-csv" % "1.3.5"
)