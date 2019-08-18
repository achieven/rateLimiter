package controllers

import akka.actor.ActorSystem
import javax.inject._
import play.api.mvc._
import play.api.libs.json.{Json, _}
import services.{UrlMap, UrlUtil}

import scala.concurrent.{ExecutionContext, Future}
@Singleton
class ReportController @Inject()(cc: ControllerComponents, actorSystem: ActorSystem, urlUtil: UrlUtil, urlMap: UrlMap, parse: PlayBodyParsers)(implicit exec: ExecutionContext) extends AbstractController(cc) {
  def report = Action(parse.json).async { implicit request =>
    try {
      val url = (request.body \ "url").asOpt[String].getOrElse("")
      val isValid = urlUtil.validateUrl(url)
      isValid match {
        case false => Future(BadRequest("url is invalid"))
        case true => {
          val urlHash = urlUtil.hashUrl(url)
          val isBlocked = urlMap.upsertAndGetStatus(url)
          Future.successful(Ok(Json.obj("blocked" -> isBlocked)))
        }
      }
    }
    catch {case e: Exception => {
      Future.successful(InternalServerError(e.getMessage))
    }}
  }

}
