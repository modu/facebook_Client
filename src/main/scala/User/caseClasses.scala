package facebookClient

import spray.httpx.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsValue}

object protocol {

  /*Receives of Users */
  case class userRequestRegister_Put(id: Int, name: String, email: String)

  case class userRequestWallMessageUpdate_Post(id: Int, message: String)

  case class userRequestFriendRequest_Post(optionalMessage: String)

  case class userRequestGetBio_Get(id: Int)

  case class userRequestGetAllPosts_Get(id :Int)


  /*Messages sent by User*/
  case class RegisterUserRequest(id: Int, name: String, email: String)

  case class UserPostMessageOwnWall(id: Int, message: String)

  case class FriendRequest(requesterID: Int, requestedToID: Int, message: String)

  case class GetBio(id: Int)



  object RegisterUserRequestProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val format = jsonFormat3(RegisterUserRequest.apply)
  }

  object UserPostMessageOwnWallProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val format = jsonFormat2(UserPostMessageOwnWall.apply)
  }

  object FriendRequestProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val format = jsonFormat3(FriendRequest.apply)
  }

  object GetBioProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val format = jsonFormat1(GetBio.apply)
  }

}


//case class PostUser(message: String)
//
//case class PostMessage(message: String, userId: Int)
//
//case class UserRegistrationRequest(name: String, email: String)
//
//
//case class UserPostRequest(message: String, userId: Int)
//
///* Add public key sending too */
//
//case class GetUser(userID: Int)

