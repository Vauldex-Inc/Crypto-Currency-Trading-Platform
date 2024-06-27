package models.domain

import play.api.libs.json._

case class Issuer(key: String, address: Option[String]) {
  def isNative: Boolean = address.isEmpty
  def toJson: JsObject = Issuer.Implicits.IssuerWrites.writes(this).as[JsObject]
}

object Issuer {
  val KEY_MAX_LENGTH = 100
  val ADDRESS_MAX_LENGTH = 255
  val tupled = (apply _).tupled

  object Implicits {
    implicit val IssuerWrites = new Writes[Issuer] {
      def writes(Issuer: Issuer): JsValue = Json.obj(
        "issuer_key" -> Issuer.key,
        "issuer_address" -> Issuer.address)
    }
  }
}
