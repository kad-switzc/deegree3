//$HeadURL: svn+ssh://mschneider@svn.wald.intevation.org/deegree/base/trunk/src/org/deegree/ogcwebservices/wfs/operation/DescribeFeatureType.java $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

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
package org.deegree.protocol.wfs.getfeature;

import javax.xml.namespace.QName;

import org.deegree.crs.CRS;
import org.deegree.filter.Filter;
import org.deegree.filter.expression.Function;
import org.deegree.filter.expression.PropertyName;
import org.deegree.filter.sort.SortProperty;

/**
 * A {@link Query} that selects features using an optional {@link Filter}.
 * <p>
 * NOTE: XML-based queries are always of this type. Only for KVP requests it is possible to specify a <code>BBOX</code>
 * or a <code>FEATUREID</code> parameter.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: schneider $
 * 
 * @version $Revision: $, $Date: $
 */
public class FilterQuery extends Query {

    private final Filter filter;

    /**
     * Creates a new {@link FilterQuery} instance.
     * 
     * @param handle
     *            client-generated query identifier, may be null
     * @param typeNames
     *            requested feature types (with optional aliases), must not be null and must always contain at least one
     *            entry
     * @param featureVersion
     *            version of the feature instances to be retrieved, may be null
     * @param srsName
     *            WFS-supported SRS that should be used for returned feature geometries, may be null
     * @param propertyNames
     *            properties of the features that should be retrieved, may be null
     * @param xLinkPropertyNames
     *            properties for which the the traversal of nested XLinks is selectively requested, may be null
     * @param functions
     *            properties for which a function value should be used instead of the original property value, may be
     *            null
     * @param sortBy
     *            properties whose values should be used to order the set of feature instances that satisfy the query,
     *            may be null
     * @param filter
     *            filter constraint, may be null
     */
    public FilterQuery( String handle, TypeName[] typeNames, String featureVersion, CRS srsName,
                        PropertyName[] propertyNames, XLinkPropertyName[] xLinkPropertyNames, Function[] functions,
                        SortProperty[] sortBy, Filter filter ) {
        super( handle, typeNames, featureVersion, srsName, propertyNames, xLinkPropertyNames, functions, sortBy );
        if ( typeNames == null || typeNames.length == 0 ) {
            throw new IllegalArgumentException();
        }
        this.filter = filter;
    }

    /**
     * Creates a new {@link FilterQuery} instance from the most commonly used parameters.
     * 
     * @param typeName
     *            requested feature type name, must not be null
     * @param srsName
     *            WFS-supported SRS that should be used for returned feature geometries, may be null
     * @param sortBy
     *            properties whose values should be used to order the set of feature instances that satisfy the query,
     *            may be null
     * @param filter
     *            filter constraint, may be null
     */
    public FilterQuery( QName typeName, CRS srsName, SortProperty[] sortBy, Filter filter ) {
        this( null, new TypeName[] { new TypeName( typeName, null ) }, null, srsName, null, null, null, sortBy, filter );
    }

    /**
     * Returns the filter constraint.
     * 
     * @return the filter constraint, may be null
     */
    public Filter getFilter() {
        return filter;
    }
}
