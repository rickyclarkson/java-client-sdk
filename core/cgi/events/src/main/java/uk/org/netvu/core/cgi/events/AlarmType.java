package uk.org.netvu.core.cgi.events;

import java.util.Random;

// See the comment in Status.java:

// Alarm type:-
// #define EVENT_TYPE_ZONE (1<<0) /* alarm zone (contact and/or VMD ) */
// #define EVENT_TYPE_VMD (1<<1) /* VMD event */
// #define EVENT_TYPE_GPS (1<<2) /* GPS event */
// #define EVENT_TYPE_SYSTEM (1<<3) /* system event eg startup, set/unset */
// #define EVENT_TYPE_CAMERA (1<<4) /* dummy - used for CGI parameters, never
// appears in a database record */
// #define EVENT_TYPE_ACTIVITY (1<<5) /* used for activity search, never appears
// in a database record */
// #define EVENT_TYPE_KEYWORD (1<<6) /* used for text-in-image keyword search,
// never appears in a database record */

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
     * A dummy alarm type used for CGI parameters - never appears in a database
     * record.
     */
    // TODO decide whether to make this NONE
    CAMERA( 16 ),

    /**
     * Used for activity search, never appears in a database record.
     */
    // TODO decide whether to make this NONE
    ACTIVITY( 32 ),

    /**
     * Used for text-in-image keyword search, never appears in a database
     * record.
     */
    // TODO decide whether to make this NONE.
    KEYWORD( 64 );

    /**
     * The numeric value associated with this state.
     */
    public final int value;

    AlarmType( final int value )
    {
        this.value = value;
    }

    /**
     * Finds the AlarmType whose numeric value is the same as the value
     * parameter.
     * 
     * @throws IllegalArgumentException
     *         if the value parameter does not match any AlarmType.
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

        throw new IllegalArgumentException( value
                + " is not a valid alarm code" );
    }

    static AlarmType oneOf( final Random random )
    {
        return AlarmType.values()[random.nextInt( AlarmType.values().length )];
    }
}
