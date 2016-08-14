package controllers
import java.sql.Timestamp

import play.api.mvc._
import models.daos._
import javax.inject.{Inject, Singleton}

import models.entities.Count
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import scala.concurrent.ExecutionContext

@Singleton
class ChartsController extends Controller {
  def finances = Action(implicit request =>
    Ok(views.html.finances()))

  def soldProducts = Action(implicit request =>
    Ok(views.html.soldProducts()))

  def consCalories = Action(implicit request =>
    Ok(views.html.consCalories()))
}
