package controllers
import java.sql.Timestamp
import java.util.Calendar

import play.api.mvc._
import models.daos._
import javax.inject.{Inject, Singleton}

import models.entities.{Product, Stock}
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import scala.concurrent.ExecutionContext

@Singleton
class ProductsController @Inject()(productDAO: ProductDAO, stockDAO: StockDAO)(implicit ec: ExecutionContext) extends Controller{

    val productForm = Form(
        mapping(
            "id" -> longNumber,
            "producto" -> nonEmptyText,
            "calorias" -> number
        )({
            case (id, producto, calorias) => Product(id, producto, calorias, 0)
        })({
            case p: Product => Some((p.id, p.product, p.calories))
        }
        )
    )

    def products = Action.async(implicit request =>
        productDAO.getAllWithStock.map{ products =>
            Ok(views.html.products(products.toList, productForm))
        }
    )

    def createProduct = Action.async{ implicit request =>
        productForm.bindFromRequest.fold(
            formWithErrors => {
                /* imprimir error*/
                productDAO.getAllWithStock.map{ products =>
                    Ok(views.html.products(products.toList, formWithErrors))
                }
            },
            product => {
                /* insertar el room en la base dato*/
                productDAO.insert(product).map{ id =>
                    val calendar = Calendar.getInstance()
                    val currentDate = calendar.getTime

                    val currentTimestamp: Timestamp = new Timestamp(currentDate.getTime)

                    stockDAO.insert(Stock(0, id, 0, currentTimestamp))
                    Redirect(routes.ProductsController.products())
                }
            }
        )
    }

}