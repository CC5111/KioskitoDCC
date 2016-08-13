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
}

@Singleton
class PeriodDAO extends BaseDAO[PurchaseTable, Purchase]{
  import dbConfig.driver.api._

  override protected val tableQ = SlickTables.purchaseQ

  def all: Future[Seq[Purchase]] = {
    db.run(tableQ.result)
  }


    def getPeriodsTotalCost: Future[Seq[(java.sql.Timestamp, Option[Int])]] = {
        val detailQ = SlickTables.purchaseDetailQ

        val query = (for {
            (period, detail) <- tableQ join detailQ on (_.id === _.purchaseId)
        } yield (period.date, detail))
            .groupBy(_._1).map {
                case (date, pairs) => (date, pairs.map(x => x._2.pricePerPackage * x._2.numberOfPackages).sum)
            }

        println(query.result.statements: Iterable[String])
        db.run(query.result)
    }


}

@Singleton
class CountDAO extends BaseDAO[CountDetailByProductTable, CountDetailByProduct]{
  import dbConfig.driver.api._

  override protected val tableQ = SlickTables.countDetailQ

  def all: Future[Seq[CountDetailByProduct]] = {
    db.run(tableQ.result)
  }

    def getCountsWithEarnings(): Future[Seq[(java.sql.Timestamp, Option[Int])]] = {
        val countQ = SlickTables.countQ

        val query = (for {
            count <- countQ
            detail <- tableQ if detail.countId === count.id
        } yield (count.date, detail))
            .groupBy(_._1).map {
                case (date, countDetail) => (date, countDetail.map(x => x._2.soldQuantity * x._2.sellingPrice).sum)
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
            val rr: Seq[ProductWithStock] = r.groupBy( x => (x._1.id, x._1.product)).map{ x =>
                val stocks: Seq[Stock] = x._2.map{_._2}
                ProductWithStock(x._1._1, x._1._2, stocks.sortBy(_.date.getTime()).lastOption.map{_.stock}.getOrElse(0))
            }.toSeq
            rr.filter(_.stock > 0)
        }

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