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


/**
 * This modules defines a basic layer class that can be added to maps.
 */
define(function (require) {
    "use strict";



    var Class = require('../class'),
        Util = require('../util/Util'),
        Layer;



    Layer = Class.extend({
        ClassName: "Layer",


        init: function ( spec, map ) {

            this.id = spec.layer;
            this.uuid = Util.generateUuid();
            this.name = spec.name || spec.layer;
            this.domain = spec.domain;
            this.map = map;
            this.layerSpec = spec;
            this.layerInfo = null;
        },

        /**
         *  Returns the layer's specification object
         */
        getLayerSpec: function () {
            return this.layerSpec;
        },

        /**
         *  Returns the layer's information object
         */
        getLayerInfo: function () {
            return this.layerInfo;
        },

        /**
         *  Returns the layers channel path
         */
        getChannel: function () {
            return 'layer.' + this.domain + '.' + this.uuid;
        },

        /**
         *  Sets the layers opacity
         */
        setOpacity: function( opacity ) {
            return true;
        },

        /**
         *  Returns the layers opacity
         */
        getOpacity: function() {
            return true;
        },

        /**
         *  Sets the layers visibility
         */
        setVisibility: function( visible ) {
            return true;
        },

        /**
         *  Returns the layers visibility
         */
        getVisibility: function() {
            return true;
        },

        /**
         *  Sets the layers z index
         */
        setZIndex: function( zIndex ) {
            return true;
        },

        /**
         *  Returns the layers z index
         */
        getZIndex: function() {
            return true;
        }

    });

    return Layer;
});
