// package swopen.jsonToolbox.schema

// import swopen.jsonToolbox.json.Json

// trait Validator:
//   def validate(data:Json): Boolean

// case class NumberValidator(
//   // common 
//   `type`: ValidatorType,
//   `enum`: Vector[Json] = Vector.empty,
//   const: Option[Json] = None,

//   // numeric 
//   multipleOf: Option[Long] = None,
//   maximum: Option[Long] = None,
//   exclusiveMaximum: Option[Long] = None,
//   minimum: Option[Long] = None,
//   exclusiveMinimum: Option[Long] = None) extends Validator:
//   def validate(data:Json):Boolean = 
//     data match
//       case Json.JNumber(number) =>
//         ???
//       case _ => false

// case class StringValidator(
//   // common 
//   `type`: ValidatorType,
//   `enum`: Vector[Json] = Vector.empty,
//   const: Option[Json] = None,
//   // string
//   maxLength: Option[Long] = None,
//   minLength: Option[Long] = None,
//   pattern: Option[String] = None,
// )
// case class ArrayValidator(
//   // common 
//   `type`: ValidatorType,
//   `enum`: Vector[Json] = Vector.empty,
//   const: Option[Json] = None,
//   //Array
//   maxItems: Option[Long] = None,
//   minItems: Option[Long] = None,
//   uniqueItems: Option[Boolean] = None,
//   maxContains: Option[Long] = None,
//   minContains: Option[Long] = None
// )
// case class ObjectValidator(
//   // common 
//   `type`: ValidatorType,
//   `enum`: Vector[Json] = Vector.empty,
//   const: Option[Json] = None,
//   //Objects
//   maxProperties: Option[Long] = None,
//   minProperties: Option[Long] = None,
//   required: Option[Vector[String]] = None,
//   dependentRequired: Option[Map[String,Vector[String]]],
// )

// enum ValidatorType:
//   case Single(tp:String)
//   case UnionType(tps:Vector[String])



