package ovlesser.webmovies.util;

import ovlesser.webmovies.BuildConfig;

/**
 * definition of constant variables
 */
public class Constant {
    public final static String ARG_MOVIE = "movie";
    public enum Status { STOP, STARTING, RUNNING, ERROR};
    public static final int MSG_JOB_START = 2;
    public static final int MSG_JOB_STOP = 3;
    public static final int MSG_UPDATE = 4;
    public static final int MSG_ERROR = 5;

    public static final String MESSENGER_INTENT_KEY = BuildConfig.APPLICATION_ID + ".KEY_MESSENGER_INTENT";
    public static final String KEY_URL = BuildConfig.APPLICATION_ID + "KEY_URL";
    public static final String KEY_MEOVIES = BuildConfig.APPLICATION_ID + ".KEY_MOVIES";
}
