import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import de.heikoseeberger.akkahttpjackson.JacksonSupport

import java.util.UUID

// step 1
import spray.json._

case class Person(name: String, age: Int)
case class UserAdded(id: String, timestamp: Long)

// step 2
trait PersonJsonProtocol extends DefaultJsonProtocol {
  implicit val personFormat = jsonFormat2(Person)
  implicit val userAddedFormat = jsonFormat2(UserAdded)
}

// step 3
object AkkaHttpJson extends PersonJsonProtocol with SprayJsonSupport {

  implicit val system = ActorSystem(Behaviors.empty, "AkkaHttpJson")

  val route: Route = (path("api" / "user") & post) {
    entity(as[Person]) { person: Person =>
      complete(UserAdded(UUID.randomUUID().toString, System.currentTimeMillis()))
    }
    //complete("Yep, roger that")
  }

  def main(args: Array[String]): Unit = {
    Http().newServerAt("localhost", 8081).bind(route)
  }
}
/*
object AkkaHttpCirce extends FailFastCirceSupport {
  import io.circe.generic.auto._ // implicit encoders/decoders

  implicit val system = ActorSystem(Behaviors.empty, "AkkaHttpJson")

  val route: Route = (path("api" / "user") & post) {
    entity(as[Person]) { person: Person =>
      complete(UserAdded(UUID.randomUUID().toString, System.currentTimeMillis()))
    }
    //complete("Yep, roger that")
  }

  def main(args: Array[String]): Unit = {
    Http().newServerAt("localhost", 8082).bind(route)
  }
 */
/*
object AkkaHttpJackson extends JacksonSupport {

  implicit val system = ActorSystem(Behaviors.empty, "AkkaHttpJson")

  val route: Route = (path("api" / "user") & post) {
    entity(as[Person]) { person: Person =>
      complete(UserAdded(UUID.randomUUID().toString, System.currentTimeMillis()))
    }
    //complete("Yep, roger that")
  }

  def main(args: Array[String]): Unit = {
    Http().newServerAt("localhost", 8083).bind(route)
  }
 */
}