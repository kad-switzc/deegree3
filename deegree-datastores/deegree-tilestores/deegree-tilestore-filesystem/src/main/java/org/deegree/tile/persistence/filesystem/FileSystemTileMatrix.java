//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2012 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.tile.persistence.filesystem;

import java.io.File;

import org.deegree.geometry.Envelope;
import org.deegree.geometry.SimpleGeometryFactory;
import org.deegree.tile.Tile;
import org.deegree.tile.TileMatrix;
import org.deegree.tile.TileMatrixMetadata;

/**
 * {@link TileMatrix} implementation for the {@link FileSystemTileStore}.
 * 
 * @see DiskLayout
 * 
 * @author <a href="mailto:schneider@occamlabs.de">Markus Schneider</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
class FileSystemTileMatrix implements TileMatrix {

    private final SimpleGeometryFactory fac = new SimpleGeometryFactory();

    private final TileMatrixMetadata metadata;

    private final DiskLayout layout;

    /**
     * Creates a new {@link FileSystemTileMatrix} instance.
     * 
     * @param metadata
     * @param layout
     */
    FileSystemTileMatrix( TileMatrixMetadata metadata, DiskLayout layout ) {
        this.metadata = metadata;
        this.layout = layout;
    }

    @Override
    public TileMatrixMetadata getMetadata() {
        return metadata;
    }

    @Override
    public Tile getTile( int x, int y ) {
        if ( metadata.getNumTilesX() <= x || metadata.getNumTilesY() <= y || x < 0 || y < 0 ) {
            return null;
        }
        Envelope bbox = calcEnvelope( x, y );
        File file = layout.resolve( metadata.getIdentifier(), x, y );
        return new FileSystemTile( bbox, file );
    }

    private Envelope calcEnvelope( int x, int y ) {
        double width = metadata.getTileWidth();
        double height = metadata.getTileHeight();
        Envelope env = metadata.getSpatialMetadata().getEnvelope();
        double minx = width * x + env.getMin().get0();
        double miny = env.getMax().get1() - height * y;
        return fac.createEnvelope( minx, miny - height, minx + width, miny, env.getCoordinateSystem() );
    }

    public DiskLayout getLayout() {
        return layout;
    }

}