import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors
import Constants.FINAL_DATA_PATH
import Constants.PARTITION_RATIO
import Constants.NUM_CLUSTERS
import Constants.NUM_ITERATIONS
import org.apache.spark.sql.functions.col
import scala.collection.immutable.ListMap
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

    val final_final_filtered_df = final_filtered_df.filter(
      row => row.get(2) != null
    )

    val final_final_final_filtered_df = final_final_filtered_df.filter(
      row => (row.get(10) != null && row.get(11) != null)
    )

    println("11111", final_final_final_filtered_df.show(5))
    println("22222", final_final_final_filtered_df.sort(col("CRASH DATE").asc).show(5))
    println("33333", final_final_final_filtered_df.sort(col("CRASH DATE").desc).show(5))

    println("df count -> ", final_final_final_filtered_df.count(),
      " ---- df partition count -> ", final_final_final_filtered_df.count() * PARTITION_RATIO)

    val partitionRDD = final_final_final_filtered_df.rdd
      .sample(withReplacement = false, fraction = PARTITION_RATIO)
      .cache()

    val trainRDD = partitionRDD.map(
      s => Vectors.dense(s.get(10).toString.toDouble, s.get(11).toString.toDouble)
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

    val clustersRDD = clusters.predict(trainRDD)
    val stateClustersRDD = partitionRDD.map(s => (s.get(2), (s.get(18), s.get(19), s.get(20), s.get(21), s.get(22)), s.get(1))).zip(clustersRDD)

    val clusterBasedStateSeverity: Array[scala.collection.mutable.Map[String, Int]] = new Array(clusters.clusterCenters.length)
    val clusterBasedFactor: Array[scala.collection.mutable.Map[String, Int]] = new Array(clusters.clusterCenters.length)
    val clusterBasedHour: Array[scala.collection.mutable.Map[String, Int]] = new Array(clusters.clusterCenters.length)

    def getValueFromKey(x: Option[Int]): Int = x match {
      case Some(s) => s
      case None => 0
    }

    stateClustersRDD.collect().foreach((row) => {
      val state = if (row._1._1 != null) row._1._1.toString else ""

      val factors: Array[String] = new Array(6);
      factors.update(1, if (row._1._2._1 != null && row._1._2._1 != "Unspecified") row._1._2._1.toString else "");
      factors.update(2, if (row._1._2._2 != null && row._1._2._2 != "Unspecified") row._1._2._2.toString else "");
      factors.update(3, if (row._1._2._3 != null && row._1._2._3 != "Unspecified") row._1._2._3.toString else "");
      factors.update(4, if (row._1._2._4 != null && row._1._2._4 != "Unspecified") row._1._2._4.toString else "");
      factors.update(5, if (row._1._2._5 != null && row._1._2._5 != "Unspecified") row._1._2._5.toString else "");

      val hour = if (row._1._3 != null) row._1._3.toString.split(":")(0) else ""
      val cluster = row._2

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
      for (idx <- 1 to 5) {
        val factor = factors(idx);
        if (factor != "") {
          if (!clusterBasedFactor(cluster).contains(factor)) {
            clusterBasedFactor(cluster).update(factor, 0)
          }
          clusterBasedFactor(cluster).update(factor, clusterBasedFactor(cluster)(factor) + 1)
        }
      }

      if (clusterBasedHour(cluster) == null) {
        clusterBasedHour(cluster) = scala.collection.mutable.Map()
      }
      if (!clusterBasedHour(cluster).contains(hour)) {
        clusterBasedHour(cluster).update(hour, 0)
      }
      clusterBasedHour(cluster).update(hour, clusterBasedHour(cluster)(hour) + 1)
    })

    sc.stop()

    def sortValuesOfMap(i: scala.collection.mutable.Map[String, Int]): ListMap[String, Int] = {
      ListMap(i.toSeq.sortWith(_._2 > _._2):_*);
    }

    println("state severity based on cluster severity -> ")
    clusterBasedStateSeverity.map(sortValuesOfMap).foreach(println)

    println("\n\nfactor based on cluster of severity -> ")
    clusterBasedFactor.map(sortValuesOfMap).foreach(println)

    println("\n\nhour based on cluster of severity -> ")
    clusterBasedHour.map(sortValuesOfMap).foreach(println)

    println("\ncluster-centers -> ")
    clusters.clusterCenters.foreach(println)
    new PrintWriter("./data/cluster-centroids") {
      clusters.clusterCenters.foreach(println); close
    }
  }
}
