package com.chriniko

import com.chriniko.Playground4.{GetLine, InOut, PrintLine}
import org.scalatest._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import scalaz.Id._
import scalaz._
import scalaz.{Id, ~>}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class Playground4Test extends AnyFreeSpec with Matchers {


  // test interpreter
  def interpreter(input: mutable.Stack[String], output: ListBuffer[String]): InOut ~> Id = new (InOut ~> Id) {
    override def apply[A](fa: InOut[A]): Id[A] = fa match {

      case PrintLine(line) =>
        output += line
        ()

      case GetLine =>
        input.pop()
    }
  }


  "A program" - {
    "should ask for a name and greet the user" in {

      // given
      val input = mutable.Stack.empty[String]
      val output = ListBuffer.empty[String]
      input.push("Nick")

      // when
      Playground4.program.foldMap(interpreter(input, output))

      // then
      input.size should be (0)
      output should equal(ListBuffer("What is your name?", "Nice to meet you Nick"))
    }
  }


  // sample test
  "A Set" - {
    "when empty" - {
      "should have size 0" in {
        assert(Set.empty.size === 0)
      }

      "should produce NoSuchElementException when head is invoked" in {
        assertThrows[NoSuchElementException] {
          Set.empty.head
        }
      }
    }
  }

}


