package com.yadavan88.opaque

import models._

import java.time.LocalDate

class OpaqueSpec extends munit.FunSuite {

  test("opaque models test for valid case") {

    val patient = Patient("P1", Age(10), Height(100), Weight(50))

    assert(patient.age.value == 10)
    assert(patient.height.value == 100)
    assert(patient.weight.value == 50)
  }

  test("opaque models test with safe building") {

    val patient = for {
      age <- Age.safe(10)
      height <- Height.safe(100)
      weight <- Weight.safe(90)
    }yield Patient("P2",age, height, weight)

    assert(patient.isDefined)
    assert(patient.get.age.value == 10)
  }

  test("opaque models should not build for invalid age") {

    val patient = for {
      age <- Age.safe(-1)
      height <- Height.safe(100)
      weight <- Weight.safe(90)
    }yield Patient("P3",age, height, weight)

    assert(patient.isEmpty)
  }

  test("opaque models should get the height in meter") {

    val patient = Patient("P4", Age(25), Height(170), Weight(65))
    assertEquals(patient.height.toMeter, BigDecimal(1.7))
  }

  test("context bounded opaque type should be okay") {
    val seniorAge = SeniorAge.safe(70)
    assert(seniorAge.isDefined)
  }

  test("context bounded opaque type should return None for unsafe value") {
    val seniorAge = SeniorAge.safe(110)
    assert(seniorAge.isEmpty)
  }

  test("build a patient using Senior age") {
    val seniorAge = SeniorAge.safe(70)
    assert(seniorAge.isDefined)
    val patient = Patient("P5", seniorAge.get, Height(170), Weight(65))
    assert(patient.age.value == 70)
  }

  test("access undelying apis for context bounded opaque types") {
    val dt: Option[MyLocalDate] = MyLocalDate.parse("2021-03-24")
    val now = LocalDate.parse("2021-03-24")
    assert(dt.isDefined)
    assert(dt.get.isEqual(now))
  }


}