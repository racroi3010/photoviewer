package ptv.me.com.photoviewer.util;

/**
 *
 */
public class ValidationUtil {
    /**
     * Pattern for validating url
     */
    public static final String URL_PATTERN = "^(http|https)\\://[a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&amp;%\\$#\\=~]+$";
    public static final String URI_PATTERN = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
    /**
     * Validating url
     * @param url url value
     * @return return true if valid, otherwise return false
     */
    public static boolean validateURL(String url){
        if(url == null || url.isEmpty()){
            return false;
        }

        return url.matches(URL_PATTERN);
        //return true;
    }

    public static boolean validateURI(String uri){
        if(uri == null || uri.isEmpty()){
            return false;
        }

        return uri.matches(URI_PATTERN);
    }

}
