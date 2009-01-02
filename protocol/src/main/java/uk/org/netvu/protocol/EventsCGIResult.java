package uk.org.netvu.protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import uk.org.netvu.util.CheckParameters;

/**
 * A single result from the events database. This can be used to build a result
 * (via EventsCGIResult.Builder), or to parse a result from CSV.
 */
public final class EventsCGIResult
{
    /**
     * CAMERA_PARAMETER is not named CAMERA because that would conflict with
     * AlarmType.CAMERA.
     */
    private static final ParameterDescription<Integer, Option<Integer>> CAMERA_PARAMETER =
            ParameterDescription.parameterWithBounds( 0, 64, ParameterDescription.parameterWithoutDefault( "cam",
                    StringConversion.integer() ) );

    private static final ParameterDescription<String, Option<String>> ALARM =
            ParameterDescription.parameterWithoutDefault( "alarm", StringConversion.string() );

    private static final ParameterDescription<Integer, Option<Integer>> JULIAN_TIME =
            ParameterDescription.nonNegativeParameter( ParameterDescription.parameterWithoutDefault( "time",
                    StringConversion.integer() ) );

    private static final ParameterDescription<Integer, Option<Integer>> OFFSET =
            ParameterDescription.parameterWithBounds( -90000, 90000, ParameterDescription.parameterWithoutDefault(
                    "offset", StringConversion.integer() ) );

    private static final ParameterDescription<String, Option<String>> FILE =
            ParameterDescription.parameterWithoutDefault( "file", StringConversion.string() );

    private static final ParameterDescription<Boolean, Option<Boolean>> ON_DISK =
            ParameterDescription.parameterWithoutDefault( "onDisk", StringConversion.total(
                    Function.equal( "exists" ), Function.fromBoolean( "exists", "overwitten" ) ) );

    private static final ParameterDescription<Integer, Option<Integer>> DURATION =
            ParameterDescription.nonNegativeParameter( ParameterDescription.parameterWithoutDefault( "duration",
                    StringConversion.integer() ) );

    private static final ParameterDescription<Integer, Option<Integer>> PRE_ALARM =
            ParameterDescription.nonNegativeParameter( ParameterDescription.parameterWithoutDefault( "preAlarm",
                    StringConversion.integer() ) );

    private static final ParameterDescription<Integer, Option<Integer>> ARCHIVE =
            ParameterDescription.nonNegativeParameter( ParameterDescription.parameterWithoutDefault( "archive",
                    StringConversion.integer() ) );

    private static final ParameterDescription<Status, Status> STATUS =
            ParameterDescription.parameterWithDefault( "status", Status.NONE,
                    StringConversion.convenientPartial( Status.fromString() ) );

    private static final ParameterDescription<AlarmType, AlarmType> ALARM_TYPE =
            ParameterDescription.parameterWithDefault( "alarmType", AlarmType.NONE,
                    StringConversion.convenientPartial( AlarmType.fromString ) );

