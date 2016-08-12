package controllers
import java.sql.Timestamp

import play.api.mvc._
import models.daos._
import javax.inject.{Inject, Singleton}

import models.entities.CountDetailByProduct
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import scala.concurrent.ExecutionContext

@Singleton
class CountsController @Inject()(countDAO: CountDAO, stockDAO: StockDAO)(implicit ec: ExecutionContext) extends Controller{
    def counts() = Action.async(implicit request =>
        countDAO.getCountsWithEarnings().map { counts =>
            Ok(views.html.counts(counts.toList))
        }
    )

    def newCount() = Action.async(implicit request =>
        stockDAO.getLastWithPositiveStock.map { stocks =>
            Ok(views.html.new_count(stocks.toList))
        }
    )

}