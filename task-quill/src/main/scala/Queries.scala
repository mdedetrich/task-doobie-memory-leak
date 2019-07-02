import io.getquill.{PostgresMonixJdbcContext, SnakeCase}
import monix.eval.Task
import org.flywaydb.core.Flyway

class Queries(ctx: PostgresMonixJdbcContext[SnakeCase]) {
  import ctx._

  implicit private val testTableSchemaMeta = schemaMeta[TestTable]("data.test_table")

  def createTestTable(id: Long, firstName: String, lastName: String): Task[Long] = {
    val testTable = TestTable(id, firstName, lastName)
    ctx.run {
      quote {
        query[TestTable].insert(lift(testTable))
      }
    }
  }

  def getTestTable(id: Long): Task[Option[TestTable]] =
    ctx
      .run {
        quote {
          query[TestTable].filter(_.id == lift(id)).take(1)
        }
      }
      .map(_.headOption)

  def updateTestTable(id: Long, firstName: String, lastName: String): Task[Long] =
    ctx.run {
      quote {
        query[TestTable].filter(_.id == lift(id)).update(_.firstName -> lift(firstName), _.lastName -> lift(lastName))
      }
    }

  def deleteTestTable(id: Long): Task[Long] =
    ctx.run {
      quote {
        query[TestTable].filter(_.id == lift(id)).delete
      }
    }

  def migrate() = {
    val flyway = Flyway.configure().dataSource(ctx.dataSource)
    flyway.load().migrate()
  }

}
