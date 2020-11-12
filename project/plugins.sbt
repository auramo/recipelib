resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("org.scalatra.sbt" % "scalatra-sbt" % "0.5.0")

addSbtPlugin("com.earldouglas"  % "xsbt-web-plugin" % "4.2.1")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.0")
