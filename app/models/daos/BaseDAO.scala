package models.daos

import java.sql.Timestamp
import javax.inject.{Inject, Singleton}

import models.entities.BaseEntity
import models.persistence.SlickTables
import models.persistence.SlickTables._
import models.entities._
import play.api.Play
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.CanBeQueryCondition

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

trait AbstractBaseDAO[T,A] {
  def insert(row : A): Future[Long]
  def insert(rows : Seq[A]): Future[Seq[Long]]
  def update(row : A): Future[Int]
  def update(rows : Seq[A]): Future[Unit]
  def findById(id : Long): Future[Option[A]]
  def findByFilter[C : CanBeQueryCondition](f: (T) => C): Future[Seq[A]]
  def deleteById(id : Long): Future[Int]
  def deleteById(ids : Seq[Long]): Future[Int]
  def deleteByFilter[C : CanBeQueryCondition](f:  (T) => C): Future[Int]
}

@Singleton
class ProductDAO extends BaseDAO[ProductTable, Product]{
    import dbConfig.driver.api._

    override protected val tableQ = SlickTables.productQ

    def all: Future[Seq[Product]] = {
        db.run(tableQ.result)
    }

    def getAllWithStock : Future[Seq[(Product, Int)]] = {
        val stockQ = SlickTables.stockQ

        val query = for {
            (product, stock) <- tableQ join stockQ on (_.id === _.productId)
        } yield (product, stock)

        db.run(query.result).map { results =>
            val grouped: Map[(Long, Product), Seq[(Product, Stock)]] = results.groupBy(x => (x._1.id, x._1))
            grouped.map{
                case (prod, rest) => (prod._2, rest.map(_._2).sortBy(_.date.getTime).lastOption.map(_.stock).getOrElse(0))
            }.toSeq
        }
    }

    def updateCurrentPrice(id: Long, newPrice: Int) = {
        val query = tableQ.filter(_.id === id).map(
            product => product.currentPrice
        )

        db.run(query.update(newPrice))

    }
}

@Singleton
class PurchaseDAO extends BaseDAO[PurchaseTable, Purchase]{
  import dbConfig.driver.api._

  override protected val tableQ = SlickTables.purchaseQ

  def all: Future[Seq[Purchase]] = {
    db.run(tableQ.result)
  }


    def getPeriodsTotalCost: Future[Seq[(Long, java.sql.Timestamp, Option[Int])]] = {
        val detailQ = SlickTables.purchaseDetailQ

        val query = (for {
            (period, detail) <- tableQ join detailQ on (_.id === _.purchaseId)
        } yield (period, detail))
            .groupBy(x => (x._1.date, x._1.id)).map {
                case (purchase, pairs) => (purchase._2, purchase._1, pairs.map(x => x._2.pricePerPackage * x._2.numberOfPackages).sum)
            }

        println(query.result.statements: Iterable[String])
        db.run(query.result)
    }

    def purchaseDetail(id: Long) : Future[(Option[Purchase], Seq[(String, PurchaseDetailByProduct)])] = {
        val detailQ = SlickTables.purchaseDetailQ
        val productQ = SlickTables.productQ

        val purchaseDetails = for {
            detail <- detailQ if detail.purchaseId === id
            product <- detail.product
        } yield (product.product, detail)

        findById(id).flatMap{purchase =>
            db.run(purchaseDetails.result).map{details =>

                (purchase, details)
            }
        }

    }

}

@Singleton
class CountDAO extends BaseDAO[CountTable, Count]{
    import dbConfig.driver.api._

    override protected val tableQ = SlickTables.countQ

    def countDetail(id: Long) : Future[(Option[Count], Seq[(String, CountDetailByProduct)])] = {
        val detailQ = SlickTables.countDetailQ

        val countDetails = for {
            detail <- detailQ if detail.countId === id
            product <- detail.product
        } yield (product.product, detail)

        findById(id).flatMap{count =>
            db.run(countDetails.result).map{details =>

                (count, details)
            }
        }

    }

    def totalCaloriesPerCount : Future[Seq[CaloriesPerCount]] = {
        val detailQ = SlickTables.countDetailQ
        val productQ = SlickTables.productQ

        val query = (for {
            ((count, detail), product) <- tableQ join detailQ on (_.id === _.countId) join productQ on (_._2.productId === _.id)

        } yield (count.date, detail, product))
            .groupBy(_._1).map{
            case (date, details) => (date, details.map(d => d._2.soldQuantity * d._3.calories).sum)
        }

        println(query.result.statements)
        db.run(query.result).map{
            res => res.map(x => CaloriesPerCount(x._1, x._2))
        }
    }
}

@Singleton
class CountDetailByProductDAO extends BaseDAO[CountDetailByProductTable, CountDetailByProduct]{
  import dbConfig.driver.api._

