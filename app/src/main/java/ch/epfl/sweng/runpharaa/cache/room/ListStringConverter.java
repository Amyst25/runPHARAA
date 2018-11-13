package ch.epfl.sweng.runpharaa.cache.room;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.sweng.runpharaa.utils.Required;

public class ListStringConverter {

    /**
     * Converts a given string into a list of strings
     * @param data which has the format Value,Values where Values = Value1,Value2,...
     * @return the corresponding List of strings
     */
    @TypeConverter
    public static List<String> fromStringToListString(String data){
        return (data == null)? null : (data.isEmpty()) ? new ArrayList<String>(): Arrays.asList(data.split(","));
    }

    /**
     * Converts a given List of string in a single string
     * @param ls
     * @return the corresponding string with the format Value,Values where Values = Value1,Value2,...
     */
    @TypeConverter
    public static String fromListStringToString(List<String> ls){
        Required.nonNull(ls, "A null list cannot be converted");

        if(ls.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();

        for(String s : ls){
            sb.append(s);
            sb.append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));

        return sb.toString();

    }
}
