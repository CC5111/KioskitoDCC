package implicits

import models.entities.{CaloriesPerCount, ProductWithStock}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

/**
  *
  */
object JsonWrites {
    implicit val caloriesPerCountReads: Writes[CaloriesPerCount] = (
        (JsPath \ "date").write[java.sql.Timestamp] and
            (JsPath \ "totalCalories").write[Option[Int]]
        )(unlift(CaloriesPerCount.unapply))

    implicit val productWithStockWrites: Writes[ProductWithStock] = (
        (JsPath \ "id").write[Long] and
            (JsPath \ "product" ).write[String] and
            (JsPath \ "salePrice").write[Int] and
            (JsPath \ "stock").write[Int]
        )(unlift(ProductWithStock.unapply))
}
