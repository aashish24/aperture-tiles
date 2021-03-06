/*
 * Copyright (c) 2014 Oculus Info Inc. http://www.oculusinfo.com/
 * 
 * Released under the MIT License.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.oculusinfo.annotation;

import com.oculusinfo.annotation.data.AnnotationData;
import com.oculusinfo.annotation.data.AnnotationTile;
import com.oculusinfo.annotation.data.impl.JSONAnnotation;
import com.oculusinfo.annotation.index.AnnotationIndexer;
import com.oculusinfo.annotation.index.impl.AnnotationIndexerImpl;
import com.oculusinfo.binning.TilePyramid;
import com.oculusinfo.binning.impl.WebMercatorTilePyramid;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AnnotationJSONTest extends AnnotationTestsBase {
	
	static final boolean VERBOSE = false;
    private static double [] BOUNDS = { 180, 85.05, -180, -85.05};
    private static String [] GROUPS = {"Urgent", "High", "Medium", "Low"};

	private AnnotationIndexer _indexer;
	private TilePyramid _pyramid;
	
    @Before
    public void setup () {
	    _pyramid = new WebMercatorTilePyramid();
    	_indexer = new AnnotationIndexerImpl();
    }

    @After
    public void teardown () {
    	_indexer = null;   	
    	_pyramid = null;
    }


    @Test
    public void jsonAnnotationTest () throws Exception {

        AnnotationGenerator generator = new AnnotationGenerator( BOUNDS, GROUPS );

		List<AnnotationData<?>> before = generator.generateJSONAnnotations( NUM_ENTRIES );
		List<AnnotationData<?>> after = new ArrayList<>();
			
		if (VERBOSE) {
			System.out.println( "*** Before ***");
			printData( before );
		}
		
		for ( AnnotationData<?> annotation : before ) {
			
			JSONObject json = annotation.toJSON();
			after.add( JSONAnnotation.fromJSON(json) );
		}
		
		if (VERBOSE) {
			System.out.println( "*** After ***");
			printData( after );
		}
		
		Assert.assertTrue( compareData( before, after, false ) );
		
    }
	
	
    @Test
    public void testTileJSONSerialization () throws Exception {

        AnnotationGenerator generator = new AnnotationGenerator( BOUNDS, GROUPS );

		List< AnnotationTile > before = generator.generateTiles( generator.generateJSONAnnotations( NUM_ENTRIES ), _indexer, _pyramid );
		List< AnnotationTile > after = new ArrayList<>();

		if (VERBOSE) {
			System.out.println( "*** Before ***");
			printTiles( before );
		}

		
		for ( AnnotationTile tile : before ) {
			
			JSONObject json = tileToJSON( tile );
			after.add( getTileFromJSON( json ) );
		}
		
		
		if (VERBOSE) {
			System.out.println( "*** After ***");
			printTiles( after );
		}
		
		Assert.assertTrue( compareTiles( before, after, false ) );
    }
	
}
