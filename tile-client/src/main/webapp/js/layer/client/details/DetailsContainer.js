/*
 * Copyright (c) 2013 Oculus Info Inc.
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


define(function (require) {
    "use strict";



    var GenericDetailsElement = require('./GenericDetailsElement'),
        DetailsContainer;



    DetailsContainer = GenericDetailsElement.extend({
        ClassName: "DetailsContainer",

        init: function( spec ) {
            this._super( spec );
            this.$container = null;
        },

        parseInputSpec: function( spec ) {
            spec.width = spec.width || "257px";
            spec.height = spec.height || "513px";
            return spec;
        },

        createStyles: function() {
            return true;
        },

        create: function( value, closeCallback ) {

            var html = '', i;

            html += '<div class="details-on-demand" style="'
                  + 'height:'+this.spec.height+';'
                  + 'width:'+this.spec.width+';'
                  + '"></div>';

            this.$container = $( html ).draggable().resizable({
                minHeight: this.spec.height,
                minWidth: this.spec.width
            });

            this.$closeButton = $('<div class="details-close-button"></div>');
            this.$closeButton.click( closeCallback );

            for ( i=0; i<this.content.length; i++ ) {
                this.$container.append( this.content[i].create( value ) );
            }

            return this.$container.append( this.$closeButton );
        },

        destroy : function() {
            if ( this.$container ) {
                this.$container.remove();
                this.$container = null;
            }
        }

    });

    return DetailsContainer;

});