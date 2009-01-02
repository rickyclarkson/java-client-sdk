package uk.org.netvu.protocol;

import static uk.org.netvu.protocol.ParameterDescription.nonNegativeParameter;
import static uk.org.netvu.protocol.ParameterDescription.parameter;
import static uk.org.netvu.protocol.ParameterDescription.parameterWithBounds;
import static uk.org.netvu.protocol.ParameterDescription.parameterWithBoundsAndAnException;
import static uk.org.netvu.protocol.ParameterDescription.parameterWithDefault;
import static uk.org.netvu.protocol.StringConversion.convenientPartial;
import static uk.org.netvu.protocol.StringConversion.hexInt;
import static uk.org.netvu.protocol.StringConversion.integer;
import static uk.org.netvu.protocol.StringConversion.string;

import java.util.ArrayList;
import java.util.List;

import uk.org.netvu.util.CheckParameters;

public final class DisplayPicCGI
{
    private final ParameterMap parameterMap;

    private static final List<ParameterDescription<?, ?>> params = new ArrayList<ParameterDescription<?, ?>>();

    private static final ParameterDescription<Integer, Integer> CAM =
            parameter( "cam", integer() ).withDefault( 1 ).withBounds( 1, 16, Num.integer );

    private static final ParameterDescription<Integer, Integer> FIELDS =
            parameter( "fields", integer() ).withDefault( 1 ).positive( Num.integer );
    private static final ParameterDescription<String, String> RES =
            parameter( "res", string() ).withDefault( "med" ).allowedValues( "hi", "med", "lo" );
    private static final ParameterDescription<Integer, Integer> SEQ =
            parameter( "seq", hexInt() ).withDefault( 0 ).withBounds( 0, 0xF, Num.integer );
    private static final ParameterDescription<Integer, Integer> DWELL =
            parameter( "dwell", integer() ).withDefault( 0 );
    private static final ParameterDescription<Integer, Integer> ID = parameter( "id", integer() ).withDefault( 0 );
    private static final ParameterDescription<Integer, Integer> DINDEX =
            parameter( "dindex", integer() ).withDefault( 0 );
    private static final ParameterDescription<Integer, Integer> PRESEL =
            parameter( "presel", integer() ).withDefault( 0 ).withBounds( 0, 3, Num.integer );
    private static final ParameterDescription<Integer, Integer> CHANNEL =
            parameter( "channel", integer() ).withDefault( -1 ).withBounds( -1, 1, Num.integer );
    private static final ParameterDescription<Integer, Integer> RATE = parameter( "rate", integer() ).withDefault( 0 );
    private static final ParameterDescription<Integer, Integer> FORCED_Q =
            parameter( "forcedq", integer() ).withDefault( 0 ).withBounds( 0, 255, Num.integer ).disallowing( 1 );
    private static final ParameterDescription<Integer, Integer> DURATION =
            nonNegativeParameter( parameter( "duration", integer() ).withDefault( 0 ) );
    private static final ParameterDescription<Integer, Integer> N_BUFFERS =
            nonNegativeParameter( parameter( "nbuffers", integer() ).withDefault( 0 ) );
    private static final ParameterDescription<Integer, Integer> TELEM_Q =
            nonNegativeParameter( parameter( "telemq", integer() ).withDefault( -1 ) );
    private static final ParameterDescription<Integer, Integer> PKT_SIZE =
            parameterWithBoundsAndAnException( 100, 1500, 0, parameterWithDefault( "pkt_size", 0, integer() ) );
    private static final ParameterDescription<Integer, Integer> UDP_PORT =
            parameterWithBounds( 0, 65535, parameterWithDefault( "udp_port", 0, integer() ) );
    private static final ParameterDescription<String, String> AUDIO =
            parameter( "audio", string() ).withDefault( "0" ).allowedValues( "on", "off", "0", "1", "2" );
    private static final ParameterDescription<Format, Format> FORMAT =
            parameterWithDefault( "format", Format.JFIF, convenientPartial( Format.fromStringFunction() ) );
    private static final ParameterDescription<AudioMode, AudioMode> AUD_MODE =
            parameterWithDefault( "aud_mode", AudioMode.UDP, convenientPartial( AudioMode.fromStringFunction() ) );
    private static final ParameterDescription<TransmissionMode, TransmissionMode> TX_MODE =
            parameterWithDefault( "txmode", new Function<ParameterMap, TransmissionMode>()
            {
                @Override
                public TransmissionMode apply( final ParameterMap map )
                {
                    return map.get( FORMAT ) == Format.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL;
                }
            }, convenientPartial( TransmissionMode.fromStringFunction() ) );
    private static final ParameterDescription<Integer, Integer> PPS = parameterWithDefault( "pps", 0, integer() );
    private static final ParameterDescription<Integer, Integer> MP4_RATE =
            parameterWithDefault( "mp4rate", 0, integer() );
    private static final ParameterDescription<IPAddress, IPAddress> SLAVE_IP =
            parameterWithDefault( "slaveip", IPAddress.fromString( "0.0.0.0" ).get(),
                    convenientPartial( IPAddress.fromString ) );
    private static final ParameterDescription<Integer, Integer> OP_CHAN =
            parameterWithDefault( "opchan", -1, integer() );
    private static final ParameterDescription<ProxyMode, ProxyMode> PROXY_MODE =
            parameterWithDefault( "proxymode", ProxyMode.TRANSIENT, convenientPartial( ProxyMode.fromStringFunction() ) );
    private static final ParameterDescription<Integer, Integer> PROXY_PRI =
            parameterWithDefault( "proxypri", 1, integer() );
    private static final ParameterDescription<Integer, Integer> PROXY_RETRY =
            parameterWithDefault( "proxyretry", 0, integer() );
    static
    {
        params.add( CAM );
    }
    static
    {
        params.add( FIELDS );
    }
    static
    {
        params.add( RES );
    }
    static
    {
        params.add( SEQ );
    }
    static
    {
        params.add( DWELL );
    }
    static
    {
        params.add( ID );
    }
    static
    {
        params.add( DINDEX );
    }
    static
    {
        params.add( PRESEL );
    }
    static
    {
        params.add( CHANNEL );
    }
    static
    {
        params.add( RATE );
    }
    static
    {
        params.add( FORCED_Q );
    }
    static
    {
        params.add( DURATION );
    }
    static
    {
        params.add( N_BUFFERS );
    }
    static
    {
        params.add( TELEM_Q );
    }
    static
    {
        params.add( PKT_SIZE );
    }
    static
    {
        params.add( UDP_PORT );
    }
    static
    {
        params.add( AUDIO );
    }
    static
    {
        params.add( FORMAT );
    }
    static
    {
        params.add( AUD_MODE );
    }
    static
    {
        params.add( TX_MODE );
    }
    static
    {
        params.add( PPS );
    }
    static
    {
        params.add( MP4_RATE );
    }
    static
    {
        params.add( SLAVE_IP );
    }
    static
    {
        params.add( OP_CHAN );
    }
    static
    {
        params.add( PROXY_MODE );
    }
    static
    {
        params.add( PROXY_PRI );
    }
    static
    {
        params.add( PROXY_RETRY );
    }

