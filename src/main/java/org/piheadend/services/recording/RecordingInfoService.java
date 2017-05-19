package org.piheadend.services.recording;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
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
 * Service for gathering information about scheduled recordings in TVHeadend.
 *
 * @author aw
 * @since 25.02.2017
 */
@Service
public class RecordingInfoService {

    private static final Logger log = LoggerFactory.getLogger(RecordingInfoService.class);

    private final String tvh_protocol;
    private final String tvh_host;
    private final String tvh_port;
    private final String tvh_path;
    private final String tvh_user;
    private final String tvh_password;

    @Autowired
    public RecordingInfoService(final Environment environment) {
        Assert.notNull(environment);
        this.tvh_protocol = environment.getRequiredProperty("tvheadend.protocol");
        this.tvh_host = environment.getRequiredProperty("tvheadend.host");
        this.tvh_port = environment.getRequiredProperty("tvheadend.port");
        this.tvh_path = environment.getRequiredProperty("tvheadend.path");
        this.tvh_user = environment.getRequiredProperty("tvheadend.user");
        this.tvh_password = environment.getRequiredProperty("tvheadend.password");
    }

    /**
     * Gets a list of {@link RecordingInfo} from the TVheadend server.
     *
     * @return List of {@link RecordingInfo} from the TVheadend server.
     * @throws IOException In case the List cannot be obtained from the server.
     */
    public List<RecordingInfo> gatherRecordingInfo() throws IOException {
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
        final String auth = this.tvh_user + ":" + this.tvh_password;
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
                    .setScheme(this.tvh_protocol)
                    .setHost(this.tvh_host)
                    .setPort(Integer.valueOf(this.tvh_port))
                    .setPath(this.tvh_path)
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
