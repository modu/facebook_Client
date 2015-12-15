package facebookClient

import java.security._
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base64
import java.util

import sun.misc.{BASE64Decoder, BASE64Encoder}

/**
 * Created by varunvyas on 13/12/15.
 */
object CryptoUtil {

  val ALGORITHMRSA = "RSA"
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
    val keyGenerator: KeyPairGenerator = KeyPairGenerator.getInstance(ALGORITHMRSA)
    val rng: SecureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN")
    rng.setSeed(123123) /*Have to get the true seed here*/
    keyGenerator.initialize(1024, rng)
    keyGenerator.generateKeyPair() /*key pair will be */
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

  def decryptRSA(cypherText: Array[Byte], privatekey: PrivateKey) = {
    // get an RSA cipher object and print the provider
    val cipher: Cipher = Cipher.getInstance(ALGORITHMRSA)
    // encrypt the plain text using the public key
    cipher.init(Cipher.DECRYPT_MODE, privatekey)
    cipher.doFinal(cypherText).toString
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

  def encryptAES(key: String, value: String): String = {
    val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, keyToSpec(key))
    Base64.encodeBase64String(cipher.doFinal(value.getBytes("UTF-8")))
  }

  def decryptAES(key: String, encryptedValue: String): String = {
    val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
    cipher.init(Cipher.DECRYPT_MODE, keyToSpec(key))
    new String(cipher.doFinal(Base64.decodeBase64(encryptedValue)))
  }

  def keyToSpec(key: String): SecretKeySpec = {
    var keyBytes: Array[Byte] = (SALT + key).getBytes("UTF-8")
    val sha: MessageDigest = MessageDigest.getInstance("SHA-1")
    keyBytes = sha.digest(keyBytes)
    keyBytes = util.Arrays.copyOf(keyBytes, 16)
    new SecretKeySpec(keyBytes, "AES")
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
    val decoder = new BASE64Decoder()
    val c = decoder.decodeBuffer(base64EncodedStringOfPublicKey);
    val keyFact = KeyFactory.getInstance("RSA")
    val x509KeySpec: X509EncodedKeySpec = new X509EncodedKeySpec(c)
    keyFact.generatePublic(x509KeySpec)
  }


}


//http://www.java2s.com/Code/Java/Security/SignatureSignAndVerify.htm
//https://gist.github.com/alexandru/ac1c01168710786b54b0
//https://javadigest.wordpress.com/2012/08/26/rsa-encryption-example/
