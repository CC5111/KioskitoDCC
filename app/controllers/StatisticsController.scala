package controllers
import play.api.mvc._
import models.daos._
import javax.inject.{Inject, Singleton}

import models.entities.Product
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current

import play.api.i18n.Messages.Implicits._

import scala.concurrent.ExecutionContext

@Singleton
class StatisticsController extends Controller {
  def stats = Action(implicit request =>
    Ok(views.html.statistics()))
}
