package uk.org.netvu.core.cgi.events;

import java.util.Random;

import uk.org.netvu.core.cgi.common.Conversion;

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

    static final Conversion<String, AlarmType> fromString = new Conversion<String, AlarmType>()
    {
        @Override
        public AlarmType convert( final String t )
        {
            return AlarmType.valueOf( t.toUpperCase() );
        }
    };
}
