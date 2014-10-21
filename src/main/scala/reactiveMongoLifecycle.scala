import akka.actor.ActorSystem
import reactivemongo.api.MongoDriver
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

import org.apache.logging.log4j.LogManager

object ReactiveMongoLifecycle {
  val logger = LogManager.getLogger(this.getClass.getCanonicalName)

  def completeCycle(i: Int) {
    val system = ActorSystem()

    import system.dispatcher

    val driver = MongoDriver(system)
    val connection = driver.connection(nodes = Seq("localhost"))
    val db = connection.db("ReactiveMongoLifecycle")

    Try {
      logger.info("Create collection")
      db.collection("foo")
      connection.close()
    } match {
      case Success(x) => logger.debug("Created foo collection and closed connection")
      case Failure(x) => logger.debug("Failed to create foo collection and close connection:", x)
    }

    Try {
      val future = Future {
        while (driver.connections.size > 0)
          Thread.sleep(100)
      }

      Await.result(future, 2.seconds)
    } match {
      case Success(x) => logger.debug("All mongoDB connections closed.")
      case Failure(x) => logger.debug("Failed to close all mongoDB connections, " + driver.connections.size + " remain: ", x)
    }

    for (connection <- driver.connections) {
      logger.debug("Connection remains open:" + connection)
    }

    Try {
      driver.close()
      system.shutdown()
      driver.system.awaitTermination(5.seconds)
    } match {
      case Success(x) => logger.debug("The mongoDB driver has been closed.")
      case Failure(x) => logger.debug("The mongoDB driver failed to close: ", x)
    }
  }

  def main(args: Array[String]) {
    val count = if (args.size > 0) args(0).toInt else 1
    for(i <- 1 to count) {
      logger.info(s"~~~~~ Runnning Test #$i ~~~~~")
      completeCycle(i)
    }
    logger.info("Done")
  }
}
