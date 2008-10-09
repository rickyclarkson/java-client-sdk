package uk.org.netvu.core.cgi.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Lists;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.ParameterMap;
import uk.org.netvu.core.cgi.common.Strings;
import uk.org.netvu.core.cgi.common.TwoWayConversion;

/**
 * A single result from the events database.
 */
public final class EventsCGIResult
{
    private static final Parameter<Integer, Option<Integer>> CAMERA_PARAMETER = Parameter.bound(
            0, 64, Parameter.parameter( "cam", TwoWayConversion.integer ) );

    private static final Parameter<String, Option<String>> ALARM = Parameter.parameter(
            "alarm", TwoWayConversion.string );

    private static final Parameter<Integer, Option<Integer>> JULIAN_TIME = Parameter.notNegative( Parameter.parameter(
            "time", TwoWayConversion.integer ) );

    private static final Parameter<Integer, Option<Integer>> OFFSET = Parameter.bound(
            -90000, 90000, Parameter.parameter( "offset", TwoWayConversion.integer ) );

    private static final Parameter<String, Option<String>> FILE = Parameter.parameter(
            "file", TwoWayConversion.string );

    private static final Parameter<Boolean, Option<Boolean>> ON_DISK = Parameter.parameter(
            "onDisk", TwoWayConversion.total( Conversion.equal( "exists" ),
                    Conversion.fromBoolean( "exists", "overwitten" ) ) );

    private static final Parameter<Integer, Option<Integer>> DURATION = Parameter.notNegative( Parameter.parameter(
            "duration", TwoWayConversion.integer ) );

    private static final Parameter<Integer, Option<Integer>> PRE_ALARM = Parameter.notNegative( Parameter.parameter(
            "preAlarm", TwoWayConversion.integer ) );

    private static final Parameter<Integer, Option<Integer>> ARCHIVE = Parameter.notNegative( Parameter.parameter(
            "archive", TwoWayConversion.integer ) );
    private static final Parameter<Status, Status> STATUS = Parameter.parameterWithDefault(
            "status", Status.NONE,
            TwoWayConversion.convenientPartial( Status.fromString ) );
    private static final Parameter<AlarmType, AlarmType> ALARM_TYPE = Parameter.parameterWithDefault(
            "alarmType", AlarmType.NONE,
            TwoWayConversion.convenientPartial( AlarmType.fromString ) );

    // this is an anonymous intialiser - it is creating a new ArrayList and
    // adding values to it inline.
    private static final ArrayList<Parameter<?, ? extends Option<?>>> compulsoryParameters = new ArrayList<Parameter<?, ? extends Option<?>>>()
    {
        {
            add( CAMERA_PARAMETER );
            add( ALARM );
            add( JULIAN_TIME );
            add( OFFSET );
            add( FILE );
            add( ON_DISK );
            add( DURATION );
            add( PRE_ALARM );
            add( ARCHIVE );
        }
    };

    /**
     * Parses comma separated values in the format defined in the Video Server
     * Specification.
     * 
     * @param line
     *        the comma-separated values.
     * @return an EventsCGIResult containing the parsed values.
     */
    public static EventsCGIResult fromString( final String line )
    {
        final String[] values = Strings.split( line );

        if ( values.length < 11 || values.length > 13 )
        {
            throw new IllegalArgumentException( "The line ( " + line
                    + ") needs to contain 11 to 13 fields, not "
                    + values.length );
        }

        final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>(
                compulsoryParameters );
        if ( values.length > 11 )
        {
            params.add( STATUS );
        }
        if ( values.length > 12 )
        {
            params.add( ALARM_TYPE );
        }

        final Option<EventsCGIResult> result = ParameterMap.fromStrings(
                params, Lists.removeIndices( Arrays.asList( values ), 0, 7 ) ).map(
                new Conversion<ParameterMap, EventsCGIResult>()
                {
                    @Override
                    public EventsCGIResult convert( final ParameterMap map )
                    {
                        return new EventsCGIResult( map );
                    }
                } );

        if ( result.isEmpty() )
        {
            throw new IllegalArgumentException(
                    "The supplied data, "
                            + line
                            + ", is not valid for EventsCGIResult.fromString, because: "
                            + result.reason() );
        }

        return result.get();
    }

