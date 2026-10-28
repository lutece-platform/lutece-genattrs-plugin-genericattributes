/*
 * Copyright (c) 2002-2025, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.genericattributes.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * Matches a value typed by a user against a regular expression written by an administrator. The value comes from the front office, the expression may
 * backtrack catastrophically: the matching is bounded in time and in length, so that a crafted answer cannot hold a server thread.
 */
public final class RegularExpressionUtils
{
    private static final String PROPERTY_TIMEOUT = "genericattributes.regex.timeout.ms";
    private static final String PROPERTY_MAX_LENGTH = "genericattributes.regex.max.length";
    private static final int DEFAULT_TIMEOUT = 200;
    private static final int DEFAULT_MAX_LENGTH = 10000;
    private static final int MAX_CACHED_PATTERNS = 500;

    private static final Map<String, Pattern> _mapPatterns = new ConcurrentHashMap<>( );

    /**
     * The result of a match
     */
    public enum MatchResult
    {
        /** The value matches the expression */
        MATCH,
        /** The value does not match the expression */
        NO_MATCH,
        /** The expression could not be evaluated: invalid syntax, value too long, or time limit reached */
        UNDECIDED
    }

    private RegularExpressionUtils( )
    {
    }

    /**
     * Check that an expression compiles
     * 
     * @param strRegex
     *            the expression
     * @return the description of the syntax error, or null if the expression is valid
     */
    public static String getSyntaxError( String strRegex )
    {
        try
        {
            Pattern.compile( strRegex );
            return null;
        }
        catch( PatternSyntaxException e )
        {
            return e.getDescription( );
        }
    }

    /**
     * Match the whole value against the expression, within the time and length limits set by the properties
     * 
     * @param strRegex
     *            the expression
     * @param strValue
     *            the value
     * @return the result of the match
     */
    public static MatchResult matches( String strRegex, String strValue )
    {
        return matches( strRegex, strValue, AppPropertiesService.getPropertyInt( PROPERTY_TIMEOUT, DEFAULT_TIMEOUT ),
                AppPropertiesService.getPropertyInt( PROPERTY_MAX_LENGTH, DEFAULT_MAX_LENGTH ) );
    }

    /**
     * Match the whole value against the expression
     * 
     * @param strRegex
     *            the expression
     * @param strValue
     *            the value
     * @param nTimeoutMs
     *            the time limit of the matching, in milliseconds
     * @param nMaxLength
     *            the longest value evaluated
     * @return the result of the match
     */
    public static MatchResult matches( String strRegex, String strValue, long nTimeoutMs, int nMaxLength )
    {
        if ( strValue.length( ) > nMaxLength )
        {
            AppLogService.info( "Value of {} characters not matched against a regular expression, the limit is {}", strValue.length( ), nMaxLength );
            return MatchResult.UNDECIDED;
        }

        Pattern pattern = getPattern( strRegex );

        if ( pattern == null )
        {
            return MatchResult.UNDECIDED;
        }

        try
        {
            return pattern.matcher( TimeLimitedCharSequence.withTimeout( strValue, nTimeoutMs ) ).matches( ) ? MatchResult.MATCH : MatchResult.NO_MATCH;
        }
        catch( MatchTimeoutException e )
        {
            AppLogService.error( "Regular expression {} took more than {} ms to match a value of {} characters", strRegex, nTimeoutMs, strValue.length( ) );
            return MatchResult.UNDECIDED;
        }
    }

    private static Pattern getPattern( String strRegex )
    {
        Pattern pattern = _mapPatterns.get( strRegex );

        if ( pattern == null )
        {
            try
            {
                pattern = Pattern.compile( strRegex );
            }
            catch( PatternSyntaxException e )
            {
                AppLogService.error( "Invalid regular expression {} : {}", strRegex, e.getDescription( ) );
                return null;
            }

            if ( _mapPatterns.size( ) >= MAX_CACHED_PATTERNS )
            {
                _mapPatterns.clear( );
            }
            _mapPatterns.put( strRegex, pattern );
        }

        return pattern;
    }

    /**
     * Thrown when the time limit of a matching is reached
     */
    private static final class MatchTimeoutException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        MatchTimeoutException( )
        {
            super( null, null, false, false );
        }
    }

    /**
     * A character sequence the regex engine reads character by character: reading it past the deadline aborts the matching
     */
    private static final class TimeLimitedCharSequence implements CharSequence
    {
        private static final int CHECK_INTERVAL = 1000;

        private final CharSequence _sequence;
        private final long _lDeadline;
        private int _nReads;

        static TimeLimitedCharSequence withTimeout( CharSequence sequence, long nTimeoutMs )
        {
            return new TimeLimitedCharSequence( sequence, System.nanoTime( ) + TimeUnit.MILLISECONDS.toNanos( nTimeoutMs ) );
        }

        private TimeLimitedCharSequence( CharSequence sequence, long lDeadline )
        {
            _sequence = sequence;
            _lDeadline = lDeadline;
        }

        @Override
        public char charAt( int index )
        {
            if ( ++_nReads % CHECK_INTERVAL == 0 && System.nanoTime( ) > _lDeadline )
            {
                throw new MatchTimeoutException( );
            }
            return _sequence.charAt( index );
        }

        @Override
        public int length( )
        {
            return _sequence.length( );
        }

        @Override
        public CharSequence subSequence( int start, int end )
        {
            return new TimeLimitedCharSequence( _sequence.subSequence( start, end ), _lDeadline );
        }

        @Override
        public String toString( )
        {
            return _sequence.toString( );
        }
    }
}
