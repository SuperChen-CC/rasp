package org.javaweb.rasp.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class AgentClassLoader extends URLClassLoader {

    public AgentClassLoader(final URL url) {
        super(new URL[]{url});
    }

}