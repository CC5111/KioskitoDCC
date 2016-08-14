package controllers
import play.api.mvc._
import models.daos._
import javax.inject.{Inject, Singleton}

import models.entities.Product
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current

import play.api.i18n.Messages.Implicits._

import play.api.libs.json._

import scala.concurrent.ExecutionContext

@Singleton
class NewPurchaseController@Inject()(productDAO: ProductDAO)(implicit ec: ExecutionContext) extends Controller {
  def newPurchase = Action(implicit request =>
    Ok(views.html.newPurchase()))

  def getProductsNames = Action.async{ implicit request =>
    productDAO.all.map{ products =>
      val json = products.map{
        product => Json.obj(
          "name" -> product.product,
          "id" -> product.id
        )
      }
      Ok(Json.toJson(json)).as(JSON)
    }
  }

}
