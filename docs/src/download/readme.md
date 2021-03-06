---
section: Download
permalink: download/index.html
layout: section
---

Download
============================

## Source Code ##

The Aperture Tiles source code is available on GitHub. Aperture Tiles is under ongoing development and is freely available for download under [The MIT License](http://www.opensource.org/licenses/MIT) (MIT) open source licensing. Unlike GNU General Public License (GPL), MIT freely permits distribution of derivative work under proprietary license, without requiring the release of source code.

<a href="https://github.com/oculusinfo/aperture-tiles" class="download-link">Aperture Tiles Source Code</a>

Please note that Aperture Tiles is dependent on the *master* branch of Aperture JS, an agile, extensible visualization framework library project. Like Aperture Tiles, the Aperture JS source code is under ongoing development and is freely available for download under the The MIT License. The Aperture JS source code is available on GitHub.

<a href="https://github.com/oculusinfo/aperturejs/tree/master" class="download-link">Aperture JS Source Code</a>

For information on full installations of Aperture Tiles (along with Aperture JS), see the [Installation](../documentation/installation/) page.

## Virtual Machine ##

This virtual machine (VM) has been preconfigured with all of the third-party prerequisites required to begin a basic Aperture Tiles project:

- Apache Spark version 1.0.0 (Cloudera Distribution 4)
- Java (JDK version 1.7)
- Jetty version 9.2.1

Before downloading the VM, install [Oracle VM VirtualBox](https://www.virtualbox.org/) on your machine. For more information on the basic Aperture Tiles project and on using the VM, see the [Quick Start](../documentation/quickstart/#virtual-machine) documentation.

<a href="http://assets.oculusinfo.com/tiles/downloads/tile-vm-0.3.1.ova" class="download-link">Virtual Machine</a>

## Packaged Distribution ##

The following pre-built distribution can be used to quickly generate and display tiles without having to build the software. See the [Quick Start](../documentation/quickstart) documentation to help you quickly learn how to process a data set, transform it and create an Aperture Tiles project that allows you to visualize it in a web browser.

- **Tile Generator**: Enables you to generate a set of tiles that can be viewed using the template Tile Client available below. This version was built for Apache Spark 1.0 and CDH 4.6.
	<br/><br/><a href="http://assets.oculusinfo.com/tiles/downloads/tile-generator-0.3-dist.zip" class="download-link">Tile Generator</a><br/><br/>
- **Tile Client Template**: An example Tile Client that you can quickly copy and deploy to your web server after minimal modification to display tiles in a browser.
	<br/><br/><a href="http://assets.oculusinfo.com/tiles/downloads/tile-server-0.3-dist.zip" class="download-link">Tile Client Template</a>

<div class="git">
	<h2>Interested in Learning More?</h2>

	<ul>
		<li><a href="../tour/overview/">Tour</a>: Take our tour to learn more about Aperture Tiles.
		<li><a href="../docs/development/quickstart/">Documentation</a>: Learn how to install, implement and test your Aperture Tiles applications.
		<ul>
			<li><a href="../docs/development/quickstart/">Quick Start</a>: Our Julia data set provides an example of the process for generating tiles and visualizing them using Aperture Tiles.
		</ul>
		<li><a href="demos/">Live Examples</a>: See our demos page to explore live examples of the capabilities of Aperture Tiles.
	</ul>
</div>