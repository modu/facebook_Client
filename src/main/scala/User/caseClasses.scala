package facebookClient

import java.security.{PublicKey, PrivateKey}

import spray.httpx.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsValue}

object protocol {

  /*Receives of Users */
  case class userRequestGetServerPublicKey()

  case class userRequestRegister_Put(id: Int, name: String, email: String)

  case class userRequestWallMessageUpdate_Post(message: String)

  case class userRequestFriendRequest_Post(requesterID: Int, toBeFriendID: Int, optionalMessage: String)

  case class userRequestGetBio_Get(id: Int)

  case class userRequestGetAllPosts_Get(id: Int)

  case class userRequestPostOnAPage(pageId: Int, creatorID: Int, message: String)

  case class userRequestGetAllPostsOnAPage_Get()

  case class userRequestPageFeed(pageId: Int)

  case class userRequestGetFriendList_Get()

  case class userRequestGetNewsFeed()

  /*Messages sent by User*/
  case class RegisterUserRequest(id: Int, name: String, email: String, timeStamp :String, data: Array[Byte], publicKey: String)

  case class UserPostMessageOwnWall(id: Int, message: String)

  case class FriendRequest(requesterID: Int, requestedToID: Int, message: String)

  case class GetBio(id: Int)

  case class UserPostMessageOnAPage(pageId: Int, CreatorID: Int, message: String)


  object UserPostMessageOnAPageProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val format1 = jsonFormat3(UserPostMessageOnAPage.apply)
  }


  object RegisterUserRequestProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val format = jsonFormat6(RegisterUserRequest.apply)
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

  // Response from Server
  case class publicKeyResponseFromServer(str :String)

  object publicKeyResponseFromServerProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val publicKeyResponseFromServerP = jsonFormat1(publicKeyResponseFromServer.apply)
  }

  // images and private messages
  case class userRequestCreateAlbum(id: Int, albumName: String)

  case class userRequestCreateImage(id: Int, albumName: String, imageName: String)

  case class userRequestGetImage(id: Int, albumName: String, imageName: String)

  case class userRequestGetAlbum(id: Int, albumName: String)

  case class userSendMessage(id: Int, toUserId: Int, message: String)

  case class userRequestGetMessage(id: Int)

  case class RegisterAlbum(albumName: String)

  case class RegisterImage(albumName: String, imageName: String, imageData: String)

  object RegisterAlbumProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val format = jsonFormat1(RegisterAlbum.apply)
  }

  object RegisterImageProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val format = jsonFormat3(RegisterImage.apply)
  }

  case class Image(imageName: String, imageData: String);

  case class Album(images: List[Image]);

  object ImageProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val format2 = jsonFormat2(Image.apply)
  }

  object AlbumProtocol extends DefaultJsonProtocol with SprayJsonSupport {

    import ImageProtocol._

    implicit val format23 = jsonFormat1(Album.apply)
  }

  case class RegisterMessage(fromUserId: Int, toUserId: Int, msg: String);

  case class Packet(fromUserId: Int, msg: String);

  case class ChatMessages(images: List[Packet]);

  object RegisterMessageProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val format2 = jsonFormat3(RegisterMessage.apply)
  }

  object PacketProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val format2 = jsonFormat2(Packet.apply)
  }

  object ChatMessagesProtocol extends DefaultJsonProtocol with SprayJsonSupport {

    import PacketProtocol._

    implicit val format23 = jsonFormat1(ChatMessages.apply)
  }

  // Album and Image


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