    public static DisplayPicCGI fromString( final String string )
    {
        CheckParameters.areNotNull( string );
        if ( string.length() == 0 )
        {
            throw new IllegalArgumentException( "Cannot parse an empty String into a DisplayPicCGI." );
        }
        final Option<ParameterMap> map = ParameterMap.fromURL( string, params );
        if ( map.isEmpty() )
        {
            throw new IllegalArgumentException( map.reason() );
        }
        return new DisplayPicCGI( map.get() );
    }

    DisplayPicCGI( final ParameterMap parameterMap )
    {
        this.parameterMap = parameterMap;
    }

    public String getAudio()
    {
        return parameterMap.get( AUDIO );
    }

    public AudioMode getAudMode()
    {
        return parameterMap.get( AUD_MODE );
    }

    public int getCam()
    {
        return parameterMap.get( CAM );
    }

    public int getChannel()
    {
        return parameterMap.get( CHANNEL );
    }

    public int getDIndex()
    {
        return parameterMap.get( DINDEX );
    }

    public int getDuration()
    {
        return parameterMap.get( DURATION );
    }

    public int getDwell()
    {
        return parameterMap.get( DWELL );
    }

    public int getFields()
    {
        return parameterMap.get( FIELDS );
    }

    public int getForcedQ()
    {
        return parameterMap.get( FORCED_Q );
    }

    public Format getFormat()
    {
        return parameterMap.get( FORMAT );
    }

    public int getId()
    {
        return parameterMap.get( ID );
    }

    public int getMp4Rate()
    {
        return parameterMap.get( MP4_RATE );
    }

    public int getNBuffers()
    {
        return parameterMap.get( N_BUFFERS );
    }

    public int getOpChan()
    {
        return parameterMap.get( OP_CHAN );
    }

    public int getPktSize()
    {
        return parameterMap.get( PKT_SIZE );
    }

    public int getPPS()
    {
        return parameterMap.get( PPS );
    }

    public int getPresel()
    {
        return parameterMap.get( PRESEL );
    }

    public ProxyMode getProxyMode()
    {
        return parameterMap.get( PROXY_MODE );
    }

    public int getProxyPri()
    {
        return parameterMap.get( PROXY_PRI );
    }

    public int getProxyRetry()
    {
        return parameterMap.get( PROXY_RETRY );
    }

    public int getRate()
    {
        return parameterMap.get( RATE );
    }

    public String getRes()
    {
        return parameterMap.get( RES );
    }

    public int getSeq()
    {
        return parameterMap.get( SEQ );
    }

    public IPAddress getSlaveIP()
    {
        return parameterMap.get( SLAVE_IP );
    }

    public int getTelemQ()
    {
        return parameterMap.get( TELEM_Q );
    }

    public TransmissionMode getTxMode()
    {
        return parameterMap.get( TX_MODE );
    }

