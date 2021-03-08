package com.chriniko

object Playground2 {

  def main(args: Array[String]): Unit = {
  }

  /*

  ======================================================================================================================

  ADTs = algebraic data types
    is a composite type of
      Product Types (or Tagged Unions)
        eg: case class, tuple, HList

         case class User(id: Long, username: String)... THINK AND...


      Sum Types (or Co-Product or Unions)
        eg: either, sealed trait, Coproduct

          sealed trait Animal
          case class Dog(name: String) extends Animal
          case class Cat(name: String) extends Animal

          THINK OR...


       common ADTs: Option, Either, List

  ======================================================================================================================


  GADTs = generalized algebraic data types
    A generalization of parametric algebraic data types

    sealed trait Expr[T]
    case class Num(v: Int) extends Expr[Int]
    case class Bool(v: Boolean) extends Expr[Boolean]
    case class Add (v: Expr[Int], v2: Expr[Int]) extends Expr[Int]
    case class Mult (v: Expr[Int], v2: Expr[Int]) extends Expr[Int]
    case class Eq[A](v: Expr[A], v2: Expr[A]) extends Expr[Boolean]

    def eval[A <: Any](expr: Expr[A]): A = expr match {
      case Num(v) => v
      case Bool(v) => v

      case Add(v, v2) =>
        eval(v) + eval(v2)

      case Mult(v, v2) =>
        eval(v) * eval(v2)

      case Eq(v, v2) =>
        eval(v) == eval(v2)

    }

    val expr1: Mult = Mult(Add(Num(1), Num(2)), Num(3))
    println(eval(expr1))


    Type safe business logic
    Type safe DSLs

  ======================================================================================================================

  Function Type


  type Like = Colour => Boolean


  sealed trait Colour
  case object Red extends Colour
  case object Yellow extends Colour
  case object Blue extends Colour

  type Like = Colour => Boolean

  // Red -> {true, false}
  // Yellow -> {true, false}
  // Blue -> {true, false}
  // How many implementations ?

  2 * 2 * 2 = 2^3 implementations


  Brand => Country
  5^20 implementations

  Exponentials in Category Theory: In mathematical literature, the function object, or the internal
  hom-object between two objects a and b, is often called the exponential and denoted by b^a

  val f: A => B == B^A implementations


  ======================================================================================================================


  Tagless Final Algebra


    Tagless Final vs Free Monads

    sealed trait DatabaseError
    case class DbIoError(msg: String) extends DatabaseError
    case object DbGeneralError extends DatabaseError


    // tagless final encoding
    trait DatabaseAlgebra[F[_], T] {
      def create(t: T): F[Boolean]

      def read(id: Long): F[Either[DatabaseError, T]]

      def delete(id: Long): F[Either[DatabaseError, Unit]]
    }


    // free monad
    sealed trait DBFreeAlgebraT[T]
    case class Create[T](t: T) extends DBFreeAlgebraT[Boolean]
    case class Read[T](id: Long) extends DBFreeAlgebraT[Either[DatabaseError, T]]
    case class Delete[T](id: Long) extends DBFreeAlgebraT[Either[DatabaseError, Unit]]

    ... We need to create some interpreters to interpret our algebra into actions


    So it looks like the Interpreter Pattern.
    A grammar for a simple language should be defined.
    In Free Monad and Tagless Final: the grammar is called Algebra.
    The grammar/algebra is implemented by concrete implementations.



    Category Theory:
      Formalizes mathematical structure and its concepts in terms of a labeled directed graph called a category,
      whose nodes are called objects, and whose labelled directed edges are called arrows (or morphisms).

      The mathematics of mathematics.

      The science of composition and abstraction.

      The mathematics behind functional programming.

    F-algebra (functor)

    F-coalgebra
    F[A] => A
    type Coalgebra[F[_], A] = A => F[A]

    F-algebra & F-coalgebra are using Recursion Schemes.


    Recursion Schemes: a calculus for lazy functional programming based on recursion operators associated
    with data type definitions.
    In simple terms, is an advanced functional programming technique that moves the recursion in the type level.



    Free-algebra
    Cofree-algebra

  ======================================================================================================================

  Free Monads Algebra



  ======================================================================================================================

  Recursion Schemes Algebra



   */


  // ==============================================================
  // adt
  // product
  case class User(id: Long, username: String)
  // sum
  sealed trait Animal
  case class Dog(name: String) extends Animal
  case class Cat(name: String) extends Animal



  // ==============================================================
  // gadt
  sealed trait Expr[T <: Any]
  case class Num(v: Int) extends Expr[Int]
  case class Bool(v: Boolean) extends Expr[Boolean]
  case class Add (v: Expr[Int], v2: Expr[Int]) extends Expr[Int]
  case class Mult (v: Expr[Int], v2: Expr[Int]) extends Expr[Int]
  case class Eq[A](v: Expr[A], v2: Expr[A]) extends Expr[Boolean]


  def eval[A <: Any](expr: Expr[A]): A = expr match {
    case Num(v) => v
    case Bool(v) => v

    case Add(v, v2) =>
      eval(v) + eval(v2)

    case Mult(v, v2) =>
      eval(v) * eval(v2)

    case Eq(v, v2) =>
      eval(v) == eval(v2)

  }

  val expr1: Mult = Mult(Add(Num(1), Num(2)), Num(3))
  println(s"expr1: ${eval(expr1)}")



  // ==============================================================
  // function type
  sealed trait Colour
  case object Red extends Colour
  case object Yellow extends Colour
  case object Blue extends Colour

  type Like = Colour => Boolean

  // Red -> {true, false}
  // Yellow -> {true, false}
  // Blue -> {true, false}
  // How many implementations ? in the above 2^3 implementations.




  // ==============================================================
  // tagless final encoded algebras vs free monads algebras

  sealed trait DatabaseError
  case class DbIoError(msg: String) extends DatabaseError
  case object DbGeneralError extends DatabaseError


  // tagless final encoding
  trait DatabaseAlgebra[F[_], T] {
    def create(t: T): F[Boolean]

    def read(id: Long): F[Either[DatabaseError, T]]

    def delete(id: Long): F[Either[DatabaseError, Unit]]
  }

  // free monad
  sealed trait DBFreeAlgebraT[T]
  case class Create[T](t: T) extends DBFreeAlgebraT[Boolean]
  case class Read[T](id: Long) extends DBFreeAlgebraT[Either[DatabaseError, T]]
  case class Delete[T](id: Long) extends DBFreeAlgebraT[Either[DatabaseError, Unit]]

}