    private final ParameterMap builtMap;

    private EventsCGIResult( final ParameterMap builtMap )
    {
        for ( final Parameter<?, ? extends Option<?>> parameter : compulsoryParameters )
        {
            if ( builtMap.get( parameter ).isEmpty() )
            {
                throw new IllegalStateException( "The parameter " + parameter
                        + " has not been given a value" );
            }
        }

        this.builtMap = builtMap;
    }

    /**
     * The alarm description.
     * 
     * @return the alarm description.
     */
    public String getAlarm()
    {
        return builtMap.get( ALARM ).get();
    }

    /**
     * Gives the type of alarm that this event is.
     * 
     * @return the type of alarm that this event is.
     */
    public AlarmType getAlarmType()
    {
        return builtMap.get( ALARM_TYPE );
    }

    /**
     * The number of seconds since 1970 after which the video for this event may
     * be overwritten.
     * 
     * @return the number of seconds since 1970 after which the video for this
     *         event may be overwritten.
     */
    public int getArchive()
    {
        return builtMap.get( ARCHIVE ).get();
    }

    /**
     * The system camera number.
     * 
     * @return the system camera number.
     */
    public int getCam()
    {
        return builtMap.get( CAMERA_PARAMETER ).get();
    }

    /**
     * The duration of the event in seconds. The duration plus the julian time
     * will never be larger than Integer.MAX_VALUE (or negative).
     * 
     * @return the duration of the event in seconds.
     */
    public int getDuration()
    {
        return builtMap.get( DURATION ).get();
    }

    /**
     * Gets the name of the video file that the result was found in.
     * 
     * @return the name of the video file that the result was found in.
     */
    public String getFile()
    {
        return builtMap.get( FILE ).get();
    }

    /**
     * The time in seconds since 1970 that the event occurred at.
     * 
     * @return the time in seconds.
     */
    public int getJulianTime()
    {
        return builtMap.get( JULIAN_TIME ).get();
    }

    /**
     * The timezone offset from UTC, in seconds. E.g., BST is 3600.
     * 
     * @return the timezone offset from UTC.
     */
    public int getOffset()
    {
        return builtMap.get( OFFSET ).get();
    }

    /**
     * The number of seconds before the alarm time that are related to the
     * event.
     * 
     * @return the number of seconds before the alarm time that are related to
     *         the event.
     */
    public int getPreAlarm()
    {
        return builtMap.get( PRE_ALARM ).get();
    }

    /**
     * Gives the status of the database record - i.e., how complete it is on
     * disk.
     * 
     * @return the status of the database record.
     */
    public Status getStatus()
    {
        return builtMap.get( STATUS );
    }

    /**
     * Reports whether the video for the event is available on disk.
     * 
     * @return whether the video for the event is available on disk.
     */
    public boolean isOnDisk()
    {
        return builtMap.get( ON_DISK ).get();
    }

    /**
     * Creates a CSV representation of this EventsCGIResult. Note that the CSV
     * contains an index, twice, and the EventsCGIResult does not store that, so
     * a caller needs to pass the index of the result to toCSV().
     * 
     * @param index
     *        the value of the index field in the CSV.
     * @return a CSV representation of this EventsCGIResult.
     */
    public String toCSV( final int index )
    {
        // "overwitten" is taken from server output

        final List<Object> all = new ArrayList<Object>( Arrays.<Object> asList(
                index, getCam(), getAlarm(), getJulianTime(), getOffset(),
                getFile(), isOnDisk() ? "exists" : "overwitten", index,
                getDuration(), getPreAlarm(), getArchive() ) );

        final List<Object> alarmAndStatus = getAlarmType() == AlarmType.NONE
                && getStatus() == Status.NONE ? Collections.emptyList()
                : Arrays.<Object> asList( getStatus().value,
                        getAlarmType().value );

        all.addAll( alarmAndStatus );

        return Strings.intersperse( ", ", Lists.map( all,
                Conversion.objectToString() ) );
    }

