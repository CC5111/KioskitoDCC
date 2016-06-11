package models.entities

case class Producto(id: Long, producto: String, calorias: Int) extends BaseEntity

case class Stock(id: Long, productoId: Long, fecha: java.sql.Timestamp, cantidad: Int) extends BaseEntity