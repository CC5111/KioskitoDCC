package implicits

import models.entities.CaloriesPerCount
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Writes}

/**
  *
  */
object JsonWrites {
    implicit val caloriesPerCountReads: Writes[CaloriesPerCount] = (
        (JsPath \ "date").write[java.sql.Timestamp] and
            (JsPath \ "totalCalories").write[Option[Int]]
        )(unlift(CaloriesPerCount.unapply))

}
