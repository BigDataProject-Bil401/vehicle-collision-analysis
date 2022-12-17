import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import Constants.FINAL_DATA_PATH
import Constants.PARTITION_RATIO
import Constants.NUM_CLUSTERS
import Constants.NUM_ITERATIONS

import scala.concurrent.Await
import scala.concurrent.duration.Duration

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

    val final_final_filtered_df = final_filtered_df.filter(
      row => row.get(2) != null
    )

    println(final_final_filtered_df.show(5))
    println("df count -> ", final_final_filtered_df.count(),
      " ---- df partition count -> ", final_final_filtered_df.count() * PARTITION_RATIO)

    val partitionRDD = final_final_filtered_df.rdd
      .sample(withReplacement = false, fraction = PARTITION_RATIO)
      .cache()

    val trainRDD = partitionRDD.map(
      s => Vectors.dense(s.get(10).toString.toDouble, s.get(11).toString.toDouble)
    )

    println("train starts -> ")
    val clusters = KMeans.train(trainRDD, NUM_CLUSTERS, NUM_ITERATIONS)

    val clustersRDD = clusters.predict(trainRDD)
    val stateClustersRDD = partitionRDD.map(s => (s.get(2), s.get(18))).zip(clustersRDD)

    val clusterBasedStateSeverity: Array[scala.collection.mutable.Map[String, Int]] = new Array(clusters.clusterCenters.length)
    val clusterBasedFactor: Array[scala.collection.mutable.Map[String, Int]] = new Array(clusters.clusterCenters.length)

    def getValueFromKey(x: Option[Int]): Int = x match {
      case Some(s) => s
      case None => 0
    }

    stateClustersRDD.collect().foreach((row) => {
      val state = if (row._1._1 != null) row._1._1.toString else ""
      val factor = if (row._1._2 != null) row._1._2.toString else ""
      val cluster = row._2

      println("hey -> ", row._1._2)

      if (clusterBasedStateSeverity(cluster) == null) {
        clusterBasedStateSeverity(cluster) = scala.collection.mutable.Map()
      }

      if (!clusterBasedStateSeverity(cluster).contains(state)) {
        clusterBasedStateSeverity(cluster).update(state, 0)
      }
      clusterBasedStateSeverity(cluster).update(state, clusterBasedStateSeverity(cluster)(state) + 1)

      if (clusterBasedFactor(cluster) == null) {
        clusterBasedFactor(cluster) = scala.collection.mutable.Map()
      }

      if (!clusterBasedFactor(cluster).contains(factor)) {
        clusterBasedFactor(cluster).update(factor, 0)
      }
      clusterBasedFactor(cluster).update(factor, clusterBasedFactor(cluster)(factor) + 1)

    })

    sc.stop()

    println("state severity based on cluster severity -> ")
    clusterBasedStateSeverity.foreach(println)

    println("\n\nfactor based on cluster of severity -> ")
    clusterBasedFactor.foreach(println)

    println("\ncluster-centers -> ")
    clusters.clusterCenters.foreach(println)
  }
}
