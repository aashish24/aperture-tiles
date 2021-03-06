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

package com.oculusinfo.tilegen.spark

import java.io.File

import org.apache.spark._
import org.apache.spark.SparkContext._



class MavenReference (groupId: String,
                      artifactId: String,
                      version: String = "0.0.1-SNAPSHOT") {
	override def toString: String = {
		var libLocation = (System.getProperty("user.home") + "/.m2/repository/"
			                   + groupId.split("\\.").mkString("/") + "/" + artifactId + "/"
			                   + version + "/" + artifactId + "-" + version + ".jar")
		// we have to do some stupid name-mangling on windows
		return (new File(libLocation)).toURI().toString()
	}
}


object SparkConnector {
	def getDefaultSparkConnector: SparkConnector =
		new SparkConnector(getLibrariesFromClasspath)

	def getLibrariesFromClasspath = {
		val allSparkLibs = System.getenv("SPARK_CLASSPATH")
		if (null == allSparkLibs) {
			Seq[String]()
		} else {
			// When running on windows paths are ';' separated.  Cygwin paths are normally
			// the same as linux, but we have to force them to the windows style in the
			// spark-run script, otherwise Java/Scala won't recognize them (scala/java exe are windows
			// programs and expect windows paths).
			val os = System.getProperty("os.name").toLowerCase()
			if (os.contains("windows") || os.contains("cygwin")) {
				allSparkLibs.split(";").filter(!_.isEmpty).toSeq
			} else {
				allSparkLibs.split(":").filter(!_.isEmpty).toSeq
			}
		}
	}

	def getLibrariesFromClassLoader = {
		val loader = classOf[SparkConnector].getClassLoader
		if (loader.isInstanceOf[java.net.URLClassLoader]) {
			loader.asInstanceOf[java.net.URLClassLoader].getURLs.toSeq
		} else {
			Seq[String]()
		}
	}

	def getDefaultLibraries = {
		println("Checking classpath")
		val classpath = getLibrariesFromClasspath
		val libraries = if (classpath.isEmpty) {
			println("Classpath empty; checking class loader")
			getLibrariesFromClassLoader
		} else {
			println("Classpath ok")
			classpath
		}
		println("Default Libraries:")
		libraries.foreach(lib => println("\t"+lib))
		libraries
	}

	def getDefaultVersions = {
		val properties = new java.util.Properties()
		properties.load(classOf[SparkConnector].getResourceAsStream("/build.properties"))
		Map("base" -> properties.getProperty("aperture.tiles.version"),
		    "hadoop" -> properties.getProperty("aperture.tiles.hadoop.version"),
		    "hbase" -> properties.getProperty("aperture.tiles.hbase.version"))
	}

	def getDefaultLibrariesFromMaven = {
		val version = getDefaultVersions

		Seq(new MavenReference("com.oculusinfo", "math-utilities", version("base")),
		    new MavenReference("com.oculusinfo", "binning-utilities", version("base")),
		    new MavenReference("com.oculusinfo", "tile-generation", version("base")),
		    // These two are needed for avro serialization
		    // new MavenReference("org.apache.avro", "avro", "1.7.4"),
		    // new MavenReference("org.apache.commons", "commons-compress", "1.4.1"),
		    new MavenReference("org.apache.hbase", "hbase", version("hbase"))
		)
	}
}

class SparkConnector (jars: Seq[Object]) {
	protected lazy val jarList : Seq[String] = {
		jars.map(_.toString).map(jar =>
			{
				println("Checking "+jar)
				println("\t"+new File(jar).exists())
				jar
			}
		)
	}


	private def getHost: String =
		java.net.InetAddress.getLocalHost().getHostName()

	private def isActive (hostname: String): Boolean =
		java.net.InetAddress.getByName(hostname).isReachable(5000)


	def getSparkContext (jobName: String): SparkContext = {
		getLocalSparkContext(jobName)
	}


	def debugConnection (connectionType: String,
	                     jobName: String): Unit = {
		println("Connection to " + connectionType + " spark context")
		println("\tjob: "+jobName)
		println("\tjars:")
		if (jarList.isEmpty) println("\t\tNone")
		else jarList.foreach(j => println("\t\t"+j))
	}

	def getLocalSparkContext (jobName: String): SparkContext = {
		debugConnection("local", jobName)
		new SparkContext("local", jobName, "/opt/spark", jarList, null, null)
	}
}


object TestSparkConnector {
	def main (args: Array[String]): Unit = {
		testDefaultSparkConnector()
	}

	def testDefaultSparkConnector (): Unit = {
		val connector = SparkConnector.getDefaultSparkConnector
		connector.debugConnection("test", "test")
	}
}
