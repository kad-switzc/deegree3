//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
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
package org.deegree.feature.persistence.sql.config;

import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
import static javax.xml.XMLConstants.NULL_NS_URI;
import static org.deegree.commons.xml.CommonNamespaces.XSINS;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.deegree.feature.persistence.sql.FeatureTypeMapping;
import org.deegree.feature.persistence.sql.MappedApplicationSchema;
import org.deegree.feature.persistence.sql.blob.BlobMapping;
import org.deegree.feature.persistence.sql.expressions.JoinChain;
import org.deegree.feature.persistence.sql.id.AutoIDGenerator;
import org.deegree.feature.persistence.sql.id.FIDMapping;
import org.deegree.feature.persistence.sql.id.IDGenerator;
import org.deegree.feature.persistence.sql.id.SequenceIDGenerator;
import org.deegree.feature.persistence.sql.id.UUIDGenerator;
import org.deegree.feature.persistence.sql.rules.CompoundMapping;
import org.deegree.feature.persistence.sql.rules.FeatureMapping;
import org.deegree.feature.persistence.sql.rules.GeometryMapping;
import org.deegree.feature.persistence.sql.rules.Mapping;
import org.deegree.feature.persistence.sql.rules.PrimitiveMapping;
import org.deegree.feature.types.FeatureType;
import org.deegree.feature.types.property.GeometryPropertyType.CoordinateDimension;
import org.deegree.feature.types.property.GeometryPropertyType.GeometryType;
import org.deegree.filter.sql.DBField;
import org.deegree.filter.sql.MappingExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates configuration documents for the {@link PostGISFeatureStore} from {@link MappedApplicationSchema} instances.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class PostGISFeatureStoreConfigWriter {

    private static Logger LOG = LoggerFactory.getLogger( PostGISFeatureStoreConfigWriter.class );

    private static final String CONFIG_NS = "http://www.deegree.org/datasource/feature/postgis";

    private static final String SCHEMA_LOCATION = "http://www.deegree.org/datasource/feature/postgis http://schemas.deegree.org/datasource/feature/postgis/3.1.0/postgis.xsd";

    private final MappedApplicationSchema schema;

    /**
     * Creates a new {@link PostGISFeatureStoreConfigWriter} instance.
     * 
     * @param schema
     *            the mapped application schema to export, must not be <code>null</code>
     */
    public PostGISFeatureStoreConfigWriter( MappedApplicationSchema schema ) {
        this.schema = schema;
    }

    /**
     * Exports the configuration document.
     * 
     * @param writer
     * @param connId
     * @param schemaURLs
     * @throws XMLStreamException
     */
    public void writeConfig( XMLStreamWriter writer, String connId, List<String> schemaURLs )
                            throws XMLStreamException {

        writer.writeStartElement( "PostGISFeatureStore" );
        writer.writeAttribute( "configVersion", "3.1.0" );
        writer.writeNamespace( DEFAULT_NS_PREFIX, CONFIG_NS );
        writer.writeNamespace( "xsi", XSINS );
        writer.writeAttribute( XSINS, "schemaLocation", SCHEMA_LOCATION );
        int i = 1;
        for ( String ns : schema.getXSModel().getAppNamespaces() ) {
            String prefix = schema.getXSModel().getNamespacePrefixes().get( ns );
            if ( prefix != null && !prefix.equals( XMLConstants.DEFAULT_NS_PREFIX ) ) {
                writer.writeNamespace( prefix, ns );
            } else {
                writer.writeNamespace( "app" + ( i++ ), ns );
            }
        }

        // writer.writeStartElement( CONFIG_NS, "StorageCRS" );
        // writer.writeCharacters( storageCrs );
        // writer.writeEndElement();

        writer.writeStartElement( CONFIG_NS, "JDBCConnId" );
        writer.writeCharacters( connId );
        writer.writeEndElement();

        for ( String schemaUrl : schemaURLs ) {
            writer.writeStartElement( CONFIG_NS, "GMLSchema" );
            writer.writeCharacters( schemaUrl );
            writer.writeEndElement();
        }

        if ( schema.getBlobMapping() != null ) {
            writeBlobMapping( writer, schema.getBlobMapping(), schemaURLs );
        }

        List<FeatureType> fts = schema.getFeatureTypes( null, false, false );
        SortedSet<String> ftNames = new TreeSet<String>();
        for ( FeatureType ft : fts ) {
            ftNames.add( ft.getName().toString() );
        }

        for ( String qName : ftNames ) {
            QName ftName = QName.valueOf( qName );
            FeatureType ft = schema.getFeatureType( ftName );
            if ( schema.getFtMapping( ft.getName() ) != null ) {
                writeFeatureTypeMapping( writer, ft );
            }
        }

        writer.writeEndElement();
    }

    private void writeBlobMapping( XMLStreamWriter writer, BlobMapping blobMapping, List<String> schemaURLs )
                            throws XMLStreamException {
        writer.writeStartElement( CONFIG_NS, "BLOBMapping" );
        writer.writeStartElement( CONFIG_NS, "StorageCRS" );
        writer.writeCharacters( schema.getStorageCRS().getAlias() );
        writer.writeEndElement();
        writer.writeEndElement();
    }

    private void writeFeatureTypeMapping( XMLStreamWriter writer, FeatureType ft )
                            throws XMLStreamException {

        LOG.debug( "Feature type '" + ft.getName() + "'" );
        FeatureTypeMapping ftMapping = schema.getFtMapping( ft.getName() );

        writer.writeStartElement( CONFIG_NS, "FeatureTypeMapping" );
        writer.writeAttribute( "name", getName( ft.getName() ) );
        writer.writeAttribute( "table", ftMapping.getFtTable().toString() );

        FIDMapping fidMapping = ftMapping.getFidMapping();
        writer.writeStartElement( CONFIG_NS, "FIDMapping" );
        writer.writeStartElement( CONFIG_NS, "Column" );
        writer.writeAttribute( "name", fidMapping.getColumn() );
        writer.writeAttribute( "type", fidMapping.getColumnType().getXSTypeName() );
        writer.writeEndElement();
        IDGenerator generator = fidMapping.getIdGenerator();
        if ( generator instanceof AutoIDGenerator ) {
            writer.writeEmptyElement( CONFIG_NS, "AutoIdGenerator" );
        } else if ( generator instanceof SequenceIDGenerator ) {
            writer.writeEmptyElement( CONFIG_NS, "SequenceIDGenerator" );
        } else if ( generator instanceof UUIDGenerator ) {
            writer.writeEmptyElement( CONFIG_NS, "UUIDGenerator" );
        }
        writer.writeEndElement();

        for ( Mapping particle : ftMapping.getMappings() ) {
            writeMapping( writer, particle );
        }

        writer.writeEndElement();
    }

    private void writeMapping( XMLStreamWriter writer, Mapping particle )
                            throws XMLStreamException {

        if ( particle instanceof PrimitiveMapping ) {
            PrimitiveMapping pm = (PrimitiveMapping) particle;
            writer.writeStartElement( CONFIG_NS, "Primitive" );
            writer.writeAttribute( "path", particle.getPath().getAsText() );
            writer.writeAttribute( "type", pm.getType().getXSTypeName() );
            MappingExpression mapping = pm.getMapping();
            if ( mapping instanceof DBField ) {
                writer.writeAttribute( "mapping", ( (DBField) mapping ).getColumn() );
            } else {
                writer.writeAttribute( "mapping", mapping.toString() );
            }
            if ( particle.getNilMapping() != null ) {
                writer.writeAttribute( "nilMapping", particle.getNilMapping().toString() );
            }
            if ( particle.getJoinedTable() != null ) {
                writeJoinedTable( writer, particle.getJoinedTable() );
            }
            writer.writeEndElement();
        } else if ( particle instanceof GeometryMapping ) {
            GeometryMapping gm = (GeometryMapping) particle;
            writer.writeStartElement( CONFIG_NS, "Geometry" );
            writer.writeAttribute( "path", particle.getPath().getAsText() );
            writer.writeAttribute( "mapping", gm.getMapping().toString() );
            if ( particle.getNilMapping() != null ) {
                writer.writeAttribute( "nilMapping", particle.getNilMapping().toString() );
            }
            GeometryType gt = gm.getType();
            switch ( gt ) {
            case POINT: {
                writer.writeAttribute( "type", "Point" );
                break;
            }
            case LINE_STRING:
            case LINEAR_RING:
            case CURVE: {
                writer.writeAttribute( "type", "LineString" );
                break;
            }
            case POLYGON:
            case SURFACE: {
                writer.writeAttribute( "type", "Polygon" );
                break;
            }
            case MULTI_POINT: {
                writer.writeAttribute( "type", "MultiPoint" );
                break;
            }
            case MULTI_LINE_STRING:
            case MULTI_CURVE: {
                writer.writeAttribute( "type", "MultiLineString" );
                break;
            }
            case MULTI_POLYGON:
            case MULTI_SURFACE: {
                writer.writeAttribute( "type", "MultiPolygon" );
                break;
            }
            case MULTI_GEOMETRY: {
                writer.writeAttribute( "type", "MultiGeometry" );
                break;
            }
            default: {
                writer.writeAttribute( "type", "Geometry" );
            }
            }
            writer.writeAttribute( "crs", gm.getCRS().getAlias() );
            writer.writeAttribute( "srid", gm.getSrid() );
            CoordinateDimension dim = gm.getDim();
            switch ( dim ) {
            case DIM_2: {
                writer.writeAttribute( "dim", "2D" );
                break;
            }
            case DIM_3: {
                writer.writeAttribute( "dim", "3D" );
                break;
            }
            case DIM_2_OR_3: {
                // TODO
                writer.writeAttribute( "dim", "2D" );
                break;
            }
            }
            if ( particle.getJoinedTable() != null ) {
                writeJoinedTable( writer, particle.getJoinedTable() );
            }
            writer.writeEndElement();
        } else if ( particle instanceof FeatureMapping ) {
            writer.writeStartElement( CONFIG_NS, "Feature" );
            writer.writeAttribute( "path", particle.getPath().getAsText() );
            if ( particle.getNilMapping() != null ) {
                writer.writeAttribute( "nilMapping", particle.getNilMapping().toString() );
            }
            if ( particle.getJoinedTable() != null ) {
                writeJoinedTable( writer, particle.getJoinedTable() );
            }
            writer.writeEndElement();
        } else if ( particle instanceof CompoundMapping ) {
            writer.writeStartElement( CONFIG_NS, "Complex" );
            writer.writeAttribute( "path", particle.getPath().getAsText() );
            if ( particle.getNilMapping() != null ) {
                writer.writeAttribute( "nilMapping", particle.getNilMapping().toString() );
            }
            if ( particle.getJoinedTable() != null ) {
                writeJoinedTable( writer, particle.getJoinedTable() );
            }
            CompoundMapping compound = (CompoundMapping) particle;
            for ( Mapping childMapping : compound.getParticles() ) {
                writeMapping( writer, childMapping );
            }
            writer.writeEndElement();
        } else {
            LOG.warn( "Unhandled mapping particle " + particle.getClass().getName() );
        }
    }

    private void writeJoinedTable( XMLStreamWriter writer, JoinChain jc )
                            throws XMLStreamException {
        writer.writeStartElement( CONFIG_NS, "JoinedTable" );
        writer.writeAttribute( "indexColumn", "idx" );
        writer.writeCharacters( jc.getFields().get( 0 ).getColumn() + "=" + jc.getFields().get( 1 ).getTable()
                                + ".parentfk" );
        writer.writeEndElement();
    }

    private String getName( QName name ) {
        if ( name.getNamespaceURI() != null && !name.getNamespaceURI().equals( NULL_NS_URI ) ) {
            String prefix = schema.getXSModel().getNamespacePrefixes().get( name.getNamespaceURI() );
            return prefix + ":" + name.getLocalPart();
        }
        return name.getLocalPart();
    }
}