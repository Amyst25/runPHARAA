package ch.epfl.sweng.runpharaa;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.sweng.runpharaa.cache.room.ListStringConverter;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ListStringConverterTests {

    private String s;
    private List<String> ls;

    @Before
    public void init(){
        s = "abba,cddd";
        ls = Arrays.asList(new String[]{"abba", "cddd"});

    }

    @Test
    public void conversionFromStringToList(){
        assertEquals(ls, ListStringConverter.fromStringToListString(s));
    }

    @Test
    public void conversionFromListToString(){
        assertEquals(s, ListStringConverter.fromListStringToString(ls));
    }

    @Test(expected = NullPointerException.class)
    public void conversionOfNullList(){
        ListStringConverter.fromListStringToString(null);
    }

    @Test
    public void conversionOfEmptyList(){
        assertEquals("", ListStringConverter.fromListStringToString(new ArrayList<String>()));
    }

    @Test
    public void conversionOfNullString(){
        assertNull(ListStringConverter.fromStringToListString(null));
    }

    @Test
    public void conversionEmptyListToStringBackToList(){
        String s1 = ListStringConverter.fromListStringToString(new ArrayList<String>());
        List<String> ls1 = ListStringConverter.fromStringToListString(s1);
        assertTrue(ls1.isEmpty());
    }
}
