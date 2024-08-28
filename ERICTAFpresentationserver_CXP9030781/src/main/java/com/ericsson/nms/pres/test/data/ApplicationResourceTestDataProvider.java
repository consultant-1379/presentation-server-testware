package com.ericsson.nms.pres.test.data;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;

/**
 *
 * Test DataProvider for executing Test Cases for ApplicationResource
 */
public class ApplicationResourceTestDataProvider {

    public final static String HOST_MS1_USER_APPLICATION_USER = (String) DataHandler.getAttribute("host.ms1.user.application.user");
    public final static String HOST_MS1_USER_APPLICATION_USER_1 = (String) DataHandler.getAttribute("host.ms1.user.application.user1");
    public final static String HOST_MS1_USER_APPLICATION_USER_2 = (String) DataHandler.getAttribute("host.ms1.user.application.user2");
    public final static String HOST_MS1_USER_APPLICATION_USER_3 = (String) DataHandler.getAttribute("host.ms1.user.application.user3");
    public final static String HOST_MS1_USER_APPLICATION_USER_WITHOUT_ROLE = (String) DataHandler
            .getAttribute("host.ms1.user.application.user_without_role");
    public final static String HOST_SC1_NODE_JBOSS1_APPLICATIONS_XML = (String) DataHandler.getAttribute("host.sc1.node.jboss1.applications.xml");
    public final static String HOST_SC1_NODE_JBOSS1_APPINFO_XML = (String) DataHandler.getAttribute("host.sc1.node.jboss1.appinfo.xml");

    // Dummy Test Data Provider Code generated to show interworkings of TAF Classes
    @DataProvider(name = "ApplicationResourceTestData")
    public static String[][] ApplicationResourceTestData() {

        final String[][] result = { {} };
        return result;
    }

    @DataProvider(name = "dummyTestData")
    public static String[][] dummyTestData() {

        final String[][] result = { {} };
        return result;
    }

    private static Map<String, Host> hostMap = new HashMap<>();

}
