import cats.effect.IO
import doobie.free.connection.ConnectionIO
import doobie._
import doobie.implicits._
import monix.eval.Task

object TaskHelper {
  implicit final class DoobieTaskHelper[T](connectionIO: ConnectionIO[T]) {
    def toTask(implicit xa: Transactor[IO]): Task[T] =
      Task.deferFuture(connectionIO.transact(xa).unsafeToFuture())
  }
}
