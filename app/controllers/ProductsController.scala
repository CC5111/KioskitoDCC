package controllers
import play.api.mvc._
import models.daos._
import javax.inject.{Inject, Singleton}

import models.entities.Product
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current

import play.api.i18n.Messages.Implicits._

import scala.concurrent.ExecutionContext

@Singleton
class ProductsController @Inject()(productDAO: ProductDAO)(implicit ec: ExecutionContext) extends Controller{

    val productForm = Form(
        mapping(
            "id" -> longNumber,
            "producto" -> nonEmptyText,
            "calorias" -> number
        )(Product.apply)(Product.unapply)
    )

    def products = Action.async{ implicit request =>
        productDAO.all.map{ products =>
            Ok(views.html.products(products.toList, productForm))
        }
    }

    def createProduct = Action.async{ implicit request =>
        productForm.bindFromRequest.fold(
            formWithErrors => {
                /* imprimir error*/
                productDAO.all.map{ products =>
                    Ok(views.html.products(products.toList, formWithErrors))
                }
            },
            product => {
                /* insertar el room en la base dato*/
                productDAO.insert(product).map{ id =>
                    Redirect(routes.ProductsController.products())
                }
            }
        )
    }

}