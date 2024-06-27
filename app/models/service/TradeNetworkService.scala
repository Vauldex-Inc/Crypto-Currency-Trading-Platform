package models.service

import javax.inject._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import cats.data.EitherT
import cats.implicits._
import errors._
import models.repo.{ NetworkRepo, TradeNetworkRepo}

@Singleton
class TradeNetworkService @Inject()(
    tradeNetworkRepo: TradeNetworkRepo,
    networkRepo: NetworkRepo,
    protected val dbConfigProvider: DatabaseConfigProvider,
    protected implicit val ec: ExecutionContext)
  extends HasDatabaseConfigProvider[utils.db.PostgresDriver] {
  import driver.api._

  def add(keyNetwork: String): EitherT[Future, MrError, Unit] = {
    for {
      _ <- EitherT[Future, MrError, Unit] {
        networkRepo
          .exists(keyNetwork)
          .map(exists => if (exists) Right(()) else Left(ObjectNotExists))
      }
      _ <- EitherT[Future, MrError, Unit] {
        tradeNetworkRepo
          .exists(keyNetwork)
          .map(exists => if (exists) Left(ObjectConflicts) else Right(()))

      }
      _ <- EitherT[Future, MrError, Unit] {
        tradeNetworkRepo
          .add(keyNetwork)
          .map(count => if (count == 1) Right(()) else Left(UnknownError))
      }
    } yield ()
  }
}
