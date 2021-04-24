package swopen.jsonToolbox.utils


// inline def summonAll[T <: Tuple,Target]: List[T] = 
//   inline erasedValue[T] match
//       case _: (t *: ts) => summonInline[JsonSchema[t]].schema :: summonAll[ts]
//       case _: EmptyTuple => Nil