package com.yadavan88.opaque

import com.yadavan88.opaque.models.Patient

import java.time.LocalDate

object models {

  opaque type Age = Int

  object Age {
    def apply(value: Int): Age = value

    def safe(value: Int): Option[Age] = if (value > 0 && value < 100) Some(value) else None

    extension (age: Age) {
      def value: Int = age
    }
  }

  opaque type Height = Int

  object Height {
    def apply(value: Int): Height = value

    def safe(value: Int): Option[Height] = if (value > 40 && value < 250) then Some(value) else None

    extension (height: Height) {
      def value: Int = height
      def toMeter: BigDecimal = BigDecimal(height) / 100
    }
  }

  opaque type Weight = Int

  object Weight {
    def apply(value: Int): Weight = value

    def safe(value: Int): Option[Weight] = if (value > 0) Some(value) else None

    extension (weight: Weight) {
      def value: Int = weight
    }
  }

  opaque type SeniorAge <: Age = Int
  object SeniorAge {
    def safe(value: Int): Option[Age] = if (value > 60 && value < 100) Some(value) else None
  }

  opaque type MyLocalDate <: LocalDate = LocalDate
  object MyLocalDate {
    def parse(dt:String): MyLocalDate = LocalDate.parse(dt)
  }

  final case class Patient(id: String, age: Age, height: Height, weight: Weight)

  Patient("P1", 100, 10, 10) //still valid since opaque type declaration is in same scope
}

object impl {
  // Patient("P1", 100, 10, 10) : doesnt compile since opaque type declaration is not done in same scope

}