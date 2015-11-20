package facebookClient

import akka.event.Logging
import facebookClient.protocol._
import spray.http.{ContentType, HttpEntity, HttpRequest, ContentTypes}
import scala.util.{Success, Failure}
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
  //  implicit val system = ActorSystem("simple-spray-client")
  //
  //  val log = Logging(system, getClass)

  def receive = {
    case userRequestRegister_Put(id, name, email) => {
      /*Other things like public key need to be sent ?*/
      registerUser_Put(id, name, email)
      killYourself
    }
    case userRequestWallMessageUpdate_Post(id, message) => {
      userPostMessageOwnWall_Put(id, message)
      killYourself
    }
    case userRequestFriendRequest_Post( optionalMessage) => {

      /*Friend next three users */
      userSendFriendRequest_post(id, id+1, optionalMessage)
      userSendFriendRequest_post(id, id+2, optionalMessage) /*Have to maintain consistency that two users are already friends and inform user about it*/
      userSendFriendRequest_post(id, id+3, optionalMessage)

      killYourself
    }

//    case PostUser(message: String) =>
//      postRequest(message)
//      killYourself
//    case GetUser(userID) => {
//      getRequest()
//      killYourself
//    }
    //    case PostMessage(message :String ,userId :Int) => {
    //      postMessageUser(message , userId)
    //      killYourself
    //    }

  }

  def registerUser_Put(id: Int, name: String, email: String): Unit = {
    val clientPipeline = sendReceive
    val startTimestamp = System.currentTimeMillis()
    val requestForRegister = RegisterUserRequest(id, name, email)
    val response = clientPipeline {
      Put(url + "User/Register/" + id, requestForRegister)
    }
    /* s"{ UserId : $id , userName : $name , email : $email }"  */
    response onComplete {
      case Failure(ex) => {
        ex.printStackTrace()
        log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.debug("success: " + resp.status + resp.message)
        log.info(s"User $name Is registered ")
      }
    }
  }
  /*TODO: unregister a user or delete  a user
  * TODO: Delete a post or unshare a post
  * */

  def userPostMessageOwnWall_Put(id: Int, message: String): Unit = {
    val clientPipeline = sendReceive
    val startTimestamp = System.currentTimeMillis()
    val response = clientPipeline {
      Put(url + "User/Post/" + id, UserPostMessageOwnWall(id, message))
    }
    /* s"{ UserId : $id , userName : $name , email : $email }"  */
    response onComplete {
      case Failure(ex) => {
        ex.printStackTrace()
        log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.debug("success: " + resp.status + "  " + resp.message)
        log.info(s"User $name Posted the message $message ")
      }
    }
  }

  def userSendFriendRequest_post(id :Int , friendsID : Int, message: String): Unit = {
    val clientPipeline = sendReceive
    val startTimestamp = System.currentTimeMillis()
    val response = clientPipeline {
      Post(url + "User/FriendRequest/" + id, FriendRequest(id, friendsID, message))
    }
    /* s"{ UserId : $id , userName : $name , email : $email }"  */
    response onComplete {
      case Failure(ex) => {
        ex.printStackTrace()
        log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.debug("success: " + resp.status + "  " + resp.message)
        log.info(s"User $name userID $id Friend Requested to userID $friendsID")
      }
    }
  }


//  def postRequest(message: String) = {
//    val clientPipeline = sendReceive
//    val startTimestamp = System.currentTimeMillis()
//
//    val response = clientPipeline {
//      Post(url + "User/" + id, HttpEntity(ContentTypes.`application/json`, message))
//    }
//    response onComplete {
//      case Failure(ex) => {
//        ex.printStackTrace()
//        println(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
//      }
//      case Success(resp) => {
//        println(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
//        println("success: " + resp.status + resp.message)
//      }
//    }
//
//  }

  //  def postRequest(message :String ) = {
  //    val clientPipeline = sendReceive
  //    //val obj = UserRegistrationRequest("Varun Vyas","example@ufl.edu")
  //
  ////    val response: Future[OrderConfirmation] =
  ////      pipeline(Post("http://example.com/orders", Order(42)))
  //
  //    val response = clientPipeline {
  //      Post("/Registration/User/", UserRegistrationRequest("Varun Vyas","example@ufl.edu"))
  //    }
  //    response onComplete{
  //      case Failure(ex) => ex.printStackTrace()
  //      case Success(resp) => println("success: " + resp.status)
  //    }
  //  }

//  def getRequest() = {
//    val clientPipeline = sendReceive
//    val startTimestamp = System.currentTimeMillis()
//    val response = clientPipeline {
//      Get(url + "User/" + id)
//    }
//    response onComplete {
//      case Failure(ex) => {
//        ex.printStackTrace()
//        println(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
//      }
//      case Success(resp) => {
//        println(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
//        println("success: " + resp.status)
//      }
//    }
//  }

  //  def postMessageUser(message :String , userId :Int ): Unit = {
  //    val clientPipeline = sendReceive
  //    val obj = UserPostRequest(name, ID )
  //
  //    val response = clientPipeline {
  //      Post("/User/" + userId, obj)
  //    }
  //    response onComplete{
  //      case Failure(ex) => ex.printStackTrace()
  //      case Success(resp) => println("success: " + resp.status)
  //    }
  //  }

  private def killYourself = self ! PoisonPill

}