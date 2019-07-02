import com.typesafe.scalalogging.StrictLogging
import doobie._
import doobie.implicits._

trait Queries extends StrictLogging {
  def createTestTable(id: Long, firstName: String, lastName: String): Update0 =
    sql"""
      INSERT INTO data.test_table(
        id,
        first_name,
        last_name
      ) VALUES (
        $id,
        $firstName,
        $lastName
      )
    """.update

  def getTestTable(id: Long): Query0[TestTable] =
    sql"""
      SELECT id,
             first_name,
             last_name
      FROM data.test_table
      WHERE id = $id
      LIMIT 1
    """.query[TestTable]

  def updateTestTable(id: Long, firstName: String, lastName: String): Update0 =
    sql"""
      UPDATE data.test_table
      SET first_name = ${firstName}, last_name = ${lastName}
      WHERE id = $id
    """.update

  def deleteTestTable(id: Long): Update0 =
    sql"""
      DELETE
      FROM data.test_table
      WHERE id = $id
    """.update

  def getTopFromList[A](resultSet: List[A], queryStr: String): Option[A] =
    resultSet match {
      case Nil => None
      case head :: tail =>
        if (tail.nonEmpty) {
          logger.info(s"Expected only one element but found more than one!. Sql query executed is: $queryStr")
          logger.info(s"The first row from the result set is: ${head.toString}")
          logger.info(s"The second row from the result set is: ${tail.headOption.getOrElse("EMPTY").toString}")
        }
        Some(head)
    }

}

object Queries extends Queries
