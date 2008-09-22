package uk.org.netvu.core.cgi.events;

import static uk.org.netvu.core.cgi.common.Option.someRef;
import static uk.org.netvu.core.cgi.common.Parameter.bound;
import static uk.org.netvu.core.cgi.common.Parameter.notNegative;
import static uk.org.netvu.core.cgi.common.Parameter.param2;
import static uk.org.netvu.core.cgi.common.Parameter.param3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.Lists;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.ParameterMap;
import uk.org.netvu.core.cgi.common.Strings;

/**
 * A single result from the events database.
 */
public final class EventsCGIResult
{
    private static final Parameter<Integer, Option<Integer>> CAM = bound( 0,
            64, Parameter.param2( "cam", "The system camera number",
                    Conversion.stringToInt ) );

    private static final Parameter<String, Option<String>> ALARM = param2(
            "alarm", "The alarm description.", Option.<String> some() );

    private static final Parameter<Integer, Option<Integer>> JULIAN_TIME = notNegative( param2(
            "time", "The Julianised time that the event occurred at",
            Conversion.stringToInt ) );

    private static final Parameter<Integer, Option<Integer>> OFFSET = Parameter.bound(
            -90000, 90000, param2( "offset", "", Conversion.stringToInt ) );

    private static final Parameter<String, Option<String>> FILE = param2(
            "file", "", Option.<String> some() );

    private static final Parameter<Boolean, Option<Boolean>> ON_DISK = param2(
            "onDisk", "", someRef( Conversion.stringToBoolean ) );

    private static final Parameter<Integer, Option<Integer>> DURATION = notNegative( param2(
            "duration", "", Conversion.stringToInt ) );

    private static final Parameter<Integer, Option<Integer>> PRE_ALARM = notNegative( param2(
            "preAlarm", "", Conversion.stringToInt ) );

    private static final Parameter<Integer, Option<Integer>> ARCHIVE = notNegative( param2(
            "archive", "", Conversion.stringToInt ) );
    private static final Parameter<Status, Status> STATUS = param3( "status",
            "", Status.NONE, Status.fromString );
    private static final Parameter<AlarmType, AlarmType> ALARM_TYPE = param3(
            "alarmType", "", AlarmType.NONE, AlarmType.fromString );

