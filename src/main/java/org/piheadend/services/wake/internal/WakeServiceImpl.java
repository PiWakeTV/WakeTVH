package org.piheadend.services.wake.internal;

import com.jamierf.wol.WakeOnLan;
import org.apache.commons.codec.DecoderException;
import org.piheadend.services.wake.WakeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * {@inheritDoc}
 *
 * @author aw
 * @since 11.08.2017
 */
@Service
class WakeServiceImpl implements WakeService {

    private static final Logger log = LoggerFactory.getLogger(WakeServiceImpl.class);

    private final String mac;

    @Autowired
    public WakeServiceImpl(@Value("${tvheadend.mac}") final String mac) {
        this.mac = mac;
    }

    @Override
    public void wake() {
        try {
            WakeOnLan.wake(mac);
        } catch (IOException | DecoderException e) {
            log.error("Wake-on-LAN of TVH server failed.", e);
        }
    }
}
