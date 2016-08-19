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
import implicits.JsonReads.{countDetailsReads, countDetailByProductReads}
import implicits.JsonWrites.{caloriesPerCountReads}

@Singleton
class CountsController @Inject()(countDAO: CountDAO, countDetailDAO: CountDetailByProductDAO, stockDAO: StockDAO)(implicit ec: ExecutionContext) extends Controller{

    def counts() = Action.async(implicit request =>
        countDetailDAO.getCountsWithEarnings().map { counts =>
            Ok(views.html.counts(counts.toList.sortBy(_._2.getTime).reverse))
        }
    )

    def count(id: Long) = Action.async{ implicit request =>
        countDAO.countDetail(id).map{ count =>
            val expectedEarnings = count._2.map(x => x._2.soldQuantity * x._2.salePrice).sum
            Ok(views.html.count((count._1, count._2, expectedEarnings)))
        }
    }

    def newCount() = Action.async(implicit request =>
        stockDAO.getLastWithPositiveStock.map { stocks =>
            Ok(views.html.new_count())
        }
    )


    def createCount = Action.async(BodyParsers.parse.json) { implicit request =>
        println(request.body)
        val countResult = request.body.validate[CountDetails]

        println(countResult)
        countResult.fold (
            errors => {
                Future(BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toJson(errors))))
            },

            countDetails => {
                val calendar = Calendar.getInstance()
                val currentDate = calendar.getTime
                val currentTimestamp: Timestamp = new Timestamp(currentDate.getTime)

                countDAO.insert(Count(0, currentTimestamp, countDetails.actualEarnings)).map {
                    insertedCountId =>
                        countDetails.countDetails.map(
                            countDetail => {
                                countDetailDAO.insert(CountDetailByProduct(0, insertedCountId, countDetail.productId,
                                    countDetail.remainingQuantity, countDetail.soldQuantity, countDetail.salePrice))
                                stockDAO.insert(Stock(0, countDetail.productId, countDetail.remainingQuantity, currentTimestamp))
                            }
                        )
                }

                Future(Ok(Json.obj("status" ->"OK", "message" -> ("Count '"+ countDetails +"' saved.") )))

            }

        )
    }

    def countsTotalCalories = Action.async{ implicit request =>
            countDAO.totalCaloriesPerCount.map{calories =>
                val sorted: Seq[CaloriesPerCount] = calories.sortBy(_.date.getTime)
                Ok(Json.toJson(sorted))
            }
        }

    def dateActualExpEarnings = Action.async{ implicit request =>
            countDetailDAO.getCountsWithEarnings().map{ data =>
                val json = data.map{
                    d => Json.obj(
                        "date" -> d._2,
                        "actual" -> d._3,
                        "expected" -> d._4
                    )
                }
                Ok(Json.toJson(json))
            }
        }

}