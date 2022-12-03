import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

import Constants.FINAL_DATA_PATH

object Clusterer {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("Cluster")
    val sc = new SparkContext(conf)
    val sparkSession = org.apache.spark.sql.SparkSession.builder
      .config(conf = conf)
      .appName("Cluster Session")
      .getOrCreate()

    val base_df = sparkSession.read
      .option("header","true")
      .option("delimiter", ",")
      .csv(FINAL_DATA_PATH)

    base_df.show(5)
  }
}
