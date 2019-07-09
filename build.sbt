val doobie                 = "0.7.0"
val quill                  = "3.3.0-SNAPSHOT"
val fs2                    = "1.0.5"
val logisticsCommon        = "2.0.36"
val newts                  = "0.2.0"
val postgres               = "42.2.5"
val scalaCacheCaffeine     = "0.9.4"
val flyway                 = "5.2.4"
val scalaLogging           = "3.7.2"
val monixVersion           = "3.0.0-RC3"
val catsEffectVersion      = "1.3.1"
val catsVersion            = "1.6.1"
val typesafeLoggingVersion = "3.9.0"
val logbackVersion         = "1.2.3"

scalaVersion in ThisBuild := "2.12.8"

updateOptions in ThisBuild := updateOptions.value.withLatestSnapshots(false)

resolvers in ThisBuild +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

lazy val taskDoobie = project.in(file("task-doobie")) settings (
  name := "task-doobie",
  libraryDependencies ++= Seq(
    "org.tpolecat"               %% "doobie-specs2"   % doobie % Test,
    "org.tpolecat"               %% "doobie-core"     % doobie,
    "org.tpolecat"               %% "doobie-postgres" % doobie,
    "org.tpolecat"               %% "doobie-hikari"   % doobie,
    "org.tpolecat"               %% "doobie-postgres" % doobie,
    "com.zaxxer"                 % "HikariCP"         % "3.3.1",
    "co.fs2"                     %% "fs2-core"        % fs2,
    "org.postgresql"             % "postgresql"       % postgres,
    "org.flywaydb"               % "flyway-core"      % flyway,
    "io.monix"                   %% "monix"           % monixVersion,
    "org.typelevel"              %% "cats-effect"     % catsEffectVersion,
    "org.typelevel"              %% "cats-core"       % catsVersion,
    "org.typelevel"              %% "cats-free"       % catsVersion,
    "com.typesafe.scala-logging" %% "scala-logging"   % typesafeLoggingVersion,
    "ch.qos.logback"             % "logback-classic"  % logbackVersion,
    "ch.qos.logback"             % "logback-core"     % logbackVersion
  )
)

lazy val taskQuill = project.in(file("task-quill")) settings (
  name := "task-quill",
  libraryDependencies ++= Seq(
    "io.getquill"                %% "quill-jdbc-monix" % quill,
    "com.zaxxer"                 % "HikariCP"          % "3.3.1",
    "co.fs2"                     %% "fs2-core"         % fs2,
    "org.postgresql"             % "postgresql"        % postgres,
    "org.flywaydb"               % "flyway-core"       % flyway,
    "io.monix"                   %% "monix"            % monixVersion,
    "org.typelevel"              %% "cats-effect"      % catsEffectVersion,
    "org.typelevel"              %% "cats-core"        % catsVersion,
    "org.typelevel"              %% "cats-free"        % catsVersion,
    "com.typesafe.scala-logging" %% "scala-logging"    % typesafeLoggingVersion,
    "ch.qos.logback"             % "logback-classic"   % logbackVersion,
    "ch.qos.logback"             % "logback-core"      % logbackVersion
  )
)

fork in ThisBuild := true

javaOptions in run in ThisBuild ++= Seq(
  "-XX:+UseConcMarkSweepGC",
  "-XX:+CMSParallelRemarkEnabled",
  "-XX:+ScavengeBeforeFullGC",
  "-XX:+CMSScavengeBeforeRemark",
  "-XX:+UnlockExperimentalVMOptions",
  "-XX:+UseCGroupMemoryLimitForHeap",
  "-Xms64M",
  "-Xmx128M"
)
