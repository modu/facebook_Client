package facebookClient

import akka.actor
import akka.actor.{Props, ActorRef, ActorSystem, Actor}
import akka.event.Logging
import facebookClient.protocol._
import scala.Array._
import scala.concurrent.duration._

case class start(totalUsers: Int, modelNumber: Int, system: ActorSystem)

case class stop()

class ClientCoordinator(totalUsers: Int, modelNumber: Int, system: ActorSystem) extends Actor {
  val log = Logging(system, getClass)

  def receive = {
    case start(totalUsers, modelNumber, system) => {
      if (modelNumber == 1)
        modelOne(totalUsers, modelNumber, system)
      else if (modelNumber == 2)
        modelTwo(totalUsers, modelNumber, system)
      else if (modelNumber == 3)
        modelThree(totalUsers, modelNumber, system)
    }
    case stop() => {
      /*TODO :  Class all the actors of model or switch to different model */
    }

  }

  def modelOne(numberOfUser: Int, modelNumber: Int, system: ActorSystem): Unit = {
    /*Create different types of User */
    var activityRate: Int = 0
    val userArray = ofDim[ActorRef](numberOfUser)

    for (i <- 1 to numberOfUser - 1) {
      if (i > 0.80 * numberOfUser)
        activityRate = 100 /*Heavy Active users */
      else if (i > 0.30 * numberOfUser && i < 0.80 * numberOfUser)
        activityRate = 50 /*Medium Users --> Passive plus Active */
      else if (i < 0.30 * numberOfUser)
        activityRate = 10 /*Passive Users */
      userArray(i) = system.actorOf(Props(new User(i, activityRate, system)), name = "User" + i);
    }
    log.info("***********    Users Created on Client  ***********")
    import system.dispatcher
    val actorRefUser = context.actorFor("../User" + 1)
    readLine()
    actorRefUser ! userRequestRegister_Put(1, "name" + 1, "Hi 1 @gmail.com")
    readLine()
    system.scheduler.scheduleOnce(40 millisecond, context.actorFor("../User" + 1),
      userRequestGetBio_Get(1))
    readLine()

    actorRefUser ! userRequestWallMessageUpdate_Post(1, "This is the first post for User " + 1)
    readLine()

    actorRefUser ! userRequestWallMessageUpdate_Post(1, "This is the Second post for User " + 1)
    readLine()

    actorRefUser ! userRequestGetAllPosts_Get(1)
    readLine()
    //Use the system's dispatcher as ExecutionContext
    import system.dispatcher

    //var temp = context.actorSelection("../User"+1)
    /*TODO: Figure out ways to parallel -> start registering and posting and sending friend Requests*/
    //    log.info("*************************  Registering the users Starts ********************")
    //    for (i <- 1 to numberOfUser) {
    //
    //      system.scheduler.scheduleOnce(20 millisecond, context.actorFor("../User" + i),
    //        userRequestRegister_Put(i, "Name" + i, "Hi" + i + "@gmail.com"))
    //    }
    //    log.info("*************************  Registering the users Ends ********************")
    //    log.info("*************************  GetBio the users Starts ********************")
    //    for (i <- 1 to numberOfUser) {
    //
    //      system.scheduler.scheduleOnce(100 millisecond, context.actorFor("User" + i),
    //        userRequestGetBio_Get(i))
    //
    //    }
    //    log.info("*************************  GetBio the users Ends ********************")

    //
    //    log.info("*************************  Posting by users Starts ********************")
    //    for (i <- 1 to numberOfUser) {
    //      system.scheduler.scheduleOnce(100 millisecond, context.actorFor("../User" + i),
    //        userRequestWallMessageUpdate_Post(i, "This is Post of the user " + i))
    //    }
    //    log.info("*************************  Posting by users Ends ********************")
    //
    //    log.info("*************************  Friend Request Sending by users Starts ********************")
    //
    //    for (i <- 1 to numberOfUser) {
    //      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
    //        userRequestFriendRequest_Post("Hi I would Like to be your friend"))
    //    }
    //    log.info("*************************   Friend Request Sending by users Ends ********************")

    /*
    ToDo: Create pages by some users and other subscribe/follow them and gets updates
    */

    /*
    * Todo: Randomly each user will start asking what his friends are doing
    */

  }

  def modelTwo(numberOfUser: Int, modelNumber: Int, system: ActorSystem): Unit = {
    var activityRate: Int = 0
    val userArray = ofDim[ActorRef](numberOfUser)
    for (i <- 1 to numberOfUser - 1) {
      if (i > 0.80 * numberOfUser)
        activityRate = 100 /*Heavy Active users */
      else if (i > 0.30 * numberOfUser && i < 0.80 * numberOfUser)
        activityRate = 50 /*Medium Users --> Passive plus Active */
      else if (i < 0.30 * numberOfUser)
        activityRate = 10 /*Passive Users */
      userArray(i) = system.actorOf(Props(new User(i, activityRate, system)), name = "User" + i);
    }
    log.info("After for loop    ***********")
  }

  def modelThree(numberOfUser: Int, modelNumber: Int, system: ActorSystem): Unit = {
    val userArray = ofDim[ActorRef](numberOfUser)
    for (i <- 1 to numberOfUser - 1) {
      userArray(i) = system.actorOf(Props[User], name = "User" + i);
    }
    log.info("After for loop    ***********")
  }

}