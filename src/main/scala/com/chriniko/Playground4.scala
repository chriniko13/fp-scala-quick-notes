package com.chriniko

import com.chriniko.Playground4.InOut.{getLine, printLine}
import scalaz.{Coproduct, Free, ~>}
import scalaz.concurrent.Task

object Playground4 {


  /*

      What is a Free monad, how to use it?
        notion of the concept, [almost] no mechanics

      Q&A from the Internet

      What motivates usage of a Free monad?


      Functional Programming ===> we strive for modularity ===> Divide and Conquer


      Free Monad
        Free[S, A]

        Free is the program
        S is the language-algebra
        A this is the type of a value it will produce (once it is run)


      Composition of languages-algebras
        type Eff[A] = Coproduct[InOut, Logging, A]
        val program: Free[Eff, String] = ???

   */

  sealed trait InOut[A]

  case class PrintLine(line: String) extends InOut[Unit]

  case object GetLine extends InOut[String]

  object InOut {
    def printLine(line: String): Free[InOut, Unit] = Free.liftF(PrintLine(line))

    def getLine(): Free[InOut, String] = Free.liftF(GetLine)

    object Ops {
      def ask(question: String): Free[InOut, String] = for {
        _ <- printLine(question)
        answer <- getLine()
      } yield answer
    }

  }


  // natural transformation,  F ~> G == F[A] => G[A]
  object ConsoleInterpreter extends (InOut ~> Task) {

    override def apply[A](inout: InOut[A]): Task[A] = {

      inout match {
        case PrintLine(line) => Task.delay {
          println(line)
        }
        case GetLine => Task.delay {
          scala.io.StdIn.readLine()
        }
      }

    }
  }


  // Note: we need an interpreter to add value
  val program: Free[InOut, Unit] = for {
    _ <- printLine("What is your name?")
    name <- getLine()
    _ <- printLine(s"Nice to meet you $name")

  } yield ()


  // Note: if we want to compose languages-algebras then
  //sealed trait Logging
  // ....
  //type Eff[A] = Coproduct[InOut, Logging, A]
  //val programWithComposedLanguages: Free[Eff, String] = ???


  // Note: compose interpreters
  // val interpreter: Eff ~> Task = ConsoleInterpreter :+: Log4JInterpreter (scalaz)
  // val interpreter: Eff ~> Task = ConsoleInterpreter or Log4JInterpreter (cats)


  def main(args: Array[String]): Unit = {

    // Note: realization of the program
    val task: Task[Unit] = program.foldMap(ConsoleInterpreter)
    task.unsafePerformSync
  }

}








