package com.yadavan88.traditional

object models {
  //age, height and weight is represented as Int. There is a chance to mix up while filling
  final case class Patient(id:String, age: Int, height:Int, weight:Int)

}

object improvedModels {
  class Age(value:Int) extends AnyVal
  class Height(value:Int) extends AnyVal
  class Weight(value:Int) extends AnyVal

  //better than using the same type(Int) for age, heigh, weight
  //but, there are drawbacks of heap usage during boxing
  final case class Patient(id:String, age: Age, height:Height, weight:Weight)

}
