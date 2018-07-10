package sgm.recevicecardanimation.utils;

public class AppConstants {

    public static int REQUEST_SUCCESS = 200;
    public static boolean isTestMode = true;

    public static class STATUS_USER {
        public static final int NORMAL = 0;
        public static final int REQUESTING = 1;
        public static final int DONE = 2;
        public static final int ERROR = 3;
    }

    public static class TYPE_UER {
        public static int NORMAL = 1;
        public static int TEACHER = 2;
    }

    public static class WebSocketLink {
        public static String LINK_SEND = "ws://app.sgm.vn:9595/";
    }

    public static class STATUS_REQUEST {
        public static int REQUEST_ERROR = 0;
        public static int EMAIL_HAS_REGISTED = 455;
        public static int SERVER_MAINTAIN = 503;
        public static int TOKEN_EXPIRED = 401;
        public static int LIMIT_DEVICE = 460;
        public static int CONTACT_HAS_EXISTS = 458;
        public static int EMAIL_HAS_REGISTER = 462;
        public static int REQUEST_REQUIRE_EMAIL = 461;
        public static int ACCOUNT_BLOCKED = 463;
        public static int NOT_ENOUGH_POINT = 506;
        public static int VIDEO_HAS_BUY = 507;
    }

    public enum KEY_PARAMS {
        DEVICE_ID("device_id"),
        MESSAGE("message"),
        DATA("data"),
        STATUS("status");

        private String mName;

        KEY_PARAMS(String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }
}
