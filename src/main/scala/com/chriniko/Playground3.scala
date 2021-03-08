package com.chriniko

import java.util.concurrent.Executors

import cats.effect.IO

import scala.concurrent.ExecutionContext

object Playground3 {


  def main(args: Array[String]): Unit = {

    // 1st example
    val e = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors() + 1)
    val ec = ExecutionContext.fromExecutor(e)

    val task: IO[Int] = addToGauge(42)
    val program: IO[Unit] = for {
      _ <- task
      _ <- task
      _ <- IO.shift(ec)
      _ <- task
    } yield ()


    program.unsafeRunSync()
    program.unsafeRunSync()


    e.shutdown()
  }


  var counter: Int = 0

  def addToGauge(i: Int): IO[Int] = IO {
    println(s"current thread: ${Thread.currentThread().getName}")
    counter += i
    println(s"counter is: ${counter}")
    counter
  }



  /*
      Effects should be controlled ====> so use monads

      Concurrency, eg: Scala FS2 - https://fs2.io/#/, https://github.com/typelevel/fs2

      Build a description of the program to be run
        Descriptions can be changed

      Separate the composition from the declaration
        Concurrency
        Dynamic scaling

      Program actions needs to be sequentialized
        Monads are the essence of sequencing
        A happens before B
        this is literally the definition
        this was Philip Wandler's insight


      All roads lead to IO

      Effects in Java and Scala are eager-by-default evaluation

      Low-level concurrency primitives

      IO monad --> control runtime

      Native threads ----> explicit asynchronous IO


      IO in Scala
        still a monad

        stack safety

        different evaluation modes
          strict (for performance)
          lazy and synchronous
          asynchronous (callbacks)

        effect capturing constructors


        Future
          runs actions eagerly by default
          memoizes result, so we can't run multiple times
          also leakes memory
          doesnt encapsulate asynchronous execution well
          Promise is leaky
          Rewrites exceptions into wrapped thingies


        Scalaz 7 IO
          doesnt support asynchronous at all!
          encourages to thread blocking and other horrors
            MVar
          only defines a lazy effect-capturing constructor
            very slow for pure computation


        Scalaz 7 Task
          supports all the ctors we want
            now, delay, async, fail
          bizarre and slow implementation based on actors
          still no abstraction (not event LiftIO)
          misleading and unsafe concurrency primitives
          fork is a trap, don't use it
            this use-case is incredibly common
          encourages deceptively dangerous parallelism
            resource management
          doesnt provide sane thread control functionality
          has extremely broken concurrency features
            mostly because of resource management
          is really, really slow
          is really, really concrete


        Thread best practises
          Divide your thread usage into three categories
            computation (#cpus in size, work-stealing non-daemon)
            blocking IO (unbounded and caching)
            event dispatchers (very small 1-4 threads, high priority, daemon)



        Other Options

          fs2
          monix
            resource management, abstraction, performance
            but tied to a larger framework

          cats-effect
            all the ctors you know and love (pure, apply, async, raiseError)
            much simpler and faster internals
            abstract typeclasses and laws
            no concurrency!


            eg:

            for {
               _ <- IO.shift(BlockingIO)
               bytes <- readFileUsingJavaIO(file)
               _ <- IO.shift(DefaultPool)

               secure = encrypt(bytes, KeyManager)
               _ <- sendResponse(Protocol.v1, secure)

               _ <- IO { println("it worked!") }
            } yield ()


            shift allows thread assignment as an effect
            relocates computation following its sequencing
            the only thread-related function
              nearly everything 'real' uses it

            not a primitive
              baked into the library for convenience

            it is relative easy to be a Sync
              really, just laziness and error handling

            async requires callback support

            Effect requires you are literally equivalent to IO


            Kleisli
              Stack safety

   */

}


