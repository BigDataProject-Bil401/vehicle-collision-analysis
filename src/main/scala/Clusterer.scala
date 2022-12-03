import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import Constants.FINAL_DATA_PATH
import Constants.PARTITION_RATIO
import Constants.NUM_CLUSTERS
import Constants.NUM_ITERATIONS
import java.io.PrintWriter


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

    val null_filtered_df = base_df.filter(
      base_df("LATITUDE").isNotNull && base_df("LONGITUDE").isNotNull
    )
    val final_filtered_df = null_filtered_df.filter(
      row => row.get(4).toString.toDouble != 0 && row.get(5).toString.toDouble != 0
    )

    println(final_filtered_df.show(5))
    println("df count -> ", final_filtered_df.count(),
      " ---- df partition count -> ", final_filtered_df.count() * PARTITION_RATIO)

    val partitionRDD = final_filtered_df.rdd
      .sample(withReplacement = false, fraction = PARTITION_RATIO)
      .cache()

    val trainRDD = partitionRDD.map(
      s => Vectors.dense(s.get(4).toString.toDouble, s.get(5).toString.toDouble)
    )

    println("train starts -> ")
    val clusters = KMeans.train(trainRDD, NUM_CLUSTERS, NUM_ITERATIONS)

    println("PRINT PREDICTION -> ")

    val vectorsAndClusterIdx = trainRDD.map{ point =>
      val prediction = clusters.predict(point)
      (point.toString, prediction)
    }

    new PrintWriter("./data/vector-cluster-ids") {
      vectorsAndClusterIdx.collect().foreach(pair => println(pair.toString())); close
    }

    sc.stop()

    println("cluster-centers -> ")
    clusters.clusterCenters.foreach(println)
  }
}
