package com.cvx.cdf.dwepcr.producer.priceanomalydetection

import scala.collection.{Map, Set, mutable}
import Functions.jaccardSimilarity



object HierarchicalClusterizer extends Serializable {

  def calculateClusters(vertecies: Seq[Set[String]], similarityThreshold: Double): Map[Set[String], Int] = {

    case class SimilarityEntry(src: Set[Set[String]], dst: Set[Set[String]], sim:Double) extends Ordered[SimilarityEntry] {
      import scala.math.Ordered.orderingToOrdered
      override def compare(that: SimilarityEntry): Int = this.sim.compare(that.sim)
    }

    def createInitialGroupSimilarity(vertecies: Seq[Set[String]]):
    (mutable.PriorityQueue[SimilarityEntry], mutable.Map[Set[String], mutable.Map[Set[String], Double]]) = {

      val queue = mutable.PriorityQueue.empty(Ordering[SimilarityEntry])
      val completeSimilarityMatrix = mutable.Map[Set[String], mutable.Map[Set[String], Double]]()
      for (i <- 0 until vertecies.length) {
        for (j <- i+1 until vertecies.length) {
          val similarity = jaccardSimilarity(vertecies(i), vertecies(j))
          queue.enqueue(SimilarityEntry(Set(vertecies(i)), Set(vertecies(j)), similarity))
          val srcNeighbours = completeSimilarityMatrix.getOrElse(vertecies(i),   mutable.Map[Set[String], Double]())
          val dstNeighbours = completeSimilarityMatrix.getOrElse(vertecies(j),   mutable.Map[Set[String], Double]())
          srcNeighbours.put(vertecies(j), similarity)
          dstNeighbours.put(vertecies(i), similarity)
          completeSimilarityMatrix.put(vertecies(i), srcNeighbours)
          completeSimilarityMatrix.put(vertecies(j), dstNeighbours)
        }
      }
      (queue, completeSimilarityMatrix)
    }

    def computeSimilarityBetweenClusters(cluster_1: List[Set[String]], cluster_2: List[Set[String]],
                                         similarityMatrix: mutable.Map[Set[String], mutable.Map[Set[String], Double]]) : Double = {
      val pairwiseSimilarities =
        for(elem_1 <- cluster_1; elem_2 <- cluster_2) yield similarityMatrix(elem_1)(elem_2)
      pairwiseSimilarities.sum/pairwiseSimilarities.length.toDouble
    }

    val groupSimilarityData = createInitialGroupSimilarity(vertecies)
    var queue = groupSimilarityData._1
    val similarityMatrix = groupSimilarityData._2

    var stop = false
    var currMaxSimilarity = 1.0
    val keys = similarityMatrix.keySet.map(set => Set(set))
    var existingClusters = mutable.Set[Set[Set[String]]]().empty
    existingClusters ++= keys

    while (queue.nonEmpty && !stop) {

      val maxSimilarEntry = queue.dequeue()
      currMaxSimilarity = maxSimilarEntry.sim
      if (currMaxSimilarity >= similarityThreshold) {

        val srcNodes = maxSimilarEntry.src
        val dstNodes = maxSimilarEntry.dst
        val newCluster = srcNodes.union(dstNodes)

        existingClusters -= srcNodes
        existingClusters -= dstNodes

        queue = queue
          .filter(entry => !entry.src.equals(srcNodes))
          .filter(entry => !entry.src.equals(dstNodes))
          .filter(entry => !entry.dst.equals(srcNodes))
          .filter(entry => !entry.dst.equals(dstNodes))

        val newQueue = mutable.PriorityQueue.empty(Ordering[SimilarityEntry])
        existingClusters.foreach(cluster => {
          newQueue.enqueue(SimilarityEntry(cluster, newCluster, computeSimilarityBetweenClusters(cluster.toList, newCluster.toList, similarityMatrix)))
        })

        newQueue.foreach( cluster => { queue.enqueue(cluster) } )
        existingClusters += newCluster
        stop = queue.isEmpty
      }
    }
    existingClusters.zipWithIndex.flatMap { case (cluster:Set[Set[String]], id:Int) => cluster.map(node => (node, id)) }.toMap
  }

}