    /**
     * Throws an UnsupportedOperationException - use toCSV instead.
     */
    @Deprecated
    @Override
    public String toString()
    {
        throw new UnsupportedOperationException( "Use toCSV instead." );
    }

    /**
     * The type of alarm that caused an event.
     */
    public enum AlarmType
    {
        /**
         * No alarm type was specified in the data.
         */
        NONE( 0 ),

        /**
         * An alarm contact and/or a VMD event.
         */
        ZONE( 1 ),

        /**
         * A VMD event.
         */
        VMD( 2 ),

        /**
         * A GPS event.
         */
        GPS( 4 ),

        /**
         * A system event, e.g., startup, set or unset.
         */
        SYSTEM( 8 ),

        /**
         * A dummy alarm type used for CGI parameters - never appears in a
         * database record.
         */
        CAMERA( 16 ),

        /**
         * Used for activity search, never appears in a database record.
         */
        ACTIVITY( 32 ),

        /**
         * Used for text-in-image keyword search, never appears in a database
         * record.
         */
        KEYWORD( 64 );

        /**
         * Finds an AlarmType that has the supplied value.
         * 
         * @param value
         *        the value to search the AlarmTypes for.
         * @return the AlarmType that has the supplied value.
         * @throws IllegalArgumentException
         *         if no matching AlarmType exists.
         */
        public static AlarmType find( final int value )
                throws IllegalArgumentException
        {
            for ( final AlarmType type : AlarmType.values() )
            {
                if ( type.value == value )
                {
                    return type;
                }
            }

            throw new IllegalArgumentException(
                    "There is no AlarmType with the value " + value );
        }

        /**
         * The numeric value associated with this state.
         */
        public final int value;

        /**
         * A Conversion that converts any String into an AlarmType if it can,
         * storing the result in an Option.
         */
        static final Conversion<String, Option<AlarmType>> fromString = new Conversion<String, Option<AlarmType>>()
        {
            @Override
            public Option<AlarmType> convert( final String t )
            {
                try
                {
                    return Option.some( find( Integer.parseInt( t ) ) );
                }
                catch ( final IllegalArgumentException e )
                {
                    return Option.none( t + " is not a valid AlarmType" );
                }
            }
        };

        AlarmType( final int value )
        {
            this.value = value;
        }
    }

    /**
     * A builder that takes in all the information needed to construct an
     * EventsCGIResult and constructs it.
     */
    public static final class Builder
    {
        private Option<ParameterMap> real = Option.some( new ParameterMap() );

        /**
         * Constructs a builder ready to take in all the information needed to
         * construct an EventsCGIResult.
         */
        public Builder()
        {
        }

        /**
         * Adds the alarm description to the builder. Cannot be null.
         * 
         * @param alarm
         *        the alarm description.
         * @return the builder.
         */
        public Builder alarm( final String alarm )
        {
            real = real.map( ParameterMap.setter( ALARM, alarm ) );
            return this;
        }

        /**
         * Specifies the type of alarm that this event represents. Cannot be
         * null.
         * 
         * @param alarmType
         *        the type of alarm that this event represents.
         * @return the builder.
         */
        public Builder alarmType( final AlarmType alarmType )
        {
            real = real.map( ParameterMap.setter( ALARM_TYPE, alarmType ) );
            return this;
        }

        /**
         * Specifies the number of seconds since 1970 after which the video for
         * this event may be overwritten. Cannot be negative.
         * 
         * @param archive
         *        the number of seconds since 1970 after which the video for
         *        this event may be overwritten.
         * @return the builder.
         */
        public Builder archive( final int archive )
        {
            real = real.map( ParameterMap.setter( ARCHIVE, archive ) );
            return this;
        }

        /**
         * Creates an EventsCGIResult containing all the values added to the
         * builder.
         * 
         * @return the created event.
         */
        public EventsCGIResult build()
        {
            try
            {
                return new EventsCGIResult( real.get() );
            }
            finally
            {
                real = Option.none( "The Builder has already had build() called on it" );
            }
        }

