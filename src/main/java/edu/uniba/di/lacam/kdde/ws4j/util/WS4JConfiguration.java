package edu.uniba.di.lacam.kdde.ws4j.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final public class WS4JConfiguration {

	private final static String CONFIGURATION = "WS4J.conf";

	private Properties properties;

	private boolean trace;
	private boolean cache;
	private boolean memoryDB;
	private String infoContent;
	private boolean stem;
	private String stopWords;
	private boolean leskNormalize;
	private boolean mfs;

    private static final WS4JConfiguration ws4jConfiguration = new WS4JConfiguration();

	private WS4JConfiguration() {
		InputStream stream;
		try {
			stream = getClass().getResourceAsStream(File.separator + CONFIGURATION);
			properties = new Properties();
			properties.load(stream);
			cache = readInt("cache",1) == 1;
			trace = readInt("trace",0) == 1;
			memoryDB = readInt("memoryDB", 1) == 1;
			infoContent = readString("infoContent", "infoContent");
			stem = readInt("stem",0) == 1;
			stopWords = readString("stopWords", "stopWords");
			leskNormalize = readInt("leskNormalize", 0) == 1;
			mfs = readInt("MFS", 0) == 1;
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int readInt(String key, int defaultValue) {
		return Integer.parseInt(readString(key, String.valueOf(defaultValue)));
	}
	
	private String readString(String key, String defaultValue) {
		String value = properties.getProperty(key);
		if (value == null) {
		    Log.error("Configuration \"%s\" not found in \"%s\"", key, CONFIGURATION);
			return defaultValue;
		}
		value = value.replaceAll("#.+", "").trim();
		return value;
	}

	public static WS4JConfiguration getInstance(){
		return ws4jConfiguration;
	}

	public boolean useCache() {
		return cache;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean useTrace() {
		return trace;
	}

    public boolean useMemoryDB() {
        return memoryDB;
    }

    public void setMemoryDB(boolean memoryDB) {
        this.memoryDB = memoryDB;
    }

	String getInfoContent() {
		return infoContent;
	}

	public boolean useStem() {
		return stem;
	}

	public void setStem(boolean stem) {
		this.stem = stem;
	}

	String getStopWords() {
		return stopWords;
	}

	public boolean useLeskNormalizer() {
		return leskNormalize;
	}

	public void setLeskNormalize(boolean leskNormalize) {
		this.leskNormalize = leskNormalize;
	}

	public boolean useMFS() {
		return mfs;
	}

	public void setMFS(boolean mfs) {
		this.mfs = mfs;
	}
}
