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
class PeriodDAO extends BaseDAO[PeriodTable, Period]{
  import dbConfig.driver.api._

  override protected val tableQ = SlickTables.periodQ

  def all: Future[Seq[Period]] = {
    db.run(tableQ.result)
  }


    def getPeriodsTotalCost: Future[Seq[(java.sql.Timestamp, Option[Int])]] = {
        val detailQ = SlickTables.productDetailByPeriodQ

        val query = (for {
            (period, detail) <- tableQ join detailQ on (_.id === _.periodId)
        } yield (period.startingDate, detail.buyingPrice))
            .groupBy(_._1).map {
                case (date, pairs) => (date, pairs.map(_._2).sum)
            }

        println(query.result.statements: Iterable[String])
        db.run(query.result)
    }


}

@Singleton
class CountDAO extends BaseDAO[CountTable, Count]{
  import dbConfig.driver.api._

  override protected val tableQ = SlickTables.countQ

  def all: Future[Seq[Count]] = {
    db.run(tableQ.result)
  }

    def getCountsWithEarnings(): Future[Seq[(java.sql.Timestamp, Option[Int])]] = {
        val detailQ = SlickTables.productDetailByPeriodQ

        val query = (for {
            (count, detail) <- tableQ join detailQ on (_.periodId === _.periodId)
        } yield (count, detail))
            .groupBy(_._1.date).map {
            case (date, countsAndDetails) =>
                (date, countsAndDetails.map (x => ((x._2.quantityByPackage * x._2.numberOfPackages) - x._1.remainingQuantity) * x._2.sellingPrice).sum)
        }

        println(query.result.statements: Iterable[String])
        db.run(query.result)
    }
}

@Singleton
class ProductDetailByPeriodDAO extends BaseDAO[ProductDetailByPeriodTable, ProductDetailByPeriod]{
    import dbConfig.driver.api._

    override protected val tableQ = SlickTables.productDetailByPeriodQ

    def all: Future[Seq[ProductDetailByPeriod]] = {
        db.run(tableQ.result)
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