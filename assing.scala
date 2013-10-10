import scala.collection.mutable.HashMap

abstract class Expression
case class VarExpr(varName: String) extends Expression
case class IntExpr(value: Int) extends Expression
case class PlusExpr(e1: Expression, e2: Expression) extends Expression
case class MultExpr(e1: Expression, e2: Expression) extends Expression
case class AssignExpr(e1: VarExpr,e2:Expression) extends Expression

object ExpressionApp {
  def asString(e: Expression): String = e match {
    case v: VarExpr => v.varName
    case i: IntExpr => "" + i.value
    case p: PlusExpr => "(" + asString(p.e1) + " + " + asString(p.e2) + ")"
    case p: MultExpr => asString(p.e1) + " * " + asString(p.e2)
    case p: AssignExpr => asString(p.e1) +" = "+asString(p.e2) 
  }

 def eval(e:Expression):Int= e match{
    case v: VarExpr => hashMap.getOrElse(v.varName, 0)
    case i: IntExpr => i.value
    case p: PlusExpr => eval(p.e1)+eval(p.e2)
    case p: MultExpr => eval(p.e1)*eval(p.e2)
    case p: AssignExpr => hashPlus(p.e1,p.e2)
   
 } 
  
 val hashMap = new HashMap[String,Int]()
 
 def hashPlus(e1:Expression,e2:Expression):Int=(e1,e2) match{
   case a: (VarExpr,Expression) => val wert=eval(a._2)
   hashMap.put(asString(a._1), wert)
   wert
   
 }
  
  def main(args: Array[String]): Unit = {
    // 7 + 2 + x*3
    val myExpr = PlusExpr(IntExpr(7), PlusExpr(IntExpr(2), MultExpr(VarExpr("x"), IntExpr(3))))
    // Standard print
    println(myExpr)
    // asString print
    println(asString(myExpr))

    // expression evaluation
    println("\nexpression evaluation:")
    println("----------------------")
    // x = y = 2
    val myAssign = AssignExpr(VarExpr("x"), AssignExpr(VarExpr("y"), IntExpr(2)))
    println(asString(myAssign))
    println(" => " + eval(myAssign))
    // x = x + 1
    val myAssign2 = AssignExpr(VarExpr("x"), PlusExpr(VarExpr("x"), IntExpr(1)))
    println(asString(myAssign2))
    println(" => " + eval(myAssign2))
    // y * (7 + 2 + x * 3)
    val myExpr2 = MultExpr(VarExpr("y"), myExpr)
    println(asString(myExpr2))
    println(" => " + eval(myExpr2))
    // => 36

    // console output:
    // ===============

    //PlusExpr(IntExpr(7),PlusExpr(IntExpr(2),MultExpr(VarExpr(x),IntExpr(3))))
    //(7 + (2 + x * 3))
    //
    //expression evaluation:
    //----------------------
    //x = y = 2
    // => 2
    //x = (x + 1)
    // => 3
    //y * (7 + (2 + x * 3))
    // => 36
  }

}