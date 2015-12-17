package facebookClient

import java.security.PublicKey
import java.util.Base64

import akka.actor.{Props, ActorRef, ActorSystem, Actor}
import akka.event.Logging
import facebookClient.PageProtocol.{userRequestLikeAPage, PageRequestGetBio_Get, PageRequestRegister_Put}
import facebookClient.protocol._
import scala.Array._
import scala.concurrent.duration._

case class start(totalUsers: Int, modelNumber: Int, system: ActorSystem)

case class stop()

case class setPublicKey(publicKey: String)

class ClientCoordinator(totalUsers: Int, modelNumber: Int, system: ActorSystem) extends Actor {
  val log = Logging(system, getClass)
  var serverPublicKey: String = ""

  def receive = {
    case start(totalUsers, modelNumber, system) => {
      test(system)
      //      if (modelNumber == 1)
      //        modelOne(totalUsers, modelNumber, system)
      //      else if (modelNumber == 2)
      //        modelTwo(totalUsers, modelNumber, system)
      //      else if (modelNumber == 3)
      //        modelThree(totalUsers, modelNumber, system)
    }
    case stop() => {
      /*TODO :  Class all the actors of model or switch to different model */
    }
    case setPublicKey(publicKey: String) => {
      serverPublicKey = publicKey
      log.info("Public Key of server is set ")
    }

  }

  def test(system: ActorSystem): Unit = {
    import system.dispatcher
    var kp = CryptoUtil.generateKeyPair()

    val temp = " HI thi"
    println(temp);

    /*input : secretKey , iv , and message
    * output : String*/
    val encryptedText = CryptoUtil.encryptAES(CryptoUtil.keyToSpec(temp), "iv", "TOBEENcrypteddafdfs")

    /*input : secretKey( which is to be encrypted) , publicKey
    * output : EncryptedKey Array[Byte]*/
    val byteArrayEncrypted = CryptoUtil.encryptRSAKey(CryptoUtil.keyToSpec(temp), kp.getPublic)

    /*input :  EncryptedKey Array[Byte] , privateKey
    * output : secretKey( Which was earlier encrypted)*/
    val decryptedKeySpec = CryptoUtil.decryptRSAKey(byteArrayEncrypted, kp.getPrivate)

    /*input : secretKey , iv , and EncryptedMessage
    * output : String*/
    println(CryptoUtil.decryptAES(decryptedKeySpec, "iv", encryptedText))

    system.actorOf(Props(new User(1, 3, system)), name = "User" + 1);
    system.actorOf(Props(new User(2, 4, system)), name = "User" + 2);
    system.actorOf(Props(new User(3, 3, system)), name = "User" + 3);
    system.actorOf(Props(new User(4, 4, system)), name = "User" + 4);

    readLine()
    system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + 1),
      userRequestGetServerPublicKey())
    system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + 1),
      userRequestRegister_Put(1, "Name" + 1, "Hi" + 1 + "@gmail.com"))
    system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + 2),
      userRequestRegister_Put(2, "Name" + 2, "Hi" + 2 + "@gmail.com"))
    system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + 3),
      userRequestRegister_Put(3, "Name" + 3, "Hi" + 3 + "@gmail.com"))
    system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + 4),
      userRequestRegister_Put(4, "Name" + 4, "Hi" + 4 + "@gmail.com"))

    system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + 1),
      userRequestFriendRequest_Post(1, 2, "Hi I wanna be Friends with you! :) "))
    system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + 1),
      userRequestFriendRequest_Post(1, 3, "Hi I wanna be Friends with you! :) "))
    system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + 1),
      userRequestFriendRequest_Post(1, 4, "Hi I wanna be Friends with you! :) "))
    system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + 2),
      userRequestFriendRequest_Post(2, 3, "Hi I wanna be Friends with you! :) "))
    system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + 2),
      userRequestFriendRequest_Post(2, 4, "Hi I wanna be Friends with you! :) "))
    readLine()

    system.scheduler.scheduleOnce(150 millisecond, context.actorFor("../User" + 1),
      userRequestGetFriendList_Get())

    system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + 1),
      userRequestWallMessageUpdate_Post(s"This is  1 th Post of the user "))
    readLine()
    system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + 1),
      userRequestWallMessageUpdate_Post(s"This is  2nd Post of the user "))
    readLine()

    system.scheduler.scheduleOnce(150 millisecond, context.actorFor("../User" + 3),
      userRequestGetFriendList_Get())
    readLine()

    system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + 3),
      userRequestWallMessageUpdate_Post(s"This is  1st Post of the user "))

    system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + 2),
      userRequestGetNewsFeed())

  }

