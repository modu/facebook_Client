
object FaceBookClientJson {

  import spray.json._
  case class RegistrationRequest(name: String, email: String, ID: Int)

  case class GetUserPostRequest(name :String , options :String)

  object RegistrationRequestObject extends DefaultJsonProtocol {
    implicit val RegistrationRequestFormat = jsonFormat3(RegistrationRequest)
  }

  object  GetUserPostObject extends DefaultJsonProtocol {
    implicit val GetUserPostObjectRequestFormat = jsonFormat2(GetUserPostRequest)

  }

}