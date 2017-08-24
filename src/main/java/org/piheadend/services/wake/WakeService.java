package org.piheadend.services.wake;

import com.jamierf.wol.WakeOnLan;
import org.apache.commons.codec.DecoderException;

import java.io.IOException;

/**
 * Service for waking up the TVH server via Wake-on-LAN.
 *
 * @author aw
 * @since 11.08.2017
 */
public interface WakeService {

    /**
     * Checks wheter a wake-up of the TVHeadend server is required - if so, a magic packet will be sent.
     */
    void checkWake();

    /**
     * Sends a magic packet (Wake-on-LAN) to the TVH server.
     */
    void wake();
}
