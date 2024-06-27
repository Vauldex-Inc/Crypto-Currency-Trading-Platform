package controllers.mastergate

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import play.api.libs.json.{ Json, JsArray }
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, MessagesApi }
import cats.implicits._
import utils.security.{ SecureActionSupport, SecureActionApi }
import errors._
import models.domain.WalletIndex

@Singleton
class WalletIndexController @Inject()(
    walletIndexService: models.service.WalletIndexService,
    walletIndexRepo:  models.repo.WalletIndexRepo,
    val secureActionApi: SecureActionApi,
    val messagesApi: MessagesApi,
    implicit val ec: ExecutionContext)
  extends Controller with I18nSupport with SecureActionSupport {

  def getWalletIndexInfo(index: Long) = SecureClientApiAction.async {
    walletIndexService
      .getWalletInfo(index)
      .map({walletInfo =>
        val wallet = walletInfo._2.fold(_.toJson, _.toJson)
        Ok(walletInfo._1.toJson ++ wallet)
      }) getOrElse(NotFound)
  }

  def getAllUserWalletIndex = SecureClientApiAction.async {
    walletIndexService
      .getAllUserWalletIndex
      .map(_.map(wallet => Json.obj("network_key" -> wallet._1, "index" -> wallet._2)))
      .map(wallets => Ok(Json.toJson(wallets)))
  }

  private def getWalletIndex(idAccount: UUID, idDataRef: UUID): Future[Result] = {
    walletIndexRepo.findAccountAndData(idAccount, idDataRef)
      .map(walletIndex => Ok(walletIndex.toJson))
      .getOrElse(NotFound)
  }

  def findMyAccountAndData(idDataRef: UUID) = SecureUserAction.async { implicit request =>
    getWalletIndex(request.subject.id, idDataRef)
  }

  def findAccountAndDataOf(idAccount: UUID, idDataRef: UUID) = SecureClientApiAction.async { implicit request =>
    getWalletIndex(idAccount, idDataRef)
  }

  def findByIndexOf(index: Long) = SecureClientApiAction.async { implicit request =>
    walletIndexRepo.find(index)
      .map(walletIndex => Ok(walletIndex.toJson))
      .getOrElse(NotFound)
  }
}

