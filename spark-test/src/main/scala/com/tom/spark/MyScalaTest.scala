package com.tom.spark

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
;

object MyScalaTest {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf()
    conf.setAppName("Tom").setMaster("local")
    val resourcesDir: String = Thread.currentThread.getContextClassLoader.getResource("").getFile



    val context: SparkContext = new SparkContext(conf)






    context.setLogLevel("WARN")

    println(resourcesDir)

    val text: RDD[String] = context.textFile(resourcesDir+"data.txt",4)
    val word: RDD[String] = text.flatMap(x => x.split("\\W+"))
    word.foreach(str => println(str))
    println("===========================================")
    word.foreachPartition(iter => iter.foreach(str => println(str)))
    val wordMap: RDD[(String, Int)] = word.map(x=>(x,1))
    wordMap.reduceByKey((temp,num)=>{
      println(temp+" ---- "+num)
      temp + num
    }).foreach(println(_))
//    wordMap.reduceByKey(_+_)




    context.stop()
  }
}
