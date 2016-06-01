package models.entities

case class Venta(id: Long, fechaInicio: java.sql.Timestamp, fechaFin: java.sql.Timestamp) extends BaseEntity

case class DetalleVenta(id: Long, venta: Venta, producto: Producto, cantidadVendida: Int, precioDeVenta: Int) extends BaseEntity
