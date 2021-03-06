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

package com.oculusinfo.tilegen.tiling


import java.lang.{Double => JavaDouble}
import java.util.{List => JavaList}

import scala.collection.JavaConverters._

import org.scalatest.FunSuite

import com.oculusinfo.binning.TileData
import com.oculusinfo.binning.TileIndex
import com.oculusinfo.binning.TileData
import com.oculusinfo.binning.impl.AOITilePyramid
import com.oculusinfo.binning.util.Pair



class AnalyticsTestSuite extends FunSuite {
	def toJava (l: List[Double]) = l.map(new JavaDouble(_)).asJava

	def assertSeqsEqual[T] (a: Seq[T], b: Seq[T]): Unit = {
		assert(a.size === b.size)
		for (n <- 0 until a.size) assert(a(n) === b(n))
	}

	def assertListsEqual[T] (a: JavaList[T], b: JavaList[T]): Unit = {
		assert(a.size === b.size)
		for (n <- 0 until a.size) assert(a.get(n) === b.get(n))
	}

	test("Standard Double Analytic") {
		val analytic = new SumDoubleAnalytic
		assert(0.0 === analytic.defaultProcessedValue)
		assert(0.0 === analytic.defaultUnprocessedValue)
		assert(3.0 === analytic.aggregate(1.0, 2.0))
	}

	test("Standard Double Binning Analytic") {
		val analytic = new SumDoubleAnalytic with StandardDoubleBinningAnalytic

		assert(4.0 === analytic.finish(4.0).doubleValue)
	}

	test("Standard Mean Double Binning Analytic") {
		val analytic = new MeanDoubleBinningAnalytic()
		assert((0.0, 0) === analytic.defaultProcessedValue)
		assert((0.0, 0) === analytic.defaultUnprocessedValue)
		assert((3.0, 5) === analytic.aggregate((1.9, 2), (1.1, 3)))
		assert(0.6 === analytic.finish((3.0, 5)))
		assert(JavaDouble.isNaN(analytic.finish((0.0, 0))))
	}

	test("Standard Mean Double Binning Analytic - default value") {
		val analytic0 = new MeanDoubleBinningAnalytic(emptyValue=0.0)
		assert(0.0 == analytic0.finish((1.0, 0)))
		val analytic1 = new MeanDoubleBinningAnalytic(emptyValue=1.0)
		assert(1.0 == analytic1.finish((1.0, 0)))
	}

	test("Standard Mean Double Binning Analytic - minimum count") {
		val analytic = new MeanDoubleBinningAnalytic(minCount=4)
		assert(analytic.finish((3.0, 3)).isNaN)
		assert(1.0 === analytic.finish((4.0, 4)))
	}

	test("Standard Double Tile Analytic") {
		val analytic = new SumDoubleAnalytic with TileAnalytic[Double] {
			def name = "test"
		}

		assert("1.3" === analytic.valueToString(1.3))
	}

	test("Minimum Double Analytic") {
		val analytic = new MinimumDoubleAnalytic
		assert(1.0 === analytic.aggregate(new JavaDouble(1.0),
		                                  new JavaDouble(2.0)).doubleValue)
	}

	test("Maximum Double Analytic") {
		val analytic = new MaximumDoubleAnalytic
		assert(2.0 === analytic.aggregate(new JavaDouble(1.0),
		                                  new JavaDouble(2.0)).doubleValue)
	}

	test("Minimum/Maximum Double Analytics ignore NaN") {
		val sampleTile = new TileData[JavaDouble](new TileIndex(0, 0, 0, 4, 4), JavaDouble.NaN)
		sampleTile.setBin(0, 0, 1.0)
		sampleTile.setBin(1, 1, 2.0)
		sampleTile.setBin(2, 2, 3.0)
		sampleTile.setBin(3, 3, 4.0)
		val maxConvert = AnalysisDescriptionTileWrapper.acrossTile((d: JavaDouble) => d.doubleValue,
		                                                           new MaximumDoubleTileAnalytic)
		assert(4.0 === maxConvert(sampleTile))
		val minConvert = AnalysisDescriptionTileWrapper.acrossTile((d: JavaDouble) => d.doubleValue,
		                                                           new MinimumDoubleTileAnalytic)
		assert(1.0 === minConvert(sampleTile))
	}

