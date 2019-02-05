import org.scalatest._

import scala.collection.mutable

class Example extends FlatSpec {

  "A Stack" should "pop values in last-in-first-out order 11" in {
    val stack = new mutable.Stack[Int]
    stack.push(1)
    stack.push(2)
    assert(stack.pop() === 2)
    assert(stack.pop() === 1)
  }

  it should "throw NoSuchElementException if an empty stack is popped" in {
    val emptyStack = new mutable.Stack[String]
    //emptyStack.push("aaa")
    assertThrows[NoSuchElementException] {
      emptyStack.pop()
    }
  }
}