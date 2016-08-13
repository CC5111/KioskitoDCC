package controllers
import java.sql.Timestamp
import java.util.Calendar

import play.api.mvc._
import models.daos._
import javax.inject.{Inject, Singleton}

import models.entities.{Count, CountDetailByProduct}
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CountsController @Inject()(countDAO: CountDAO, countDetailDAO: CountDetailByProductDAO, stockDAO: StockDAO)(implicit ec: ExecutionContext) extends Controller{

    case class CountDetails(countId: Long, countDetails: Seq[CountDetailWithoutCountId])
    case class CountDetailWithoutCountId(id: Long, productId: Long, quantity: Int, soldQuantity: Int, salePrice: Int)
    val countsForm = Form(
        mapping (
            "countId" -> longNumber,
            "countDetails" -> seq(
                mapping(
                    "id" -> longNumber,
                    "productId" -> longNumber,
                    "remainingQuantity" -> number,
                    "soldQuantity" -> number,
                    "salePrice" -> number
                )(CountDetailWithoutCountId.apply)(CountDetailWithoutCountId.unapply)
            )
        )(CountDetails.apply)(CountDetails.unapply)
    )

    def counts() = Action.async(implicit request =>
        countDetailDAO.getCountsWithEarnings().map { counts =>
            Ok(views.html.counts(counts.toList))
        }
    )

    def newCount() = Action.async(implicit request =>
        stockDAO.getLastWithPositiveStock.map { stocks =>
            Ok(views.html.add_count(stocks.toList))
        }
    )

    def createCounts = Action.async(implicit request =>
        countsForm.bindFromRequest().fold (
            formWithErrors => {
                Future(Redirect(routes.CountsController.counts()))
            },
            details => {
                val calendar = Calendar.getInstance()
                val currentDate = calendar.getTime
                countDAO.insert(Count(details.countId, new Timestamp(currentDate.getTime), 0))

                for {
                    partialCountDetail <- details.countDetails
                } countDetailDAO.insert(CountDetailByProduct(partialCountDetail.id,
                    details.countId, partialCountDetail.productId, partialCountDetail.quantity, partialCountDetail.soldQuantity,
                    partialCountDetail.salePrice))

                Future(Redirect(routes.CountsController.counts()))

            }
        )
    )

}