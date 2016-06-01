package models.entities

case class Compra(id: Long, fecha: java.sql.Timestamp) extends BaseEntity

case class DetalleCompra(id: Long, compra: Compra, paquetes: Int, unidadesPorPaquete: Int, precioPaquete: Int) extends BaseEntity