package org.piheadend.services.recording.internal;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.piheadend.services.recording.RecordingInfo;
import org.piheadend.services.recording.RecordingInfoService;
import org.piheadend.services.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * {@inheritDoc}
 *
 * @author aw
 * @since 25.02.2017
 */
@Service
class RecordingInfoServiceImpl implements RecordingInfoService {

    private static final Logger log = LoggerFactory.getLogger(RecordingInfoServiceImpl.class);

    private final String tvhProtocol;
    private final String tvhHost;
    private final String tvhPort;
    private final String tvhPath;
    private final String tvhUser;
    private final String tvhPassword;

    private final StorageService storageService;

    @Autowired
    public RecordingInfoServiceImpl(final Environment environment, final StorageService storageService) {
        Assert.notNull(environment);
        Assert.notNull(storageService, "StorageService must not be 'null'.");
        this.tvhProtocol = environment.getRequiredProperty("tvheadend.protocol");
        this.tvhHost = environment.getRequiredProperty("tvheadend.host");
        this.tvhPort = environment.getRequiredProperty("tvheadend.port");
        this.tvhPath = environment.getRequiredProperty("tvheadend.path");
        this.tvhUser = environment.getRequiredProperty("tvheadend.user");
        this.tvhPassword = environment.getRequiredProperty("tvheadend.password");
        this.storageService = storageService;
    }

    /**
     * Checks the list of {@link RecordingInfo} and persists them on disk.
     */
    @Override
    public void updateRecordings() {
        try {
            final List<RecordingInfo> info = gatherRecordingInfo();
            for(final RecordingInfo i : info) {
                log.info("Film: {}, Start: {}, Ende: {}", i.getTitle(), i.getStart(), i.getEnd());
            }
            storageService.saveToDisk(info);
        } catch (IOException e) {
            log.error("JSON konnte nicht geladen werden.");
        }
    }

    /**
     * Gets a list of {@link RecordingInfo} from the TVheadend server.
     *
     * @return List of {@link RecordingInfo} from the TVheadend server.
     * @throws IOException In case the List cannot be obtained from the server.
     */
    private List<RecordingInfo> gatherRecordingInfo() throws IOException {
        return parseJson();
    }

    /**
     * Parses the response from the TVheadend server.
     *
     * @return Parsed response from the TVheadend server.
     * @throws IOException In case the List cannot be obtained from the server.
     */
    private List<RecordingInfo> parseJson() throws IOException {
        final List<RecordingInfo> mapped = new ArrayList<>();
        final JSONArray entries = readJson().getJSONArray("entries");

        if(entries == null || entries.length() == 0) {
            return mapped;
        }

        for(int i = 0; i < entries.length(); i++) {
            final JSONObject entry = entries.getJSONObject(i);
            mapped.add(mapJsonToRecordingInfo(entry));
        }

        return mapped;
    }

    /**
     * Maps the JSON response to {@link RecordingInfo}.
     *
     * @param entry The {@link JSONObject} to map into a {@link RecordingInfo}.
     * @return The entry as a {@link RecordingInfo}.
     */
    private RecordingInfo mapJsonToRecordingInfo(final JSONObject entry) {
        final String title = entry.getString("disp_title");
        final long start = entry.getLong("start");
        final long ende = entry.getLong("stop");

        final Date s = new Date(start * 1000);
        final Date e = new Date(ende * 1000);

        final LocalDateTime localStart = s.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        final LocalDateTime localEnd = e.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        return new RecordingInfo(title, localStart, localEnd);
    }

    /**
     * Reads the JSON from the TVheadend API.
     *
     * @return List of all recording information as a {@link JSONObject}.
     * @throws IOException In case the List cannot be obtained from the server.
     */
    private JSONObject readJson() throws IOException {
        final String auth = this.tvhUser + ":" + this.tvhPassword;
        final HttpURLConnection connection = (HttpURLConnection) buildURL().openConnection();
        final String encoded = Base64.getEncoder().encodeToString((auth).getBytes(StandardCharsets.UTF_8));
        connection.setRequestProperty("Authorization", "Basic "+encoded);

        try (InputStream is = connection.getInputStream()) {
            final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            final String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    /**
     * Builds the URL to the TVheadend server from the application properties.
     *
     * @return The URL to the TVheadend server.
     */
    private URL buildURL() {
        URL url = null;

        try {
            url = new URIBuilder()
                    .setScheme(this.tvhProtocol)
                    .setHost(this.tvhHost)
                    .setPort(Integer.valueOf(this.tvhPort))
                    .setPath(this.tvhPath)
                    .build().toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            log.error("Could not create the URL for the TVheadend endpoint.", e);
        }

        Assert.notNull(url);

        return url;
    }

    /**
     * Reads all data of a reader and returns the read data as {@link String}.
     *
     * @param rd The reader which should be read.
     * @return All the read data as {@link String}.
     * @throws IOException In case the read process is erroneous.
     */
    private static String readAll(final Reader rd) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
