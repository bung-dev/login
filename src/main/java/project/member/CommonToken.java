package project.member;

public class CommonToken {
    public final static long JWT_ACCESS_TOKEN_EXPIRED_TIME= 1000 * 60 * 5L; //15분
    public final static long JWT_REFRESH_TOKEN_EXPIRED_TIME= 1000L * 60 * 60 * 24;; //1일(ms)
    public final static int JWT_COOKIE_REFRESH_TOKEN_EXPIRED_TIME = 60 * 60 * 24; //1일(초)

    public final static String JWT_ACCESS_TOKEN_NAME = "access";
    public final static String JWT_REFRESH_TOKEN_NAME = "refresh";
    public final static String JWT_HEADER = "Authorization";

}

