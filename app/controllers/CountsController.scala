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

    case class CountDetails(countId: Long, countDetails: Seq[CountDetailByProduct])

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
                )({
                    case (id, productId, remaining, sold, salePrice) => CountDetailByProduct(id, 0, productId, remaining, sold, salePrice)
                })
                ({
                    case detail : CountDetailByProduct => Some((detail.id, detail.productId, detail.quantity, detail.soldQuantity, detail.sellingPrice))
                })
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
            Ok(views.html.new_count(stocks.toList))
        }
    )

    def createCounts = Action.async(implicit request =>
        countsForm.bindFromRequest().fold (
            formWithErrors => {
                Future(Redirect(routes.CountsController.counts()))
            },
            countDetails => {
                val calendar = Calendar.getInstance()
                val currentDate = calendar.getTime
                countDAO.insert(Count(countDetails.countId, new Timestamp(currentDate.getTime), 0))

                countDetails.countDetails.map(countDetail => countDetailDAO.insert(countDetail.copy(countId = countDetails.countId)))

                Future(Redirect(routes.CountsController.counts()))

            }
        )
    )

}