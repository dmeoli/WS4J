package edu.uniba.di.lacam.kdde.ws4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

final public class WS4JConfiguration {

	private final static String CONFIGURATION = "/WS4J.conf";

	private Properties properties;

	private boolean trace;
	private boolean cache;
	private boolean memoryDB;
	private String infoContent;
	private boolean stem;
	private String stopList;
	private boolean leskNormalize;
	private boolean mfs;

    private static final WS4JConfiguration instance = new WS4JConfiguration();

	private WS4JConfiguration() {
		InputStream stream;
		try {
			stream = WS4JConfiguration.class.getResourceAsStream(CONFIGURATION);
			properties = new Properties();
			properties.load(stream);
			cache = readInt("cache",1) == 1;
			trace = readInt("trace",0) == 1;
			memoryDB = readInt("memoryDB", 1) == 1;
			infoContent = readString("infoContent","ic-semcor.dat");
			stem = readInt("stem",0) == 1;
			stopList = readString("stopList", "stopList");
			leskNormalize = readInt("leskNormalize", 0) == 1;
			mfs = readInt("MFS", 0) == 1;
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int readInt(String key, int defaultValue) {
		return Integer.parseInt(readString(key, defaultValue + ""));
	}
	
	private String readString(String key, String defaultValue) {
		String value = properties.getProperty(key);
		if (value == null) {
		    Log.error("Configuration \"%d\" not found in ", CONFIGURATION);
			return defaultValue;
		}
		value = value.replaceAll("#.+", "").trim();
		return value;
	}

	public static WS4JConfiguration getInstance(){
		return WS4JConfiguration.instance;
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

	public String getInfoContent() {
		return infoContent;
	}

	public boolean useStem() {
		return stem;
	}

	public String getStopList() {
		return stopList;
	}

	public void setStopList(String stopList) {
		this.stopList = stopList;
	}

	public boolean useLeskNormalizer() {
		return leskNormalize;
	}

	public void setLeskNormalize(boolean leskNormalize) {
		this.leskNormalize = leskNormalize;
	}

	boolean useMFS() {
		return mfs;
	}

	public void setMFS(boolean mfs) {
		this.mfs = mfs;
	}

	public void setStem(boolean stem) {
		this.stem = stem;
	}
	
}
