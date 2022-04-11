package io.github.liewhite.common

trait CommonExtensions {
    extension [T](i: Option[T]) {
        def ! = {
            i.get
        }
    }
    extension [E <: java.lang.Throwable, T](i: Either[E, T]) {
        def ! = {
            i match {
                case Right(o) => o
                case Left(e)  => throw e
            }
        }
    }
}

object Extensions extends CommonExtensions{

}
