package implicits

import models.entities._
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}

/**
  *
  */
object JsonReads {
    implicit val purchasedProductReads: Reads[PurchasedProduct] = (
        (JsPath \ "id").read[Long] and
            (JsPath \ "productId").read[Long] and
            (JsPath \ "packages").read[Int] and
            (JsPath \ "quantityPerPackage").read[Int] and
            (JsPath \ "pricePerPackage").read[Int] and
            (JsPath \ "salePrice").read[Int]
        )(PurchasedProduct.apply _)

    implicit val shoppingListReads: Reads[ShoppingList] = (
        (JsPath \ "purchaseId").read[Long] and
            (JsPath \ "products").read[Seq[PurchasedProduct]]
        )(ShoppingList.apply _)


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
