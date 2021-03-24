## Scala-3 Opaque Type Alias Sample

This is a very simple example of the newly introduced Opaque Type Alias in Scala 3

### What is Opaque Type Alias

Opaque Type Alias is a new feature introduced in Scala 3. 
It helps to implement the domain specific models in a better way, with less overhead. 
Prior to Scala 3, this was generally implemented using Value Classes. However, there were some sort of performance overheads during boxing operations.

### How to create an opaque type

Consider that we are developing a software for the hospital domain. One of the important domain entity will be a Patient.
Let's see how we can define a very basic Patient entity.

```scala
final case class Patient(id:String, age:Int, height:Int, weight:Int)
```

In the above case class, we have 3 fields which are of type `Int`. While filling up the data, there is a chance that `height` value is set for `weight` field and vice versa.
Since both are `Int` fields, the compiler will not show any errors. 
To avoid such mistakes, we can define a special model for these values. Here, opaque types becomes useful.

Let's see how we can define opaque type. 
```scala
opaque type Age = Int
```
The above line of code will define a type alias for Int as Age. This type then can be used in the case class.
```scala
final case class Patient(id:String, age:Age, height:Int, weight:Int)
```

Note the type of `age` field in the above case class.

Similarly, we can define opaque types for weight and height as well.
```scala
opaque type Weight = Int
opaque type Height = Int
```
Note that, these opaque types can be interchangeably used only in the same scope where it is defined.
We can move them into an object, so that they can be used without any issues later.


```scala
object models {
  final case class Patient(id:String, age:Age, height:Height, weight:Weight)
  opaque type Age = Int
  opaque type Weight = Int
  opaque type Height = Int
}
```

Now, let's see how we can create `Patient` instance.
```scala
object impl {
  Patient("P1", 11, 100, 35)
}
```
The above code will not compile, since compiler expects the opaque types for `age`, `height` and `weight` fields, and hence making it safer.

However, if we try to create the Patient instance inside the `object models`, the compiler doesn't complain. The reason for that is, the Int and the opaque types are interchangeable with in the scope where it is defined.

Now, let's put back the instance within the object `impl`.

Next, we can try creating the instance using the opaque types.
```scala
val patient = Patient("P1", Age(22), Height(170), Weight(74))
```
But, the above code still doesn't compile. The reason for that is opaque types doesn't have any api's available unlike case classes.
We need to define the required functions for the opaque type. We can do that by defining companion objects for types.

```scala
opaque type Age = Int
object Age {
  def apply(value:Int): Age = value
}
```
Similarly, we can define api's for other opaque types as well. Now, the below code will compile
```scala
val patient = Patient("P1", Age(22), Height(170), Weight(74))
```

If we want to get the `age` value as Int, opaque type age doesn't have any api's for that. We can implement this by using the extension method as below:
```scala
opaque type Age = Int
object Age {
  def apply(value:Int): Age = value
  
  extension (age: Age) {
    def value: Int = age
  }
}
```
Now, we can access the Int value from the opaque type as: 

```scala
val patient:Patient = Patient("P1", Age(22), Height(170), Weight(74))
patient.age.value // this will return the value 22 
```

Since we are modeling a domain entity, there will some specific business logic and conditions on the fields.
For example, we can't have a patient with negative age, or negative weight etc. 
For handling that, we can provide an api say `safe`, to each of the opaque types.
```scala
object Age {
    def apply(value: Int): Age = value
    def safe(value: Int): Option[Age] = if (value > 0 && value < 100) Some(value) else None
    extension (age: Age) {
      def value: Int = age
    }
}
```
The `safe` method will validate the input data and build an option of `Age`. Similar `safe` api can be added to other types as well. 
This will help us to safely build a model entity, which follows the domain rules. For e.g., we can do as:
```scala
val patient: Option[Patient] = for {
  age <- Age.safe(10)
  height <- Height.safe(100)
  weight <- Weight.safe(90)
}yield Patient("P2",age, height, weight)
```
The above code will return an `Option[Patient]` based on the validations provided in the `safe` api.

### Context Bounds

Similar to other types, we can apply context bounds to the opaque types.
For example, 
```scala
opaque type SeniorAge <: Age = Int
```
This `SeniorAge` can then be used in place of `Age`. However, please note that the validation api's needs to be implemented for the type `SeniorAge` separately.

The underlying apis of the main type will be accessible for the opaque type with context bound. For example,
```scala
opaque type MyLocalDate <: java.time.LocalDate = java.time.LocalDate
object MyLocalDate {
  def parse(dt:String): MyLocalDate = java.time.LocalDate.parse(dt)
}
```
Now, on an instance of `MyLocalDate`, all the api's of `java.time.LocalDate` will be also accessible, since `MyLocalDate` is having context bound of java.time.LocalDate

### How it is different from value classes and case classes
- Opaque type aliases do not have any api's by default. The developer needs to implement all required apis. 
- No toString() or apply() methods by default
- Doesn't support pattern matching.
- Underlying type's apis will not be accessible outside, only the explicitly defined api's are exposed (Except in the case of context bounds)
- Runtime erasure of opaque types

For more information on the sbt-dotty plugin, see the
[dotty-example-project](https://github.com/lampepfl/dotty-example-project/blob/master/README.md).
