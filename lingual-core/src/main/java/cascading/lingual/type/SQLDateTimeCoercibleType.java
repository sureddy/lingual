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

package cascading.lingual.type;

import java.lang.reflect.Type;
import java.util.Date;

import cascading.CascadingException;
import cascading.tuple.type.CoercibleType;
import cascading.util.Util;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eigenbase.sql.type.BasicSqlType;
import org.eigenbase.sql.type.SqlTypeName;
import org.eigenbase.util14.ZonelessDatetime;

/**
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
  fieldVisibility = JsonAutoDetect.Visibility.NONE,
  getterVisibility = JsonAutoDetect.Visibility.NONE,
  setterVisibility = JsonAutoDetect.Visibility.NONE
)
public abstract class SQLDateTimeCoercibleType extends BasicSqlType implements CoercibleType
  {
  ZonelessDatetime date;

  protected SQLDateTimeCoercibleType( SqlTypeName sqlTypeName, ZonelessDatetime zonelessDatetime )
    {
    super( sqlTypeName );
    this.date = zonelessDatetime;
    }

  @Override
  public int hashCode()
    {
    if( digest == null ) // no idea how or why this becomes null
      computeDigest();

    return super.hashCode();
    }

  @Override
  public Object canonical( Object value )
    {
    if( value == null )
      return null;

    Class from = value.getClass();

    if( from == String.class )
      return parse( (String) value ).getDateValue();

    if( from == Date.class )
      return ( (Date) value ).getTime(); // in UTC

    if( from == Long.class || from == long.class )
      return value;

    throw new CascadingException( "unknown type coercion requested from: " + Util.getTypeName( from ) );
    }

  @Override
  public Object coerce( Object value, Type to )
    {
    if( value == null )
      return null;

    Class from = value.getClass();

    if( from != Long.class )
      throw new IllegalStateException( "was not normalized" );

    // no coercion, or already in canonical form
    if( to == Long.class || to == long.class || to == Object.class )
      return value;

    if( to == String.class )
      {
      date.setZonelessTime( (Long) value );
      return date.toString();
      }

    throw new CascadingException( "unknown type coercion requested, from: " + Util.getTypeName( from ) + " to: " + Util.getTypeName( to ) );
    }

  protected abstract ZonelessDatetime parse( String value );

  @Override
  public boolean isNullable()
    {
    return true;
    }
  }