/*
 * Copyright (c) 2014 Oculus Info Inc.
 * http://www.oculusinfo.com/
 *
 * Released under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.oculusinfo.tilegen.binning



import java.io.File
import java.io.FileWriter
import java.lang.{Double => JavaDouble}
import java.util.Properties

import scala.collection.JavaConverters._

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfterAll

import org.apache.spark.SparkContext
import org.apache.spark.SharedSparkContext

import com.oculusinfo.binning.impl.AOITilePyramid
import com.oculusinfo.binning.TileData
import com.oculusinfo.binning.TileIndex



class LiveTileTestSuite extends FunSuite with SharedSparkContext with BeforeAndAfterAll {
	val pyramidId = "simple test"
	var dataFile: File = null
	var pyramidIo: LiveStaticTilePyramidIO = null

	override def beforeAll (configMap: Map[String, Any]) = {
		super.beforeAll(configMap)
		createDataset(sc)
	}

	override def afterAll (configMap: Map[String, Any]) = {
		cleanupDataset
		super.afterAll(configMap)
	}

	private def createDataset (sc: SparkContext): Unit = {
		// Create our data
		dataFile = File.createTempFile("simple-live-tile-test", ".csv")
		println("Creating temporary data file "+dataFile.getAbsolutePath())
		val writer = new FileWriter(dataFile)
		Range(0, 8).foreach(n =>
			writer.write("%f,%f\n".format(n.toDouble, (7-n).toDouble))
		)
		writer.flush()
		writer.close()

		// Create our pyramid IO
		pyramidIo = new LiveStaticTilePyramidIO(sc)

		// Read the one into the other
		val props = new Properties()
		props.setProperty("oculus.binning.source.location.0", dataFile.getAbsolutePath())
		props.setProperty("oculus.binning.projection.autobounds", "false")
		props.setProperty("oculus.binning.projection.minx", "0.0")
		props.setProperty("oculus.binning.projection.maxx", "7.9999")
		props.setProperty("oculus.binning.projection.miny", "0.0")
		props.setProperty("oculus.binning.projection.maxy", "7.9999")
		props.setProperty("oculus.binning.parsing.separator", ",")
		props.setProperty("oculus.binning.parsing.x.index", "0")
		props.setProperty("oculus.binning.parsing.y.index", "1")
		props.setProperty("oculus.binning.index.type", "cartesian")
		props.setProperty("oculus.binning.xField", "x")
		props.setProperty("oculus.binning.yField", "y")
		props.setProperty("oculus.binning.levels.0", "1")

		pyramidIo.initializeForRead(pyramidId, 4, 4, props)
	}

	private def cleanupDataset: Unit = {
		if (dataFile.exists) {
			println("Deleting temporary data file "+dataFile)
			dataFile.delete
		}
	}



	test("Simple binning") {
		val tile00 = pyramidIo.readTiles(pyramidId, null, List(new TileIndex(1, 0, 0, 4, 4)).asJava)
		assert(tile00.isEmpty)
		val tile11 = pyramidIo.readTiles(pyramidId, null, List(new TileIndex(1, 1, 1, 4, 4)).asJava)
		assert(tile11.isEmpty)

		// Noting that visually, the tiles should look exactly as we enter them here.
		val tile01: TileData[_] =
			pyramidIo.readTiles(pyramidId, null, List(new TileIndex(1, 0, 1, 4, 4)).asJava).get(0)
		assert(tile01.getDefinition.getXBins() === 4)
		assert(tile01.getDefinition.getYBins() === 4)
		assert(tile01.getData.asScala.map(_.toString.toDouble) ===
			       List[Double](1.0, 0.0, 0.0, 0.0,
			                    0.0, 1.0, 0.0, 0.0,
			                    0.0, 0.0, 1.0, 0.0,
			                    0.0, 0.0, 0.0, 1.0))
		val tile10: TileData[_] =
			pyramidIo.readTiles(pyramidId, null, List(new TileIndex(1, 1, 0, 4, 4)).asJava).get(0)
		assert(tile10.getDefinition.getXBins() === 4)
		assert(tile10.getDefinition.getYBins() === 4)
		assert(tile10.getData.asScala.map(_.toString.toDouble) ===
			       List[Double](1.0, 0.0, 0.0, 0.0,
			                    0.0, 1.0, 0.0, 0.0,
			                    0.0, 0.0, 1.0, 0.0,
			                    0.0, 0.0, 0.0, 1.0))
	}
}
