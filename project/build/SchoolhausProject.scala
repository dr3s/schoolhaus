import sbt._
import sbt_akka_bivy._


class SchoolhausProject(info: ProjectInfo) extends DefaultProject(info) with AkkaProject with assembly.AssemblyBuilder {

  // akka deps
  val akkaKernel = akkaModule("kernel")
  val akkaStm = akkaModule("stm")
  val akkaTypedActor = akkaModule("typed-actor")
  val akkaRemote = akkaModule("remote")
  val akkaHttp = akkaModule("http")
  val akkaVoldemort = akkaModule("persistence-voldemort")

  val upstartAkkaHome = "target/akka"
  val upstartExecUserGroup = ("andres", "staff")


}
