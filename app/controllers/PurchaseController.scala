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

import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._
import play.api.libs.functional.syntax._

import implicits.JsonReads.{shoppingListReads}
import implicits.JsonWrites.caloriesPerCountReads

@Singleton
class PurchaseController @Inject()(periodDAO: PurchaseDAO, productDAO: ProductDAO, purchaseDetailDAO: ProductDetailByPeriodDAO,
                                   stockDAO: StockDAO)(implicit ec: ExecutionContext) extends Controller{

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

    def purchases() = Action.async{ implicit request =>
        periodDAO.getPeriodsTotalCost.map{ purchases =>
            Ok(views.html.purchases(purchases.toList))
        }
    }

    def createPurchase = Action.async(BodyParsers.parse.json) { implicit request =>
        val purchaseResult = request.body.validate[ShoppingList]

        println(purchaseResult)
        purchaseResult.fold (
            errors => {
                Future(Redirect(routes.PurchaseController.purchases()))
            },
            shoppingList => {

                println(shoppingList)
                val calendar = Calendar.getInstance()
                val currentDate = calendar.getTime

                val currentTimestamp: Timestamp = new Timestamp(currentDate.getTime)

                val insertedPurchase: Future[Long] = periodDAO.insert(Purchase(shoppingList.purchaseId, currentTimestamp))

                insertedPurchase.map(purchaseId => {
                    shoppingList.products.map(x => {
                        productDAO.updateCurrentPrice(x.productId, x.salePrice)
                        stockDAO.createNewStock(x.productId, x.packages * x.quantityPerPackage, currentTimestamp)
                        purchaseDetailDAO.insert(PurchaseDetailByProduct(x.id, x.productId, purchaseId, x.packages, x.quantityPerPackage, x.pricePerPackage))
                    }

                    )
                }
                )


                Future(Redirect(routes.PurchaseController.purchases()))
            }
        )

    }
}