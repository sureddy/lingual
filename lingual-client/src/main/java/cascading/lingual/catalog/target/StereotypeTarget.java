/*
 * Copyright (c) 2007-2013 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.cascading.org/
 *
 * This file is part of the Cascading project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cascading.lingual.catalog.target;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import cascading.lingual.catalog.CatalogOptions;
import cascading.lingual.catalog.SchemaCatalog;
import cascading.lingual.common.Printer;
import cascading.lingual.platform.PlatformBroker;
import cascading.lingual.type.SQLTypeMap;
import cascading.lingual.type.TypeMap;
import cascading.tuple.Fields;

/**
 *
 */
public class StereotypeTarget extends CRUDTarget
  {
  TypeMap typeMap = new SQLTypeMap();

  public StereotypeTarget( Printer printer, CatalogOptions options )
    {
    super( printer, options );
    }

  @Override
  protected boolean performRename( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();

    return catalog.renameStereotype( getOptions().getSchemaName(), getOptions().getStereotypeName(), getOptions().getRenameName() );
    }

  @Override
  protected boolean performRemove( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();

    return catalog.removeStereotype( getOptions().getSchemaName(), getOptions().getStereotypeName() );
    }

  @Override
  protected String performAdd( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();

    String schemaName = getOptions().getSchemaName();
    String stereotypeName = getOptions().getStereotypeName();
    List<String> columns = getOptions().getColumns();
    List<String> types = getOptions().getTypes();
    Fields fields = createFields( columns, types );

    catalog.createStereotype( schemaName, stereotypeName, fields );

    return stereotypeName;
    }

  @Override
  protected Collection<String> performGetNames( PlatformBroker platformBroker )
    {
    SchemaCatalog catalog = platformBroker.getCatalog();

    String schemaName = getOptions().getSchemaName();
    if( schemaName != null && !schemaName.isEmpty() )
      return catalog.getStereotypeNames( schemaName );
    else
      return catalog.getStereotypeNames();
    }

  private Fields createFields( List<String> columns, List<String> types )
    {
    Fields fields = new Fields( columns.toArray( new Comparable[ columns.size() ] ) );

    Type[] typeArray = typeMap.getTypesFor( types.toArray( new String[ types.size() ] ) );

    return fields.applyTypes( typeArray );
    }
  }
