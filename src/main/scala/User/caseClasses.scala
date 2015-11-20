package facebookClient

import spray.json.{DefaultJsonProtocol, JsValue}

object protocol {

  /*Receives of Users */
  case class userRequestRegister_Put(id: Int, name: String, email: String)

  case class userRequestWallMessageUpdate_Post(id: Int, message: String)

  case class userRequestFriendRequest_Post( optionalMessage: String)


  /*Messages sent by User*/
  case class RegisterUserRequest(id: Int, name: String, email: String)

  case class UserPostMessageOwnWall(id: Int, message: String)

  case class FriendRequest(requesterID: Int, requestedToID: Int, message: String)



  object registerRequestProtocol extends DefaultJsonProtocol {
    implicit val format = jsonFormat3(RegisterUserRequest.apply)
  }

  object UserPostMessageOwnWallProtocol extends DefaultJsonProtocol {
    implicit val format = jsonFormat2(UserPostMessageOwnWall.apply)
  }

  object FriendRequestProtocol extends DefaultJsonProtocol {
    implicit val format = jsonFormat3(FriendRequest.apply)
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

