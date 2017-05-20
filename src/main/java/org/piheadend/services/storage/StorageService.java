package org.piheadend.services.storage;

import org.piheadend.services.recording.RecordingInfo;

import java.io.IOException;
import java.util.List;

/**
 * Service to store data on the physical disc.
 *
 * @author aw
 * @since 20.05.17.
 */
public interface StorageService {
    void saveToDisk(final List<RecordingInfo> recordingInfo) throws IOException;
}
