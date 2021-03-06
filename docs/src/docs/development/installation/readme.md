---
section: Docs
subtitle: Development
chapter: Installation
permalink: docs/development/installation/index.html
layout: submenu
---

Installation and Compilation
============================

The instructions on this page are intended for developers who want to install the Aperture Tiles source code and build their own custom projects. For quick examples of the capabilities of Aperture Tiles:

- See the [Demos](../../../demos/) page to access fully functional demonstrations of Aperture Tiles from your web browser.
- See the [Download](../../../download) page to access a pre-built distribution designed to help you quickly get started using Aperture Tiles and to understand the high-level process of creating an Aperture Tiles application. Instructions for using the packages to project a Julia set fractal on an X/Y plot are available on the [Quick Start](../quickstart) page. 

## <a name="prerequisites"></a>Prerequisites

This project has the following prerequisites:

- **Operating System**: Linux or OS X.
- **Languages**:
	-   [**Scala**](http://www.scala-lang.org/) version 2.10.3
	-   [**Java**](http://www.java.com/) (JDK version 1.7+)
- **Cluster Computing**: To facilitate large tiling jobs, Aperture Tiles supports a cluster computing framework. Note that if you only intend to run small jobs (data sets that fit in the memory of a single machine) or are willing to take a long time to complete them, you can skip the Hadoop/HDFS/HBase installation and run Spark on a single node and read/write to your local file system.
	-   **Apache Spark** - [Apache Spark](http://spark.incubator.apache.org/) distributed computing framework for distributing tile generation across a cluster of machines.  Aperture Tiles requires version 0.9.0 or greater (version 1.0.0 recommended). Note: when you set up Spark, you need to configure the version of Hadoop with which it will be working (if applicable). NOTE: In the latest version of Spark, class path issues may arise if you compile Spark from the source code. For this reason, we recommend using one of the pre-built Spark packages.
	-   **Hadoop/HDFS/HBase** (Optional) - Distributed computing stack.  HDFS is a file system for storing large data sets. Choose your preferred flavor  ([Cloudera](http://www.cloudera.com/content/cloudera/en/products/cdh.html) version 4.6 recommended, though other flavors such as [Apache](http://hadoop.apache.org/docs/r1.2.1/index.html), [MapR](http://www.mapr.com/products/apache-hadoop) and [HortonWorks](http://hortonworks.com/) may work). HBase is a non-relational database that sits atop Hadoop/HDFS. 
-  **Web Server**: the Tile Server and client are built using the [Restlet](http://restlet.org/) web framework, and require a servlet compatible web server. Choose your preferred implementation ([Apache Tomcat](http://tomcat.apache.org/) or [Jetty](http://www.eclipse.org/jetty/)).
-   **Build Automation**: All Aperture Tiles projects build with [Apache Maven](http://maven.apache.org/) version 3.1.0 (other 3.x versions may work). Ensure Maven is configured properly on the system on which you are building Aperture Tiles.

<img src="../../../img/architecture.png" class="screenshot" alt="Aperture Tiles Architecture Diagram"/>

## <a name="source-code"></a>Source Code

The Aperture Tiles source code is available on [GitHub](https://github.com/oculusinfo/aperture-tiles). Aperture Tiles is dependent on the *master* branch of Aperture JS source code, which you can also download from [GitHub](https://github.com/oculusinfo/aperturejs/tree/master). To install both projects:

1. Run the `mvn install` command in the *aperture* folder found in the root Aperture JS directory.
2. Run the `mvn install` command in the root Aperture Tiles directory.

### <a name="environment-variables"></a>Environment Variables
Set the following environment variables:

<div class="details props">
	<div class="innerProps">
		<ul class="methodDetail" id="MethodDetail">
			<dl class="detailList params">
				<dt>
					<b>SPARK_HOME</b>
				</dt>
				<dd>The location of the Spark installation</dd>
				
				<dt>
					<b>SPARK_MEM</b>
				</dt>
				<dd>The amount of memory to allocate to Spark</dd>
				
				<dt>
					<b>MASTER</b>
				</dt>
				<dd>The node on which the cluster is installed (set to <code>local</code> for running Spark on a single machine).</dd>
			</dl>
		</ul>
	</div>
</div>

### <a name="project-structure"></a>Project Structure

Aperture Tiles is made up of ten sub-projects:

<div class="details props">
	<div class="innerProps">
		<ul class="methodDetail" id="MethodDetail">
			<dl class="detailList params">
				<dt>
					<b>math-utilities</b>
				</dt>
				<dd>Basic, underlying Java utilities (for angles, linear algebra and statistics) to aid in processing data.</dd>
				
				<dt>
					<b>geometric-utilities</b>
				</dt>
				<dd>Advanced math utilities for processing geometry and geographic problems.</dd>
				
				<dt>
					<b>binning-utilities</b>
				</dt>
				<dd>Basic substrate of tiling, bin data structures, bin processing and basic bin storage classes.</dd>
				
				<dt>
					<b>tile-generation</b>
				</dt>
				<dd>Spark-based tools to generate tile pyramids from raw data.</dd>
				
				<dt>
					<b>tile-service</b>
				</dt>
				<dd>Web service to serve tiles from tile pyramids to web clients.</dd>
				
				<dt>
					<b>annotation-service</b>
				</dt>
				<dd>Services for adding annotations to Aperture Tiles visualizations.</dd>
				
				<dt>
					<b>tile-client</b>
				</dt>
				<dd>Simple web client to display tiles from tile pyramids.</dd>
				
				<dt>
					<b>tile-packaging</b>
				</dt>
				<dd>Packaged assembly of the tile generation service for the <a href="../quickstart/">Quick Start</a> example on this site.</dd>
				
				<dt>
					<b>tile-client-template</b>
				</dt>
				<dd>Starter template for creating a Tile Client and Server application.</dd>
				
				<dt>
					<b>tile-examples</b>
				</dt>
				<dd>Example applications.</dd>
				
				<dt>
					<b>docs</b>
				</dt>
				<dd>Source files for the documentation on this website.</dd>		
			</dl>
		</ul>
	</div>
</div>
 
### <a name="building-project"></a>Building the Project

#### <a name="hbase-version"></a>Specifying Your Hadoop/HBase Version

Prior to building the project, you need to specify the version of Hadoop and/or HBase installed (if applicable). Edit the `<properties>` section of the *aperture-tiles/pom.xml* build file to select the valid settings for your version. See the comments in the file for more details.
 
If you plan to run Apache Spark only in standalone mode on single machine, you can skip this step.

#### <a name="compiling"></a>Compiling the Aperture Tiles Projects

Before you compile the Aperture Tiles source code, you must install the Aperture JS project. Run the `mvn install` command in the *aperture* folder in the aperture subdirectory of the Aperture JS root directory.

Once the Aperture JS installation is complete, run the `mvn install` command again, this time in the root Aperture Tiles directory. This will compile all the project components and install .jar files for each project into your local maven repostitory on your build machine.

## <a name="next-steps"></a>Next Steps

For details on generating tile sets from raw data, see the [Tile Generation](../generation) topic.