package org.javaweb.rasp.commons.sync;

import java.io.IOException;
import java.util.Map;

public interface RASPAPIRequest {

	Map<String, Object> request(Map<String, String> data, String apiURL, String rc4Key) throws IOException;

}
