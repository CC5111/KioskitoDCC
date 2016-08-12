package models.entities

case class Period(id: Long, startingDate: java.sql.Timestamp, endDate: java.sql.Timestamp, earnings: Int) extends BaseEntity

case class ProductDetailByPeriod(id: Long, productId: Long, periodId: Long, numberOfPackages: Int,
                                 quantityByPackage: Int, buyingPrice: Int, sellingPrice: Int) extends BaseEntity

case class Count(id: Long, periodId: Long, productId: Long, remainingQuantity: Int, date: java.sql.Timestamp) extends BaseEntity