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

  class ProductoTable(tag: Tag) extends BaseTable[Producto](tag, "Producto"){
      def producto = column[String]("Nombre")
      def precioActual = column[Int]("precio_actual")
      def calorias = column[Int]("calorias")

      def * = (id, producto, precioActual, calorias) <> (Producto.tupled, Producto.unapply _)
  }

    val productQ = TableQuery[ProductoTable]

    class StockTable(tag: Tag) extends BaseTable[Stock](tag, "Stock"){
        def productoId = column[Long]("producto_id")
        def fecha = column[java.sql.Timestamp]("fecha")
        def cantidad = column[Int]("cantidad")

        def productoFK = foreignKey("producto_fk", productoId, productQ)(_.id)

        def * = (id, productQ, fecha, cantidad) <> (Stock.tupled, Stock.unapply _)
    }

    val stockQ = TableQuery[StockTable]

    class VentaTable(tag: Tag) extends BaseTable[Venta](tag, "Venta"){
        def fechaInicio = column[java.sql.Timestamp]("fecha_inicio")
        def fechaFin = column[java.sql.Timestamp]("fecha_fin")

        def * = (id, fechaInicio, fechaFin) <> (Venta.tupled, Venta.unapply _)
    }

    val ventaQ = TableQuery[VentaTable]

    class DetalleVentaTable(tag: Tag) extends BaseTable[DetalleVenta](tag, "DetalleVenta"){
        def ventaId = column[Long]("venta_id")
        def productoId = column[Long]("producto_id")
        def cantidadVendida = column[Int]("cantidad_vendida")
        def precioDeVenta = column[Int]("precio_venta")

        def ventaFK = foreignKey("venta_fk", ventaId, ventaQ)(_.id)
        def productoFK = foreignKey("producto_fk", productoId, productQ)(_.id)

        def * = (id, ventaId, productoId, cantidadVendida, precioDeVenta) <> (DetalleVenta.tupled, DetalleVenta.unapply _)
    }

    val detalleVentaQ = TableQuery[DetalleVentaTable]

    class CompraTable(tag: Tag) extends BaseTable[Compra](tag, "Compra"){
        def fecha = column[java.sql.Timestamp]("fecha")

        def * = (id, fecha) <> (Compra.tupled, Compra.unapply _)
    }

    val compraQ = TableQuery[CompraTable]

    class DetalleCompraTable(tag: Tag) extends BaseTable[DetalleCompra](tag, "DetalleCompra"){
        def compraId = column[Long]("compra_id")
        def productoId = column[Long]("producto_id")
        def paquetes = column[Int]("paquetes")
        def unidadesPorPaquete = column[Int]("unidades_paquete")
        def precioPorPaquete = column[Int]("precio_paquete")

        def compraFK = foreignKey("compra_fk", compraId, compraQ)(_.id)
        def productoFK = foreignKey("producto_fk", productoId, productQ)(_.id)

        def * = (id, compraId, productoId, paquetes, unidadesPorPaquete, precioPorPaquete) <> (DetalleCompra.tupled, DetalleCompra.unapply _)
    }

    val detalleCompraQ = TableQuery[DetalleCompraTable]

}
