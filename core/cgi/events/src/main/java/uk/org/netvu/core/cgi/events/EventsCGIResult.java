package uk.org.netvu.core.cgi.events;

import static uk.org.netvu.core.cgi.common.Parameter.bound;
import static uk.org.netvu.core.cgi.common.Parameter.notNegative;
import static uk.org.netvu.core.cgi.common.Parameter.param;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.GenericBuilder;
import uk.org.netvu.core.cgi.common.Lists;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.Strings;

/**
 * A single result from the events database.
 */
public final class EventsCGIResult
{
    private static final Parameter<Integer, Option<Integer>> camParameter = bound(
            0, 64, Parameter.param( "cam", "The system camera number",
                    Conversion.stringToInt ) );

    private static final Parameter<String, Option<String>> alarmParameter = param(
            "alarm", "The alarm description.", Conversion.<String> identity() );

    private static final Parameter<Integer, Option<Integer>> julianTimeParameter = notNegative( param(
            "time", "The Julianised time that the event occurred at",
            Conversion.stringToInt ) );

    private static final Parameter<Integer, Option<Integer>> offsetParameter = Parameter.bound(
            -90000, 90000, param( "offset", "", Conversion.stringToInt ) );

    private static final Parameter<String, Option<String>> fileParameter = param(
            "file", "", Conversion.<String> identity() );

    private static final Parameter<Boolean, Option<Boolean>> onDiskParameter = param(
            "onDisk", "", Conversion.stringToBoolean );

    private static final Parameter<Integer, Option<Integer>> durationParameter = notNegative( param(
            "duration", "", Conversion.stringToInt ) );

    private static final Parameter<Integer, Option<Integer>> preAlarmParameter = notNegative( param(
            "preAlarm", "", Conversion.stringToInt ) );

    private static final Parameter<Integer, Option<Integer>> archiveParameter = notNegative( param(
            "archive", "", Conversion.stringToInt ) );
    private static final Parameter<Status, Status> statusParameter = param(
            "status", "", Status.NONE, Status.fromString );
    private static final Parameter<AlarmType, AlarmType> alarmTypeParameter = param(
            "alarmType", "", AlarmType.NONE, AlarmType.fromString );

    private static final ArrayList<Parameter<?, ? extends Option<?>>> compulsoryParams = new ArrayList<Parameter<?, ? extends Option<?>>>()
    {
        {
            add( camParameter );
            add( alarmParameter );
            add( julianTimeParameter );
            add( offsetParameter );
            add( fileParameter );
            add( onDiskParameter );
            add( durationParameter );
            add( preAlarmParameter );
            add( archiveParameter );
        }
    };

    private final GenericBuilder builder;

    /**
     * Constructs an EventsCGIResult given a Builder that contains all the
     * necessary information.
     * 
     * @param builder
     *        the Builder to base this EventsCGIResult on.
     */
    private EventsCGIResult( final GenericBuilder builder )
    {
        for ( final Parameter<?, ? extends Option<?>> param : compulsoryParams )
        {

            if ( builder.get( param ).isNone() )
            {
                throw new IllegalStateException( "The parameter " + param
                        + " has not been given a value" );
            }
        }

        this.builder = builder;
    }

    /**
     * A builder that takes in all the information needed to construct an
     * EventsCGIResult and constructs it.
     */
    public static final class Builder
    {
        private GenericBuilder real = new GenericBuilder();

        /**
         * Adds the system camera number to the builder.
         * 
         * @param cam
         *        the system camera number.
         * @return the builder.
         */
        public Builder cam( final int cam )
        {
            real = real.with( camParameter, cam );
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
            real = real.with( alarmParameter, alarm );
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
            real = real.with( julianTimeParameter, julianTime );
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
            real = real.with( offsetParameter, offset );
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
            real = real.with( fileParameter, file );
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
            real = real.with( onDiskParameter, onDisk );
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
            real = real.with( durationParameter, duration );
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
            real = real.with( preAlarmParameter, preAlarm );
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
            real = real.with( archiveParameter, archive );
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
            real = real.with( statusParameter, status );
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
            real = real.with( alarmTypeParameter, alarmType );
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
            return new EventsCGIResult( real );
        }
    }

    /**
     * The system camera number.
     * 
     * @return the system camera number.
     */
    public int getCam()
    {
        return builder.get( camParameter ).get();
    }

    /**
     * The alarm description.
     * 
     * @return the alarm description.
     */
    public String getAlarm()
    {
        return builder.get( alarmParameter ).get();
    }

    /**
     * The time in seconds since 1970 that the event occurred at.
     * 
     * @return the time in seconds.
     */
    public int getJulianTime()
    {
        return builder.get( julianTimeParameter ).get();
    }

    /**
     * The timezone offset from UTC, in seconds. E.g., BST is 3600.
     * 
     * @return the timezone offset from UTC.
     */
    public int getOffset()
    {
        return builder.get( offsetParameter ).get();
    }

    /**
     * Gets the name of the video file that the result was found in.
     * 
     * @return the name of the video file that the result was found in.
     */
    public String getFile()
    {
        return builder.get( fileParameter ).get();
    }

    /**
     * Reports whether the video for the event is available on disk.
     * 
     * @return whether the video for the event is available on disk.
     */
    public boolean isOnDisk()
    {
        return builder.get( onDiskParameter ).get();
    }

    /**
     * The duration of the event in seconds. The duration plus the julian time
     * will never be larger than Integer.MAX_VALUE (or negative).
     * 
     * @return the duration of the event in seconds.
     */
    public int getDuration()
    {
        return builder.get( durationParameter ).get();
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
        return builder.get( preAlarmParameter ).get();
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
        return builder.get( archiveParameter ).get();
    }

    /**
     * Gives the type of alarm that this event is.
     * 
     * @return the type of alarm that this event is.
     */
    public AlarmType getAlarmType()
    {
        return builder.get( alarmTypeParameter );
    }

    /**
     * Gives the status of the database record - i.e., how complete it is on
     * disk.
     * 
     * @return the status of the database record.
     */
    public Status getStatus()
    {
        return builder.get( statusParameter );
    }

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
                compulsoryParams );
        if ( values.length > 11 )
        {
            params.add( statusParameter );
        }
        if ( values.length > 12 )
        {
            params.add( alarmTypeParameter );
        }

        return new EventsCGIResult( GenericBuilder.fromStrings( params,
                Lists.removeIndices( Arrays.asList( values ), 0, 7 ) ) );
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
}