	test("Standard Double Array Analytic") {
		val aBase = List(1.0, 2.0, 3.0, 4.0)
		val a = toJava(aBase)
		val bBase = List(5.0, 4.0, 3.0, 2.0, 1.0)
		val b = toJava(bBase)

		val analytic = new SumDoubleArrayAnalytic
		assertSeqsEqual(analytic.aggregate(aBase, bBase),
		                List(6.0, 6.0, 6.0, 6.0, 1.0))
	}
	
	test("Standard Double Array Tile Analytic") {
		val aBase = List(1.0, 2.0, 3.0, 4.0)
		val a = toJava(aBase)
		val bBase = List(5.0, 4.0, 3.0, 2.0, 1.0)
		val b = toJava(bBase)

		val analytic = new SumDoubleArrayAnalytic with StandardDoubleArrayTileAnalytic {
			def name="test"
		}
		assert("[4.1,3.2,2.3,1.4]" === analytic.valueToString(List(4.1, 3.2, 2.3, 1.4)))
	}

	test("Minimum Double Array Analytic") {
		val a = List(1.0, 2.0, 3.0, 4.0)
		val b = List(5.0, 4.0, 3.0, 2.0, 1.0)

		val analytic = new MinimumDoubleArrayAnalytic
		assert(analytic.aggregate(analytic.defaultUnprocessedValue, analytic.aggregate(a, b)) ===
			       Seq(1.0, 2.0, 3.0, 2.0, 1.0))
	}

	test("Maximum Double Array Analytic") {
		val a = List(1.0, 2.0, 3.0, 4.0)
		val b = List(5.0, 4.0, 3.0, 2.0, 1.0)

		val analytic = new MaximumDoubleArrayAnalytic
		assert(analytic.aggregate(analytic.defaultUnprocessedValue, analytic.aggregate(a, b)) ===
			       Seq(5.0, 4.0, 3.0, 4.0, 1.0))
	}

	test("String Score Analytic") {
		val a = Map("a" -> 1.0, "b" -> 2.0, "c" -> 3.0, "d" -> 4.0)
		val b = Map("a" -> 5.0, "b" -> 4.0, "c" -> 3.0, "d" -> 2.0, "e" -> 1.0)

		val analytic = new StringScoreAnalytic
		assert(Map("a" -> 6.0, "b" -> 6.0, "c" -> 6.0, "d" -> 6.0, "e" -> 1.0) ===
			       analytic.aggregate(a, b))
	}

	test("String Score Tile Analytic") {
		val a = Map("a" -> 1.0, "b" -> 2.0, "c" -> 3.0, "d" -> 4.0)
		val b = Map("a" -> 5.0, "b" -> 4.0, "c" -> 3.0, "d" -> 2.0, "e" -> 1.0)

		val analytic = new StringScoreAnalytic with StandardStringScoreTileAnalytic {
			def name = "test"
		}
		println("value: \""+analytic.valueToString(a)+"\"")
		assert("[\"a\":1.0,\"b\":2.0,\"c\":3.0,\"d\":4.0]" === analytic.valueToString(a))
	}


