package controllers

import java.net.URI
import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ Future, ExecutionContext }
import scala.util.parsing.json.JSON._
import play.api.mvc._
import play.api.i18n.{ I18nSupport, MessagesApi, DefaultLangs, Lang }
import play.api.libs.json._
import cats.implicits._
import ejisan.play.i18n.JsI18nSupport
import utils.security.{ SecureActionApi, SecureActionSupport }
import libs.MrIDAPI
// import utils.GatewayParser._

@Singleton
class HomeController @Inject() (
    preferenceService: models.service.PreferenceService,
    mrIDAPI: MrIDAPI,
    val secureActionApi: SecureActionApi,
    val messagesApi: MessagesApi,
    implicit val ec: ExecutionContext,
    implicit val wja: WebJarAssets,
    implicit val langs: DefaultLangs,
    implicit val configuration: play.api.Configuration
) extends Controller with I18nSupport with JsI18nSupport with SecureActionSupport  {

  /**
   * Create an Action to render an HTML page.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  def requestToURI(request: RequestHeader): URI = {
    val scheme: String = s"${if (request.secure) "https" else "http"}://"
    val uri = s"${scheme}${request.host}/api/code"
    val redirectUri = s"${scheme}${request.host}${request.uri}"
    val idClient = preferenceService.predef.idClient.synchronized.get
    val mridUrl = preferenceService.predef.mridServer.synchronized.get
    new URI(s"${mridUrl}authorize?response_type=code&client_id=${idClient}&redirect_uri=${uri}?redirect_uri=${redirectUri}&client_id=${idClient}")
  }

  def launcher = SecureUserAction.async({ implicit request =>
    preferenceService.predef.mridServer.get flatMap { mridUrl =>
      mrIDAPI.getAccount(request.subject.id)
        .flatMap(account => mrIDAPI.getSecurity(account.id).map {
          case (emailVerification, isLocked, isDisabled) =>
            if (emailVerification) {
              Redirect(s"${mridUrl}${Lang(account.locale).code}/email/verification")
            } else Ok(views.html.page()).withLang(Lang(account.locale))
        })
        .getOrElse(Redirect(requestToURI(request).toString))
    }
  }, { request =>
    Future.successful(Redirect(requestToURI(request).toString))
  })

  def wallet(walletName: String) = Action { implicit request =>
    Ok(views.html.page())
  }

  def trade = Action { implicit request =>
    Ok(views.html.trade())
  }

  def logout = Action {
    Redirect(routes.HomeController.launcher).withNewSession
  }
}
