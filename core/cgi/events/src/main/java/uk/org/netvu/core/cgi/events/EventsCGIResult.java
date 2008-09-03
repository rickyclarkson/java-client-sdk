package uk.org.netvu.core.cgi.events;

import static java.lang.Integer.parseInt;

import java.util.ArrayList;

import uk.org.netvu.core.cgi.common.Conversion;
import uk.org.netvu.core.cgi.common.GenericBuilder;
import uk.org.netvu.core.cgi.common.Option;
import uk.org.netvu.core.cgi.common.Parameter;
import uk.org.netvu.core.cgi.common.Strings;

/**
 * A single result from the events database.
 */
public class EventsCGIResult
{
    private static final Parameter<Integer, Option<Integer>> camParameter = Parameter.bound(
            0, 64, Parameter.param( "cam", "The system camera number",
                    Conversion.stringToInt ) );

    private static final Parameter<String, Option<String>> alarmParameter = Parameter.param(
            "alarm", "The alarm description.", Conversion.<String> identity() );

    private static final Parameter<Integer, Option<Integer>> julianTimeParameter = Parameter.notNegative( Parameter.param(
            "time", "The Julianised time that the event occurred at",
            Conversion.stringToInt ) );

    private static final Parameter<Integer, Option<Integer>> offsetParameter = Parameter.bound(
            -90000, 90000, Parameter.param( "offset", "",
                    Conversion.stringToInt ) );

    private static final Parameter<String, Option<String>> fileParameter = Parameter.param(
            "file", "", Conversion.<String> identity() );

    private static final Parameter<Boolean, Option<Boolean>> onDiskParameter = Parameter.param(
            "onDisk", "", Conversion.stringToBoolean );

    private static final Parameter<Integer, Option<Integer>> durationParameter = Parameter.notNegative( Parameter.param(
            "duration", "", Conversion.stringToInt ) );

    private static final Parameter<Integer, Option<Integer>> preAlarmParameter = Parameter.notNegative( Parameter.param(
            "preAlarm", "", Conversion.stringToInt ) );

    private static final Parameter<Integer, Option<Integer>> archiveParameter = Parameter.notNegative( Parameter.param(
            "archive", "", Conversion.stringToInt ) );
    private static final Parameter<Status, Status> statusParameter = Parameter.param(
            "status", "", Status.NONE, Status.fromString );
    private static final Parameter<AlarmType, AlarmType> alarmTypeParameter = Parameter.param(
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
    private EventsCGIResult( final Builder builder )
    {
        this.builder = builder.real;
    }

    /**
     * A builder that takes in all the information needed to construct an
     * EventsCGIResult and constructs it.
     */
    static class Builder
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

        public Builder offset( final int offset )
        {
            real = real.with( offsetParameter, offset );
            return this;
        }

        public Builder file( final String file )
        {
            real = real.with( fileParameter, file );
            return this;
        }

        public Builder onDisk( final boolean onDisk )
        {
            real = real.with( onDiskParameter, onDisk );
            return this;
        }

        /**
         * Adds the duration of the event to the builder.
         * 
         * @return the builder.
         */
        public Builder duration( final int duration )
        {
            real = real.with( durationParameter, duration );
            return this;
        }

        public Builder preAlarm( final int preAlarm )
        {
            real = real.with( preAlarmParameter, preAlarm );
            return this;
        }

        public Builder archive( final int archive )
        {
            real = real.with( archiveParameter, archive );
            return this;
        }

        public Builder status( final Status status )
        {
            real = real.with( statusParameter, status );
            return this;
        }

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
            for ( final Parameter<?, ? extends Option<?>> param : compulsoryParams )
            {

                if ( real.get( param ).isNone() )
                {
                    throw new IllegalStateException( "The parameter " + param
                            + " has not been given a value" );
                }
            }

            return new EventsCGIResult( this );
        }
    }

    /**
     * The system camera number.
     */
    public int getCam()
    {
        return builder.get( camParameter ).get();
    }

    /**
     * The alarm description.
     */
    public String getAlarm()
    {
        return builder.get( alarmParameter ).get();
    }

    /**
     * The time in seconds since Jan 1st, 1970 00:00 that the event occurred at.
     */
    public int getJulianTime()
    {
        return builder.get( julianTimeParameter ).get();
    }

    /**
     * The timezone offset from UTC, in seconds. E.g., BST is 3600.
     */
    public int getOffset()
    {
        return builder.get( offsetParameter ).get();
    }

    /**
     * Gets the video file that the result was found in.
     */
    public String getFile()
    {
        return builder.get( fileParameter ).get();
    }

    /**
     * Reports whether the video for the event is available on disk.
     */
    public boolean isOnDisk()
    {
        return builder.get( onDiskParameter ).get();
    }

    /**
     * The duration of the event in seconds. The duration plus the julian time
     * will never be larger than Integer.MAX_VALUE (or negative).
     */
    public int getDuration()
    {
        return builder.get( durationParameter ).get();
    }

    /**
     * The number of seconds before the alarm time that are related to the
     * event.
     */
    public int getPreAlarm()
    {
        return builder.get( preAlarmParameter ).get();
    }

    /**
     * The number of seconds since 1970 after which the video for this event may
     * be overwritten.
     */
    public int getArchive()
    {
        return builder.get( archiveParameter ).get();
    }

    /**
     * Gives the type of alarm that this event is.
     */
    public AlarmType getAlarmType()
    {
        return builder.get( alarmTypeParameter );
    }

    /**
     * Gives the status of the database record - i.e., how complete it is on
     * disk.
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

        final Builder builder = new EventsCGIResult.Builder().cam( Integer.parseInt( values[1] ) );
        builder.alarm( values[2] );
        builder.julianTime( Integer.parseInt( values[3] ) );
        builder.offset( Integer.parseInt( values[4] ) ).file( values[5] );
        builder.onDisk( values[6].equals( "exists" ) );
        builder.duration( parseInt( values[8] ) );
        builder.preAlarm( parseInt( values[9] ) );
        builder.archive( parseInt( values[10] ) );

        if ( values.length > 11 )
        {
            builder.status( Status.find( parseInt( values[11] ) ) );
        }

        if ( values.length > 12 )
        {
            builder.alarmType( AlarmType.find( parseInt( values[12] ) ) );
        }

        return builder.build();
    }

    String toCSV( final int index )
    {
        return index
                + ", "
                + getCam()
                + ", "
                + getAlarm()
                + ", "
                + getJulianTime()
                + ", "
                + getOffset()
                + ", "
                + getFile()
                + ", "
                + ( isOnDisk() ? "exists" : "overwitten" ) // overwitten is
                // taken
                // from server output
                + ", "
                + index
                + ", "
                + getDuration()
                + ", "
                + getPreAlarm()
                + ", "
                + getArchive()
                + ( getAlarmType() == AlarmType.NONE
                        && getStatus() == Status.NONE ? "" : ", "
                        + getStatus().value ) + ", " + getAlarmType().value;
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
