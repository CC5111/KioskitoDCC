package models.entities
import java.sql.Timestamp

case class Purchase(id: Long, date: Timestamp) extends BaseEntity

case class PurchaseDetailByProduct(id: Long, productId: Long, purchaseId: Long, numberOfPackages: Int, quantityByPackage: Int, pricePerPackage: Int) extends BaseEntity

case class CountDetailByProduct(id: Long, countId: Long, productId: Long, quantity: Int, soldQuantity: Int, sellingPrice: Int ) extends BaseEntity

// Can add observations later
case class Count(id: Long, date: java.sql.Timestamp, actualEarnings: Int)