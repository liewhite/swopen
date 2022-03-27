# 设计
scala <-> value <-> bytes， 其中
* scala -> value ， scala 映射到哪个value由 abi控制， 比如Int 可能映射到 int8，uint8,int32,uint32等很多类型
* value -> scala, 这个可以直接映射

* value -> bytes, 也可以直接映射
* bytes -> value, 必须由abi控制

# 环节
## scala -> value
先模拟下实际使用
定义
```scala
val f = Function[ABIArray[ABIArray[ABIBool]], ABIUint[8]]
```
调用:
```scala
val result:Int = f(List(List(true)))
```
涉及到的转换有
* 从scala类型到ABIValue的自动转换
* 从bytes到ABIValue的转换