  override protected val tableQ = SlickTables.countDetailQ

  def all: Future[Seq[CountDetailByProduct]] = {
    db.run(tableQ.result)
  }

    def getCountsWithEarnings(): Future[Seq[(Long, java.sql.Timestamp, Int, Option[Int])]] = {
        val countQ = SlickTables.countQ

        val query = (for {
            count <- countQ
            detail <- tableQ if detail.countId === count.id
        } yield (count, detail))
            .groupBy(x=> (x._1.date, x._1.id, x._1.actualEarnings)).map {
                case (dateAndActualEarn, countDetail) => (dateAndActualEarn._2, dateAndActualEarn._1, dateAndActualEarn._3, countDetail.map(x => x._2.soldQuantity * x._2.sellingPrice).sum)
            }

        println(query.result.statements: Iterable[String])
        db.run(query.result)
    }
}

@Singleton
class ProductDetailByPeriodDAO extends BaseDAO[PurchaseDetailByProductTable, PurchaseDetailByProduct]{
    import dbConfig.driver.api._

    override protected val tableQ = SlickTables.purchaseDetailQ

    def all: Future[Seq[PurchaseDetailByProduct]] = {
        db.run(tableQ.result)
    }
}

@Singleton
class StockDAO extends BaseDAO[StockTable, Stock] {
    import dbConfig.driver.api._

    override protected val tableQ = SlickTables.stockQ

    def getLastWithPositiveStock : Future[Seq[ProductWithStock]] = {
        val productQ = SlickTables.productQ


        val query = for {
            (product, stock) <- productQ join tableQ on (_.id === _.productId)
        } yield (product, stock)

        db.run(query.result).map{ r =>
            val rr: Seq[ProductWithStock] = r.groupBy( x => (x._1.id, x._1.product, x._1.currentPrice)).map{ x =>
                val stocks: Seq[Stock] = x._2.map{_._2}
                ProductWithStock(x._1._1, x._1._2, x._1._3, stocks.sortBy(_.date.getTime()).lastOption.map{_.stock}.getOrElse(0))
            }.toSeq
            rr.filter(_.stock > 0)
        }

    }

    def createNewStock(productId: Long, newStock: Int, currentTimestamp: Timestamp ) = {

        val query = tableQ.filter(_.productId === productId).sortBy(_.date.desc).take(1).map{_.stock}
        println(query.result.statements)

        db.run(query.result).map {lastStock =>
            val previousStock: Int = lastStock.head
            val stock : Int = previousStock + newStock

            db.run(tableQ returning tableQ.map(_.id) += Stock(0, productId, stock, currentTimestamp))
        }

        /*         val productStocks: Query[StockTable, Stock, Seq] = tableQ.filter(_.productId === productId)

                    db.run(productStocks.result).map { stocks =>
                    val previousStock: Int = stocks.sortBy(_.date.getTime()).lastOption.map{_.stock}.getOrElse(0)
                    val stock : Int = previousStock + newStock

                    db.run(tableQ returning tableQ.map(_.id) += Stock(0, productId, stock, currentTimestamp))

                }*/

    }
}

abstract class BaseDAO[T <: BaseTable[A], A <: BaseEntity]() extends AbstractBaseDAO[T,A] with HasDatabaseConfig[JdbcProfile] {
  protected lazy val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfigProvider.get[JdbcProfile](Play.current)
  import dbConfig.driver.api._

  protected val tableQ: TableQuery[T]

  def insert(row : A): Future[Long] ={
    insert(Seq(row)).map(_.head)
  }

  def insert(rows : Seq[A]): Future[Seq[Long]] ={
    db.run(tableQ returning tableQ.map(_.id) ++= rows.filter(_.isValid))
  }

  def update(row : A): Future[Int] = {
    if (row.isValid)
      db.run(tableQ.filter(_.id === row.id).update(row))
    else
      Future{0}
  }

  def update(rows : Seq[A]): Future[Unit] = {
    db.run(DBIO.seq((rows.filter(_.isValid).map(r => tableQ.filter(_.id === r.id).update(r))): _*))
  }

  def findById(id : Long): Future[Option[A]] = {
    db.run(tableQ.filter(_.id === id).result.headOption)
  }

  def findByFilter[C : CanBeQueryCondition](f: (T) => C): Future[Seq[A]] = {
    db.run(tableQ.withFilter(f).result)
  }

  def deleteById(id : Long): Future[Int] = {
    deleteById(Seq(id))
  }

  def deleteById(ids : Seq[Long]): Future[Int] = {
    db.run(tableQ.filter(_.id.inSet(ids)).delete)
  }

  def deleteByFilter[C : CanBeQueryCondition](f:  (T) => C): Future[Int] = {
    db.run(tableQ.withFilter(f).delete)
  }

}