	// IP Testing
	// Here are the corners of levels 0 through 3, by IP address, tab-delimited (paste into excel for ease of use):
	//		ab.ff.ff.ff		af.ff.ff.ff		bb.ff.ff.ff		bf.ff.ff.ff		eb.ff.ff.ff		ef.ff.ff.ff		fb.ff.ff.ff		ff.ff.ff.ff
	//	a8.00.00.00		ac.00.00.00		b8.00.00.00		bc.00.00.00		e8.00.00.00		ec.00.00.00		f8.00.00.00		fc.00.00.00
	//		a3.ff.ff.ff		a7.ff.ff.ff		b3.ff.ff.ff		b7.ff.ff.ff		e3.ff.ff.ff		e7.ff.ff.ff		f3.ff.ff.ff		f7.ff.ff.ff
	//	a0.00.00.00		a4.00.00.00		b0.00.00.00		b4.00.00.00		e0.00.00.00		e4.00.00.00		f0.00.00.00		f4.00.00.00
	//		8b.ff.ff.ff		8f.ff.ff.ff		9b.ff.ff.ff		9f.ff.ff.ff		cb.ff.ff.ff		cf.ff.ff.ff		db.ff.ff.ff		df.ff.ff.ff
	//	88.00.00.00		8c.00.00.00		98.00.00.00		9c.00.00.00		c8.00.00.00		cc.00.00.00		d8.00.00.00		dc.00.00.00
	//		83.ff.ff.ff		87.ff.ff.ff		93.ff.ff.ff		97.ff.ff.ff		c3.ff.ff.ff		c7.ff.ff.ff		d3.ff.ff.ff		d7.ff.ff.ff
	//	80.00.00.00		84.00.00.00		90.00.00.00		94.00.00.00		c0.00.00.00		c4.00.00.00		d0.00.00.00		d4.00.00.00
	//		2b.ff.ff.ff		2f.ff.ff.ff		3b.ff.ff.ff		3f.ff.ff.ff		6b.ff.ff.ff		6f.ff.ff.ff		7b.ff.ff.ff		7f.ff.ff.ff
	//	28.00.00.00		2c.00.00.00		38.00.00.00		3c.00.00.00		68.00.00.00		6c.00.00.00		78.00.00.00		7c.00.00.00
	//		23.ff.ff.ff		27.ff.ff.ff		33.ff.ff.ff		37.ff.ff.ff		63.ff.ff.ff		67.ff.ff.ff		73.ff.ff.ff		77.ff.ff.ff
	//	20.00.00.00		24.00.00.00		30.00.00.00		34.00.00.00		60.00.00.00		64.00.00.00		70.00.00.00		74.00.00.00
	//		0b.ff.ff.ff		0f.ff.ff.ff		1b.ff.ff.ff		1f.ff.ff.ff		4b.ff.ff.ff		4f.ff.ff.ff		5b.ff.ff.ff		5f.ff.ff.ff
	//	08.00.00.00		0c.00.00.00		18.00.00.00		1c.00.00.00		48.00.00.00		4c.00.00.00		58.00.00.00		5c.00.00.00
	//		03.ff.ff.ff		07.ff.ff.ff		13.ff.ff.ff		17.ff.ff.ff		43.ff.ff.ff		47.ff.ff.ff		53.ff.ff.ff		57.ff.ff.ff
	//	00.00.00.00		04.00.00.00		10.00.00.00		14.00.00.00		40.00.00.00		44.00.00.00		50.00.00.00		54.00.00.00

	test("IPv4 CIDR Block Analytic - theoretical blocks") {
		import IPv4ZCurveIndexScheme._

		val indexScheme = new IPv4ZCurveIndexScheme
		val pyramid = IPv4ZCurveIndexScheme.getDefaultIPPyramid
		val converter = IPv4Analytics.getCIDRBlock(pyramid)(_)

		for (i <- 1 to 16) {
			// We get the tile whose upper right hand corner is the center of the space
			// By our above chart, it's bounds will be:
			//	level 1: 00.00.00.00 to 3f.ff.ff.ff (CIDR should be: 00.00.00.00/2)
			//	level 2: 30.00.00.00 to 3f.ff.ff.ff (CIDR should be: 30.00.00.00/4)
			//	level 3: 33.00.00.00 to 3f.ff.ff.ff (CIDR should be: 33.00.00.00/6)
			// Generalizing, looks like level N should be
			//	Block: 2i
			//	Address: longToIp((ffffffff00000000 >> (2*i)) & 3fffffff)
			val center = (1 << (i-1)) - 1
			val index = new TileIndex(i, center, center)
			val tile = new TileData[Int](index)
			val cidrBlock = converter(tile)

			val expected = ipArrayToString(longToIPArray((0xffffffff00000000L >> (2*i)) & 0x3fffffffL))+"/"+(2*i)
			assert(expected === cidrBlock)
		}
	}

