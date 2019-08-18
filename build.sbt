name := "rateLimiterScala"
 
version := "1.0" 
      
lazy val `ratelimiterscala` = (project in file(".")).enablePlugins(PlayScala)
  
scalaVersion := "2.12.2"

libraryDependencies ++= Seq(guice)