    public int getUdpPort()
    {
        return parameterMap.get( UDP_PORT );
    }

    @Override
    public String toString()
    {
        return "/display_pic.cgi?" + parameterMap.toURLParameters( params );
    }

    public static enum AudioMode
    {
        UDP, INLINE;

        public static Function<String, Option<AudioMode>> fromStringFunction()
        {
            return new Function<String, Option<AudioMode>>()
            {
                @Override
                public Option<AudioMode> apply( final String s )
                {
                    for ( final AudioMode element : values() )
                    {
                        if ( element.toString().equals( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid AudioMode element" );
                }
            };
        }
    }

    public static final class Builder
    {
        private Option<ParameterMap> parameterMap = Option.getFullOption( new ParameterMap() );

        public Builder audio( final String audio )
        {
            return set( AUDIO, audio );
        }

        public Builder audioMode( final AudioMode audioMode )
        {
            return set( AUD_MODE, audioMode );
        }

        public DisplayPicCGI build()
        {
            try
            {
                return new DisplayPicCGI( parameterMap.get() );
            }
            finally
            {
                parameterMap = Option.getEmptyOption( "This Builder has already been built once." );
            }
        }

        public Builder cam( final int cam )
        {
            return set( CAM, cam );
        }

        public Builder channel( final int channel )
        {
            return set( CHANNEL, channel );
        }

        public Builder dIndex( final int dIndex )
        {
            return set( DINDEX, dIndex );
        }

        public Builder duration( final int duration )
        {
            return set( DURATION, duration );
        }

        public Builder dwell( final int dwell )
        {
            return set( DWELL, dwell );
        }

        public Builder fields( final int fields )
        {
            return set( FIELDS, fields );
        }

        public Builder forcedQ( final int forcedQ )
        {
            return set( FORCED_Q, forcedQ );
        }

        public Builder format( final Format format )
        {
            return set( FORMAT, format );
        }

        public Builder id( final int id )
        {
            return set( ID, id );
        }

        public Builder mp4Rate( final int mp4Rate )
        {
            return set( MP4_RATE, mp4Rate );
        }

        public Builder nBuffers( final int nBuffers )
        {
            return set( N_BUFFERS, nBuffers );
        }

        public Builder opChan( final int opChan )
        {
            return set( OP_CHAN, opChan );
        }

        public Builder pktSize( final int pktSize )
        {
            return set( PKT_SIZE, pktSize );
        }

        public Builder pps( final int pps )
        {
            return set( PPS, pps );
        }

        public Builder presel( final int presel )
        {
            return set( PRESEL, presel );
        }

        public Builder proxyMode( final ProxyMode proxyMode )
        {
            return set( PROXY_MODE, proxyMode );
        }

        public Builder proxyPri( final int proxyPri )
        {
            return set( PROXY_PRI, proxyPri );
        }

        public Builder proxyRetry( final int proxyRetry )
        {
            return set( PROXY_RETRY, proxyRetry );
        }

        public Builder rate( final int rate )
        {
            return set( RATE, rate );
        }

        public Builder res( final String res )
        {
            return set( RES, res );
        }

        public Builder seq( final int seq )
        {
            return set( SEQ, seq );
        }

        public Builder slaveIP( final IPAddress slaveIP )
        {
            return set( SLAVE_IP, slaveIP );
        }

        public Builder telemQ( final int telemQ )
        {
            return set( TELEM_Q, telemQ );
        }

        public Builder txMode( final TransmissionMode txMode )
        {
            return set( TX_MODE, txMode );
        }

        public Builder udpPort( final int udpPort )
        {
            return set( UDP_PORT, udpPort );
        }

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

    public static enum Format
    {
        JFIF, JPEG, MP4;

        public static Function<String, Option<Format>> fromStringFunction()
        {
            return new Function<String, Option<Format>>()
            {
                @Override
                public Option<Format> apply( final String s )
                {
                    for ( final Format element : values() )
                    {
                        if ( element.toString().equals( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid Format element" );
                }
            };
        }
    }

    public static enum ProxyMode
    {
        TRANSIENT, PERSISTENT;

        public static Function<String, Option<ProxyMode>> fromStringFunction()
        {
            return new Function<String, Option<ProxyMode>>()
            {
                @Override
                public Option<ProxyMode> apply( final String s )
                {
                    for ( final ProxyMode element : values() )
                    {
                        if ( element.toString().equals( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid ProxyMode element" );
                }
            };
        }
    }

    public static enum TransmissionMode
    {
        MIME, BINARY, MINIMAL;

        public static Function<String, Option<TransmissionMode>> fromStringFunction()
        {
            return new Function<String, Option<TransmissionMode>>()
            {
                @Override
                public Option<TransmissionMode> apply( final String s )
                {
                    for ( final TransmissionMode element : values() )
                    {
                        if ( element.toString().equals( s ) )
                        {
                            return Option.getFullOption( element );
                        }
                    }
                    return Option.getEmptyOption( s + " is not a valid TransmissionMode element" );
                }
            };
        }
    }
}
