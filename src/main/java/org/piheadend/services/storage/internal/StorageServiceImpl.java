package org.piheadend.services.storage.internal;

import com.google.gson.Gson;
import org.piheadend.services.recording.RecordingInfo;
import org.piheadend.services.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * {@inheritDoc}
 */
@Service
public class StorageServiceImpl implements StorageService {

    private final Gson gson = new Gson();
    private final String path;

    @Autowired
    public StorageServiceImpl(final Environment environment) {
        Assert.notNull(environment);
        this.path = environment.getRequiredProperty("storage.path");
    }

    @Override
    public void saveToDisk(final List<RecordingInfo> recordingInfoList) throws IOException {
        try (final FileWriter fileWriter = new FileWriter(path)) {
            gson.toJson(recordingInfoList, fileWriter);
        }
    }
}
