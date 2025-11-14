package com.github.mehrdadfalahati.reservation.service.loadtest

import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.http.Predef._
import io.gatling.commons.validation.SuccessWrapper

import java.time.Instant
import scala.concurrent.duration._

class ReservationSimulation extends Simulation {

  private val BaseUrl = sys.props.getOrElse("loadtest.baseUrl", "http://localhost:8080")
  private val RampUsers = sys.props.get("loadtest.rampUsers").map(_.toInt).getOrElse(25)
  private val HoldUsers = sys.props.get("loadtest.holdUsers").map(_.toInt).getOrElse(50)
  private val RampSeconds = sys.props.get("loadtest.rampSeconds").map(_.toInt).getOrElse(30)
  private val HoldSeconds = sys.props.get("loadtest.holdSeconds").map(_.toInt).getOrElse(60)
  private val Iterations = sys.props.get("loadtest.iterations").map(_.toInt).getOrElse(5)
  private val SlaMs = sys.props.get("loadtest.slaMs").map(_.toInt).getOrElse(100)

  private val httpProtocol = http
    .baseUrl(BaseUrl)
    .acceptHeader("*/*")
    .contentTypeHeader("application/json")

  private val credentialsFeeder = csv("data/users.csv").circular
  private val bearerHeader: Expression[String] = session =>
    ("Bearer " + session("token").as[String]).success

  private val loginBody: Expression[String] = session => {
    val username = session("username").as[String]
    val password = session("password").as[String]
    s"""{"username":"$username","password":"$password"}""".success
  }

  private val login = exec(
    http("Authenticate user")
      .post("/api/auth/login")
      .body(StringBody(loginBody))
      .asJson
      .check(status.is(200))
      .check(jsonPath("$.token").saveAs("token"))
  ).exitHereIfFailed

  private val reservationBody: Expression[String] = session => {
    val requestedTime = session("requestedTime").as[String]
    s"""{"requestedTime":"$requestedTime"}""".success
  }

  private val createReservation = exec { session =>
    // Spread requests across different times to reduce contention
    // Each user gets a different base time within the 1000-hour range
    val userId = session.userId
    val iterationCount = session("iterationCount").asOption[Int].getOrElse(0)

    // Calculate hours offset: userId ensures different users get different times
    // iterationCount ensures each iteration for the same user gets a different time
    val hoursOffset = (userId * 10 + iterationCount * 2) % 1000
    val baseTime = Instant.parse("2025-01-01T00:00:00Z")
    val requestedTime = baseTime.plusSeconds(hoursOffset * 3600)

    session
      .set("requestedTime", requestedTime.toString)
      .set("iterationCount", iterationCount + 1)
  }.exec(
    http("Create reservation")
      .post("/api/reservations")
      .header("Authorization", bearerHeader)
      .body(StringBody(reservationBody))
      .asJson
      .check(status.is(201))
  )

  private val listReservations =
    exec(
      http("List reservations")
        .get("/api/reservations")
        .header("Authorization", bearerHeader)
        .check(status.is(200))
    )

  private val scenarioBuilder = scenario("Reservation SLA validation")
    .feed(credentialsFeeder)
    .pause(100.millis, 500.millis) // Random pause to avoid auth stampede
    .exec(login)
    .repeat(Iterations) {
      exec(createReservation)
        .pause(250.millis)
        .exec(listReservations)
        .pause(250.millis)
    }

  setUp(
    scenarioBuilder.inject(
      rampConcurrentUsers(1).to(RampUsers).during(RampSeconds.seconds),
      constantConcurrentUsers(HoldUsers).during(HoldSeconds.seconds)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.max.lte(SlaMs),
      global.successfulRequests.percent.gte(99.0)
    )
}
