import java.util.concurrent._

import monix.execution.Scheduler
import TaskHelper._
import com.typesafe.scalalogging.StrictLogging
import monix.eval.Task

import scala.util._

object Main extends App with StrictLogging {
  implicit val scheduler: Scheduler = Scheduler.global

  val tx = Tx()

  import tx._

  lazy val r = scala.util.Random

  val baseTransaction = {
    for {
      id <- Task { r.nextLong() }
      _  <- Queries.createTestTable(id, "test", "face").run.toTask
      q  = Queries.getTestTable(id)
      _  <- q.to[List].map(x => Queries.getTopFromList(x, q.sql)).toTask
      _  <- Queries.updateTestTable(id, "test2", "face2").run.toTask
      _  <- Queries.deleteTestTable(id).run.toTask
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
      2,
      TimeUnit.MILLISECONDS
    )
  }

  val amount = tx.migrate()
  logger.info(s"Migrate amount = $amount")

  logger.info("Simulating")
  simulate()
  Thread.sleep(Long.MaxValue)

}
