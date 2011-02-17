import sbt._
import sbt.CompileOrder._


class SchoolhausProject(info: ProjectInfo) extends DefaultProject(info) with AkkaProject {

  // akka deps
  val akkaStm = akkaModule("stm")
    val akkaTypedActor = akkaModule("typed-actor")
    val akkaRemote = akkaModule("remote")
    val akkaHttp = akkaModule("http")
    val akkaVoldemort = akkaModule("persistence-voldemort")


  }
