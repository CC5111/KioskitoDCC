package controllers
import play.api.mvc._
import models.daos._
import javax.inject.{Inject, Singleton}

import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

import scala.concurrent.ExecutionContext

@Singleton
class CountsController extends Controller{
    def counts() = play.mvc.Results.TODO
}