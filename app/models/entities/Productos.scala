package models.entities

case class Producto(id: Long, producto: String, precioActual: Int, calorias: Int) extends BaseEntity

case class Stock(id: Long, producto: Producto, fecha: java.sql.Timestamp, cantidad: Int) extends BaseEntity