	test("IPv4 CIDR Block Analytic - empirical blocks") {
		import IPv4ZCurveIndexScheme._

		val indexScheme = new IPv4ZCurveIndexScheme
		val pyramid = IPv4ZCurveIndexScheme.getDefaultIPPyramid
		val converter = IPv4Analytics.getCIDRBlock(pyramid)(_)

		// Can't test level 32 - tile pyramids won't work at level 32, because
		// tile indices are stored as integers instead of longs.
		for (i <- 0 to 16) {
			val fullAddress1 = longToIPArray(0xffffffffL)
			val fullAddress2 = longToIPArray((0xffffffff00000000L >> i) & 0xffffffffL)
			val fullAddress3 = longToIPArray(0xffffffffL >> (2*i))
			val fullAddress4 = longToIPArray((0x100000000L >> i) & 0xffffffffL)
			val expectedFull = ipArrayToString(fullAddress2)
			val expectedOne = ipArrayToString(fullAddress4)

			val cartesian1 = indexScheme.toCartesian(fullAddress1)
			val index1 = pyramid.rootToTile(cartesian1._1, cartesian1._2, i)
			val tile1 = new TileData[Int](index1)
			val value1 = converter(tile1)
			val expected1 = ipArrayToString(longToIPArray((0xffffffff00000000L >> (2*i)) & 0xffffffff))+"/"+(2*i)
			assert(expected1 === value1)

			val cartesian2 = indexScheme.toCartesian(fullAddress2)
			val index2 = pyramid.rootToTile(cartesian2._1, cartesian2._2, i)
			val tile2 = new TileData[Int](index2)
			val value2 = converter(tile2)
			val expected2 = ipArrayToString(longToIPArray((0xffffffff00000000L >> i) & 0xffffffff))+"/"+(2*i)
			assert(expected2 === value2)

			val cartesian3 = indexScheme.toCartesian(fullAddress3)
			val index3 = pyramid.rootToTile(cartesian3._1, cartesian3._2, i)
			val tile3 = new TileData[Int](index3)
			val value3 = converter(tile3)
			val expected3 = "0.0.0.0/"+(2*i)
			assert(expected3 === value3)

			val cartesian4 = indexScheme.toCartesian(fullAddress4)
			val index4 = pyramid.rootToTile(cartesian4._1, cartesian4._2, i)
			val tile4 = new TileData[Int](index4)
			val value4 = converter(tile4)
			val expected4 = expectedOne+"/"+(2*i)
			assert(expected4 === value4)
		}
	}

	test("IPv4 Min/Max analytics") {
		import IPv4ZCurveIndexScheme._

		val maxes = List(
			"03.ff.ff.ff", "07.ff.ff.ff", "13.ff.ff.ff", "17.ff.ff.ff", "43.ff.ff.ff", "47.ff.ff.ff", "53.ff.ff.ff", "57.ff.ff.ff",
			"0b.ff.ff.ff", "0f.ff.ff.ff", "1b.ff.ff.ff", "1f.ff.ff.ff", "4b.ff.ff.ff", "4f.ff.ff.ff", "5b.ff.ff.ff", "5f.ff.ff.ff",
			"23.ff.ff.ff", "27.ff.ff.ff", "33.ff.ff.ff", "37.ff.ff.ff", "63.ff.ff.ff", "67.ff.ff.ff", "73.ff.ff.ff", "77.ff.ff.ff",
			"2b.ff.ff.ff", "2f.ff.ff.ff", "3b.ff.ff.ff", "3f.ff.ff.ff", "6b.ff.ff.ff", "6f.ff.ff.ff", "7b.ff.ff.ff", "7f.ff.ff.ff",
			"83.ff.ff.ff", "87.ff.ff.ff", "93.ff.ff.ff", "97.ff.ff.ff", "c3.ff.ff.ff", "c7.ff.ff.ff", "d3.ff.ff.ff", "d7.ff.ff.ff",
			"8b.ff.ff.ff", "8f.ff.ff.ff", "9b.ff.ff.ff", "9f.ff.ff.ff", "cb.ff.ff.ff", "cf.ff.ff.ff", "db.ff.ff.ff", "df.ff.ff.ff",
			"a3.ff.ff.ff", "a7.ff.ff.ff", "b3.ff.ff.ff", "b7.ff.ff.ff", "e3.ff.ff.ff", "e7.ff.ff.ff", "f3.ff.ff.ff", "f7.ff.ff.ff",
			"ab.ff.ff.ff", "af.ff.ff.ff", "bb.ff.ff.ff", "bf.ff.ff.ff", "eb.ff.ff.ff", "ef.ff.ff.ff", "fb.ff.ff.ff", "ff.ff.ff.ff")
		val mins = List(
			"00.00.00.00", "04.00.00.00", "10.00.00.00", "14.00.00.00", "40.00.00.00", "44.00.00.00", "50.00.00.00", "54.00.00.00",
			"08.00.00.00", "0c.00.00.00", "18.00.00.00", "1c.00.00.00", "48.00.00.00", "4c.00.00.00", "58.00.00.00", "5c.00.00.00",
			"20.00.00.00", "24.00.00.00", "30.00.00.00", "34.00.00.00", "60.00.00.00", "64.00.00.00", "70.00.00.00", "74.00.00.00",
			"28.00.00.00", "2c.00.00.00", "38.00.00.00", "3c.00.00.00", "68.00.00.00", "6c.00.00.00", "78.00.00.00", "7c.00.00.00",
			"80.00.00.00", "84.00.00.00", "90.00.00.00", "94.00.00.00", "c0.00.00.00", "c4.00.00.00", "d0.00.00.00", "d4.00.00.00",
			"88.00.00.00", "8c.00.00.00", "98.00.00.00", "9c.00.00.00", "c8.00.00.00", "cc.00.00.00", "d8.00.00.00", "dc.00.00.00",
			"a0.00.00.00", "a4.00.00.00", "b0.00.00.00", "b4.00.00.00", "e0.00.00.00", "e4.00.00.00", "f0.00.00.00", "f4.00.00.00",
			"a8.00.00.00", "ac.00.00.00", "b8.00.00.00", "bc.00.00.00", "e8.00.00.00", "ec.00.00.00", "f8.00.00.00", "fc.00.00.00")

		val pyramid = IPv4ZCurveIndexScheme.getDefaultIPPyramid
		val minConverter = IPv4Analytics.getIPAddress(pyramid, false)(_)
		val maxConverter = IPv4Analytics.getIPAddress(pyramid, true)(_)

		// Level 3
		for (x <- 0 to 7) {
			for (y <- 0 to 7) {
				val index = new TileIndex(3, x, y)
				val tile = new TileData[Int](index)

				val minIP = longToIPArray(minConverter(tile))
				assert(mins(x+8*y) === "%02x.%02x.%02x.%02x".format(minIP(0), minIP(1), minIP(2), minIP(3)))
				val maxIP = longToIPArray(maxConverter(tile))
				assert(maxes(x+8*y) === "%02x.%02x.%02x.%02x".format(maxIP(0), maxIP(1), maxIP(2), maxIP(3)))
			}
		}

		// Level 2
		for (x <- 0 to 3) {
			for (y <- 0 to 3) {
				val index = new TileIndex(2, x, y)
				val tile = new TileData[Int](index)

				val minIP = longToIPArray(minConverter(tile))
				assert(mins(2*x+16*y) === "%02x.%02x.%02x.%02x".format(minIP(0), minIP(1), minIP(2), minIP(3)))
				val maxIP = longToIPArray(maxConverter(tile))
				assert(maxes(2*x+16*y+1+8) === "%02x.%02x.%02x.%02x".format(maxIP(0), maxIP(1), maxIP(2), maxIP(3)))
			}
		}

		// Level 1
		for (x <- 0 to 1) {
			for (y <- 0 to 1) {
				val index = new TileIndex(1, x, y)
				val tile = new TileData[Int](index)

				val minIP = longToIPArray(minConverter(tile))
				assert(mins(4*x+32*y) === "%02x.%02x.%02x.%02x".format(minIP(0), minIP(1), minIP(2), minIP(3)))
				val maxIP = longToIPArray(maxConverter(tile))
				assert(maxes(4*x+32*y+3+24) === "%02x.%02x.%02x.%02x".format(maxIP(0), maxIP(1), maxIP(2), maxIP(3)))
			}
		}

	}

