package com.doubeye.commons.test.DifferentiationTeller;

import com.doubeye.commons.utils.collection.JSONArrayDifferentiationTeller;
import com.doubeye.commons.utils.json.JSONUtils;
import net.sf.json.JSONArray;

import java.io.File;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/3/20.
 */
public class TestJSONArrayDifferentiationTeller {
    public static void main(String[] args) {
        String rootPath = TEST_SECOND_SET_HAS_MORE;
        JSONArray array1 = JSONUtils.getJsonArrayFromFile(new File(PATH_RESOURCE + rootPath + FILE_NAME_FIRST));
        JSONArray array2 = JSONUtils.getJsonArrayFromFile(new File(PATH_RESOURCE + rootPath + FILE_NAME_SECOND));
        JSONArrayDifferentiationTeller teller = new JSONArrayDifferentiationTeller();
        teller.setArray1(array1);
        teller.setArray2(array2);
        teller.setObjectKeyPropertyName("id");
        teller.compare();

        System.out.println("both key " + teller.getBothKeys().toString());
        System.out.println("identical key " + teller.getIdenticalKeys().toString());
        System.out.println("only in first key " + teller.getKeyOnlyInArray1().toString());
        System.out.println("only in second key " + teller.getKeyOnlyInArray2().toString());
        System.out.println("not equal " + teller.getDiffs().toString());
    }

    public static final String PATH_RESOURCE = "D:/workcode/dbUtils/commons/src/test/resources/JSONArrayDifferentiationTeller/";
    public static final String TEST_SET_EQUAL = "equal/";
    public static final String TEST_SECOND_SET_HAS_MORE = "dataSet2HasMore/";
    public static final String FILE_NAME_FIRST = "array1.json";
    public static final String FILE_NAME_SECOND = "array2.json";


}
