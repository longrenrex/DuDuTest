package com.baidu.dudu.framework.mapper;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.baidu.dudu.framework.message.ComparisonResult;
import com.baidu.dudu.framework.message.DuDuMessage;
import com.baidu.dudu.framework.util.ReflectionHelper;
import com.baidu.dudu.framework.util.ReflectionHelper.ObjectParameters;


/**
 * 
 * @author Longbin zhao
 * 
 */
public class DefaultMapper implements DuDuMapper{

	@Override
	public ComparisonResult compare(DuDuMessage contextFromTestCase,
			DuDuMessage contextFromPlugin) {
		ComparisonResult result = new ComparisonResult();
        compare( contextFromTestCase, contextFromPlugin, result, new DifferenceState( "" ) );
		return result;
	}
	
	
    private static void compare( Object first, Object second, ComparisonResult result, DifferenceState state )
    {
        if( first == null )
        {
            return;
        }
        Class firstClass = first.getClass();

        if( hasEqualsMethodDeclared( firstClass ) )
        {
            comparePrimitives( first, second, result, state );
            return;
        }

        if( second == null )
        {
            result.addDifference( state.path, first.getClass(), "null" );
            return;
        }
        Class secondClass = second.getClass();

        /**
         * Allow child classes of message objects
         */
        if( ( first instanceof List ) && ( second instanceof List ) )
        {
            ; // This is ok, and should not be caught in the next check
        }
        else if( ( first instanceof Map ) && ( second instanceof Map ) )
        {
            ; // This is also ok, and should not be caught in the next check
        }
        else if( secondClass.equals( firstClass ) == false )
        {
            boolean isMessage = DuDuMessage.class.isAssignableFrom( firstClass );
            boolean classesAreRelated =
                firstClass.isAssignableFrom( secondClass ) || secondClass.isAssignableFrom( firstClass );
            if( ( isMessage == false ) || ( classesAreRelated == false ) )
            {
                result.addDifference( state.path, firstClass + "\n" + first, secondClass + "\n" + second );
                return;
            }
        }

        // Handle LinkedLists specially
        if( firstClass.isPrimitive() )
        {
            comparePrimitives( first, second, result, state );
        }
        else if( first instanceof List )
        {
            compareLists( first, second, result, state );
        }
        else if( first instanceof Map )
        {
            compareMaps( first, second, result, state );
        }
        else if( firstClass.isArray() )
        {
            compareArrays( first, second, result, state );
        }
        else
        {
            compareParameters( first, second, result, state );
        }
    }

    private static void comparePrimitives( Object first, Object second, ComparisonResult result, DifferenceState state )
    {
        if( first.equals( second ) == false )
        {
            result.addDifference( state.path, first, second );
        }
    }

    private static void compareArrays( Object first, Object second, ComparisonResult result, DifferenceState state )
        throws AssertionError
    {
        Class firstClass = first.getClass();
        Class secondClass = second.getClass();

        if( secondClass.isArray() == false )
        {
            throw new AssertionError( "Equal classes are not both arrays." );
        }

        Class firstComponentClass = firstClass.getComponentType();
        Class secondComponentClass = secondClass.getComponentType();
        if( ( firstComponentClass == null ) || ( secondComponentClass == null ) )
        {
            throw new AssertionError( "Component of array is null" );
        }
        if( firstComponentClass.equals( secondComponentClass ) == false )
        {
            throw new AssertionError( "Equal classes doesn't have the same ComponentType." );
        }

        int firstArrayLength = Array.getLength( first );
        int secondArrayLength = Array.getLength( second );
        if( firstArrayLength != secondArrayLength )
        {
            result.addDifference( state.path + ".length", Integer.toString( firstArrayLength ),
                Integer.toString( secondArrayLength ) );
        }
        else
        {
            for( int i = 0; i < firstArrayLength; i++ )
            {
                DifferenceState nextState = new DifferenceState( state.path + "[" + i + "]" );
                compare( Array.get( first, i ), Array.get( second, i ), result, nextState );
            }
        }
    }

    private static void compareLists( Object first, Object second, ComparisonResult result, DifferenceState state )
        throws AssertionError
    {
        if( !( second instanceof List ) )
        {
            throw new AssertionError( "Equal classes are not both instances of List." );
        }

        List firstList = ( List ) first;
        List secondList = ( List ) second;

        if( firstList.size() != secondList.size() )
        {
            result.addDifference( state.path + ".length", Integer.toString( firstList.size() ),
                Integer.toString( secondList.size() ) );
        }
        else
        {
            for( int i = 0; i < firstList.size(); i++ )
            {
                DifferenceState nextState = new DifferenceState( state.path + "[" + i + "]" );
                compare( firstList.get( i ), secondList.get( i ), result, nextState );
            }
        }
    }

    private static void compareMaps( Object first, Object second, ComparisonResult result, DifferenceState state )
        throws AssertionError
    {

        if( !( second instanceof Map ) )
        {
            throw new AssertionError( "Equal classes are not both instances of Map." );
        }

        Map firstMap = ( Map ) first;
        Map secondMap = ( Map ) second;

        Set entrySet = firstMap.entrySet();
        Iterator i = entrySet.iterator();
        while( i.hasNext() )
        {
            Map.Entry entry = ( Map.Entry ) i.next();
            DifferenceState nextState = new DifferenceState( state.path + "[" + entry.getKey() + "]" );
            compare( entry.getValue(), secondMap.get( entry.getKey() ), result, nextState );
        }
    }

    private static void compareParameters( Object first, Object second, ComparisonResult result, DifferenceState state )
    {
        ObjectParameters firstParameters = ReflectionHelper.parameters( first );
        ObjectParameters secondParameters = ReflectionHelper.parameters( second );

        if( firstParameters.size() != secondParameters.size() )
        {
            throw new AssertionError( "parameters.size() of objects to compare are not equal." );
        }

        Iterator iterator = firstParameters.namesIterator();
        while( iterator.hasNext() )
        {
            String parameterName = ( String ) iterator.next();
            String parameterPath = state.path.length() == 0 ? parameterName : state.path + "." + parameterName;
            DifferenceState nextState = new DifferenceState( parameterPath );

            Object firstValue = firstParameters.valueForName( parameterName );
            Object secondValue = secondParameters.valueForName( parameterName );
            compare( firstValue, secondValue, result, nextState );
        }
    }

    private static boolean hasEqualsMethodDeclared( Class firstClass )
    {
        try
        {
            firstClass.getDeclaredMethod( "equals", new Class[]{ Object.class } );
            return true;
        }
        catch( Exception e )
        {
            return false;
        }
    }

    
    private static class DifferenceState
    {
        public String path;

        public DifferenceState( String path )
        {
            this.path = path;
        }
    }

}
