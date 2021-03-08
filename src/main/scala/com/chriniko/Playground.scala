package com.chriniko

import scala.util.Try


object Playground {

  def main(args: Array[String]): Unit = {

    // =====
    // Reader example
    val p: Reader[Host, String] = path("api/v1/seats")
    println(p.run("reserve"))
    println(p.run("un-reserve"))
    println()


    // =====
    // State example
    val counter: Counter = 0
    val s: State[Counter, String] = greet("chri")
    println(s.run(counter))
    println()



    // =====
    // Composition for effect-ful functions
    val char10: String => Option[Array[Char]] = s => if (s.isEmpty) None else Some(s.toCharArray)

    val letter: Array[Char] => Option[Int] = c => {
      var acc = 0
      c.filter(r => r.isDigit).foreach(_ => acc += 1)
      Some(acc)
    }

    val composition: String => Option[Counter] = char10 >==> letter
    println(composition("nia1ou2niaou3"))


    // The rules (Kleisli Category for F)
    val f: Int => Some[Int] = r => Some(r)

    val eq1: Option[Int] = FishyOption.pure(1).flatMap(f)
    val eq2: Option[Int] = Option(1).flatMap(FishyOption.pure)
    println(eq1 == eq2)
    // also left-associative composition == right-associative composition


    val x1: Int => Try[String] = f => Try(f).map(r => r.toString)
    val x2: String => Try[Boolean] = f => Try(f).map(x => x == "1")
    val x3: Int => Try[Boolean] = x1 >==> x2
    println(s"x3 == ${x3(1)}")
    println()


    // =====
    // monad option
    Option(1).flatMap(x => if (x == 1) Some("okay") else None)

  }


  // Six effects

  // Option
  // Either
  // List

  /*

    What do they have in common ?

    All compute an answer but also encapsulate something extra about the computation (machinery - effect).

    All have shape F[A]

      type F[A] = Option[A]
      type F[A] = Either[E, A] // for some type E
      type F[A] = List[A]
      type F[A] = Reader[E, A] // for some type E


    An effect is whatever distinguishes F[A] from A


    F[A] == this is a program in F that computes a value of type A


    effect-ful functions do not compose! this is a problem!

    But monads, give us the ability to compose effect-ful functions.

    Monads are a family of effects.

   */


  // -------------------------------------------------------------------------------------------------------------------
  // Reader infra
  case class Reader[E /*environment*/, A](run: E => A)

  type Host = String

  def path(s: String):Reader[Host, String] = {
    Reader(host => s"http://$host/$s")
  }



  // -------------------------------------------------------------------------------------------------------------------
  // State infra
  case class State[S, A](run: S => (A, S)) {
  }

  type Counter = Int

  def greet(name: String): State[Counter, String] = {
    State(counter => (s"hello $name -- you are person with number: $counter", counter + 1))
  }



  // -------------------------------------------------------------------------------------------------------------------
  // effect-ful function composition: a => f[b], b => f[c] then we need to compose, so that a => f[c]
  // MONADS GIVE US COMPOSITION FOR EFFECT-FUL FUNCTIONS

  /*
    The rules:
      left identity:       pure >==> f = f
      right identity:      f >==> pure = f
      associativity:       f >==> (g >==> h) = (f >==> g) >==> h
   */

  // fishy infra
  trait Fishy[F[_]] { // we can understand that this is a monad.

    def pure[A](a: A): F[A]

    def map[A, B](a: F[A])(f: A => B): F[B]

    def flatMap[A, B](a: F[A])(f: A => F[B]): F[B]

    def >==>[A, B, C](f: A => F[B], g: B => F[C]): A => F[C] = {
      a => {
        val x: F[B] = f(a)
        val y: F[C] = flatMap(x)(g)
        y
      }
    }

  }

  // fishy syntax (ad-hoc polymorphic)
  implicit class FishyFunctionOps[F[_], A, B](f: A => F[B]) { // operations
    def >==>[C](g: B => F[C])(implicit ev: Fishy[F]): A => F[C] =
      a => ev.flatMap(f(a))(g)
  }


  // fishy type variable(s)
  implicit val FishyOption: Fishy[Option] =
    new Fishy[Option] {
      override def pure[A](a: A): Option[A] = Option(a)

      override def map[A, B](a: Option[A])(f: A => B): Option[B] = a.map(f)

      override def flatMap[A, B](a: Option[A])(f: A => Option[B]): Option[B] = a.flatMap(f)
    }

  implicit val FishyTry: Fishy[Try] = new Fishy[Try] {
    override def pure[A](a: A): Try[A] = Try.apply(a)

    override def map[A, B](a: Try[A])(f: A => B): Try[B] = a.map(f)

    override def flatMap[A, B](a: Try[A])(f: A => Try[B]): Try[B] = a.flatMap(f)
  }



  // -------------------------------------------------------------------------------------------------------------------
  // Monad

  /*
    functor laws:
      # identity
      # composition
   */
  trait Functor[F[_]] {
    def map[A, B](fa: F[A])(f: A => B): F[B]
  }

  // monad infra
  trait Monad[F[_]] extends Functor[F] {

    def pure[A](a: A): F[A]

    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

    // Note: other way round to perform flatMap
    override def map[A, B](fa: F[A])(f: A => B): F[B] // flatMap(fa)(a => pure(a))

    def tuple[A, B](fa: F[A], fb: F[B]): F[(A, B)] = // applicative functors
      flatMap(fa)(a => map(fb)(b => (a, b)))

  }

  // monad syntax (ad-hoc polymorphic)
  implicit class MonadOps[F[_], A](fa: F[A])(implicit ev: Monad[F]) {

    // delegate to 'ev'
    def map[B](f: A => B): F[B] = {
      ev.map(fa)(f)
    }

    def tuple[B](fb: F[B]): F[(A, B)] = {
      ev.tuple(fa, fb)
    }

    def flatMap[B](f: A => F[B]): F[B] = {
      ev.flatMap(fa)(f)
    }

    // derived syntax
    def <*[B](fb: F[B]) : F[A] = {
      ev.tuple(fa, fb).map(_._1)
    }

    def >*[B](fb: F[B]): F[B] = {
      ev.tuple(fa, fb).map(_._2)
    }

  }

  // monad type variable(s)
  implicit val MonadOption: Monad[Option] = new Monad[Option] {
    override def pure[A](a: A): Option[A] = Option(a)

    override def flatMap[A, B](fa: Option[A])(f: A => Option[B]): Option[B] =
      fa match {
        case Some(value) => f(value)
        case None => None
      }

    override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
  }

  implicit val monadList: Monad[List] = new Monad[List] {
    override def pure[A](a: A): List[A] = a :: Nil

    override def flatMap[A, B](fa: List[A])(f: A => List[B]): List[B] =
      fa.foldRight(List.empty[B])((a, bs) => f(a) ::: bs)

    override def map[A, B](fa: List[A])(f: A => B): List[B] =
      fa.map(f)
  }


}

