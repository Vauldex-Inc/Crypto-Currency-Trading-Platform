package models.domain

import java.util.UUID
import java.time.Instant
import play.api.libs.json._

case class Affiliate(
    idAccountRef: UUID,
    idParent: Option[UUID],
    isAffiliateEnabled: Boolean,
    joinedAt: Instant,
    distributedAt: Option[Instant],
    optId: Option[Int] = None) {
  lazy val id: Int = optId.getOrElse(-1)
  def toJson: JsObject = Affiliate.Implicits.affiliateWrites.writes(this).as[JsObject]
}

object Affiliate {
  val tupled = (apply _).tupled

  object Implicits {
    implicit val affiliateWrites = new Writes[Affiliate] {
      def writes(affiliate: Affiliate): JsValue = Json.obj(
        "account_id" -> affiliate.idAccountRef,
        "parent_id" -> affiliate.idParent,
        "is_affiliate_enabled" -> affiliate.isAffiliateEnabled,
        "joined_at" -> affiliate.joinedAt.getEpochSecond,
        "distributed_at" -> affiliate.distributedAt,
        "id" -> affiliate.id)
    }
  }
}
