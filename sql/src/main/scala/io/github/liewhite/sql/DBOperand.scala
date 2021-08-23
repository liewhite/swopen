package io.github.liewhite.sql

trait DBOperand[T1]{

  extension[DBOperand[T1]](t: DBOperand[T1]){
    inline def eql[T2](t1: T1,t2: T2)(using ot1:DBOperand[T1], ot2:DBOperand[T2]) = ???
  }
}