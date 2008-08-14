package uk.org.netvu.core.cgi.events;

import static java.lang.Integer.parseInt;
import static uk.org.netvu.core.cgi.events.Checks.checks;
import static uk.org.netvu.core.cgi.events.Checks.notNull;

/**
 * A single result from the events database.
 */
public class EventsCGIResult
{
    private final BoundInt cam;
    private final String alarm;
    private final UInt31 julianTime;
    private final BoundInt offset;
    private final String file;
    private final boolean onDisk;
    private final UInt31 duration;
    private final UInt31 preAlarm;
    private final UInt31 archive;
    private final Status status;
    private final AlarmType alarmType;

    /**
     * Constructs an EventsCGIResult given a Builder that contains all the
     * necessary information.
     * 
     * @param builder
     *        the Builder to base this EventsCGIResult on.
     */
    private EventsCGIResult( final Builder builder )
    {
        cam = notNull( builder.cam, "cam" );
        alarm = notNull( builder.alarm, "alarm" );
        julianTime = notNull( builder.julianTime, "julianTime" );
        offset = notNull( builder.offset, "offset" );
        file = notNull( builder.file, "file" );
        onDisk = builder.onDisk;
        duration = checks( builder.duration, "duration" ).notNull().notGreaterThan(
                new UInt31( Integer.MAX_VALUE - julianTime.toInt() ),
                "the lowest duration that would cause a numeric overflow in end time" ).done();

        preAlarm = builder.preAlarm;
        archive = builder.archive;
        status = notNull( builder.status, "status" );
        alarmType = notNull( builder.alarmType, "alarmType" );
    }

    /**
     * A builder that takes in all the information needed to construct an
     * EventsCGIResult and constructs it.
     */
    static class Builder
    {
        private BoundInt cam = new BoundInt( 0, 0, 64 );
        private String alarm;
        private UInt31 julianTime;
        private BoundInt offset = new BoundInt( 0, -90000, 90000 );
        private String file;
        private Boolean onDisk;
        private UInt31 duration;
        private UInt31 preAlarm;
        private UInt31 archive;
        private Status status = Status.NONE;
        private AlarmType alarmType = AlarmType.NONE;

        /**
         * Adds the system camera number to the builder.
         * 
         * @param cam
         *        the system camera number.
         * @return the builder.
         */
        public Builder cam( final int cam )
        {
            this.cam = this.cam.newValue( cam );
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
            this.alarm = notNull( alarm, "alarm" );
            return this;
        }

        /**
         * Adds the time of the event to the builder.
         * 
         * @param julianTime
         *        the time of the event.
         * @return the builder.
         */
        public Builder julianTime( final UInt31 julianTime )
        {
            this.julianTime = notNull( julianTime, "julianTime" );
            return this;
        }

        public Builder offset( final int offset )
        {
            this.offset = this.offset.newValue( offset );
            return this;
        }

        public Builder file( final String file )
        {
            this.file = notNull( file, "file" );
            return this;
        }

        public Builder onDisk( final boolean onDisk )
        {
            this.onDisk = onDisk;
            return this;
        }

        /**
         * Adds the duration of the event to the builder.
         * 
         * @return the builder.
         */
        public Builder duration( final UInt31 duration )
        {
            this.duration = notNull( duration, "duration" );
            return this;
        }

        public Builder preAlarm( final UInt31 preAlarm )
        {
            this.preAlarm = preAlarm;
            return this;
        }

        public Builder archive( final UInt31 archive )
        {
            this.archive = archive;
            return this;
        }

        public Builder status( final Status status )
        {
            this.status = status;
            return this;
        }

        public Builder alarmType( final AlarmType alarmType )
        {
            this.alarmType = alarmType;
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
            return new EventsCGIResult( this );
        }
    }

    /**
     * The system camera number.
     */
    public int getCam()
    {
        return cam.value;
    }

    /**
     * The alarm description.
     */
    public String getAlarm()
    {
        return alarm;
    }

    /**
     * The time in seconds since Jan 1st, 1970 00:00 that the event occurred at.
     */
    public int getJulianTime()
    {
        return julianTime.toInt();
    }

    /**
     * The timezone offset from UTC, in seconds. E.g., BST is 3600.
     */
    public int getOffset()
    {
        return offset.value;
    }

    /**
     * Gets the video file that the result was found in.
     */
    public String getFile()
    {
        return file;
    }

    /**
     * Reports whether the video for the event is available on disk.
     */
    public boolean isOnDisk()
    {
        return onDisk;
    }

    /**
     * The duration of the event in seconds. The duration plus the julian time
     * will never be larger than Integer.MAX_VALUE (or negative).
     */
    public int getDuration()
    {
        return duration.toInt();
    }

    /**
     * The number of seconds before the alarm time that are related to the
     * event.
     */
    public int getPreAlarm()
    {
        return preAlarm.toInt();
    }

    /**
     * The number of seconds since 1970 after which the video for this event may
     * be overwritten.
     */
    public int getArchive()
    {
        return archive.toInt();
    }

    /**
     * Gives the type of alarm that this event is.
     */
    public AlarmType getAlarmType()
    {
        return alarmType;
    }

    /**
     * Gives the status of the database record - i.e., how complete it is on
     * disk.
     */
    public Status getStatus()
    {
        return status;
    }

    static String[] split( final String line )
    {
        return line.replaceAll( ",([^ ])", ", $1" ).split( ", " );
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
        final String[] values = split( line );

        if ( values.length < 11 || values.length > 13 )
        {
            throw new IllegalArgumentException( "The line ( " + line
                    + ") needs to contain 11 to 13 fields, not "
                    + values.length );
        }

        final Builder builder = new EventsCGIResult.Builder().cam( Integer.parseInt( values[1] ) );
        builder.alarm( values[2] );
        builder.julianTime( new UInt31( Integer.parseInt( values[3] ) ) );
        builder.offset( Integer.parseInt( values[4] ) ).file( values[5] );
        builder.onDisk( values[6].equals( "exists" ) );
        builder.duration( new UInt31( parseInt( values[8] ) ) );
        builder.preAlarm( new UInt31( parseInt( values[9] ) ) );
        builder.archive( new UInt31( parseInt( values[10] ) ) );

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
                + ( onDisk ? "exists" : "overwitten" ) // overwitten is taken
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
     * Tests this object against another for equality.
     * 
     * @return true if all the fields have equal values, false otherwise.
     */
    @Override
    public boolean equals( final Object object )
    {
        if ( object instanceof EventsCGIResult )
        {
            final EventsCGIResult other = (EventsCGIResult) object;

            final EqualsBuilder builder = new EqualsBuilder().with( getCam(),
                    other.getCam(), "cam" );
            builder.with( getAlarm(), other.getAlarm(), "alarm" );
            builder.with( getJulianTime(), other.getJulianTime(), "julian time" );
            builder.with( getOffset(), other.getOffset(), "offset" );
            builder.with( isOnDisk(), other.isOnDisk(), "is on disk" );
            builder.with( getDuration(), other.getDuration(), "duration" );
            builder.with( getPreAlarm(), other.getPreAlarm(), "pre alarm" );
            builder.with( getArchive(), other.getArchive(), "archive" );
            builder.with( getAlarmType(), other.getAlarmType(), "alarm type" );
            builder.with( getStatus(), other.getStatus(), "status" );

            return builder.equal();
        }

        return false;
    }

    /**
     * A hashcode consistent with the equals implementation.
     */
    @Override
    public int hashCode()
    {
        return 1;
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
