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
    for (i <- 1 to numberOfUser ) {
      if (i > 0.80 * numberOfUser)
        activityRate = 100 /*Heavy Active users */
      else if (i > 0.30 * numberOfUser && i < 0.80 * numberOfUser)
        activityRate = 50 /*Medium Users --> Passive plus Active */
      else if (i < 0.30 * numberOfUser)
        activityRate = 10 /*Passive Users */
      system.actorOf(Props(new User(i, activityRate, system)), name = "User" + i);
    }
    log.info("\n\n***********    Users Created on Client  ***********\n\n")
    readLine()

    import system.dispatcher

    log.info("\n\n ***********   Registering Few Users on Server Starts *********** \n\n")

    for (i <- 1 to numberOfUser) {
      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
        userRequestRegister_Put(i, "Name" + i, "Hi" + i + "@gmail.com"))
      Thread.sleep(100)
    }
    log.info("\n\n ***********    Registering Few Users on Server Ends *********** \n\n")
    //readLine()
    Thread.sleep(2000)
    log.info("\n\n ***********   GetBio for Users on Server Starts *********** \n\n")
    for (i <- 1 to numberOfUser) {
      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
        userRequestGetBio_Get(i))
      Thread.sleep(200)
    }
    Thread.sleep(1000)
    log.info("\n\n ***********   GetBio for Users on Server Ends *********** \n\n")

    log.info("\n\n ***********   Posting On wall for Users on Server Starts *********** \n\n")

    for (i <- 1 to numberOfUser) {
      system.scheduler.scheduleOnce(10 * i millisecond, context.actorFor("../User" + i),
        userRequestWallMessageUpdate_Post(s"This is $i th Post of the user "))
      system.scheduler.scheduleOnce(10 * i millisecond, context.actorFor("../User" + i),
        userRequestWallMessageUpdate_Post(s"This is Second Post of the user "))
      Thread.sleep(300)
    }

    log.info("\n\n ***********   Posting On wall for Users on Server Ends *********** \n\n")
    Thread.sleep(1000)

    log.info("\n\n ***********   Getting All posts of Users Starts*********** \n\n")

    for (i <- 1 to numberOfUser) {
      system.scheduler.scheduleOnce(10 * i millisecond, context.actorFor("../User" + i),
        userRequestGetAllPosts_Get(i) )
      Thread.sleep(500)
    }
    log.info("\n\n ***********   Getting All posts of Users Ends*********** \n\n")
    Thread.sleep(1200)

    log.info("\n\n ***********  Sending Friend Request of Users Starts*********** \n\n")

    for (i <- 1 to numberOfUser) {
      system.scheduler.scheduleOnce(10  millisecond, context.actorFor("../User" + i),
        userRequestFriendRequest_Post(i, i+1, "Hi I wanna be Friends with you! :) ") )
      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
        userRequestFriendRequest_Post(i, i+2, "Hi I wanna be Friends with you! :) ") )
      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
        userRequestFriendRequest_Post(i, i+3, "Hi I wanna be Friends with you! :) ") )
      Thread.sleep(500)

    }
    log.info("\n\n ***********   Sending Friend Request of Users end *********** \n\n")
    Thread.sleep(1000)

    log.info("\n\n ***********   Getting FriendsList for user Starts *********** \n\n")

    for (i <- 1 to numberOfUser) {
      system.scheduler.scheduleOnce(150 millisecond, context.actorFor("../User" + i),
        userRequestGetFriendList_Get() )
      Thread.sleep(600)
    }
    log.info("\n\n ***********   Getting FriendsList for user Ends *********** \n\n")
