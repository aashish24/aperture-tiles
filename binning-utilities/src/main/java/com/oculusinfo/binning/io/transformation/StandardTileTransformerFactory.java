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
package com.oculusinfo.binning.io.transformation;

import java.util.List;

import org.json.JSONObject;

import com.oculusinfo.factory.ConfigurableFactory;
import com.oculusinfo.factory.properties.JSONProperty;
import com.oculusinfo.factory.properties.StringProperty;
import com.oculusinfo.binning.io.transformation.TileTransformer;


/**
 * Factory class to create the standard types of Tile Transformers
 * 
 * @author tlachapelle
 */

public class StandardTileTransformerFactory extends ConfigurableFactory<TileTransformer> {
	

	public static StringProperty TILE_TRANSFORMER_TYPE 	= new StringProperty("type",
		   "The type of Transformer desired.  Currently this includes only the default and the filtervars transformer",
		   "generic", 
		   new String[] {
				     "generic",
				     "filtervars"
				     });
	
	public static JSONProperty   INITIALIZATION_DATA    = new JSONProperty("data",
		 "Data to be passed to the tile transformer for read initialization",
		 null);

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Class<TileTransformer> getGenericTransformerClass () {
		return (Class) TileTransformer.class;
	}

	public StandardTileTransformerFactory (ConfigurableFactory<?> parent, 
	                                      List<String> path) {
		super(getGenericTransformerClass(), parent, path);

		initializeProperties();
	}

	public StandardTileTransformerFactory (String name,
	                                      ConfigurableFactory<?> parent,
	                                      List<String> path) {
		super(name, getGenericTransformerClass(), parent, path);

		initializeProperties();
	}

	protected void initializeProperties () {
		addProperty(TILE_TRANSFORMER_TYPE);
		addProperty(INITIALIZATION_DATA);
	}

	@Override
	protected TileTransformer create () {
	    
	    String transformerTypes = getPropertyValue(TILE_TRANSFORMER_TYPE);

		if ("filtervars".equals(transformerTypes)) {
			JSONObject variables = getPropertyValue(INITIALIZATION_DATA);
			return new FilterVarsDoubleArrayTileTransformer(variables);
		}
		else {  // 'generic' or none passed in will give the default transformer
			return new GenericTileTransformer();
		}
	}
}
