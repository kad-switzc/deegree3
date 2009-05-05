//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2008 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.rendering.r2d.se.parser;

import static java.awt.Color.decode;
import static org.deegree.commons.utils.ArrayUtils.splitAsDoubles;
import static org.deegree.commons.xml.CommonNamespaces.getNamespaceContext;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import org.deegree.commons.filter.Expression;
import org.deegree.commons.filter.FilterEvaluationException;
import org.deegree.commons.filter.xml.Filter110XMLAdapter;
import org.deegree.commons.utils.Pair;
import org.deegree.commons.xml.NamespaceContext;
import org.deegree.commons.xml.XMLAdapter;
import org.deegree.commons.xml.XPath;
import org.deegree.feature.Feature;
import org.deegree.rendering.r2d.se.unevaluated.Continuation;
import org.deegree.rendering.r2d.se.unevaluated.Symbolizer;
import org.deegree.rendering.r2d.se.unevaluated.Continuation.Updater;
import org.deegree.rendering.r2d.styling.PointStyling;
import org.deegree.rendering.r2d.styling.components.Fill;
import org.deegree.rendering.r2d.styling.components.Graphic;
import org.deegree.rendering.r2d.styling.components.Mark;
import org.deegree.rendering.r2d.styling.components.Stroke;
import org.deegree.rendering.r2d.styling.components.Mark.SimpleMark;
import org.deegree.rendering.r2d.styling.components.Stroke.LineCap;
import org.deegree.rendering.r2d.styling.components.Stroke.LineJoin;
import org.slf4j.Logger;

