package facebookClient

import java.nio.file.{Paths, Files}
import java.security.{SecureRandom, PublicKey}
import javax.xml.bind.DatatypeConverter

import akka.event.Logging
import facebookClient.PageProtocol.userRequestLikeAPage
import facebookClient.protocol._
import akka.actor.{PoisonPill, Actor, ActorRef, ActorSystem}
import spray.client.pipelining._
import spray.http.HttpRequest
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by  on 14/11/15.
 */

/*
POST is used to create.
PUT is used to create or update.
http://stackoverflow.com/questions/630453/put-vs-post-in-rest
*/
/*User Actor which does actual request  to server */

class User(id: Int, activityRate: Int, system: ActorSystem ) extends Actor {
  val log = Logging(system, getClass)
  var name: String = "Name" + id
  var email: String = "EmailAddress" + id + "@gmail.com"
  val url: String = "http://127.0.0.1:9090/"
  val keyPair = CryptoUtil.generateKeyPair()
  /*Only client can access the private key */
  private val privateKey = keyPair.getPrivate
  val publicKey = keyPair.getPublic
  log.info(s"\nPublic Of User $id is " + publicKey + "\n")
  var friendWithPublicKeyMap = Map[String, String]()

  /*Mapping of message with its symmetric key */
  var posts = Map[String, String]()

  /*list of tuples[message, key , IV]*/
  //var postsListOfTuples = List[(String, String, String)]
  var encryptedPosts = List[EncryptedPost]()

