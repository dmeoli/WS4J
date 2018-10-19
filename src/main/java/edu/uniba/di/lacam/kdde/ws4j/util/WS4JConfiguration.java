package edu.uniba.di.lacam.kdde.ws4j.util;

final public class WS4JConfiguration {

    private boolean trace;
    private boolean cache;
    private boolean memoryDB;
    private boolean stem;
    private boolean leskNormalize;
    private boolean mfs;

    private static final WS4JConfiguration ws4jConfiguration = new WS4JConfiguration();

    public static WS4JConfiguration getInstance() {
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

    public boolean useStem() {
        return stem;
    }

    public void setStem(boolean stem) {
        this.stem = stem;
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
