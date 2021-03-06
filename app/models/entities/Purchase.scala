package models.entities
import java.sql.Timestamp

case class Purchase(id: Long, date: Timestamp) extends BaseEntity

case class PurchaseDetailByProduct(id: Long, productId: Long, purchaseId: Long, numberOfPackages: Int, quantityByPackage: Int, pricePerPackage: Int) extends BaseEntity

case class CountDetailByProduct(id: Long, countId: Long, productId: Long, quantity: Int, soldQuantity: Int, salePrice: Int ) extends BaseEntity

// Can add observations later
case class Count(id: Long, date: java.sql.Timestamp, actualEarnings: Int) extends BaseEntity


case class ShoppingList(purchaseId: Long, products: Seq[PurchasedProduct])
case class PurchasedProduct(id: Long, productId: Long, packages: Int, quantityPerPackage: Int,
                            pricePerPackage: Int, salePrice: Int)


case class CountDetails(actualEarnings: Int, countDetails: Seq[PartialCountDetail])
case class PartialCountDetail(productId: Long, remainingQuantity: Int, soldQuantity: Int, salePrice: Int)

case class CaloriesPerCount(date: java.sql.Timestamp, totalCalories: Option[Int])


case class PurchaseTotalCost(id: Long, date: java.sql.Timestamp, totalCost: Option[Int])