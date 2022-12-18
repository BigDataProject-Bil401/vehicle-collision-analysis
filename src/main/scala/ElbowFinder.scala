import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import Constants.FINAL_DATA_PATH
import Constants.PARTITION_RATIO
import Constants.NUM_CLUSTERS
import Constants.NUM_ITERATIONS

object ElbowFinder {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("ClusterElbow")
    val sc = new SparkContext(conf)
    val sparkSession = org.apache.spark.sql.SparkSession.builder
      .config(conf = conf)
      .appName("Cluster Elbow Finder Session")
      .getOrCreate()

    val base_df = sparkSession.read
      .option("header", "true")
      .option("delimiter", ",")
      .csv(FINAL_DATA_PATH)

    val null_filtered_df = base_df.filter(
      base_df("LATITUDE").isNotNull && base_df("LONGITUDE").isNotNull
    )
    val final_filtered_df = null_filtered_df.filter(
      row => row.get(4).toString.toDouble != 0 && row.get(5).toString.toDouble != 0
    )

    val final_final_filtered_df = final_filtered_df.filter(
      row => row.get(2) != null
    )

    val final_final_final_filtered_df = final_final_filtered_df.filter(
      row => (row.get(10) != null && row.get(11) != null)
    )

    println(final_final_final_filtered_df.show(5))
    println("df count -> ", final_final_final_filtered_df.count(),
      " ---- df partition count -> ", final_final_final_filtered_df.count() * PARTITION_RATIO)

    val partitionRDD = final_final_final_filtered_df.rdd
      .sample(withReplacement = false, fraction = PARTITION_RATIO)
      .cache()

    val trainRDD = partitionRDD.map(
      s => Vectors.dense(s.get(10).toString.toDouble, s.get(11).toString.toDouble)
    )

    println("train starts -> ")
    for (nOfCluster <- 1 to 20) {
      val clusters = KMeans.train(trainRDD, nOfCluster, NUM_ITERATIONS)
      val err = clusters.computeCost(trainRDD)
      println("SSE score for cluster number ->", nOfCluster, err)
    }

    sc.stop()
//
//    println("\ncluster-centers -> ")
//    clusters.clusterCenters.foreach(println)
  }
}
