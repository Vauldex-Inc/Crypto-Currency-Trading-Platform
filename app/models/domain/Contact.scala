package models.domain

import java.util.UUID
import play.api.libs.json._

case class Contact(
    id: UUID,
    idAccountRef: UUID,
    name: String,
    destination: String,
    notes: Option[String],
    codeCurrency: String,
    keyNetwork: String,
    keyIssuer: String) {
  def toJson: JsObject = Contact.Implicits.contactWrites.writes(this).as[JsObject]
}

object Contact {
  val NAME_MAX_LENGTH = 100
  val COLOR_MAX_LENGTH = 100
  val NOTES_MAX_LENGTH = 100

  val tupled = (apply _).tupled

  object Implicits {
    implicit val contactWrites = new Writes[Contact] {
      def writes(contact: Contact): JsValue = Json.obj(
        "contact_id" -> contact.id,
        "account_id" -> contact.idAccountRef,
        "name" -> contact.name,
        "notes" -> contact.notes,
        "destination" -> contact.destination,
        "code_currency" -> contact.codeCurrency,
        "network_key" -> contact.keyNetwork,
        "issuer_key" -> contact.keyIssuer)
    }
  }
}
