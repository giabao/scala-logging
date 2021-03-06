import com.typesafe.sbt.pgp._
import com.typesafe.sbt.SbtScalariform._
import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleaseStateTransformations._
import scalariform.formatter.preferences._

object Common {

  val settings =
    publishSettings ++
    releaseSettings ++
    scalariformSettings ++
    List(
      // Core settings
      organization := "com.typesafe.scala-logging",
      scalaVersion := Version.scala,
      crossScalaVersions := List(scalaVersion.value),
      scalacOptions ++= List(
        "-unchecked",
        "-deprecation",
        "-language:_",
        "-target:jvm-1.6",
        "-encoding", "UTF-8"
      ),
      unmanagedSourceDirectories in Compile := List((scalaSource in Compile).value),
      unmanagedSourceDirectories in Test := List((scalaSource in Test).value),
      // Release settings
      ReleaseKeys.versionBump := sbtrelease.Version.Bump.Minor,
      ReleaseKeys.releaseProcess := List(
        checkSnapshotDependencies,
        inquireVersions,
        runClean,
        runTest,
        setReleaseVersion,
        commitReleaseVersion,
        tagRelease,
        publishArtifacts.copy(action = publishSignedAction),
        setNextVersion,
        commitNextVersion,
        pushChanges
      ),
      // Scalariform settings
      ScalariformKeys.preferences := ScalariformKeys.preferences.value
        .setPreference(AlignSingleLineCaseStatements, true)
        .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
        .setPreference(DoubleIndentClassDeclaration, true)
        .setPreference(PreserveDanglingCloseParenthesis, true)
    )

  def publishSettings =
    List(
      licenses := Seq("Apache 2.0 License" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
      homepage := Some(url("https://github.com/typesafehub/scala-logging")),
      publishTo <<= isSnapshot(isSnapshot => Some(if (isSnapshot) Opts.resolver.sonatypeSnapshots else Opts.resolver.sonatypeStaging)),
      publishArtifact in Test := false,
      pomIncludeRepository := (_ => false),
      pomExtra :=
        <scm>
          <url>https://github.com/typesafehub/scala-logging</url>
          <connection>scm:git:git://github.com/typesafehub/scala-logging.git</connection>
        </scm>
        <developers>
          <developer>
            <id>hseeberger</id>
            <name>Heiko Seeberger</name>
            <url>http://heikoseeberger.de</url>
          </developer>
          <developer>
            <id>analytically</id>
            <name>Mathias Bogaert</name>
            <url>http://twitter.com/analytically</url>
          </developer>
        </developers>
    )

  def publishSignedAction(state: State) = {
    val extracted = Project.extract(state)
    val ref = extracted.get(thisProjectRef)
    extracted.runAggregated(PgpKeys.publishSigned in Global in ref, state)
  }
}
