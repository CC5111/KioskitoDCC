package models.persistence

import models.entities._
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

/**
  * The companion object.
  */
object SlickTables extends HasDatabaseConfig[JdbcProfile] {

  protected lazy val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._

  abstract class BaseTable[T](tag: Tag, name: String) extends Table[T](tag, name) {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  }

  class ProductTable(tag: Tag) extends BaseTable[Product](tag, "Product"){
      def product = column[String]("product")
      def calories = column[Int]("calories")
      def currentPrice = column[Int]("current_price")
      def currentStock = column[Int]("current_stock")

      def * = (id, product, calories, currentPrice, currentStock) <> (Product.tupled, Product.unapply _)
  }

    val productQ = TableQuery[ProductTable]

    class PurchaseTable(tag: Tag) extends BaseTable[Purchase](tag, "Purchase"){
        def date = column[java.sql.Timestamp]("date")

        def * = (id, date) <> (Purchase.tupled, Purchase.unapply _)
    }

    val purchaseQ = TableQuery[PurchaseTable]


    class PurchaseDetailByProductTable(tag: Tag) extends BaseTable[PurchaseDetailByProduct](tag, "PurchaseDetailByProduct"){
        def productId = column[Long]("product_id")
        def purchaseId = column[Long]("purchase_id")
        def numberOfPackages = column[Int]("number_of_packages")
        def quantityByPackage = column[Int]("quantity_by_package")
        def pricePerPackage = column[Int]("price_per_package")

        def * = (id, productId, purchaseId, numberOfPackages, quantityByPackage, pricePerPackage) <> (PurchaseDetailByProduct.tupled, PurchaseDetailByProduct.unapply _)
    }

    val purchaseDetailQ = TableQuery[PurchaseDetailByProductTable]


    class CountDetailByProductTable(tag: Tag) extends BaseTable[CountDetailByProduct](tag, "CountDetailByProduct"){
        def countId = column[Long]("count_id")
        def productId = column[Long]("product_id")
        def quantity = column[Int]("quantity")
        def soldQuantity = column[Int]("sold_quantity")
        def sellingPrice = column[Int]("selling_price")

        def * = (id, countId, productId, quantity, soldQuantity, sellingPrice) <> (CountDetailByProduct.tupled, CountDetailByProduct.unapply _)
    }

    val countDetailQ = TableQuery[CountDetailByProductTable]

    class CountTable(tag: Tag) extends BaseTable[Count](tag, "Count") {
        def date = column[java.sql.Timestamp]("date")
        def actualEarnings = column[Int]("actual_earnings")

        def * = (id, date, actualEarnings) <> (Count.tupled, Count.unapply _)
    }

    val countQ = TableQuery[CountTable]

    class StockTable(tag: Tag) extends BaseTable[Stock](tag, "Stock") {
        def productId = column[Long]("product_id")
        def stock = column[Int]("stock")
        def date = column[java.sql.Timestamp]("date")

        def * = (id, productId, stock, date) <> (Stock.tupled, Stock.unapply _)
    }

    val stockQ = TableQuery[StockTable]
}