        /**
         * Adds the system camera number to the builder. Valid values are 0 to
         * 64.
         * 
         * @param camera
         *        the system camera number.
         * @return the builder.
         */
        public Builder camera( final int camera )
        {
            real = real.map( ParameterMap.setter( CAMERA_PARAMETER, camera ) );
            return this;
        }

        /**
         * Adds the duration of the event to the builder. Cannot be negative.
         * 
         * @param duration
         *        the duration of the event.
         * @return the builder.
         */
        public Builder duration( final int duration )
        {
            real = real.map( ParameterMap.setter( DURATION, duration ) );
            return this;
        }

        /**
         * Adds the name of the video file that the result was found in to the
         * builder. Cannot be null.
         * 
         * @param file
         *        the name of the video file.
         * @return the builder.
         */
        public Builder file( final String file )
        {
            real = real.map( ParameterMap.setter( FILE, file ) );
            return this;
        }

        /**
         * Adds the time of the event to the builder. Cannot be negative.
         * 
         * @param julianTime
         *        the time of the event.
         * @return the builder.
         */
        public Builder julianTime( final int julianTime )
        {
            real = real.map( ParameterMap.setter( JULIAN_TIME, julianTime ) );
            return this;
        }

        /**
         * Adds the offset from UTC of the event to the builder. Must be between
         * -90000 and +90000.
         * 
         * @param offset
         *        the offset from UTC.
         * @return the builder.
         */
        public Builder offset( final int offset )
        {
            real = real.map( ParameterMap.setter( OFFSET, offset ) );
            return this;
        }

        /**
         * Adds information about whether the video file is still available, to
         * the builder.
         * 
         * @param onDisk
         *        true if the video file is still available, false otherwise.
         * @return the builder.
         */
        public Builder onDisk( final boolean onDisk )
        {
            real = real.map( ParameterMap.setter( ON_DISK, onDisk ) );
            return this;
        }

        /**
         * Specifies the number of seconds before the alarm time that are
         * relevant. Cannot be negative.
         * 
         * @param preAlarm
         *        the number of seconds before the alarm time that are relevant.
         * @return the builder.
         */
        public Builder preAlarm( final int preAlarm )
        {
            real = real.map( ParameterMap.setter( PRE_ALARM, preAlarm ) );
            return this;
        }

        /**
         * Specifies the status of the database record - i.e., how complete it
         * is on disk. Cannot be null.
         * 
         * @param status
         *        the status of the database record.
         * @return the builder.
         */
        public Builder status( final Status status )
        {
            real = real.map( ParameterMap.setter( STATUS, status ) );
            return this;
        }
    }

    /**
     * An enum consisting of various states a database record can be in.
     */
    public enum Status
    {
        /**
         * The status was not included in the data.
         */
        NONE( 0 ),
        /**
         * A handle for writing the record has been allocated, and a timestamp
         * for it set.
         */
        PENDING( 1 ),
        /**
         * The initial data in the record has been written, but there may be
         * more data to write.
         */
        NEW( 2 ),
        /**
         * All data has been written or updated.
         */
        CLOSED( 4 ),
        /**
         * The data for this record has been archived to FTP server.
         */
        ARCHIVED( 8 );

        /**
         * Finds the Status whose numeric value is the same as the value
         * parameter.
         * 
         * @param value
         *        the numeric value to search for.
         * @return the Status whose numeric value is the same as the value
         *         parameter.
         */
        public static Status find( final int value )
        {
            for ( final Status status : Status.values() )
            {
                if ( status.value == value )
                {
                    return status;
                }
            }

            throw new IllegalArgumentException(
                    "There is no Status with the value " + value );
        }

        /**
         * The numeric value associated with this state.
         */
        public final int value;

        private static final Conversion<String, Option<Status>> fromString = new Conversion<String, Option<Status>>()
        {
            @Override
            public Option<Status> convert( final String s )
            {
                try
                {
                    return Option.some( find( Integer.parseInt( s ) ) );
                }
                catch ( final IllegalArgumentException e )
                {
                    return Option.none( s + " is not a valid Status" );
                }
            }
        };

        Status( final int value )
        {
            this.value = value;
        }
    }
}
