package facebookClient

import akka.actor
import akka.actor.{Props, ActorRef, ActorSystem, Actor}
import akka.event.Logging
import facebookClient.PageProtocol.{PageRequestGetBio_Get, PageRequestRegister_Put}
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
    readLine()
    log.info("***********    Users Created on Client  ***********")
    import system.dispatcher

    log.info("\n\n ***********   4  Users Registered on Server Starts *********** \n\n")

    val actorRefUser = context.actorSelection("../User" + 1)
    actorRefUser ! userRequestRegister_Put(1, "name" + 1, "Hi 1 @gmail.com")

    val actorRefUser2 = context.actorSelection("../User" + 2)
    actorRefUser2 ! userRequestRegister_Put(2, "name" + 2, "Hi 2 @gmail.com")

    val actorRefUser3 = context.actorSelection("../User" + 3)
    actorRefUser3 ! userRequestRegister_Put(3, "name" + 3, "Hi 3 @gmail.com")
    val actorRefUser4 = context.actorSelection("../User" + 4)
    actorRefUser4 ! userRequestRegister_Put(4, "name" + 4, "Hi 4 @gmail.com")
    log.info("\n\n ***********   4  Users Registered on Server Ends *********** \n\n")
    //readLine()
    system.scheduler.scheduleOnce(40 millisecond, context.actorFor("../User" + 1),
      userRequestGetBio_Get(1))

    actorRefUser ! userRequestWallMessageUpdate_Post("This is the first post for User " + 1)

    actorRefUser ! userRequestWallMessageUpdate_Post("This is the Second post for User " + 1)

    actorRefUser ! userRequestGetAllPosts_Get(1)
    //readLine()

    log.info("\n\n ***********    Users 1 Sending Freind request to others Users Start*********** \n\n")
    actorRefUser ! userRequestFriendRequest_Post(1 , 2 , "Hi I wanna be Friends with you! :) ")
    actorRefUser ! userRequestFriendRequest_Post(1 , 3 , "Hi I wanna be Friends with you! :) ")
    actorRefUser ! userRequestFriendRequest_Post(1 , 4 , "Hi I wanna be Friends with you! :) ")
    actorRefUser2 ! userRequestFriendRequest_Post(2 , 3 , "Hi I wanna be Friends with you! :) ")
    actorRefUser2 ! userRequestFriendRequest_Post(2 , 4 , "Hi I wanna be Friends with you! :) ")
    log.info("\n\n ***********    Users 1,2 Sending Freind request to others Users End *********** \n\n")
    readLine()
    log.info("\n\n ***********    Users Getting back there friendList Start*********** \n\n")
    actorRefUser !  userRequestGetFriendList_Get()
    actorRefUser2 ! userRequestGetFriendList_Get()

//    actorRefUser3 ! userRequestGetFriendList_Get()
    log.info("\n\n ***********    Users Getting back there friendList  End *********** \n\n")
    actorRefUser2 ! userRequestWallMessageUpdate_Post("This is the first post for User " )
    actorRefUser2 ! userRequestWallMessageUpdate_Post("This is the Second post for User ")
    actorRefUser2 ! userRequestWallMessageUpdate_Post("This is the third post for User " )
    actorRefUser2 ! userRequestWallMessageUpdate_Post("This is the Fourth post for User ")

    actorRefUser3 ! userRequestWallMessageUpdate_Post("This is the first post for User " )
    actorRefUser3 ! userRequestWallMessageUpdate_Post("This is the Second post for User ")
    actorRefUser3 ! userRequestWallMessageUpdate_Post("This is the third post for User " )
    actorRefUser3 ! userRequestWallMessageUpdate_Post("This is the Fourth post for User ")

    readLine()
    actorRefUser ! userRequestGetNewsFeed()

    //    var temp = system.actorOf(Props(new Pages(1 , 5, system )), name = "Page" + 1);
//
//    val actorRefPage = context.actorFor("akka://Facebook-Client/user/Page" + 1)
//    readLine()
//    actorRefPage ! PageRequestRegister_Put(1, " About of the Page 1 ", "General Info Of The Page 1 ")
//    readLine()
////    actorRefPage ! PageRequestGetBio_Get(1)
////    readLine()
//
//    actorRefUser ! userRequestPostOnAPage(1, 1, "This is First Post of Page from user 1 ")
//
//    readLine()
//
//    actorRefUser ! userRequestPostOnAPage(1, 1, "This is Second Post of Page from user 1 ")
//
//    readLine()
//
//    actorRefUser ! userRequestPostOnAPage(1, 1, "This is Third Post of Page from user 1 ")
//
//    actorRefUser ! userRequestPageFeed(1)



    //    readLine()
    //
    //    actorRefUser ! userRequestGetAllPostsOnAPage_Get(1 )


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