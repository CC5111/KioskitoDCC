package controllers
import play.api.mvc._
import models.daos._
import javax.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext

@Singleton
class ProductsController @Inject()(productDAO: ProductDAO)(implicit ec: ExecutionContext) extends Controller{
    def products() = Action.async{ implicit request =>
        productDAO.all.map{ products =>
            Ok(views.html.products(List()))
        }
    }

}