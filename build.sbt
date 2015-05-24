val scalatra = "org.scalatra" %% "scalatra" % "2.3.1"
val scalatraAuth = "org.scalatra" %% "scalatra-auth" % "2.3.1"
val mongo = "org.mongodb" %% "casbah" % "2.8.1"
val specs2 = "org.scalatra" %% "scalatra-specs2" % "2.3.1" % "test"
val logback = "ch.qos.logback" % "logback-classic" % "1.0.6" % "compile"
val json4sNative = "org.json4s" %% "json4s-native" % "3.2.11"
val json4sJackson = "org.json4s" %% "json4s-jackson" % "3.2.11"
val jettylib = "org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "compile"
val servletStuff = "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "compile;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar"))

jetty()

enablePlugins(JavaAppPackaging)

lazy val root = (project in file(".")).
  settings(
    name := "recipelib",
    version := "0.2",
    scalaVersion := "2.11.6",
    mainClass in (Compile) := Some("JettyLauncher"),
    libraryDependencies += scalatra,
    libraryDependencies += scalatraAuth,
    libraryDependencies += mongo,
    libraryDependencies += specs2,
    libraryDependencies += logback,
    libraryDependencies += json4sNative,
    libraryDependencies += json4sJackson,
    libraryDependencies += jettylib,
    libraryDependencies += servletStuff
  )
