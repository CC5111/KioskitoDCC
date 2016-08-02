package controllers
import play.api.mvc._
import models.daos._
import javax.inject.{Inject, Singleton}

import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import scala.concurrent.ExecutionContext

@Singleton
class PeriodsController @Inject()(periodDAO: PeriodDAO)(implicit ec: ExecutionContext) extends Controller{

    def periods() = Action.async{ implicit request =>
        periodDAO.getPeriodsTotalCost.map{ periods =>
            Ok(views.html.periods(periods.toList))
        }
    }
}