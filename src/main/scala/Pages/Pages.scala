package facebookClient

import akka.actor.{ActorSystem, Actor}
import akka.event.Logging
import facebookClient.PageProtocol._
import spray.http.{ContentType, HttpEntity, HttpRequest, ContentTypes}
import scala.util.{Success, Failure}
import akka.actor.{PoisonPill, Actor, ActorRef, ActorSystem}
import spray.client.pipelining._
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * Created by varunvyas on 29/11/15.
 */


/*
POST is used to create.
PUT is used to create or update.
http://stackoverflow.com/questions/630453/put-vs-post-in-rest
*/


class Pages(creatorID: Int, popularityRate : Int, system: ActorSystem) extends Actor{
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
    case PageRequestRegister_Put(creatorID , about , general_info) => {
      /*Other things like public key need to be sent ?*/
      registerPage_Put(creatorID, about, general_info)
      //      killYourself
    }
    case PageRequestGetBio_Get(id) => {
      log.info("In the PageRequestGetBio_Get request of Pages Client side")
      getBioPage_Get(id)
      //      killYourself
    }

    case PageRequestGetAllPosts_Get(userID) => {
      log.info("In the PageRequestGetAllPosts_Get request of Pages Client side")
      PagerequestGetAllPosts_GetF(userID)
    }
  }

  def getBioPage_Get(id: Int): Unit = {
    val clientPipeline = sendReceive
    //val startTimestamp = System.currentTimeMillis()
    val response = clientPipeline {
      Get(url + "Page/GetBio/" + id)
    }
    response onComplete {
      case Failure(ex) => {
        ex.printStackTrace()
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.info("\nsuccess: \n" + resp.entity)
      }
    }
  }

  def registerPage_Put(creatorID: Int, about: String, general_info: String): Unit = {
    val clientPipeline = sendReceive
    ////val startTimestamp = System.currentTimeMillis()
    import RegisterPageRequestProtocol._
    val requestForRegister = RegisterPageRequest(creatorID, about, general_info)
    val response = clientPipeline {
      Put(url + "Page/Register", requestForRegister)
    }
    response onComplete {
      case Failure(ex) => {
        ex.printStackTrace()
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        ////log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.info("\nsuccess: \n" + resp.entity)
        ////log.info(s"User $name Is registered ")
      }
    }
  }

/*Public api for getting all the posts of a Page*/
  def PagerequestGetAllPosts_GetF(PageID: Int) = {
    val clientPipeline = sendReceive
    //val startTimestamp = System.currentTimeMillis()
    val response = clientPipeline {
      Get(url + "Page/Get/AllPosts/" + PageID)
    }
    response onComplete {
      case Failure(ex) => {
        ex.printStackTrace()
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.info("\nsuccess: \n" + resp.entity)
      }
    }
  }
  private def killYourself = self ! PoisonPill

}
