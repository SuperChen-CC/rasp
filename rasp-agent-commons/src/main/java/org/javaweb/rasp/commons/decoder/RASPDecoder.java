package org.javaweb.rasp.commons.decoder;

import org.javaweb.rasp.commons.context.RASPContext;

public interface RASPDecoder {

	void decode(String json, RASPContext context);

}
