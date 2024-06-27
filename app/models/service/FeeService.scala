package models.service

import javax.inject._
import scala.concurrent.{ ExecutionContext, Future }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import cats.data.{ EitherT, OptionT }
import cats.implicits._
import models.domain.Fee
import models.dao.FeeDAO
import models.repo.{ CurrencyNetworkRepo, FeeRepo }
import errors._

@Singleton
class FeeService @Inject()(
    feeDAO: FeeDAO,
    feeRepo: FeeRepo,
    currencyNetworkRepo: CurrencyNetworkRepo,
    protected val dbConfigProvider: DatabaseConfigProvider,
    protected implicit val ec: ExecutionContext)
  extends HasDatabaseConfigProvider[utils.db.PostgresDriver] {
  import driver.api._
  private val logger = play.api.Logger(this.getClass())

  def add(fee: Fee): EitherT[Future, MrError, Unit] = {
    for {
      _ <- EitherT[Future, MrError, Unit] {
        currencyNetworkRepo
          .exists(fee.codeCurrency, fee.keyNetwork, fee.keyIssuer)
          .map(exists => if (exists) Right(()) else Left(ObjectNotExists))
      }
      _ <- EitherT[Future, MrError, Unit] {
        feeRepo
          .exists(fee.codeCurrency, fee.keyNetwork, fee.keyIssuer, fee.isDeposit)
          .map(exists => if (exists) Left(ObjectConflicts) else Right(()))
      }
      _ <- EitherT[Future, MrError, Unit] {
        feeRepo
          .add(fee)
          .map(count => if (count == 1) Right(()) else Left(UnknownError) )
      }
    } yield ()
  }

  def update(fee: Fee): EitherT[Future, MrError, Unit] = {
    for {
      _ <- EitherT[Future, MrError, Unit] {
        feeRepo
          .exists(fee.codeCurrency, fee.keyNetwork, fee.keyIssuer, fee.isDeposit)
          .map(exists => if (exists) Right(()) else Left(ObjectNotExists))
        }
      _ <- EitherT[Future, MrError, Unit] {
        db.run({
          feeDAO
            .query(fee.codeCurrency, fee.keyNetwork, fee.keyIssuer, fee.isDeposit)
            .map(r => (r.value, r.isPercentage))
            .update((fee.value, fee.isPercentage))
        }) map (count => if (count > 0) Right(()) else Left(UnknownError))
      }
    } yield ()
  }
}