//  def modelOne(numberOfUser: Int, modelNumber: Int, system: ActorSystem): Unit = {
//    /*Create different types of User */
//    var activityRate: Int = 0
//    for (i <- 1 to numberOfUser) {
//      if (i > 0.80 * numberOfUser)
//        activityRate = 100 /*Heavy Active users */
//      else if (i > 0.30 * numberOfUser && i < 0.80 * numberOfUser)
//        activityRate = 50 /*Medium Users --> Passive plus Active */
//      else if (i < 0.30 * numberOfUser)
//        activityRate = 10 /*Passive Users */
//      system.actorOf(Props(new User(i, activityRate, system)), name = "User" + i);
//    }
//    log.info("\n\n***********    Users Created on Client  ***********\n\n")
//    //readLine()
//
//    import system.dispatcher
//
//    log.info("\n\n ***********   Registering Few Users on Server Starts *********** \n\n")
//
//    for (i <- 1 to numberOfUser) {
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestRegister_Put(i, "Name" + i, "Hi" + i + "@gmail.com"))
//      Thread.sleep(10)
//    }
//    log.info("\n\n ***********    Registering Few Users on Server Ends *********** \n\n")
//    //readLine()
//    Thread.sleep(1000)
//    log.info("\n\n ***********   GetBio for Users on Server Starts *********** \n\n")
//    for (i <- 1 to numberOfUser) {
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestGetBio_Get(i))
//      Thread.sleep(150)
//    }
//    Thread.sleep(1000)
//    log.info("\n\n ***********   GetBio for Users on Server Ends *********** \n\n")
//
//    log.info("\n\n ***********   Posting On wall for Users on Server Starts *********** \n\n")
//
//    for (i <- 1 to numberOfUser) {
//      system.scheduler.scheduleOnce(10 * i millisecond, context.actorFor("../User" + i),
//        userRequestWallMessageUpdate_Post(s"This is $i th Post of the user "))
//      system.scheduler.scheduleOnce(10 * i millisecond, context.actorFor("../User" + i),
//        userRequestWallMessageUpdate_Post(s"This is Second Post of the user "))
//      Thread.sleep(150)
//    }
//
//    log.info("\n\n ***********   Posting On wall for Users on Server Ends *********** \n\n")
//    Thread.sleep(1000)
//
//    log.info("\n\n ***********   Getting All posts of Users Starts*********** \n\n")
//
//    for (i <- 1 to numberOfUser) {
//      system.scheduler.scheduleOnce(10 * i millisecond, context.actorFor("../User" + i),
//        userRequestGetAllPosts_Get(i))
//      Thread.sleep(500)
//    }
//    log.info("\n\n ***********   Getting All posts of Users Ends*********** \n\n")
//    Thread.sleep(1200)
//
//    log.info("\n\n ***********  Sending Friend Request of Users Starts*********** \n\n")
//
//    for (i <- 1 to numberOfUser) {
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestFriendRequest_Post(i, i + 1, "Hi I wanna be Friends with you! :) "))
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestFriendRequest_Post(i, i + 2, "Hi I wanna be Friends with you! :) "))
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestFriendRequest_Post(i, i + 3, "Hi I wanna be Friends with you! :) "))
//      Thread.sleep(500)
//
//    }
//    log.info("\n\n ***********   Sending Friend Request of Users end *********** \n\n")
//    Thread.sleep(1000)
//
//    log.info("\n\n ***********   Getting FriendsList for user Starts *********** \n\n")
//
//    for (i <- 1 to numberOfUser) {
//      system.scheduler.scheduleOnce(150 millisecond, context.actorFor("../User" + i),
//        userRequestGetFriendList_Get())
//      Thread.sleep(600)
//    }
//    log.info("\n\n ***********   Getting FriendsList for user Ends *********** \n\n")
//    //    readLine()
//    Thread.sleep(1000)
//
//    log.info("\n\n ***********   Getting NewsFeed for user Starts *********** \n\n")
//
//    for (i <- 1 to 5) {
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestGetNewsFeed())
//      Thread.sleep(500)
//    }
//    log.info("\n\n ***********    Getting NewsFeed for user  Ends *********** \n\n")
//    Thread.sleep(800)
//
//    log.info("\n\n ***********   Creating 10 pages Starts *********** \n\n")
//    val numberOfpages = 8
//    for (i <- 1 to numberOfpages) {
//      system.actorOf(Props(new Pages(i, 5, system)), name = "Page" + i)
//      Thread.sleep(200)
//
//    }
//    log.info("\n\n ***********   Creating Page Ends *********** \n\n")
//
//    log.info("\n\n ***********   Registering Pages Starts*********** \n\n")
//
//    for (i <- 1 to numberOfpages) {
//      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/Page" + i),
//        PageRequestRegister_Put(i, s" About of the Page $i", s"General Info Of The Page $i "))
//      Thread.sleep(300)
//    }
//    log.info("\n\n ***********   Registering Pages Ends*********** \n\n")
//    Thread.sleep(1000)
//
//    log.info("\n\n ***********   Get Pages Bio Starts*********** \n\n")
//
//    for (i <- 1 to numberOfpages) {
//      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/Page" + i),
//        PageRequestGetBio_Get(i))
//      Thread.sleep(300)
//
//    }
//    log.info("\n\n ***********   Get Pages Bio Ends*********** \n\n")
//
//
//
//    /*No point in all users Posting to similar pages Only few uses do the posting */
//
//    log.info("\n\n ***********   Posting on Page by Users Starts*********** \n\n")
//
//    for (i <- 1 to numberOfpages) {
//      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/User" + 1),
//        userRequestPostOnAPage(i, 1, "This is First Post of Page from user 1 "))
//      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/User" + 2),
//        userRequestPostOnAPage(i, 2, "This is First Post of Page from user 2"))
//      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/User" + 3),
//        userRequestPostOnAPage(i, 3, "This is First Post of Page from user 1 "))
//      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/User" + 4),
//        userRequestPostOnAPage(i, 4, "This is First Post of Page from user 2"))
//      Thread.sleep(500)
//    }
//    log.info("\n\n ***********  Posting on Page by Users  Ends*********** \n\n")
//
//    Thread.sleep(1000)
//
//    log.info("\n\n ***********   Getting Page Feeds by Users Starts*********** \n\n")
//    for (i <- 1 to numberOfpages) {
//      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/User" + 1),
//        userRequestPageFeed(i))
//      Thread.sleep(800)
//    }
//    log.info("\n\n *********** Getting Page Feeds by Users Ends*********** \n\n")
//
//
//
//
//    log.info("\n\n ***********  Creating Albums and images *********** \n\n")
//    var temp = numberOfUser;
//    if (numberOfUser > 10)
//      temp = 10
//
//    for (i <- 1 to temp) {
//      system.scheduler.scheduleOnce(0 millisecond, context.actorFor("../User" + i),
//        userRequestCreateAlbum(i, "album" + i))
//      system.scheduler.scheduleOnce(100 millisecond, context.actorFor("../User" + i),
//        userRequestCreateImage(i, "album" + i, "profilepic" + i))
//      system.scheduler.scheduleOnce(150 millisecond, context.actorFor("../User" + i),
//        userRequestCreateImage(i, "album" + i, "coverpic" + i))
//      system.scheduler.scheduleOnce(200 millisecond, context.actorFor("../User" + i),
//        userRequestGetImage(i, "album" + i, "profilepic" + i))
//      system.scheduler.scheduleOnce(250 millisecond, context.actorFor("../User" + i),
//        userRequestGetAlbum(i, "album" + i))
//    }
//
//    context.actorSelection("../User1") ! userSendMessage(1, 5, "Hi from User1")
//    context.actorSelection("../User2") ! userSendMessage(2, 5, "Hi from User2")
//
//    context.actorSelection("../User3") ! userSendMessage(3, 5, "Hi from User3")
//    Thread.sleep(500)
//    context.actorSelection("../User5") ! userRequestGetMessage(5)
//
//    log.info("\n\n ***********  Creating Albums and images *********** \n\n")
//
//
//  }
//
//  def modelTwo(numberOfUser: Int, modelNumber: Int, system: ActorSystem): Unit = {
//    //Use the system's dispatcher as ExecutionContext
//    import system.dispatcher
//    /*Show number of Likes of a page Using Get Bio,
//    * Then next
//    * center user likes or visits that page
//    * others friends started  visiting and becomming member of that page
//    * */
//    log.info("\n\n ********************* Model Number 2 simulation Starts ******** \n\n")
//
//    for (i <- 1 to 40) {
//      system.actorOf(Props(new User(i, 100, system)), name = "User" + i)
//    }
//    for (i <- 1 to 40) {
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestRegister_Put(i, "Name" + i, "Hi" + i + "@gmail.com"))
//      Thread.sleep(10)
//    }
//
//    system.scheduler.scheduleOnce(150 millisecond, context.actorFor("akka://Facebook-Client/user/User" + 1),
//      userRequestPostOnAPage(10, 1, "Job and internship openings available "))
//    system.scheduler.scheduleOnce(150 millisecond, context.actorFor("akka://Facebook-Client/user/User" + 1),
//      userRequestPostOnAPage(10, 1, "Please apply before dead line "))
//    system.scheduler.scheduleOnce(150 millisecond, context.actorFor("akka://Facebook-Client/user/User" + 1),
//      userRequestPostOnAPage(10, 1, " Non Dev jobs are posted on below link. Please visit the site and apply. Thanks "))
//    system.scheduler.scheduleOnce(150 millisecond, context.actorFor("akka://Facebook-Client/user/User" + 1),
//      userRequestPostOnAPage(10, 1, " Please Apply before the deadLine of the Openenig"))
//    Thread.sleep(500)
//
//    /*Creating a local page to request from to server to get Bio and statistics of Page*/
//    system.actorOf(Props(new Pages(500, 100, system)), name = "Page" + 500)
//
//    system.scheduler.scheduleOnce(10 millisecond, context.actorFor("akka://Facebook-Client/user/Page" + 500),
//      PageRequestGetBio_Get(10))
//    log.info("\n\n ********************* Number of Likes Of Page before User with Higher Social capital visits the page******** \n\n")
//
//    for (i <- 5 to 15) {
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("akka://Facebook-Client/user/User" + i),
//        userRequestLikeAPage(10, i))
//      Thread.sleep(100)
//    }
//
//    log.info("\n\n ********************* Number of Likes Of Page after User with Higher Social capital visits the page******** \n\n")
//
//    system.scheduler.scheduleOnce(150 millisecond, context.actorFor("akka://Facebook-Client/user/Page" + 500),
//      PageRequestGetBio_Get(10))
//
//    log.info("\n\n ********************* Model Number 2 simulation complete ******** \n\n")
//
//  }
//
//  def modelThree(numberOfUser: Int, modelNumber: Int, system: ActorSystem): Unit = {
//    /*Create different types of User */
//    var activityRate: Int = 0
//    for (i <- 1 to numberOfUser) {
//      if (i > 0.80 * numberOfUser)
//        activityRate = 100 /*Heavy Active users */
//      else if (i > 0.30 * numberOfUser && i < 0.80 * numberOfUser)
//        activityRate = 50 /*Medium Users --> Passive plus Active */
//      else if (i < 0.30 * numberOfUser)
//        activityRate = 10 /*Passive Users */
//      system.actorOf(Props(new User(i, activityRate, system)), name = "User" + i);
//    }
//    log.info("\n\n***********    Users Created on Client  ***********\n\n")
//    //readLine()
//
//    import system.dispatcher
//
//    log.info("\n\n ***********   Registering Few Users on Server Starts *********** \n\n")
//
//    for (i <- 1 to numberOfUser) {
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestRegister_Put(i, "Name" + i, "Hi" + i + "@gmail.com"))
//      Thread.sleep(200)
//    }
//    log.info("\n\n ***********    Registering Few Users on Server Ends *********** \n\n")
//    //readLine()
//    Thread.sleep(1000)
//    log.info("\n\n ***********   GetBio for Users on Server Starts *********** \n\n")
//    for (i <- 1 to numberOfUser) {
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestGetBio_Get(i))
//      Thread.sleep(10)
//    }
//    Thread.sleep(1000)
//    log.info("\n\n ***********   GetBio for Users on Server Ends *********** \n\n")
//
//    log.info("\n\n ***********   Posting On wall for Users on Server Starts *********** \n\n")
//
//    for (i <- 1 to numberOfUser) {
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestWallMessageUpdate_Post(s"This is $i th Post of the user "))
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestWallMessageUpdate_Post(s"This is Second Post of the user "))
//      Thread.sleep(150)
//    }
//
//    log.info("\n\n ***********   Posting On wall for Users on Server Ends *********** \n\n")
//    Thread.sleep(1000)
//
//    log.info("\n\n ***********   Getting All posts of Users Starts*********** \n\n")
//
//    for (i <- 1 to numberOfUser) {
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestGetAllPosts_Get(i))
//      Thread.sleep(300)
//    }
//    log.info("\n\n ***********   Getting All posts of Users Ends*********** \n\n")
//    Thread.sleep(1200)
//
//    log.info("\n\n ***********  Sending Friend Request of Users Starts*********** \n\n")
//
//    for (i <- 1 to numberOfUser) {
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestFriendRequest_Post(i, i + 1, "Hi I wanna be Friends with you! :) "))
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestFriendRequest_Post(i, i + 2, "Hi I wanna be Friends with you! :) "))
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestFriendRequest_Post(i, i + 3, "Hi I wanna be Friends with you! :) "))
//      Thread.sleep(300)
//
//    }
//    log.info("\n\n ***********   Sending Friend Request of Users end *********** \n\n")
//    Thread.sleep(1000)
//
//    log.info("\n\n ***********   Getting FriendsList for user Starts *********** \n\n")
//
//    for (i <- 1 to numberOfUser) {
//      system.scheduler.scheduleOnce(150 millisecond, context.actorFor("../User" + i),
//        userRequestGetFriendList_Get())
//      Thread.sleep(600)
//    }
//    log.info("\n\n ***********   Getting FriendsList for user Ends *********** \n\n")
//    //    readLine()
//    Thread.sleep(1000)
//
//    log.info("\n\n ***********   Getting NewsFeed for user Starts *********** \n\n")
//
//    for (i <- 1 to 5) {
//      system.scheduler.scheduleOnce(10 millisecond, context.actorFor("../User" + i),
//        userRequestGetNewsFeed())
//      Thread.sleep(200)
//    }
//    log.info("\n\n ***********    Getting NewsFeed for user  Ends *********** \n\n")
//    Thread.sleep(800)
//
//    log.info("\n\n ***********   Creating 10 pages Starts *********** \n\n")
//    val numberOfpages = 8
//    for (i <- 1 to numberOfpages) {
//      system.actorOf(Props(new Pages(i, 5, system)), name = "Page" + i)
//      Thread.sleep(200)
//
//    }
//    log.info("\n\n ***********   Creating Page Ends *********** \n\n")
//
//    log.info("\n\n ***********   Registering Pages Starts*********** \n\n")
//
//    for (i <- 1 to numberOfpages) {
//      system.scheduler.scheduleOnce(150 millisecond, context.actorFor("akka://Facebook-Client/user/Page" + i),
//        PageRequestRegister_Put(i, s" About of the Page $i", s"General Info Of The Page $i "))
//      Thread.sleep(300)
//    }
//    log.info("\n\n ***********   Registering Pages Ends*********** \n\n")
//    Thread.sleep(1000)
//
//    log.info("\n\n ***********   Get Pages Bio Starts*********** \n\n")
//
//    for (i <- 1 to numberOfpages) {
//      system.scheduler.scheduleOnce(150 millisecond, context.actorFor("akka://Facebook-Client/user/Page" + i),
//        PageRequestGetBio_Get(i))
//      Thread.sleep(300)
//
//    }
//    log.info("\n\n ***********   Get Pages Bio Ends*********** \n\n")
//
//
//
//    /*No point in all users Posting to similar pages Only few uses do the posting */
//
//    log.info("\n\n ***********   Posting on Page by Users Starts*********** \n\n")
//
//    for (i <- 1 to numberOfpages) {
//      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/User" + 1),
//        userRequestPostOnAPage(i, 1, "This is First Post of Page from user 1 "))
//      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/User" + 2),
//        userRequestPostOnAPage(i, 2, "This is First Post of Page from user 2"))
//      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/User" + 3),
//        userRequestPostOnAPage(i, 3, "This is First Post of Page from user 1 "))
//      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/User" + 4),
//        userRequestPostOnAPage(i, 4, "This is First Post of Page from user 2"))
//      Thread.sleep(500)
//    }
//    log.info("\n\n ***********  Posting on Page by Users  Ends*********** \n\n")
//
//    Thread.sleep(1000)
//
//    log.info("\n\n ***********   Getting Page Feeds by Users Starts*********** \n\n")
//    for (i <- 1 to numberOfpages) {
//      system.scheduler.scheduleOnce(150 * i millisecond, context.actorFor("akka://Facebook-Client/user/User" + 1),
//        userRequestPageFeed(i))
//      Thread.sleep(800)
//    }
//    log.info("\n\n *********** Getting Page Feeds by Users Ends*********** \n\n")
//
//  }

}