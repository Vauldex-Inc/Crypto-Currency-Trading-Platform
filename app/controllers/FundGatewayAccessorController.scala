package controllers

import java.time.Instant
import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ Future, ExecutionContext }
import play.api.mvc._
import play.api.libs.json._
import ejisan.play.i18n.JsI18nSupport
import ejisan.play.data.Forms._
import utils.security.{ SecureActionApi, SecureActionSupport }
import libs.FundGatewayAPI
import utils.{ Page, ResultHelper }
import models.domain._


@Singleton
class FundGatewayAccessorController @Inject() (
    fundGatewayAPI: FundGatewayAPI,
    val secureActionApi: SecureActionApi,
    implicit val ec: ExecutionContext)
  extends Controller with SecureActionSupport {

    private def requestHelper(
      url: String, req: String,
      data: Map[String, Seq[String]] = Map.empty,
      queryString: Seq[(String, String)] = Seq.empty): Future[Result] = {
      val fgRequest = req match {
        case "PUT" => fundGatewayAPI.putRequest(url, data, queryString)
        case "POST" => fundGatewayAPI.postRequest(url, data, queryString)
        case "PATCH" => fundGatewayAPI.patchRequest(url, data, queryString)
        case "DELETE" => fundGatewayAPI.deleteRequest(url)
        case _ => fundGatewayAPI.getRequest(url, queryString)
      }

      fgRequest.map(ResultHelper(_))
      .recover {
        case _: Exception => InternalServerError
      }
    }
  }