//    readLine()
    Thread.sleep(1000)

    log.info("\n\n ***********   Getting NewsFeed for user Starts *********** \n\n")

    for (i <- 1 to 5) {
      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
        userRequestGetNewsFeed() )
      Thread.sleep(800)
    }
    log.info("\n\n ***********    Getting NewsFeed for user  Ends *********** \n\n")
    Thread.sleep(800)

    log.info("\n\n ***********   Creating 10 pages Starts *********** \n\n")
    val numberOfpages = 8
    for (i <- 1 to numberOfpages - 1) {
      system.actorOf(Props(new Pages(i , 5, system )), name = "Page" + i)
      Thread.sleep(200)

    }
    log.info("\n\n ***********   Creating Page Ends *********** \n\n")

    log.info("\n\n ***********   Registering Pages Starts*********** \n\n")

    for (i <- 1 to numberOfpages) {
      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/Page" + i),
        PageRequestRegister_Put(i, s" About of the Page $i", s"General Info Of The Page $i ") )
      Thread.sleep(300)
    }
    log.info("\n\n ***********   Registering Pages Ends*********** \n\n")
   Thread.sleep(1000)

    log.info("\n\n ***********   Get Pages Bio Starts*********** \n\n")

    for (i <- 1 to numberOfpages) {
      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/Page" + i),
        PageRequestGetBio_Get(i) )
      Thread.sleep(300)

    }
    log.info("\n\n ***********   Get Pages Bio Ends*********** \n\n")



    /*No point in all users Posting to similar pages Only few uses do the posting */

    log.info("\n\n ***********   Posting on Page by Users Starts*********** \n\n")

    for (i <- 1 to numberOfpages) {
      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/User" + 1),
        userRequestPostOnAPage(i, 1, "This is First Post of Page from user 1 ") )
      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/User" + 2),
        userRequestPostOnAPage(i, 2, "This is First Post of Page from user 2") )
      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/User" + 3),
        userRequestPostOnAPage(i, 3, "This is First Post of Page from user 1 ") )
      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/User" + 4),
        userRequestPostOnAPage(i, 4, "This is First Post of Page from user 2") )
      Thread.sleep(500)
    }
    log.info("\n\n ***********  Posting on Page by Users  Ends*********** \n\n")

    Thread.sleep(1000)

    log.info("\n\n ***********   Getting Page Feeds by Users Starts*********** \n\n")
    for (i <- 1 to numberOfpages) {
      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/User" + 1),
        userRequestPageFeed(i) )
      Thread.sleep(800)
    }
    log.info("\n\n *********** Getting Page Feeds by Users Ends*********** \n\n")


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
    //Use the system's dispatcher as ExecutionContext
    import system.dispatcher

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

    log.info("\n\n*************************  Registering the users Starts ********************\n\n")
    for (i <- 1 to numberOfUser) {

      system.scheduler.scheduleOnce(100 * i millisecond, context.actorFor("../User" + i),
        userRequestRegister_Put(i, "Name" + i, "Hi" + i + "@gmail.com"))
    }
    log.info("\n\n*************************  Registering the users Ends ********************\n\n")
    readLine()

    log.info("\n\n*************************  GetBio the users Starts ********************\n\n")
    for (i <- 1 to numberOfUser) {

      system.scheduler.scheduleOnce(30 * i millisecond, context.actorFor("../User" + i),
        userRequestGetBio_Get(i))

    }

    log.info("\n\n*************************  GetBio the users Ends ********************\n\n")
    readLine()

    log.info("\n\n*************************  Posting by users Starts ********************\n\n")
    for (i <- 1 to numberOfUser) {
      system.scheduler.scheduleOnce(40 * i millisecond, context.actorFor("../User" + i),
        userRequestWallMessageUpdate_Post(s"This is $i th Post of the user "))
    }
    log.info("\n\n*************************  Posting by users Ends ********************\n\n")
    readLine()

    log.info("\n\n*************************  Friend Request Sending by users Starts ********************\n\n")

    for (i <- 1 to numberOfUser) {
      system.scheduler.scheduleOnce(100 millisecond, context.actorFor("../User" + i),
        userRequestFriendRequest_Post(i, i + 1, "Hi I wanna be Friends with you! :) "))
    }
    log.info("\n\n*************************   Friend Request Sending by users Ends ********************\n\n")

  }

  def modelThree(numberOfUser: Int, modelNumber: Int, system: ActorSystem): Unit = {
    val userArray = ofDim[ActorRef](numberOfUser)
    for (i <- 1 to numberOfUser - 1) {
      userArray(i) = system.actorOf(Props[User], name = "User" + i);
    }
    log.info("After for loop    ***********")
  }

}