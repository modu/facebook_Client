package facebookClient

import java.math.BigInteger
import java.security._
import java.security.spec.X509EncodedKeySpec
import javax.crypto.{SecretKey, Cipher}
import javax.crypto.spec.{SecretKeySpec, IvParameterSpec}
import org.apache.commons.codec.binary.Base64
import java.util

import sun.misc.{BASE64Decoder, BASE64Encoder}

/**
 * Created by  on 13/12/15.
 */
object CryptoUtil {

  val ALGORITHMRSA = "RSA/ECB/PKCS1PADDING"
  //  val cipher : Cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
  //  val cipherRsa : Cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING")

  /*
  Digital Signing  function:
  INPUT:
    1. Data to be Signed
    2. Private key
  OUTPUT:
    1. reply success if signed properly with the array of byte
  * */

  def signData(data: Array[Byte], privateKey: PrivateKey) = {
    val signer: Signature = Signature.getInstance("SHA1withRSA")
    signer.initSign(privateKey)
    signer.update(data)
    signer.sign()
  }

  /*
  Verify function:
  INPUT:
    1. Data to be verified
    2. Public key
    3. Signature to verify against
  OUTPUT:
    1. Is it signed correctly or not. Boolean reply.
  * */
  def verifySignature(data: Array[Byte], publicKey: PublicKey, signature: Array[Byte]) = {
    val signer: Signature = Signature.getInstance("SHA1withRSA")
    signer.initVerify(publicKey)
    signer.update(data)
    signer.verify(signature)
  }

  /*
  * Generating keyPair
  * INPUT:
  *   None
  * OUTPUT:
  *   1. key pair
   */

  def generateKeyPair() = {
    /*TODO:Generate truly Random Seed */
    val keyGenerator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
    keyGenerator.initialize(1024)
    keyGenerator.generateKeyPair() /*key pair will be */
  }

  def encryptRSAKey(secKey :SecretKey, publickey: PublicKey) = {
    val cipher: Cipher = Cipher.getInstance(ALGORITHMRSA)
    cipher.init(Cipher.ENCRYPT_MODE, publickey)
    cipher.doFinal(secKey.getEncoded)
  }

  def decryptRSAKey(cypherKey: Array[Byte], privatekey: PrivateKey) :SecretKey = {
    val cipher :Cipher = Cipher.getInstance(ALGORITHMRSA)
    cipher.init(Cipher.DECRYPT_MODE, privatekey)
    val key  = cipher.doFinal(cypherKey)
    new SecretKeySpec(key,"AES")
  }

  /**
   * Encrypt the plain text using public key.
   *
   * @param text
 * : original plain text
   * @param publickey
 * :The public key
   * @return Encrypted text
   */

  def encryptRSA(text: String, publickey: PublicKey) = {
    // get an RSA cipher object and print the provider
    val cipher: Cipher = Cipher.getInstance(ALGORITHMRSA)
    // encrypt the plain text using the public key
    cipher.init(Cipher.ENCRYPT_MODE, publickey)
    cipher.doFinal(text.getBytes())
    Base64.encodeBase64String(cipher.doFinal(text.getBytes("UTF-8")))
    //cipher.doFinal(keyToSpec(text).getEncoded)
  }


  /**
   * Decrypt text using private key.
   *
   * @param cypherText
 * :encrypted text
   * @param privatekey
 * :The private key
   * @return plain text
   */

  def decryptRSA(cypherText: String, privatekey: PrivateKey): String = {
    // get an RSA cipher object and print the provider
    val cipher: Cipher = Cipher.getInstance(ALGORITHMRSA)
    // decrypt the plain text using the private key
    cipher.init(Cipher.DECRYPT_MODE, privatekey)
    //cipher.doFinal(cypherText)
    new String(cipher.doFinal(Base64.decodeBase64(cypherText)))
    //    println(temp)
  }
  /**
   * Sample:
   * {{{
   *   scala> val key = "My very own, very private key here!"
   *
   *   scala> Crypto.encrypt(key, "pula, pizda, coaiele!")
   *   res0: String = 9R2vVgkqEioSHyhvx5P05wpTiyha1MCI97gcq52GCn4=
   *
   *   scala> Crypto.decrypt(key", res0)
   *   res1: String = pula, pizda, coaiele!
   * }}}
   */
  def encryptAES(key: SecretKey, iv :String, value: String): String = {
    val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    //    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    //    cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key,"AES"), new IvParameterSpec(IV));
    //    key = new byte[16];
    //    iv = new byte[16];
    //    sr.nextBytes(key);
    //    sr.nextBytes(iv);
    cipher.init(Cipher.ENCRYPT_MODE, key, ivToSpec(iv))
    Base64.encodeBase64String(cipher.doFinal(value.getBytes("UTF-8")))
  }