  def receive = {
    case userRequestGetServerPublicKey() => {
      userRequestGetServerPublicKey_Get()
    }
    case userRequestRegister_Put(id, name, email) => {
      /*Other things like public key need to be sent ?*/
      registerUser_Put(id, name, email)
      //      killYourself
    }
    case userRequestGetBio_Get(id) => {
      getBioUser_Get(id)
      //      killYourself
    }
    case userRequestWallMessageUpdate_Post(message) => {
      userPostMessageOwnWall_Put(id, message + s"$id")
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

    case userRequestFriendRequest_Post(requesterID, toBeFriendID, optionalMessage) => {
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
    case userRequestGetNewsFeed() => {
      userRequestGetNewsFeed_GetF(id)
    }
    case userRequestLikeAPage(pageID: Int, userID: Int) => {
      userRequestLikeAPage_Post(pageID, userID)
    }
    // images and private messages **********************************

    case userRequestCreateAlbum(id: Int, albumName: String) => {
      val clientPipeline = sendReceive
      //val startTimestamp = System.currentTimeMillis()
      import RegisterAlbumProtocol._
      val response = clientPipeline {
        Post(url + "User/setAlbum/" + id, RegisterAlbum(albumName))
      }
      response onComplete {
        case Failure(ex) => {
          //ex.printStackTrace()
          log.error("Failed to create album ")
        }
        case Success(resp) => {
          log.info("success: Album Created\n" + resp.entity)
        }
      }
    }

    case userRequestCreateImage(id: Int, albumName: String, imageName: String) => {
      val clientPipeline = sendReceive
      //val startTimestamp = System.currentTimeMillis()
      import RegisterImageProtocol._
      var rand = new scala.util.Random().nextInt(3)
      var imagepath = ""
      if (rand == 1) {
        imagepath = "images/g.bmp"
      } else if (rand == 2) {
        imagepath = "images/b.bmp"
      } else {
        imagepath = "images/w.bmp"
      }
      val byteArray = Files.readAllBytes(Paths.get(imagepath))
      val imageData = DatatypeConverter.printBase64Binary(byteArray);
      val response = clientPipeline {
        Post(url + "User/setImage/" + id, RegisterImage(albumName, imageName, imageData))
      }
      response onComplete {
        case Failure(ex) => {
          //ex.printStackTrace()
          log.error("Failed to create Image ")
        }
        case Success(resp) => {
          log.info("success: Image Created\n" + resp.entity)
        }
      }
    }

    case userRequestGetImage(id: Int, albumName: String, imageName: String) => {
      val clientPipeline = sendReceive
      //val startTimestamp = System.currentTimeMillis()
      import RegisterImageProtocol._
      val response = clientPipeline {
        Post(url + "User/getImage/" + id, RegisterImage(albumName, imageName, "a"))
      }
      response onComplete {
        case Failure(ex) => {
          //ex.printStackTrace()
          log.error("Failed to retrieve image ")
        }
        case Success(resp) => {
          log.info("success: Image Retrieved\n" + resp.entity)
        }
      }
    }

    case userRequestGetAlbum(id: Int, albumName: String) => {
      val clientPipeline = sendReceive
      //val startTimestamp = System.currentTimeMillis()
      import RegisterAlbumProtocol._
      val response = clientPipeline {
        Post(url + "User/getAlbum/" + id, RegisterAlbum(albumName))
      }
      response onComplete {
        case Failure(ex) => {
          //ex.printStackTrace()
          log.error("Failed to retrieve album ")
        }
        case Success(resp) => {
          log.info("success: Album Retrieve\n" + resp.entity)
        }
      }
    }

    case userSendMessage(id: Int, toUserId: Int, message: String) => {
      val clientPipeline = sendReceive
      //val startTimestamp = System.currentTimeMillis()
      import RegisterMessageProtocol._
      val response = clientPipeline {
        Post(url + "User/sendMessage/" + id, RegisterMessage(id, toUserId, message))
      }
      response onComplete {
        case Failure(ex) => {
          //ex.printStackTrace()
          log.error("Failed to send message ")
        }
        case Success(resp) => {
          log.info("success: Message Sent \n " + resp.entity)
        }
      }
    }

    case userRequestGetMessage(id: Int) => {
      val clientPipeline = sendReceive
      //val startTimestamp = System.currentTimeMillis()
      import RegisterMessageProtocol._
      val response = clientPipeline {
        Get(url + "User/receiveMessage/" + id)
      }
      response onComplete {
        case Failure(ex) => {
          //ex.printStackTrace()
          log.error("Failed to create album ")
        }
        case Success(resp) => {
          log.info("success: Your Messages\n" + resp.entity)
        }
      }
    }
    // images and private messages

  }

  def userRequestGetServerPublicKey_Get() = {
    import publicKeyResponseFromServerProtocol._
    //val response = publicKeyResponseFromServer("publicKey")
    val clientPipeline  = sendReceive ~> unmarshal[publicKeyResponseFromServer]
    //:HttpRequest => Future[publicKeyResponseFromServer]
    val f = clientPipeline {
      Get(url + "GivePublicKeyOfServer")
    }
    f onComplete {
      case Failure(ex) => {
        log.error("\nFailed to Get public Key \n")
      }
      case Success(response) => {
        val clientCoordinatorService = system.actorSelection("akka://Facebook-Server/user/ClientCoorinator")
        clientCoordinatorService ! setPublicKey( response.str )
        log.info("\nsuccess: Your Messages\n" + CryptoUtil.stringToPublicKey( response.str )  )
      }
    }
  }

  def userRequestLikeAPage_Post(pageID: Int, userID: Int) = {
    val clientPipeline = sendReceive
    ////val startTimeStamp = System.currentTimeMillis()
    val response = clientPipeline {
      Post(url + "Page/LikeAPage/" + pageID)
    }
    response onComplete {
      case Failure(ex) => {
        //ex.printStackTrace()
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        log.info("\nsuccess: \n" + resp.entity)
      }
    }
  }

  def userRequestGetNewsFeed_GetF(id: Int) = {
    import EncryptionProtocol._
    val clientPipeline = sendReceive ~> unmarshal[MapPairOfMessageAndEncryptedKey]
    ////val startTimeStamp = System.currentTimeMillis()
    log.info("\nNewsFeed request from client Initiated \n")
    /*TODO: Signing of the request */
    val response = clientPipeline {
      Get(url + "User/NewsFeed/" + id)
    }
    response onComplete {
      case Failure(ex) => {
        //ex.printStackTrace()
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        /*TODO: Decrypte Each of the messages and display*/
        log.info("\nsuccess: \n" + resp.mapOfMToE)
      }
    }
  }


  def userRequestGetFriendList_GetF(id: Int) = {
    import FriendRequestProtocol._
    val clientPipeline = sendReceive ~> unmarshal[friendListsMap]
    ////val startTimeStamp = System.currentTimeMillis()
    val response = clientPipeline {
      Get(url + "User/FriendList/" + id)
    }
    response onComplete {
      case Failure(ex) => {
        //ex.printStackTrace()
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        friendWithPublicKeyMap = resp.Friends
        log.info("\nsuccess: \n" + resp.Friends)
      }
    }
  }

  def userRequestGetAllPostsOnAPage_GetF(id: Int) = {
    val clientPipeline = sendReceive
    ////val startTimeStamp = System.currentTimeMillis()
    import GetBioProtocol._
    val response = clientPipeline {
      Get(url + "User/Page/Feed/" + id)
    }
    response onComplete {
      case Failure(ex) => {
        //ex.printStackTrace()
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.error("Failure to Post on Page ")
      }
      case Success(resp) => {
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.info("\nsuccess: \n" + resp.entity)
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
        //ex.printStackTrace()
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.info("\nsuccess: \n" + resp.entity)
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
        //ex.printStackTrace()
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        log.info("success: " + resp.status + resp.entity)
      }
    }

  }

  def registerUser_Put(id: Int, name: String, email: String): Unit = {
    val clientPipeline = sendReceive
    ////val startTimeStamp = System.currentTimeMillis()
    import RegisterUserRequestProtocol._
    /*TODO: Nounce And time stamp need to tbe added here. For replay attack*/
    val timeStamp = CryptoUtil.getCurrentTimeStamp.toString
    val dataTobeSinged = s"$id+$name+$email+$timeStamp"
    println(" DataSinged " + dataTobeSinged)
    val signedData = CryptoUtil.signData(dataTobeSinged.getBytes(), privateKey)
    val requestForRegister = RegisterUserRequest(id, name, email, timeStamp, signedData, CryptoUtil.publicKeyToString(publicKey) )
    val response = clientPipeline {
      Put(url + "User/Register", requestForRegister)
    }
    /* s"{ UserId : $id , userName : $name , email : $email }"  */
    response onComplete {
      case Failure(ex) => {
        //ex.printStackTrace()
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        ////log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.info("success: " + resp.status + resp.entity)
        //log.info(s"User $name Is registered ")
      }
    }
  }

  /*TODO: unregister a user or delete  a user
  * TODO: Delete a post or unshare a post
  * */
  def userPostMessageOwnWall_Put(id: Int, message: String): Unit = {
    val clientPipeline = sendReceive
    /*TODO: Generate A random Key  and iv */
    posts += "key"->message
    //import EncryptionProtocol._
    //    var temp  = EncryptedPost(message,"key", "iv")

    /*Saving post in local copy */
    var rand :SecureRandom= new SecureRandom();
    //log.info("\n Verifying rand is actually random " + rand +"\n")
    encryptedPosts = EncryptedPost(message,"key", "iv") :: encryptedPosts

    val encryptedMessage = CryptoUtil.encryptAES(CryptoUtil.keyToSpec("Key"), "iv" ,message )

    /*Generate another map which contains friends Name ->FriendPUbKey(k) */
    var localMap = Map[String, Array[Byte]]()

    localMap += ("Key" -> CryptoUtil.encryptRSAKey(CryptoUtil.keyToSpec("Key"), publicKey ) )
    localMap += ("iv"  -> CryptoUtil.encryptRSAKey(CryptoUtil.keyToSpec("iv"), publicKey ))

    /*For each friend , Encrypt "AES Key" using friends public Key*/
    friendWithPublicKeyMap.foreach { friendsNamePublicKeyPair => {
      val byteArrayEncrypted = CryptoUtil.encryptRSAKey(CryptoUtil.keyToSpec("Key"), CryptoUtil.stringToPublicKey( friendsNamePublicKeyPair._2) )

      localMap += (friendsNamePublicKeyPair._1 -> byteArrayEncrypted)

    } }
    val tobeSent = MapFriendNameWithEncryptedSymKeyWithFriendPubKey(localMap)
    log.info("\n" + localMap + "\n")
    log.info("\n" + tobeSent.mapOfFriendNameWithEncryptedPostKeyWithPubKeyOfFriend + "\n")
    val timeStamp = CryptoUtil.getCurrentTimeStamp.toString
    val dataTobeSinged = s"$id+$encryptedMessage+$timeStamp"
    //log.info(" DataSinged " + dataTobeSinged)
    val signedData = CryptoUtil.signData(dataTobeSinged.getBytes(), privateKey)

    import UserPostMessageOwnWallProtocol._
    val response = clientPipeline {
      Put(url + "User/Post/" + id, UserPostMessageOwnWall(id, encryptedMessage ,tobeSent, timeStamp, signedData))
    }
    response onComplete {
      case Failure(ex) => {
        //ex.printStackTrace()
        log.error("Failure to Post Message ")
      }
      case Success(resp) => {
        //log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.info("success: Post Success" + resp.status + "  " + resp.entity )
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
        //ex.printStackTrace()
        log.error("Failure to register user ")
      }
      case Success(resp) => {
        ////log.info(s"Request completed in ${System.currentTimeMillis() - startTimestamp} millis.")
        log.info("success: " + resp.status + "  " + resp.entity)
      }
    }
  }

  def userSendFriendRequest_post(id: Int, friendsID: Int, message: String): Unit = {
    val clientPipeline = sendReceive
    //val startTimeStamp = System.currentTimeMillis()
    import FriendRequestProtocol._
    val timeStamp = CryptoUtil.getCurrentTimeStamp.toString
    val dataTobeSinged = s"$id+$friendsID+$message"
    log.info(" DataSinged " + dataTobeSinged)
    val signedData = CryptoUtil.signData(dataTobeSinged.getBytes(), privateKey)
    val response = clientPipeline {
      Post(url + "User/FriendRequest", FriendRequest(id, friendsID, message ,  signedData ))
    }
    response onComplete {
      case Failure(ex) => {
        //ex.printStackTrace()
        log.error("Failure to Friend other user ")
      }
      case Success(resp) => {
        log.info("success: " + resp.entity + " \n\n\n" + resp.entity)
      }
    }
  }

  private def killYourself = self ! PoisonPill

}



//val keyPairs = CryptoUtil.generateKeyPair()
//val privateKey = keyPairs.getPrivate()
//val publicKey = keyPairs.getPublic
//
//val singedData = CryptoUtil.signData("ToBeSiged".getBytes(), privateKey)
//if(CryptoUtil.verifySignature("ToBeSiged".getBytes() ,publicKey, singedData ))
//{
//println("Verified")
//}
//else
//{
//println("Not Verified")
//}
//println("Public key is " + publicKey.toString)
//val temp = CryptoUtil.publicKeyToString(publicKey)
//println(" string of public key \n" +temp )
//
//println(CryptoUtil.stringToPublicKey(temp))

