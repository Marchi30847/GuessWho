package domain;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandUtils {
    private CommandUtils() {}

    public static ArrayList<String> extractClientNames(StringBuilder message) {
        int listBegin = message.indexOf("[");
        int listEnd = message.indexOf("]");

        if (listBegin >= listEnd || message.charAt(listEnd + 1) != ' ') return null;

        String recipientList = message.substring(listBegin + 1, listEnd);
        recipientList = recipientList.replace(" ", "");
        return new ArrayList<>(Arrays.asList(recipientList.split(","))) {
        };
    }

    public static void removePrefix(StringBuilder message, String prefix) {
        message.delete(0, prefix.length());
    }

    public static void removeList(StringBuilder message, char listEnd) {
        message.delete(0, message.toString().indexOf(listEnd) + 2);
    }
}
