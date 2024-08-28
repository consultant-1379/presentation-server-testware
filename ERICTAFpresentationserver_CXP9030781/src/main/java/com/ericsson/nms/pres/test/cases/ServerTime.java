package com.ericsson.nms.pres.test.cases;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.annotations.VUsers;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.nms.pres.test.data.ApplicationResourceTestDataProvider;
import com.ericsson.nms.pres.test.operators.REST.ServerTimeRESTOperator;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * Class to execute tests against ServerTime
 **/
public class ServerTime extends TorTestCaseHelper implements TestCase {

    private static final Logger LOGGER = Logger.getLogger(ServerTime.class);

    final String user = ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER;

    /**
     * Dummy Test Case to show the interworkings of TAF classes
     */
    @VUsers(vusers = { 1 })
    @Test(dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    public void dummyTestCase() {
        setTestcase("TC-DUMMY", "Dummy Test Case");
        setTestInfo("");
    }

    /**
     * Get Server time
     * @DESCRIPTION Invoke getServerTime() in ServerTimeRESTOperator using appropriate URL
     * @PRE Application Server is running with REST I/F published
     * @PRIORITY MEDIUM
     */
    @VUsers(vusers = { 1 })
    @Context(context = { Context.REST })
    @TestId(id="Serv_TORF-60262_Func_1", title="Get Server Timer")
    @Test(groups = "acceptance", dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    void getApplicationsWithValidUser() {

        final String user = ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER;
        final ServerTimeRESTOperator operator = new ServerTimeRESTOperator(user);
        final HttpResponse serverTime = operator.getServerTime();
        final JSONParser parser = new JSONParser();

        Object timestamp = null;
        Object utcOffset = null;
        Object timezone = null;
        Object serverLocation = null;

        try {
            Object obj = parser.parse(serverTime.getBody());
            JSONObject jsonObj = (JSONObject)obj;
            timestamp = jsonObj.get("timestamp");
            utcOffset = jsonObj.get("utcOffset");
            timezone = jsonObj.get("timezone");
            serverLocation = jsonObj.get("serverLocation");
        }
        catch(ParseException e) {
            LOGGER.info("Cannot parse JSON response from server.");
        }

        Assert.assertNotNull(timestamp, "Expected that timestamp is found in REST response");
        Assert.assertNotNull(utcOffset, "Expected that utcOffset is found in REST response");
        Assert.assertNotNull(timezone, "Expected that timezone is found in REST response");
        Assert.assertNotNull(serverLocation, "Expected that serverLocation is found in REST response");
    }
}
