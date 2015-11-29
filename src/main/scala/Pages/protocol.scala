package facebookClient

import spray.httpx.SprayJsonSupport
import spray.json
import spray.json.DefaultJsonProtocol


/**
 * Created by varunvyas on 29/11/15.
 */
object PageProtocol {

  /*Messages received by the Page actor from ClientCoordinator*/
  case class PageRequestRegister_Put(creatorID :Int, about :String, general_info :String)

  case class PageRequestGetBio_Get(pageID :Int)

  case class PageRequestGetAllPosts_Get(userID :Int)


    /*Messages sent from Pages to Server Side */
  case class RegisterPageRequest(id :Int, name :String , email :String )


  object RegisterPageRequestProtocol extends json.DefaultJsonProtocol with SprayJsonSupport {
    implicit val format = jsonFormat3(RegisterPageRequest.apply)
  }

}
