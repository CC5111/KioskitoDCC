package controllers
import java.sql.Timestamp

import play.api.mvc._
import models.daos._
import javax.inject.{Inject, Singleton}

import models.entities.{CountDetailByProduct, Product}
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CountsController @Inject()(countDAO: CountDAO, stockDAO: StockDAO, productDAO: ProductDAO)(implicit ec: ExecutionContext) extends Controller{


    case class Products(products: Seq[Product])

    val form = Form(
        mapping(
            "products" -> seq(
                mapping(
                    "id" -> longNumber,
                    "product" -> text,
                    "calories" -> number,
                    "currentPrice" -> number
                )(Product.apply)(Product.unapply)
            )
        )(Products.apply)(Products.unapply)
    )

    def counts() = Action.async(implicit request =>
        countDAO.getCountsWithEarnings().map { counts =>
            Ok(views.html.counts(counts.toList))
        }
    )

    def newCount() = Action.async(implicit request =>
        stockDAO.getLastWithPositiveStock.map { stocks =>
            Ok(views.html.add_count(stocks.toList))
        }
    )

    def multiForm() = Action {
        Ok(views.html.multi_form())
    }

    def addProducts() = Action.async { implicit request =>
        form.bindFromRequest().fold (
            formWithErrors => {
                Future(Ok(views.html.multi_form()))
            },
            products => {
                productDAO.insert(products.products)
                Future(Redirect(routes.ProductsController.products()))

            }
        )
    }
}