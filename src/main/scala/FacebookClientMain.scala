package facebookClient

import scala.util.{Success, Failure}
import akka.actor.{Props, ActorRef, ActorSystem}
import akka.event.Logging
import Array._

object FacebookClientMain extends App {

  if (args.length > 1) {

    println("Starting client with ClientID = " + args(1))
    var totalUsers: Int = args(0).toInt
    var modelNumber: Int = args(1).toInt

    println("numUsers = " + modelNumber)

    // we need an ActorSystem to host our application in
    implicit val system = ActorSystem("Facebook-Client")
    val log = Logging(system, getClass)
    val clientCoorinatorService = system.actorOf(Props(new ClientCoordinator(totalUsers, modelNumber, system)), name = "ClientCoorinator")
    /*Ability to switch the model number at run time ? */
    clientCoorinatorService ! start(totalUsers, modelNumber, system)

  } else {
    println("Usage : Client <Number of Users> <Model-Number>")
    println("Model-Number range : <1-3>")
    println("1.Normal Mode")
    println("2.Medium Mode")
    println("3.High Mode")
  }

}


