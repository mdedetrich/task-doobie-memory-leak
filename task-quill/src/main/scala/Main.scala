import java.util.concurrent._

import com.typesafe.scalalogging.StrictLogging
import io.getquill.{PostgresMonixJdbcContext, SnakeCase}
import io.getquill.context.monix.Runner
import monix.eval.Task
import monix.execution.Scheduler

import scala.util.{Failure, Success}

object Main extends App with StrictLogging {
  implicit val scheduler: Scheduler = Scheduler.global

  lazy val ctx: PostgresMonixJdbcContext[SnakeCase] =
    new PostgresMonixJdbcContext(SnakeCase, "database", Runner.using(Scheduler.io()))

  lazy val queries = new Queries(ctx)

  lazy val r = scala.util.Random

  val baseTransaction = {
    for {
      id <- Task { r.nextLong() }
      _  <- queries.createTestTable(id, "test", "face")
      _  <- queries.getTestTable(id)
      _  <- queries.updateTestTable(id, "test2", "face2")
      _  <- queries.deleteTestTable(id)
    } yield ()
  }

  def simulate() = {
    val service = Executors.newSingleThreadScheduledExecutor
    service.scheduleAtFixedRate(
      () => {
        val task = for {
          _ <- baseTransaction
          _ <- baseTransaction.forkAndForget
          _ <- baseTransaction
        } yield ()

        task.runToFuture.onComplete {
          case Success(_) => logger.info("Transaction complete!")
          case Failure(t) => logger.error("error", t)
        }
      },
      0,
      16,
      TimeUnit.MILLISECONDS
    )
  }

  val amount = queries.migrate()
  logger.info(s"Migrate amount = $amount")

  logger.info("Simulating")
  simulate()
  Thread.sleep(Long.MaxValue)
}