    // this is an anonymous intialiser - it is creating a new ArrayList and
    // adding values to it inline.
    private static final ArrayList<Parameter<?, ? extends Option<?>>> compulsoryParams = new ArrayList<Parameter<?, ? extends Option<?>>>()
    {
        {
            add( CAM );
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

    private final ParameterMap parameterMap;

    /**
     * Constructs an EventsCGIResult given a Builder that contains all the
     * necessary information.
     * 
     * @param builder
     *        the ParameterMap to base this EventsCGIResult on.
     */
    private EventsCGIResult( final ParameterMap parameterMap )
    {
        for ( final Parameter<?, ? extends Option<?>> param : compulsoryParams )
        {

            if ( parameterMap.get( param ).isNone() )
            {
                throw new IllegalStateException( "The parameter " + param
                        + " has not been given a value" );
            }
        }

        this.parameterMap = parameterMap;
    }

    /**
     * A builder that takes in all the information needed to construct an
     * EventsCGIResult and constructs it.
     */
    public static final class Builder
    {
        private Option<ParameterMap> real = new Option.Some<ParameterMap>(
                new ParameterMap() );

        /**
         * Adds the system camera number to the builder.
         * 
         * @param cam
         *        the system camera number.
         * @return the builder.
         */
        public Builder cam( final int cam )
        {
            real = real.map( ParameterMap.withRef( CAM, cam ) );
            return this;
        }

        /**
         * Adds the alarm description to the builder.
         * 
         * @param alarm
         *        the alarm description.
         * @return the builder.
         */
        public Builder alarm( final String alarm )
        {
            real = real.map( ParameterMap.withRef( ALARM, alarm ) );
            return this;
        }

        /**
         * Adds the time of the event to the builder.
         * 
         * @param julianTime
         *        the time of the event.
         * @return the builder.
         */
        public Builder julianTime( final int julianTime )
        {
            real = real.map( ParameterMap.withRef( JULIAN_TIME, julianTime ) );
            return this;
        }

        /**
         * Adds the offset from UTC of the event to the builder.
         * 
         * @param offset
         *        the offset from UTC.
         * @return the builder.
         */
        public Builder offset( final int offset )
        {
            real = real.map( ParameterMap.withRef( OFFSET, offset ) );
            return this;
        }

        /**
         * Adds the name of the video file that the result was found in to the
         * builder.
         * 
         * @param file
         *        the name of the video file.
         * @return the builder.
         */
        public Builder file( final String file )
        {
            real = real.map( ParameterMap.withRef( FILE, file ) );
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
            real = real.map( ParameterMap.withRef( ON_DISK, onDisk ) );
            return this;
        }

        /**
         * Adds the duration of the event to the builder.
         * 
         * @param duration
         *        the duration of the event.
         * @return the builder.
         */
        public Builder duration( final int duration )
        {
            real = real.map( ParameterMap.withRef( DURATION, duration ) );
            return this;
        }

        /**
         * Specifies the number of seconds before the alarm time that are
         * relevant.
         * 
         * @param preAlarm
         *        the number of seconds before the alarm time that are relevant.
         * @return the builder.
         */
        public Builder preAlarm( final int preAlarm )
        {
            real = real.map( ParameterMap.withRef( PRE_ALARM, preAlarm ) );
            return this;
        }

        /**
         * Specifies the number of seconds since 1970 after which the video for
         * this event may be overwritten.
         * 
         * @param archive
         *        the number of seconds since 1970 after which the video for
         *        this event may be overwritten.
         * @return the builder.
         */
        public Builder archive( final int archive )
        {
            real = real.map( ParameterMap.withRef( ARCHIVE, archive ) );
            return this;
        }

        /**
         * Specifies the status of the database record - i.e., how complete it
         * is on disk.
         * 
         * @param status
         *        the status of the database record.
         * @return the builder.
         */
        public Builder status( final Status status )
        {
            real = real.map( ParameterMap.withRef( STATUS, status ) );
            return this;
        }

        /**
         * Specifies the type of alarm that this event represents.
         * 
         * @param alarmType
         *        the type of alarm that this event represents.
         * @return the builder.
         */
        public Builder alarmType( final AlarmType alarmType )
        {
            real = real.map( ParameterMap.withRef( ALARM_TYPE, alarmType ) );
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
                real = new Option.None<ParameterMap>();
            }
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
         * The numeric value associated with this state.
         */
        public final int value;

        Status( final int value )
        {
            this.value = value;
        }

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

            throw new IllegalArgumentException("There is no Status with the value "+value);
        }

        static Status oneOf( final Random random )
        {
            return Status.values()[random.nextInt( Status.values().length )];
        }

        static final Conversion<String, Option<Status>> fromString = new Conversion<String, Option<Status>>()
        {
            @Override
            public Option<Status> convert( final String s )
            {
                try
                {
                    return new Option.Some<Status>(find( Integer.parseInt( s ) ));
                }
                catch (IllegalArgumentException e)
                {
                    return new Option.None<Status>();
                }
            }
        };
    }

    /**
     * The system camera number.
     * 
     * @return the system camera number.
     */
    public int getCam()
    {
        return parameterMap.get( CAM ).get();
    }

    /**
     * The alarm description.
     * 
     * @return the alarm description.
     */
    public String getAlarm()
    {
        return parameterMap.get( ALARM ).get();
    }

    /**
     * The time in seconds since 1970 that the event occurred at.
     * 
     * @return the time in seconds.
     */
    public int getJulianTime()
    {
        return parameterMap.get( JULIAN_TIME ).get();
    }

    /**
     * The timezone offset from UTC, in seconds. E.g., BST is 3600.
     * 
     * @return the timezone offset from UTC.
     */
    public int getOffset()
    {
        return parameterMap.get( OFFSET ).get();
    }

    /**
     * Gets the name of the video file that the result was found in.
     * 
     * @return the name of the video file that the result was found in.
     */
    public String getFile()
    {
        return parameterMap.get( FILE ).get();
    }

    /**
     * Reports whether the video for the event is available on disk.
     * 
     * @return whether the video for the event is available on disk.
     */
    public boolean isOnDisk()
    {
        return parameterMap.get( ON_DISK ).get();
    }

    /**
     * The duration of the event in seconds. The duration plus the julian time
     * will never be larger than Integer.MAX_VALUE (or negative).
     * 
     * @return the duration of the event in seconds.
     */
    public int getDuration()
    {
        return parameterMap.get( DURATION ).get();
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
        return parameterMap.get( PRE_ALARM ).get();
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
        return parameterMap.get( ARCHIVE ).get();
    }

    /**
     * Gives the type of alarm that this event is.
     * 
     * @return the type of alarm that this event is.
     */
    public AlarmType getAlarmType()
    {
        return parameterMap.get( ALARM_TYPE );
    }

    /**
     * Gives the status of the database record - i.e., how complete it is on
     * disk.
     * 
     * @return the status of the database record.
     */
    public Status getStatus()
    {
        return parameterMap.get( STATUS );
    }

    /**
     * Parses comma separated values in the format defined in the Video Server
     * Specification.
     * 
     * @param line
     *        the comma-separated values.
     * @return an EventsCGIResult containing the parsed values.
     */
    public static Option<EventsCGIResult> fromString( final String line )
    {
        final String[] values = Strings.split( line );

        if ( values.length < 11 || values.length > 13 )
        {
            throw new IllegalArgumentException( "The line ( " + line
                    + ") needs to contain 11 to 13 fields, not "
                    + values.length );
        }

        final List<Parameter<?, ?>> params = new ArrayList<Parameter<?, ?>>(
                compulsoryParams );
        if ( values.length > 11 )
        {
            params.add( STATUS );
        }
        if ( values.length > 12 )
        {
            params.add( ALARM_TYPE );
        }

        return ParameterMap.fromStrings( params,
                Lists.removeIndices( Arrays.asList( values ), 0, 7 ) ).map(
                new Conversion<ParameterMap, EventsCGIResult>()
                {
                    @Override
                    public EventsCGIResult convert( final ParameterMap map )
                    {
                        return new EventsCGIResult( map );
                    }
                } );
    }

    String toCSV( final int index )
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
     * Converts this object to a String holding comma separated values in the
     * same format as in the events database. The index field is always 0.
     */
    @Override
    public String toString()
    {
        return toCSV( 0 );
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
         * The numeric value associated with this state.
         */
        public final int value;

        AlarmType( final int value )
        {
            this.value = value;
        }

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

            throw new IllegalArgumentException();
        }

        static AlarmType oneOf( final Random random )
        {
            return AlarmType.values()[random.nextInt( AlarmType.values().length )];
        }

        static final Conversion<String, Option<AlarmType>> fromString = new Conversion<String, Option<AlarmType>>()
        {
            @Override
            public Option<AlarmType> convert( final String t )
            {
                try
                {
                    return new Option.Some<AlarmType>(find( Integer.parseInt( t ) ));
                }
                catch (IllegalArgumentException e)
                {
                    return new Option.None<AlarmType>();
                }
            }
        };
    }
}
