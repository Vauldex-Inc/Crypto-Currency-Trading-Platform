package models.service

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import cats.data.OptionT
import errors._
import models.domain.Notification

@Singleton
class NotificationService @Inject()(
    notificationDAO:  models.dao.NotificationDAO,
    notificationRepo: models.repo.NotificationRepo,
    protected val dbConfigProvider: DatabaseConfigProvider,
    protected implicit val ec: ExecutionContext)
  extends HasDatabaseConfigProvider[utils.db.PostgresDriver] {
  import driver.api._
  private val logger = play.api.Logger(this.getClass())

  def add(notification: Notification): OptionT[Future, MrError] = OptionT {
    notificationRepo.exists(notification.code, notification.idAccountRef) flatMap { exists =>
      if (exists) {
        logger.debug(s"Unable to add notification: ${notification.toString} duplicate")
        Future(Some(ObjectConflicts))
      } else {
        notificationRepo.add(notification) map { count =>
          if (count == 1) {
            logger.debug(s"Notification: ${notification.toString} successfully added")
            None
          } else {
            logger.error(s"Unknown error occurred in adding notification ${notification.toString}")
            Some(UnknownError)
          }
        }
      }}
  }

  def delete(notification: Notification): OptionT[Future, MrError] = OptionT {
    notificationRepo.exists(notification.code, notification.idAccountRef) flatMap { exists =>
      if (exists) {
        notificationRepo.delete(notification.code, notification.idAccountRef) map { count =>
          if (count == 1) {
            logger.debug(s"Notification: ${notification.toString} successfully added")
            None
          } else {
            logger.error(s"Unknown error occurred in adding notification ${notification.toString}")
            Some(UnknownError)
          }
        }
      } else {
        logger.debug(s"Unable to add notification: ${notification.toString} duplicate")
        Future(Some(ObjectConflicts))
      }}
  }
}
