package org.piheadend.services.wake;

/**
 * Service for waking up the TVH server via Wake-on-LAN.
 *
 * @author aw
 * @since 11.08.2017
 */
public interface WakeService {

    /**
     * Sends a magic packet (Wake-on-LAN) to the TVH server.
     *
     * @throws Exception Occurs on transmission failures.
     */
    void wake() throws Exception;
}
