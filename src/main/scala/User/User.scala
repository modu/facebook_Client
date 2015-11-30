package facebookClient

import akka.event.Logging
import facebookClient.protocol._
import akka.actor.{PoisonPill, Actor, ActorRef, ActorSystem}
import spray.client.pipelining._
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by varunvyas on 14/11/15.
 */

/*
POST is used to create.
PUT is used to create or update.
http://stackoverflow.com/questions/630453/put-vs-post-in-rest
*/

class User(id: Int, activityRate: Int, system: ActorSystem) extends Actor {
  val log = Logging(system, getClass)
  var name: String = ""
  var email: String = ""
  val url: String = "http://127.0.0.1:9090/"
  /*Admin of a page
  * Pictures associated
  * Albums
  * FriendList
  * */

  def receive = {
    case userRequestRegister_Put(id, name, email) => {
      /*Other things like public key need to be sent ?*/
      registerUser_Put(id, name, email)
      //      killYourself
    }
    case userRequestGetBio_Get(id) => {
      /**/
      log.info("In the userRequestGetBio_Get request of User Client side")
      getBioUser_Get(id)
      //      killYourself
    }
    case userRequestWallMessageUpdate_Post(message) => {
      userPostMessageOwnWall_Put(id, message+s"$id")
      //      killYourself
    }
    case userRequestGetAllPosts_Get(id) => {
      userrequestGetAllPosts_GetF(id)
    }
    case userRequestGetAllPostsOnAPage_Get() => {
      userRequestGetAllPostsOnAPage_GetF(1) /*Unused apis */
    }
    case userRequestPageFeed(pageId) => {
      userRequestGetAllPostsOnAPage_GetF(pageId)
    }

    case userRequestFriendRequest_Post(requesterID , toBeFriendID ,  optionalMessage) => {
      /*Friend next three users */
      userSendFriendRequest_post(requesterID, toBeFriendID, optionalMessage) /*Have to maintain consistency that two users are already friends and inform user about it*/
      //      killYourself
    }
    case userRequestPostOnAPage(pageId: Int, creatorID: Int, message: String) => {
      val actorRefPage = context.actorFor("akka://Facebook-Client/user/Page" + pageId)
      if (actorRefPage == None) {
        log.error("No such page created on server side ")
      }
      else {
        userPostMessageOnAPage_Put(pageId: Int, creatorID: Int, message: String)
      }
    }
    case userRequestGetFriendList_Get() => {
      userRequestGetFriendList_GetF(id)
    }
    case userRequestGetNewsFeed() =>{
      userRequestGetNewsFeed_GetF(id)
    }

  }
  def userRequestGetNewsFeed_GetF(id :Int) = {
    val clientPipeline = sendReceive
    ////val startTimeStamp = System.currentTimeMillis()
    val response = clientPipeline {
      Get(url + "User/NewsFeed/" + id)
    }
    response onComplete {
      case Failure(ex) => {
        ex.printStackTrace()
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        log.info("success: \n" + resp.entity)
      }
    }
  }


  def userRequestGetFriendList_GetF(id :Int) = {
    val clientPipeline = sendReceive
    ////val startTimeStamp = System.currentTimeMillis()
    val response = clientPipeline {
      Get(url + "User/FriendList/" + id)
    }
    response onComplete {
      case Failure(ex) => {
        ex.printStackTrace()
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.info("success: \n" + resp.message)
      }
    }
  }

  def userRequestGetAllPostsOnAPage_GetF(id :Int) = {
    val clientPipeline = sendReceive
    ////val startTimeStamp = System.currentTimeMillis()
    import GetBioProtocol._
    val response = clientPipeline {
      Get(url + "User/Page/Feed/" + id)
    }
    response onComplete {
      case Failure(ex) => {
        ex.printStackTrace()
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.info("success: \n" + resp.message)
      }
    }
  }

  def userrequestGetAllPosts_GetF(id: Int) = {
    val clientPipeline = sendReceive
    ////val startTimeStamp = System.currentTimeMillis()
    import GetBioProtocol._
    val response = clientPipeline {
      Get(url + "User/Post/" + id)
    }
    /* s"{ UserId : $id , userName : $name , email : $email }"  */
    response onComplete {
      case Failure(ex) => {
        ex.printStackTrace()
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.info("success: \n" + resp.message)
      }
    }
  }

  def getBioUser_Get(id: Int): Unit = {
    val clientPipeline = sendReceive
    import GetBioProtocol._
    val response = clientPipeline {
      Get(url + "User/GetBio/" + id)
    }
    response onComplete {
      case Failure(ex) => {
        ex.printStackTrace()
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        log.info("success: " + resp.status + resp.message)
      }
    }

  }

  def registerUser_Put(id: Int, name: String, email: String): Unit = {
    val clientPipeline = sendReceive
    ////val startTimeStamp = System.currentTimeMillis()
    import RegisterUserRequestProtocol._
    val requestForRegister = RegisterUserRequest(id, name, email)
    val response = clientPipeline {
      Put(url + "User/Register", requestForRegister)
    }
    /* s"{ UserId : $id , userName : $name , email : $email }"  */
    response onComplete {
      case Failure(ex) => {
        ex.printStackTrace()
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        ////log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.info("success: " + resp.status + resp.message)
        //log.info(s"User $name Is registered ")
      }
    }
  }

  /*TODO: unregister a user or delete  a user
  * TODO: Delete a post or unshare a post
  * */

  def userPostMessageOwnWall_Put(id: Int, message: String): Unit = {
    val clientPipeline = sendReceive
    ////val startTimeStamp = System.currentTimeMillis()
    import UserPostMessageOwnWallProtocol._
    var temp = UserPostMessageOwnWall(id, message)
    val response = clientPipeline {
      Put(url + "User/Post/" + id, UserPostMessageOwnWall(id, message))
    }
    response onComplete {
      case Failure(ex) => {
        ex.printStackTrace()
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.info("success: " + resp.status + "  " + resp.message)
      }
    }
  }

  def userPostMessageOnAPage_Put(pageId: Int, creatorID: Int, message: String) = {
    val clientPipeline = sendReceive
    //val startTimeStamp = System.currentTimeMillis()
    import UserPostMessageOnAPageProtocol._
    val response = clientPipeline {
      Put(url + "User/Post/OnPage", UserPostMessageOnAPage(pageId, creatorID, message))
    }
    response onComplete {
      case Failure(ex) => {
        ex.printStackTrace()
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        ////log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.info("success: " + resp.status + "  " + resp.message)
      }
    }
  }


  def userSendFriendRequest_post(id: Int, friendsID: Int, message: String): Unit = {
    val clientPipeline = sendReceive
    //val startTimeStamp = System.currentTimeMillis()
    import FriendRequestProtocol._
    val response = clientPipeline {
      Post(url + "User/FriendRequest", FriendRequest(id, friendsID, message))
    }
    response onComplete {
      case Failure(ex) => {
        ex.printStackTrace()
        log.error("Failure to Friend other user ")
      }
      case Success(resp) => {
        log.info("success: "  + resp.message + " \n\n\n" +resp.entity )
      }
    }
  }

  private def killYourself = self ! PoisonPill

}