/**
 * <code>SE110SymbolizerAdapter</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class SE110SymbolizerAdapter extends XMLAdapter {

    static final Logger LOG = getLogger( SE110SymbolizerAdapter.class );

    private static final NamespaceContext nscontext = getNamespaceContext();

    /**
     * @return the symbolizer
     */
    public Symbolizer<PointStyling> parsePointSymbolizer() {
        QName geom = getNodeAsQName( getRootElement(), new XPath( "se:Geometry", nscontext ), null );
        PointStyling baseOrEvaluated = new PointStyling();
        final Pair<Graphic, Continuation<Graphic>> pair = parseGraphic( getRootElement() );

        if ( pair == null ) {
            return new Symbolizer<PointStyling>( baseOrEvaluated, geom );
        }

        baseOrEvaluated.graphic = pair.first;

        if ( pair.second != null ) {
            return new Symbolizer<PointStyling>( baseOrEvaluated, new Continuation<PointStyling>() {
                @Override
                public void updateStep( PointStyling base, Feature f ) {
                    pair.second.evaluate( base.graphic, f );
                }
            }, geom );
        }

        return new Symbolizer<PointStyling>( baseOrEvaluated, geom );
    }

    /**
     * @param root
     * @return a base graphic and a continuation, or an evaluated graphic
     */
    public Pair<Graphic, Continuation<Graphic>> parseGraphic( OMElement root ) {
        Filter110XMLAdapter parser = new Filter110XMLAdapter();

        OMElement graphic = getElement( root, new XPath( "se:Graphic", nscontext ) );

        if ( graphic == null ) {
            return null;
        }

        Graphic base = new Graphic();
        Continuation<Graphic> contn = null;

        final Pair<Mark, Continuation<Mark>> pair = parseMark( graphic );
        if ( pair != null ) {
            base.mark = pair.first;
            if ( pair.second != null ) {
                contn = new Continuation<Graphic>() {
                    @Override
                    public void updateStep( Graphic base, Feature f ) {
                        pair.second.evaluate( base.mark, f );
                    }
                };
            }
        }

        contn = updateOrContinue( graphic, "se:Opacity", base, new Updater<Graphic>() {
            public void update( Graphic obj, String val ) {
                obj.opacity = parseDouble( val );
            }
        }, parser, contn );

        contn = updateOrContinue( graphic, "se:Size", base, new Updater<Graphic>() {
            public void update( Graphic obj, String val ) {
                obj.size = parseDouble( val );
            }
        }, parser, contn );

        contn = updateOrContinue( graphic, "se:Rotation", base, new Updater<Graphic>() {
            public void update( Graphic obj, String val ) {
                obj.rotation = parseDouble( val );
            }
        }, parser, contn );

        contn = updateOrContinue( graphic, "se:AnchorPoint/se:AnchorPointX", base, new Updater<Graphic>() {
            public void update( Graphic obj, String val ) {
                obj.anchorPointX = parseDouble( val );
            }
        }, parser, contn );

        contn = updateOrContinue( graphic, "se:AnchorPoint/se:AnchorPointY", base, new Updater<Graphic>() {
            public void update( Graphic obj, String val ) {
                obj.anchorPointY = parseDouble( val );
            }
        }, parser, contn );

        contn = updateOrContinue( graphic, "se:Displacement/se:DisplacementX", base, new Updater<Graphic>() {
            public void update( Graphic obj, String val ) {
                obj.displacementX = parseDouble( val );
            }
        }, parser, contn );

        contn = updateOrContinue( graphic, "se:Displacement/se:DisplacementY", base, new Updater<Graphic>() {
            public void update( Graphic obj, String val ) {
                obj.displacementY = parseDouble( val );
            }
        }, parser, contn );

        return new Pair<Graphic, Continuation<Graphic>>( base, contn );
    }

    /**
     * @param root
     * @return a base mark and a continuation, or an evaluated mark
     */
    public Pair<Mark, Continuation<Mark>> parseMark( OMElement root ) {
        OMElement mark = getElement( root, new XPath( "se:Mark", nscontext ) );
        if ( mark == null ) {
            return null;
        }

        Mark base = new Mark();
        Continuation<Mark> contn = null;

        String name = getNodeAsString( mark, new XPath( "se:WellKnownName", nscontext ), null );
        if ( name != null ) {
            base.wellKnown = SimpleMark.valueOf( name.toUpperCase() );
        }

        final Pair<Fill, Continuation<Fill>> fill = parseFill( mark );
        if ( fill != null ) {
            base.fill = fill.first;
            if ( fill.second != null ) {
                contn = new Continuation<Mark>() {
                    @Override
                    public void updateStep( Mark base, Feature f ) {
                        fill.second.evaluate( base.fill, f );
                    }
                };
            }
        }

        final Pair<Stroke, Continuation<Stroke>> stroke = parseStroke( mark );
        if ( stroke != null ) {
            base.stroke = stroke.first;
            if ( stroke.second != null ) {
                contn = new Continuation<Mark>() {
                    @Override
                    public void updateStep( Mark base, Feature f ) {
                        stroke.second.evaluate( base.stroke, f );
                    }
                };
            }
        }

        return new Pair<Mark, Continuation<Mark>>( base, contn );
    }

    /**
     * @param root
     * @return a base fill + continuation, or evaluated fill
     */
    public Pair<Fill, Continuation<Fill>> parseFill( OMElement root ) {
        OMElement fill = getElement( root, new XPath( "se:Fill", nscontext ) );
        if ( fill == null ) {
            return null;
        }

        Filter110XMLAdapter parser = new Filter110XMLAdapter();

        Fill base = new Fill();
        Continuation<Fill> contn = null;

        OMElement graphicFill = getElement( fill, new XPath( "se:GraphicFill", nscontext ) );
        if ( graphicFill != null ) {
            final Pair<Graphic, Continuation<Graphic>> pair = parseGraphic( graphicFill );
            if ( pair != null ) {
                base.graphic = pair.first;
                if ( pair.second != null ) {
                    contn = new Continuation<Fill>() {
                        @Override
                        public void updateStep( Fill base, Feature f ) {
                            pair.second.evaluate( base.graphic, f );
                        }
                    };
                }
            }
        }

        contn = updateOrContinue( fill, "se:SvgParameter[@name='fill']", base, new Updater<Fill>() {
            @Override
            public void update( Fill obj, String val ) {
                // keep alpha value
                int alpha = obj.color.getAlpha();
                obj.color = decode( val );
                obj.color = new Color( obj.color.getRed(), obj.color.getGreen(), obj.color.getBlue(), alpha );
            }
        }, parser, contn );

        contn = updateOrContinue( fill, "se:SvgParameter[@name='fill-opacity']", base, new Updater<Fill>() {
            @Override
            public void update( Fill obj, String val ) {
                // keep original color
                float alpha = parseFloat( val );
                float[] cols = obj.color.getRGBColorComponents( null );
                obj.color = new Color( cols[0], cols[1], cols[2], alpha );
            }
        }, parser, contn );

        return new Pair<Fill, Continuation<Fill>>( base, contn );
    }

    /**
     * @param root
     * @return a base stroke + contn, or evaluated stroke
     */
    public Pair<Stroke, Continuation<Stroke>> parseStroke( OMElement root ) {
        OMElement stroke = getElement( root, new XPath( "se:Stroke", nscontext ) );
        if ( stroke == null ) {
            return null;
        }

        Filter110XMLAdapter parser = new Filter110XMLAdapter();

        Stroke base = new Stroke();
        Continuation<Stroke> contn = null;

        contn = updateOrContinue( stroke, "se:SvgParameter[@name='stroke']", base, new Updater<Stroke>() {
            @Override
            public void update( Stroke obj, String val ) {
                // keep alpha value
                int alpha = obj.color.getAlpha();
                obj.color = decode( val );
                obj.color = new Color( obj.color.getRed(), obj.color.getGreen(), obj.color.getBlue(), alpha );
            }
        }, parser, contn );

        contn = updateOrContinue( stroke, "se:SvgParameter[@name='stroke-opacity']", base, new Updater<Stroke>() {
            @Override
            public void update( Stroke obj, String val ) {
                // keep original color
                float alpha = parseFloat( val );
                float[] cols = obj.color.getRGBColorComponents( null );
                obj.color = new Color( cols[0], cols[1], cols[2], alpha );
            }
        }, parser, contn );

        contn = updateOrContinue( stroke, "se:SvgParameter[@name='stroke-width']", base, new Updater<Stroke>() {
            @Override
            public void update( Stroke obj, String val ) {
                obj.width = parseDouble( val );
            }
        }, parser, contn );

        contn = updateOrContinue( stroke, "se:SvgParameter[@name='stroke-linejoin']", base, new Updater<Stroke>() {
            @Override
            public void update( Stroke obj, String val ) {
                obj.linejoin = LineJoin.valueOf( val.toUpperCase() );
            }
        }, parser, contn );

        contn = updateOrContinue( stroke, "se:SvgParameter[@name='stroke-linecap']", base, new Updater<Stroke>() {
            @Override
            public void update( Stroke obj, String val ) {
                obj.linecap = LineCap.valueOf( val.toUpperCase() );
            }
        }, parser, contn );

        contn = updateOrContinue( stroke, "se:SvgParameter[@name='stroke-dasharray']", base, new Updater<Stroke>() {
            @Override
            public void update( Stroke obj, String val ) {
                obj.dasharray = splitAsDoubles( val, " " );
            }
        }, parser, contn );

        contn = updateOrContinue( stroke, "se:SvgParameter[@name='stroke-dashoffset']", base, new Updater<Stroke>() {
            @Override
            public void update( Stroke obj, String val ) {
                obj.dashoffset = parseDouble( val );
            }
        }, parser, contn );

        OMElement graphicFill = getElement( stroke, new XPath( "se:GraphicFill", nscontext ) );
        if ( graphicFill != null ) {
            final Pair<Graphic, Continuation<Graphic>> pair = parseGraphic( graphicFill );
            if ( pair != null ) {
                base.fill = pair.first;
                if ( pair.second != null ) {
                    contn = new Continuation<Stroke>() {
                        @Override
                        public void updateStep( Stroke base, Feature f ) {
                            pair.second.evaluate( base.fill, f );
                        }
                    };
                }
            }
        }

        OMElement graphicStroke = getElement( stroke, new XPath( "se:GraphicStroke", nscontext ) );
        if ( graphicStroke != null ) {
            final Pair<Graphic, Continuation<Graphic>> pair = parseGraphic( graphicStroke );

            if ( pair != null ) {
                base.stroke = pair.first;
                if ( pair.second != null ) {
                    contn = new Continuation<Stroke>() {
                        @Override
                        public void updateStep( Stroke base, Feature f ) {
                            pair.second.evaluate( base.stroke, f );
                        }
                    };
                }

                contn = updateOrContinue( stroke, "se:GraphicStroke/se:InitialGap", base, new Updater<Stroke>() {
                    @Override
                    public void update( Stroke obj, String val ) {
                        obj.strokeInitialGap = parseDouble( val );
                    }
                }, parser, contn );

                contn = updateOrContinue( stroke, "se:GraphicStroke/se:Gap", base, new Updater<Stroke>() {
                    @Override
                    public void update( Stroke obj, String val ) {
                        obj.strokeGap = parseDouble( val );
                    }
                }, parser, contn );
            }

        }

        return new Pair<Stroke, Continuation<Stroke>>( base, contn );
    }

    /**
     * @param <T>
     * @param root
     * @param name
     * @param obj
     * @param updater
     * @param parser
     * @param contn
     * @return a continuation or null, if none was created and input
     */
    private <T> Continuation<T> updateOrContinue( OMElement root, String name, final T obj, final Updater<T> updater,
                                                  Filter110XMLAdapter parser, final Continuation<T> contn ) {
        OMElement opacity = getElement( root, new XPath( name, nscontext ) );
        if ( opacity != null ) {
            Iterator<?> iter = opacity.getChildren();
            final LinkedList<Pair<String, Expression>> text = new LinkedList<Pair<String, Expression>>(); // no real
            // 'alternative', have we?
            boolean textOnly = true;
            while ( iter.hasNext() ) {
                Object cur = iter.next();
                if ( cur instanceof OMElement ) {
                    Expression expr = parser.parseExpression( (OMElement) cur );
                    text.add( new Pair<String, Expression>( null, expr ) );
                    textOnly = false;
                }
                if ( cur instanceof OMText ) {
                    OMText t = (OMText) cur;
                    if ( textOnly && !text.isEmpty() ) { // concat text in case of multiple text nodes from beginning
                        String txt = text.removeLast().first;
                        text.add( new Pair<String, Expression>( txt + t.getText(), null ) );
                    } else {
                        text.add( new Pair<String, Expression>( t.getText(), null ) );
                    }
                }
            }

            if ( textOnly ) {
                updater.update( obj, text.getFirst().first );
            } else {
                return new Continuation<T>( contn ) {
                    @Override
                    public void updateStep( T base, Feature f ) {
                        String tmp = "";
                        for ( Pair<String, Expression> p : text ) {
                            if ( p.first != null ) {
                                tmp += p.first;
                            }
                            if ( p.second != null ) {
                                try {
                                    tmp += p.second.evaluate( f );
                                } catch ( FilterEvaluationException e ) {
                                    LOG.error( "Unknown error", e );
                                }
                            }
                        }

                        updater.update( obj, tmp );
                    }
                };
            }
        }

        return contn;
    }

}
