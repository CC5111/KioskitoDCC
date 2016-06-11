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

  case class SimpleSupplier(name: String, desc: String)

  class SuppliersTable(tag: Tag) extends BaseTable[Supplier](tag, "suppliers") {
    def name = column[String]("name")
    def desc = column[String]("desc")
    def * = (id, name, desc) <> (Supplier.tupled, Supplier.unapply)
  }

  val suppliersTableQ : TableQuery[SuppliersTable] = TableQuery[SuppliersTable]

  class ProductTable(tag: Tag) extends BaseTable[Product](tag, "Product"){
      def product = column[String]("product")
      def calories = column[Int]("calories")

      def * = (id, product, calories) <> (Product.tupled, Product.unapply _)
  }

    val productQ = TableQuery[ProductTable]

    class PeriodTable(tag: Tag) extends BaseTable[Period](tag, "Period"){
        def startingDate = column[java.sql.Timestamp]("starting_date")
        def endDate = column[java.sql.Timestamp]("end_date")
        def earnings = column[Int]("earnings")

        def * = (id, startingDate, endDate, earnings) <> (Period.tupled, Period.unapply _)
    }

    val periodQ = TableQuery[PeriodTable]


    class ProductDetailByPeriodTable(tag: Tag) extends BaseTable[ProductDetailByPeriod](tag, "ProductDetailByPeriod"){
        def productId = column[Long]("product_id")
        def periodId = column[Long]("period_id")
        def numberOfPackages = column[Int]("number_of_packages")
        def quantityByPackage = column[Int]("quantity_by_package")
        def buyingPrice = column[Int]("buying_price")
        def sellingPrice = column[Int]("selling_price")

        def * = (id, productId, periodId, numberOfPackages, quantityByPackage, buyingPrice, sellingPrice) <> (ProductDetailByPeriod.tupled, ProductDetailByPeriod.unapply _)
    }

    val productDetailByPeriodQ = TableQuery[ProductDetailByPeriodTable]


    class CountTable(tag: Tag) extends BaseTable[Count](tag, "Count"){
        def productId = column[Long]("product_id")
        def periodId = column[Long]("period_id")
        def remainingQuantity = column[Int]("remaining_quantity")
        def date = column[java.sql.Timestamp]("date")

        def * = (id, periodId, productId, remainingQuantity, date) <> (Count.tupled, Count.unapply _)
    }

    val countQ = TableQuery[CountTable]
}
