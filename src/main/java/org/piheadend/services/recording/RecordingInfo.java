package org.piheadend.services.recording;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Holds information about a scheduled recording in TVHeadend.
 *
 * @author aw
 * @since 25.02.2017
 */
public class RecordingInfo implements Serializable {
    private static final long serialVersionUID = 5806398579627001463L;

    private final String title;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public RecordingInfo(final String title, final LocalDateTime start, final LocalDateTime end) {
        this.title = title;
        this.start = start;
        this.end = end;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
