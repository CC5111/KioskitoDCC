package controllers
import java.sql.Timestamp
import java.util.Calendar

import play.api.mvc._
import models.daos._
import javax.inject.{Inject, Singleton}

import models.entities.{Product, Purchase, PurchaseDetailByProduct}
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PurchaseController @Inject()(periodDAO: PeriodDAO, productDAO: ProductDAO, purchaseDetailDAO: ProductDetailByPeriodDAO,
                                   stockDAO: StockDAO)(implicit ec: ExecutionContext) extends Controller{

    case class ShoppingList(purchaseId: Long, products: Seq[PurchasedProduct])
    case class PurchasedProduct(id: Long, productId: Long, packages: Int, quantityPerPackage: Int,
                                pricePerPackage: Int, salePrice: Int)

    val purchaseForm = Form(
        mapping(
            "purchaseId" -> longNumber,
            "products" -> seq(
                mapping(
                    "id" -> longNumber,
                    "productId" -> longNumber,
                    "packages" -> number,
                    "quantityPerPackage" -> number,
                    "pricePerPackage" -> number,
                    "salePrice" -> number
                )(PurchasedProduct.apply)(PurchasedProduct.unapply)
            )
        )(ShoppingList.apply)(ShoppingList.unapply)
    )

    def periods() = Action.async{ implicit request =>
        periodDAO.getPeriodsTotalCost.map{ periods =>
            Ok(views.html.periods(periods.toList))
        }
    }

    def createPurchase = Action.async{ implicit request =>
        purchaseForm.bindFromRequest().fold(
            formWithErrors => {
                Future(Redirect(routes.PeriodsController.periods()))
            },

            shoppingList => {
                val calendar = Calendar.getInstance()
                val currentDate = calendar.getTime

                val currentTimestamp: Timestamp = new Timestamp(currentDate.getTime)

                val insertedPurchase: Future[Long] = periodDAO.insert(Purchase(shoppingList.purchaseId, currentTimestamp))
                insertedPurchase.map(purchaseId =>
                    shoppingList.products.map(x => {
                        productDAO.updateCurrentPrice(x.productId, x.salePrice)
                        stockDAO.createNewStock(x.productId, x.packages * x.quantityPerPackage, currentTimestamp)
                        purchaseDetailDAO.insert(PurchaseDetailByProduct(x.id, x.productId, purchaseId, x.packages, x.quantityPerPackage, x.pricePerPackage))
                    }
                    )
                )


                Future(Redirect(routes.PeriodsController.periods()))
            }
        )

    }
}