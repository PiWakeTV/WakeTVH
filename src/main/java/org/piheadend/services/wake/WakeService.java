package org.piheadend.services.wake;

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
}
