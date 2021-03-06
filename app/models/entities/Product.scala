package models.entities

case class Product(id: Long, product: String, calories: Int, currentPrice: Int) extends BaseEntity

case class Stock(id: Long, productId: Long, stock: Int, date: java.sql.Timestamp) extends BaseEntity

case class ProductWithStock(id: Long, product: String, salePrice: Int, stock: Int)