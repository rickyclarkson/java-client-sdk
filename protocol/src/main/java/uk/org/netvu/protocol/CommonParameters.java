package uk.org.netvu.protocol;

/**
 * A utility class containing ParameterDescriptions that are common between
 * multiple CGIs.
 */
class CommonParameters
{
    /**
     * The specification of the camera parameter.
     */
    static final ParameterDescription<Integer, Integer> CAMERA =
            ParameterDescription.parameter( "cam", StringConversion.integer() ).withDefault( 1 )
                .positive( Num.integer );
    /**
     * The specification of the fieldCount parameter.
     */
    static final ParameterDescription<Integer, Integer> FIELD_COUNT =
            ParameterDescription.parameter( "fields", StringConversion.integer() ).withDefault( 1 ).positive(
                    Num.integer );
    /**
     * The specification of the resolution parameter.
     */
    static final ParameterDescription<String, String> RESOLUTION =
            ParameterDescription.parameter( "res", StringConversion.string() ).withDefault( "med" ).allowedValues(
                    "hi", "med", "lo" );
    /**
     * The specification of the cameraSequenceMask parameter.
     */
    static final ParameterDescription<String, String> CAMERA_SEQUENCE_MASK =
            ParameterDescription.parameter( "seq", StringConversion.onlyHexDigits() ).withDefault( "0" );
    /**
     * The specification of the connectionID parameter.
     */
    static final ParameterDescription<Integer, Integer> CONNECTION_ID =
            ParameterDescription.parameter( "id", StringConversion.integer() ).withDefault( 0 );
    /**
     * The specification of the maximumTransmitRate parameter.
     */
    static final ParameterDescription<Integer, Integer> MAXIMUM_TRANSMIT_RATE =
            ParameterDescription.parameter( "rate", StringConversion.integer() ).withDefault( 0 );
    /**
     * The specification of the duration parameter.
     */
    static final ParameterDescription<Integer, Integer> DURATION =
            ParameterDescription.parameter( "duration", StringConversion.integer() ).withDefault( 0 ).notNegative(
                    Num.integer );
    /**
     * The specification of the bufferCount parameter.
     */
    static final ParameterDescription<Integer, Integer> BUFFER_COUNT =
            ParameterDescription.parameter( "nbuffers", StringConversion.integer() ).withDefault( 0 ).notNegative(
                    Num.integer );
    /**
     * The specification of the packetSize parameter.
     */
    static final ParameterDescription<Integer, Integer> PACKET_SIZE =
            ParameterDescription.parameterWithBoundsAndAnException( 100, 1500, 0, ParameterDescription.parameter(
                    "pkt_size", StringConversion.integer() ).withDefault( 0 ) );
    /**
     * The specification of the udpPort parameter.
     */
    static final ParameterDescription<Integer, Integer> UDP_PORT =
            ParameterDescription.parameter( "udp_port", StringConversion.integer() ).withDefault( 0 ).withBounds( 0,
                    65535, Num.integer );
    /**
     * The specification of the format parameter.
     */
    static final ParameterDescription<VideoFormat, VideoFormat> FORMAT =
            ParameterDescription.parameter( "format",
                    StringConversion.convenientPartial( VideoFormat.fromStringFunction() ) ).withDefault(
                    VideoFormat.JFIF );
    /**
     * The specification of the audioChannel parameter.
     */
    static final ParameterDescription<Integer, Integer> AUDIO_CHANNEL =
            ParameterDescription.parameter( "audio", StringConversion.integer() ).withDefault( 0 ).positive(
                    Num.integer );
    /**
     * The specification of the transmissionMode parameter.
     */
    static final ParameterDescription<TransmissionMode, TransmissionMode> TRANSMISSION_MODE =
            ParameterDescription.parameterWithDefault( "txmode", new Function<ParameterMap, TransmissionMode>()
            {
                @Override
                public TransmissionMode apply( final ParameterMap map )
                {
                    return map.get( FORMAT ) == VideoFormat.JFIF ? TransmissionMode.MIME : TransmissionMode.MINIMAL;
                }
            }, StringConversion.convenientPartial( TransmissionMode.fromStringFunction() ) );
    /**
     * The specification of the slaveIP parameter.
     */
    static final ParameterDescription<IPAddress, IPAddress> SLAVE_IP =
            ParameterDescription.parameter( "slaveip", StringConversion.convenientPartial( IPAddress.fromString ) )
                .withDefault( IPAddress.fromString( "0.0.0.0" ).get() );
    /**
     * The specification of the outputChannel parameter.
     */
    static final ParameterDescription<Integer, Integer> OUTPUT_CHANNEL =
            ParameterDescription.parameter( "opchan", StringConversion.integer() ).withDefault( -1 );
    /**
     * The specification of the proxyMode parameter.
     */
    static final ParameterDescription<ProxyMode, ProxyMode> PROXY_MODE =
            ParameterDescription.parameter( "proxymode",
                    StringConversion.convenientPartial( ProxyMode.fromStringFunction() ) ).withDefault(
                    ProxyMode.TRANSIENT );
    /**
     * The specification of the proxyRetries parameter.
     */
    static final ParameterDescription<Integer, Integer> PROXY_RETRIES =
            ParameterDescription.parameter( "proxyretry", StringConversion.integer() ).withDefault( 0 );
}
