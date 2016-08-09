package models.entities

case class Product(id: Long, product: String, calories: Int, currentPrice: Int, currentStock: Int) extends BaseEntity