    private static final ArrayList<ParameterDescription<?, ? extends Option<?>>> compulsoryParameters =
            new ArrayList<ParameterDescription<?, ? extends Option<?>>>()
            {
                {
                    // this is an anonymous initialiser - it creates an
                    // ArrayList and adds values to it inline.
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
    public static EventsCGIResult fromCSV( final String line )
    {
        CheckParameters.areNotNull( line );

        final String[] values = Strings.splitCSV( line );

        if ( values.length < 11 || values.length > 13 )
        {
            throw new IllegalArgumentException( "The line ( " + line + ") needs to contain 11 to 13 fields, not "
                    + values.length );
        }

        final List<ParameterDescription<?, ?>> parameterDescriptions =
                new ArrayList<ParameterDescription<?, ?>>( compulsoryParameters );

        if ( values.length > 11 )
        {
            parameterDescriptions.add( STATUS );
        }
        if ( values.length > 12 )
        {
            parameterDescriptions.add( ALARM_TYPE );
        }

        final List<String> removeItems0And7 = Lists.removeByIndices( Arrays.asList( values ), 0, 7 );

        final Option<ParameterMap> parameterMap = ParameterMap.fromStrings( parameterDescriptions, removeItems0And7 );

        final Option<EventsCGIResult> result = parameterMap.map( new Function<ParameterMap, EventsCGIResult>()
        {
            @Override
            public EventsCGIResult apply( final ParameterMap map )
            {
                return new EventsCGIResult( map );
            }
        } );

        if ( result.isEmpty() )
        {
            throw new IllegalArgumentException( "The supplied data, " + line
                    + ", is not valid for EventsCGIResult.fromString, because: " + result.reason() );
        }

        return result.get();
    }

    private final ParameterMap builtMap;

    /**
     * Constructs an EventsCGIResult with the final ParameterMap after
     * Builder.build() is called.
     * 
     * @param builtMap
     *        the final ParameterMap to get values from.
     * @throws NullPointerException
     *         if builtMap is null.
     */
    private EventsCGIResult( final ParameterMap builtMap )
    {
        CheckParameters.areNotNull( builtMap );

        for ( final ParameterDescription<?, ? extends Option<?>> parameterDescription : compulsoryParameters )
        {
            if ( builtMap.get( parameterDescription ).isEmpty() )
            {
                throw new IllegalStateException( "The parameter " + parameterDescription
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

        final List<Object> all =
                new ArrayList<Object>( Arrays.<Object> asList( index, getCam(), getAlarm(), getJulianTime(),
                        getOffset(), getFile(), isOnDisk() ? "exists" : "overwitten", index, getDuration(),
                        getPreAlarm(), getArchive() ) );

        final List<Object> alarmAndStatus;
        if ( getAlarmType() == AlarmType.NONE && getStatus() == Status.NONE )
        {
            alarmAndStatus = Collections.emptyList();
        }
        else
        {
            alarmAndStatus = Arrays.<Object> asList( getStatus().value, getAlarmType().value );
        }

        all.addAll( alarmAndStatus );

        return Strings.intersperse( ", ", Lists.map( all, Function.getObjectToStringFunction() ) );
    }

    /**
     * Throws an UnsupportedOperationException - use toCSV instead.
     */
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
        public static AlarmType find( final int value ) throws IllegalArgumentException
        {
            for ( final AlarmType type : AlarmType.values() )
            {
                if ( type.value == value )
                {
                    return type;
                }
            }

            throw new IllegalArgumentException( "There is no AlarmType with the value " + value );
        }

        /**
         * The numeric value associated with this state.
         */
        public final int value;

        /**
         * A Function that converts any String into an AlarmType if it can,
         * storing the result in an Option.
         */
        static final Function<String, Option<AlarmType>> fromString = new Function<String, Option<AlarmType>>()
        {
            @Override
            public Option<AlarmType> apply( final String t )
            {
                CheckParameters.areNotNull( t );

                try
                {
                    return Option.getFullOption( find( Integer.parseInt( t ) ) );
                }
                catch ( final IllegalArgumentException e )
                {
                    return Option.getEmptyOption( t + " is not a valid AlarmType" );
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
     * EventsCGIResult and constructs it. Note that a Builder can only have
     * build() called on it once. Calling build() more than once will cause an
     * IllegalStateException. Setting its values after build() has been called
     * will cause an IllegalStateException.
     */
    public static final class Builder
    {
        private Option<ParameterMap> parameterMap = Option.getFullOption( new ParameterMap() );

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
         * @throws NullPointerException
         *         if alarm is null.
         * @return the builder.
         */
        public Builder alarm( final String alarm )
        {
            return set( ALARM, alarm );
        }

        /**
         * Specifies the type of alarm that this event represents. Cannot be
         * null.
         * 
         * @param alarmType
         *        the type of alarm that this event represents.
         * @throws NullPointerException
         *         if alarmType is null.
         * @return the builder.
         */
        public Builder alarmType( final AlarmType alarmType )
        {
            return set( ALARM_TYPE, alarmType );
        }

        /**
         * Specifies the number of seconds since 1970 after which the video for
         * this event may be overwritten. Cannot be negative.
         * 
         * @param archive
         *        the number of seconds since 1970 after which the video for
         *        this event may be overwritten.
         * @throws IllegalArgumentException
         *         if archive is negative.
         * @return the builder.
         */
        public Builder archive( final int archive )
        {
            return set( ARCHIVE, archive );
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
                return new EventsCGIResult( parameterMap.get() );
            }
            finally
            {
                parameterMap = Option.getEmptyOption( "The Builder has already had build() called on it" );
            }
        }

        /**
         * Adds the system camera number to the builder. Valid values are 0 to
         * 63 inclusive.
         * 
         * @param camera
         *        the system camera number.
         * @throws IllegalArgumentException
         *         if camera is not within 0 to 63 inclusive.
         * @return the Builder.
         */
        public Builder camera( final int camera )
        {
            return set( CAMERA_PARAMETER, camera );
        }

        /**
         * Adds the duration of the event to the builder. Cannot be negative.
         * 
         * @param duration
         *        the duration of the event.
         * @throws IllegalArgumentException
         *         if duration is negative.
         * @return the Builder.
         */
        public Builder duration( final int duration )
        {
            return set( DURATION, duration );
        }

        /**
         * Adds the name of the video file that the result was found in to the
         * builder. Cannot be null.
         * 
         * @param file
         *        the name of the video file.
         * @throws NullPointerException
         *         if file is negative.
         * @return the Builder.
         */
        public Builder file( final String file )
        {
            return set( FILE, file );
        }

        /**
         * Adds the time of the event to the builder. Cannot be negative.
         * 
         * @param julianTime
         *        the time of the event.
         * @throws IllegalArgumentException
         *         if julianTime is negative.
         * @return the Builder.
         */
        public Builder julianTime( final int julianTime )
        {
            return set( JULIAN_TIME, julianTime );
        }

        /**
         * Adds the offset from UTC of the event to the builder. Must be between
         * -90,000 and +90,000 inclusive.
         * 
         * @param offset
         *        the offset from UTC.
         * @throws IllegalArgumentException
         *         if offset is not between -90,000 and +90,000 inclusive.
         * @return the Builder.
         */
        public Builder offset( final int offset )
        {
            return set( OFFSET, offset );
        }

        /**
         * Adds information about whether the video file is still available, to
         * the builder.
         * 
         * @param onDisk
         *        true if the video file is still available, false otherwise.
         * @return the Builder.
         */
        public Builder onDisk( final boolean onDisk )
        {
            return set( ON_DISK, onDisk );
        }

        /**
         * Specifies the number of seconds before the alarm time that are
         * relevant. Cannot be negative.
         * 
         * @param preAlarm
         *        the number of seconds before the alarm time that are relevant.
         * @throws IllegalArgumentException
         *         if preAlarm is negative.
         * @return the Builder.
         */
        public Builder preAlarm( final int preAlarm )
        {
            return set( PRE_ALARM, preAlarm );
        }

        /**
         * Specifies the status of the database record - i.e., how complete it
         * is on disk. Cannot be null.
         * 
         * @param status
         *        the status of the database record.
         * @throws NullPointerException
         *         if status is negative.
         * @return the Builder.
         */
        public Builder status( final Status status )
        {
            return set( STATUS, status );
        }

        /**
         * Sets the value of a parameter to a given value, and returns the
         * Builder.
         * 
         * @param <T>
         *        the input type of the specified parameter.
         * @param parameter
         *        the parameter to set a value for.
         * @param value
         *        the value to give that parameter.
         * @return the Builder.
         * @throws IllegalStateException
         *         if the Builder has already been built once.
         */
        private <T> Builder set( final ParameterDescription<T, ?> parameter, final T value )
        {
            if ( parameterMap.isEmpty() )
            {
                final String message = "The Builder has already been built (build() has been called on it).";
                throw new IllegalStateException( message );
            }

            parameterMap = Option.getFullOption( parameterMap.get().set( parameter, value ) );
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

            throw new IllegalArgumentException( "There is no Status with the value " + value );
        }

        /**
         * A Function that parses a String into a Status, giving the Status in
         * an Option. The Option is empty if the parsing fails.
         * 
         * @return a Function that parses a String into a Status.
         */
        private static Function<String, Option<Status>> fromString()
        {
            return new Function<String, Option<Status>>()
            {
                @Override
                public Option<Status> apply( final String s )
                {
                    CheckParameters.areNotNull( s );

                    try
                    {
                        return Option.getFullOption( find( Integer.parseInt( s ) ) );
                    }
                    catch ( final IllegalArgumentException e )
                    {
                        return Option.getEmptyOption( s + " is not a valid Status" );
                    }
                }
            };
        }

        /**
         * The numeric value associated with this state.
         */
        public final int value;

        /**
         * Constructs a Status with the given value.
         * 
         * @param value
         *        the numeric value associated with the constructed Status.
         */
        Status( final int value )
        {
            this.value = value;
        }
    }
}
