import java.util.concurrent.{Executors, SynchronousQueue, ThreadPoolExecutor, TimeUnit}

import cats.effect.{ContextShift, IO}
import com.zaxxer.hikari
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import doobie.hikari.HikariTransactor
import monix.execution.Scheduler
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext

case class Tx()(implicit scheduler: Scheduler) {

  implicit lazy val contextShift: ContextShift[IO] = IO.contextShift(scheduler)

  lazy val hikariDatasource: HikariDataSource = {
    val hikariConfig = new HikariConfig()

    hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres")
    hikariConfig.setUsername("postgres")
    hikariConfig.setPassword("mysecretpassword")

    hikariConfig.setLeakDetectionThreshold(7000)
    hikariConfig.setMaximumPoolSize(50)
    new hikari.HikariDataSource(hikariConfig)
  }

  val connectEC: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(32))
  val transactEC: ExecutionContext = ExecutionContext.fromExecutor(
    new ThreadPoolExecutor(
      0,
      Integer.MAX_VALUE,
      60,
      TimeUnit.SECONDS,
      new SynchronousQueue[Runnable]
    )
  )

  implicit lazy val xa: HikariTransactor[IO] = HikariTransactor[IO](
    hikariDatasource,
    connectEC,
    transactEC
  )

  def migrate() = {
    val flyway = Flyway.configure().dataSource(xa.kernel)
    flyway.load().migrate()
  }

}
