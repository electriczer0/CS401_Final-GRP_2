package lib.utilities;

public class Utils {

    /**
     * Pads a string to a target length with whitespace (or trims if its longer). useful for printing lists
     * @param toBeFitted
     * @param targetLength
     * @return
     */
    public static String fitString(String toBeFitted, int targetLength){
        if (toBeFitted.length() > targetLength){
            return toBeFitted.substring(0, targetLength);
        } else {
            return String.format("%1$-" + targetLength + "s", toBeFitted);
        }
    }
}
