package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json.{JsValue, Json}
import dao.UrlMap
import services.UrlUtil

import scala.concurrent.{ExecutionContext, Future}
@Singleton
class ReportController @Inject()(cc: ControllerComponents,
                                 urlUtil: UrlUtil,
                                 urlMap: UrlMap,
                                 parse: PlayBodyParsers)
                                (implicit exec: ExecutionContext) extends AbstractController(cc) {
  def report:Action[JsValue] = Action(parse.json).async { implicit request =>
    try {
      val url = (request.body \ "url").asOpt[String].getOrElse("")
      val isValid = urlUtil.validateUrl(url)

      if (!isValid) Future(BadRequest("url is invalid"))
      else {
        val urlHash = urlUtil.hashUrl(url)
        val isBlocked = urlMap.upsertAndGetStatus(urlHash, 0)
        Future.successful(Ok(Json.obj("blocked" -> isBlocked)))
      }
    }
    catch {
      case e: Exception => Future.successful(InternalServerError(e.getMessage))
    }
  }

}
