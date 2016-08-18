package controllers
import java.sql.Timestamp
import java.util.Calendar

import play.api.mvc._
import models.daos._
import javax.inject.{Inject, Singleton}

import models.entities._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.{ExecutionContext, Future}
import implicits.JsonReads.{countDetailByProductReads, countDetailsReads}
import implicits.JsonWrites.{caloriesPerCountReads}

@Singleton
class CountsController @Inject()(countDAO: CountDAO, countDetailDAO: CountDetailByProductDAO, stockDAO: StockDAO)(implicit ec: ExecutionContext) extends Controller{

    def counts() = Action.async(implicit request =>
        countDetailDAO.getCountsWithEarnings().map { counts =>
            Ok(views.html.counts(counts.toList))
        }
    )

    def newCount() = Action.async(implicit request =>
        stockDAO.getLastWithPositiveStock.map { stocks =>
            Ok(views.html.new_count())
        }
    )

    def createCounts = Action.async(BodyParsers.parse.json) { implicit request =>
        val countResult = request.body.validate[CountDetails]

        println(countResult)
        countResult.fold (
            errors => {
              Future(Redirect(routes.CountsController.counts()))
            },

            countDetails => {
                val calendar = Calendar.getInstance()
                val currentDate = calendar.getTime
                val currentTimestamp: Timestamp = new Timestamp(currentDate.getTime)

                countDAO.insert(Count(countDetails.countId, currentTimestamp, countDetails.actualEarnings)).map {
                    insertedCountId =>
                        countDetails.countDetails.map(
                            countDetail => {
                                countDetailDAO.insert(countDetail.copy(countId = insertedCountId))
                                stockDAO.insert(Stock(0, countDetail.productId, countDetail.quantity, currentTimestamp))
                            }
                        )
                }

                Future(Redirect(routes.CountsController.counts()))

            }

        )
    }

    def countsTotalCalories = Action.async{ implicit request =>
            countDAO.totalCaloriesPerCount.map{calories =>
                Ok(Json.toJson(calories))
            }
        }

}