  def decryptAES(key: SecretKey, iv :String ,encryptedValue: String): String = {
    /*TODO:Using CBC later */
    val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    cipher.init(Cipher.DECRYPT_MODE, key, ivToSpec(iv))
    new String(cipher.doFinal(Base64.decodeBase64(encryptedValue)))
  }

  def keyToSpec(key: String): SecretKeySpec = {
    var keyBytes: Array[Byte] = (SALT + key).getBytes("UTF-8")
    val sha: MessageDigest = MessageDigest.getInstance("SHA-1")
    keyBytes = sha.digest(keyBytes)
    keyBytes = util.Arrays.copyOf(keyBytes, 16)
    new SecretKeySpec(keyBytes, "AES")
  }

  def ivToSpec(iv :String)  :IvParameterSpec = {
    var keyBytes: Array[Byte] = (SALT + iv).getBytes("UTF-8")
    val sha: MessageDigest = MessageDigest.getInstance("SHA-1")
    keyBytes = sha.digest(keyBytes)
    keyBytes = util.Arrays.copyOf(keyBytes, 16)
    new IvParameterSpec(keyBytes)
  }

  /*TODO: Really good SALT */
  val SALT: String = "jMhKlOuJnM34G6NHkqo9V010GhLAqOpF0BePojHgh1HgNg8^72k"

  def getCurrentTimeStamp: Long = {
    System.currentTimeMillis / 1000
  }

  def publicKeyToString(publicKey: PublicKey): String = {
    val publicKeyBytes: Array[Byte] = publicKey.getEncoded()
    val encoder = new BASE64Encoder()
    encoder.encode(publicKeyBytes)
  }

  def stringToPublicKey(base64EncodedStringOfPublicKey: String): PublicKey = {
    println("\n Value of Public key received on client side is " + base64EncodedStringOfPublicKey)
    val decoder = new BASE64Decoder()
    val c = decoder.decodeBuffer(base64EncodedStringOfPublicKey);
    val keyFact = KeyFactory.getInstance("RSA")
    val x509KeySpec: X509EncodedKeySpec = new X509EncodedKeySpec(c)
    keyFact.generatePublic(x509KeySpec)
  }

  def getRandom() :String = {
    val random :SecureRandom =  new SecureRandom()
    new BigInteger(100, random).toString(32)
  }


}



//val tobeEncrypted =  " Hi this is toeb easdlf;kajsklsdflaksdfj;alskjfa;slkdfjas;ldkjf ;alskj alskj fasl;dkjf;alsk jdfa;lskdjfa;lsdkjf;alskdjfa;lskdfj;alskdjfa;slkdjf;alskdjf;alsdkfjas;ldjkfals;kdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kkdfja;sldkfj;laskdfja;slkdfj;alskjf;lsakdfjs;lkdfjas;lkdfjadls;kfsakdfjal;skfjals;dkfjals;kdfjals;kdfjals;kcnrypted "
//println(tobeEncrypted)
//val temp = CryptoUtil.encryptAES("Key", "iv", tobeEncrypted)
//val temp3 = CryptoUtil.decryptAES("Key", "iv", temp)
//println(temp3)

//http://www.java2s.com/Code/Java/Security/SignatureSignAndVerify.htm
//https://gist.github.com/alexandru/ac1c01168710786b54b0
//https://javadigest.wordpress.com/2012/08/26/rsa-encryption-example/



//      val kp = CryptoUtil.generateKeyPair()
//
//      val temp = " HI thi"
//      println(temp);
//
//
//      val encrypted= CryptoUtil.encryptRSA(temp, kp.getPublic)
//      val decrypted = CryptoUtil.decryptRSA(encrypted, kp.getPrivate)
//
//      println(decrypted)


//    /*input : secretKey , iv , and message
//    * output : String*/
//    val encryptedText = CryptoUtil.encryptAES(CryptoUtil.keyToSpec(temp), "iv", "TOBEENcrypteddafdfs")
//
//    /*input : secretKey( which is to be encrypted) , publicKey
//    * output : EncryptedKey Array[Byte]*/
//    val byteArrayEncrypted = CryptoUtil.encryptRSAKey(CryptoUtil.keyToSpec(temp), kp.getPublic)
//
//    /*input :  EncryptedKey Array[Byte] , privateKey
//    * output : secretKey( Which was earlier encrypted)*/
//    val decryptedKeySpec = CryptoUtil.decryptRSAKey(byteArrayEncrypted, kp.getPrivate)
//
//    /*input : secretKey , iv , and EncryptedMessage
//    * output : String*/
//    println(CryptoUtil.decryptAES(decryptedKeySpec, "iv", encryptedText))
