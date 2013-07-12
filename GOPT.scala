import sun.security.util.Length

object a1{
  
  def a= Array("ich","ei","doof")
  def b= Array(1,2,3)
  
  def gopt[T](merge:(T,T)=>T,a:Array[T]):T = {
    var start=a(0)
    for(i<-1 to a.length-1){
      start=merge(start,a(i))
    }
    start
  }
  
  def main(args: Array[String]) 
  {
    println(gopt(lang,a))
    println(gopt(add,b))
}
  def kurz(s1:String, s2:String )= if(s1.length()<s2.length()) s1 else s2
  def lang(s1:String, s2:String )= if(s1.length()>s2.length()) s1 else s2
  def add(i1:Int,i2:Int)=i1+i2
}