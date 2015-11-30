package facebookClient

import spray.httpx.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsValue}

object protocol {

  /*Receives of Users */
  case class userRequestRegister_Put(id: Int, name: String, email: String)

  case class userRequestWallMessageUpdate_Post( message: String)

  case class userRequestFriendRequest_Post(requesterID :Int , toBeFriendID :Int , optionalMessage: String)

  case class userRequestGetBio_Get(id: Int)

  case class userRequestGetAllPosts_Get(id :Int)

  case class userRequestPostOnAPage(pageId :Int , creatorID :Int, message :String)

  case class userRequestGetAllPostsOnAPage_Get()

  case class userRequestPageFeed(pageId :Int)

  case class userRequestGetFriendList_Get()

  case class userRequestGetNewsFeed()

  /*Messages sent by User*/
  case class RegisterUserRequest(id: Int, name: String, email: String)

  case class UserPostMessageOwnWall(id: Int, message: String)

  case class FriendRequest(requesterID: Int, requestedToID: Int, message: String)

  case class GetBio(id: Int)

  case class UserPostMessageOnAPage(pageId :Int , CreatorID :Int , message :String)


  object UserPostMessageOnAPageProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val format1 = jsonFormat3(UserPostMessageOnAPage.apply)
  }


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

