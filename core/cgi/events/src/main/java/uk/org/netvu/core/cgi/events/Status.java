package uk.org.netvu.core.cgi.events;

import java.util.Random;

import uk.org.netvu.core.cgi.common.Conversion;

/**
 * An enum consisting of various states a database record can be in.
 */

// The information for this class comes from an email from Chris Blood - the
// first part pasted here, the second in AlarmType:
// "Additional fields are database record status & alarm type - try
// 192.168.113.100 again.
// Status:-
// #define RECORD_UNINITIALISED (0) /* blank field,never used */
// #define RECORD_PENDING (1<<0) /* handle allocated & time stamped */
// #define RECORD_NEW (1<<1) /* initial data written */
// #define RECORD_CLOSED (1<<2) /* all data written/updated */
// #define RECORD_ARCHIVED (1<<3) /* VID/IDX for this record archived to FTP
// server */"
public enum Status
{
    /**
     * The status was not included in the data.
     */
    NONE( 0 ),
    /**
     * A handle for writing the record has been allocated, and a timestamp for
     * it set.
     */
    PENDING( 1 ),
    /**
     * The initial data in the record has been written, but there may be more
     * data to write.
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
     * Finds the Status whose numeric value is the same as the value parameter.
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

        throw new IllegalArgumentException( String.valueOf( value ) );
    }

    static Status oneOf( final Random random )
    {
        return Status.values()[random.nextInt( Status.values().length )];
    }

    static final Conversion<String, Status> fromString = new Conversion<String, Status>()
    {

        @Override
        public Status convert( final String t )
        {
            return Status.valueOf( t.toUpperCase() );
        }
    };
}
