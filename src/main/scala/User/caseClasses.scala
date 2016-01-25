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

  case class PostE(FromID: Int, toUserID: Int, encryptedMessage: String, signedData :Array[Byte], encrypteKey: Array[Byte], iv :String)

  case class encryptedPostMessage(fromID: Int, toUserID: Int, encryptedMessage: String)

  case class sendMeYourPosts(ofUserID: Int)

  case class ShowYourPosts(orginalUserID :Int, publicKey :PublicKey)

  case class postListAll(allPosts: List[PostE])

  /*Messages sent by User*/
  case class RegisterUserRequest(id: Int, name: String, email: String, timeStamp: String, Nounce :String, data: Array[Byte], publicKey: String)

  case class UserPostMessageOwnWall(id: Int, message: String, friendNameWithKey: MapFriendNameWithEncryptedSymKeyWithFriendPubKey, timeStamp: String, data: Array[Byte])

  case class FriendRequest(requesterID: Int, requestedToID: Int, message: String, data: Array[Byte])

  case class GetBio(id: Int)

  case class UserPostMessageOnAPage(pageId: Int, CreatorID: Int, message: String)

  case class EncryptedPost(message: String, key: String, iv: String)

  case class MapFriendNameWithEncryptedSymKeyWithFriendPubKey(mapOfFriendNameWithEncryptedPostKeyWithPubKeyOfFriend: Map[String, Array[Byte]])

  case class takeThisPostAndKey(lastPost :String, iv :String, newlyEncryptedSecreteKeyToBeSent :Array[Byte])
  //case class encryptedPost(mapOfFriendNameWithEncryptedPostKeyWithPubKeyOfFriend :Map[String,Array[Byte]])

  /*Sent by Server to client*/
  case class MapPairOfMessageAndEncryptedKey(mapOfMToE: Map[String, Array[Byte]])

  object EncryptionProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val EncryptedPostF = jsonFormat3(EncryptedPost.apply)
    implicit val MapFriendNameWithEncryptedSymKeyWithFriendPubKeyF = jsonFormat1(MapFriendNameWithEncryptedSymKeyWithFriendPubKey.apply)
    implicit val MapPairOfMessageAndEncryptedKeyF = jsonFormat1(MapPairOfMessageAndEncryptedKey.apply)
    implicit val PostEF = jsonFormat6(PostE.apply)
    implicit val postListAllF = jsonFormat1(postListAll.apply)

  }


  object UserPostMessageOnAPageProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val format1 = jsonFormat3(UserPostMessageOnAPage.apply)
  }


  object RegisterUserRequestProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val RegisterUserRequestf = jsonFormat7(RegisterUserRequest.apply)
  }

  object UserPostMessageOwnWallProtocol extends DefaultJsonProtocol with SprayJsonSupport {

    import EncryptionProtocol._

    implicit val UserPostMessageOwnWallformat = jsonFormat5(UserPostMessageOwnWall.apply)
  }

  object FriendRequestProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val FriendRequestf = jsonFormat4(FriendRequest.apply)
    implicit val friendListsMapformat = jsonFormat1(friendListsMap.apply)
  }

  object GetBioProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val GetBiof = jsonFormat1(GetBio.apply)
  }

  // Response from Server
  case class publicKeyResponseFromServer(str: String, nounce: String)

//  case class publicKeyResponseFromServer(str: Array[Byte], nounce: String)

  case class friendListsMap(Friends: Map[String, String])

  object publicKeyResponseFromServerProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val publicKeyResponseFromServerP = jsonFormat2(publicKeyResponseFromServer.apply)
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

