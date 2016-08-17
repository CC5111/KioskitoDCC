package implicits

import models.entities.{CaloriesPerCount, CountDetailByProduct, CountDetails}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}

/**
  *
  */
object JsonReads {
    implicit val placeWrites: Writes[CaloriesPerCount] = (
        (JsPath \ "date").write[java.sql.Timestamp] and
            (JsPath \ "totalCalories").write[Option[Int]]
        )(unlift(CaloriesPerCount.unapply))

    implicit val countDetailsReads: Reads[CountDetails] = (
        (JsPath \ "countId").read[Long] and
            (JsPath \ "countDetails").read[Seq[CountDetailByProduct]]
        )(CountDetails.apply _)

    implicit val countDetailByProductReads: Reads[CountDetailByProduct] = (
        (JsPath \ "id").read[Long] and
            (JsPath \ "countId").read[Long] and
            (JsPath \ "productId").read[Long] and
            (JsPath \ "quantity").read[Int] and
            (JsPath \ "soldQuantity").read[Int] and
            (JsPath \ "sellingPrice").read[Int]

        )(CountDetailByProduct.apply _)
}