	test("String score processing limits") {
		val a1 = new StringScoreAnalytic(Some(5), Some(_._2 < _._2))
		val a2 = new StringScoreAnalytic(Some(5), Some(_._2 > _._2))

		val a = Map("a" -> 1.0, "b" -> 2.0, "c" -> 3.0, "d" -> 4.0)
		val b = Map("c" -> 1.0, "d" -> 2.0, "e" -> 5.0, "f" -> 0.0)

		assert(Map("f" -> 0.0, "a" -> 1.0, "b" -> 2.0, "c" -> 4.0, "e" -> 5.0)
			       === a1.aggregate(a, b))
		assert(Map("a" -> 1.0, "b" -> 2.0, "c" -> 4.0, "e" -> 5.0, "d" -> 6.0)
			       === a2.aggregate(a, b))
	}

	test("String score storage limits") {
		val ba1 = new StandardStringScoreBinningAnalytic(Some(5),
		                                                 Some(_._2 < _._2),
		                                                 Some(3))
		val ba2 = new StandardStringScoreBinningAnalytic(Some(5),
		                                                 Some(_._2 > _._2),
		                                                 Some(3))

		val a = Map("a" -> 1.0, "b" -> 2.0, "c" -> 3.0, "d" -> 4.0)

		assert(List(("a", 1.0), ("b", 2.0), ("c", 3.0)) ===
			       ba1.finish(a).asScala
			       .map(p => (p.getFirst, p.getSecond.doubleValue)))
		assert(List(("d", 4.0), ("c", 3.0), ("b", 2.0)) ===
			       ba2.finish(a).asScala
			       .map(p => (p.getFirst, p.getSecond.doubleValue)))
	}
}
