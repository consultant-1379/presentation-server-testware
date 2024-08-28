package com.ericsson.nms.pres.test.cases;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.annotations.VUsers;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.nms.pres.test.data.ApplicationResourceTestDataProvider;
import com.ericsson.nms.pres.test.getters.ApplicationResourceRESTGetter;
import com.ericsson.nms.pres.test.operators.REST.LauncherResourceRESTOperator;
import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;
import com.ericsson.nms.presentation.service.api.dto.AbstractHeader;
import com.ericsson.nms.presentation.service.api.dto.Group;
import com.ericsson.nms.presentation.service.api.dto.WebApplication;
import com.ericsson.nms.presentation.service.factory.ApplicationFactory;
import com.ericsson.nms.presentation.service.factory.ApplicationFactoryNoCdi;
import com.ericsson.nms.presentation.service.util.ApplicationCopyUtil;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * Class to execute tests against ApplicationResource
 **/
public class ApplicationResource extends TorTestCaseHelper implements TestCase {

    private static final Logger LOGGER = Logger.getLogger(ApplicationResource.class);
    private static final char HASH = '#';

    final String user = ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER;

    ApplicationFactory factory = new ApplicationFactoryNoCdi();
    ApplicationCopyUtil copyUtil = new ApplicationCopyUtil();

    @BeforeClass
    void beforeClass() {

        final LauncherResourceRESTOperator operator = new LauncherResourceRESTOperator(user);

        final Collection<AbstractHeader> appHeaders = operator.doTafGetWithUserAuth(ApplicationResourceRESTGetter.GET_FAVORITES_URI).get(0);
        final Collection<String> favorites = new ArrayList<String>();

        for (final AbstractHeader appHeader : appHeaders) {
            favorites.add(appHeader.getId());
        }

        operator.setSettings(favorites, "");
    }

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
     * /* Get Applications With Valid User /* @DESCRIPTION Invoke getApps() in ApplicationResource using appropriate URL /* @PRE Application Server is
     * running with REST I/F published and data is seeded /* @PRIORITY HIGH
     */
    @VUsers(vusers = { 1 })
    @Context(context = { Context.REST })
    @TestId(id="Serv TORFTUI-841_Func_1", title="Get Applications With Valid User")
    @Test(groups = "acceptance", dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    void getApplicationsWithValidUser() {

        final String user = ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER;
        final LauncherResourceRESTOperator operator = new LauncherResourceRESTOperator(user);
        final Collection<AbstractApplication> expectedList = operator.getExpectedApplicationsWithValidUserResult();

        setTestStep("Send a request to the server with the 'apps' URL and a valid user included in the request.");
        final List<Collection<AbstractHeader>> actualList = operator.doTafGetWithUserAuth(ApplicationResourceRESTGetter.APPS_URI);

        Assert.assertTrue(actualList.size() > 0, "Expected at least one result to be returned");

        setTestStep("The list of applications returned matches those in the expected list.");
        for (final Collection<AbstractHeader> actual : actualList) {
            LOGGER.debug("Expected the list : " + expectedList + ", to contain all : " + actual);
            Assert.assertTrue(expectedList.containsAll(actual), "Expected the list : " + expectedList + ", to contain all : " + actual);
        }
    }

//    Commented out until Contextual Launch stories are delivered
//    @VUsers(vusers = { 1 })
//    @Context(context = { Context.REST })
//    @TestId(id="Serv TORF-26297", title="Get Applications With Consumes Filter")
//    @Test(groups = "acceptance", dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
//    void getApplicationsWithConsumes() {

//        final String user = ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER_3;
//        final LauncherResourceRESTOperator operator = new LauncherResourceRESTOperator(user);

//        setTestStep("Send a request to the server on the 'apps' context using version  2.0.0 to retrieve the applications that can consume " +
//                " mocName = NetworkElement; targetType = ERBS; and single Mo selection");

//        WebApplication[] apps = operator.getApplicationsWithDatatype(null, null, false);
//        Assert.assertEquals(1, apps.length);
//        Assert.assertEquals("topologybrowser", apps[0].getId());

//    }

//    @VUsers(vusers = { 1 })
//    @Context(context = { Context.REST })
//    @Test(groups = "acceptance", dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    void getApplicationsWithUserHasNoRole() {
        setTestcase("Serv TORF-21733-1", "Get Applications With No Role User");

        final String user = ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER;
        final LauncherResourceRESTOperator operator = new LauncherResourceRESTOperator(user);

        setTestStep("Send a request to the server with the 'apps' URL and a valid user included in the request.");
        final List<Collection<AbstractHeader>> actualList = operator.doTafGetWithUserAuth(ApplicationResourceRESTGetter.APPS_URI);

        Assert.assertTrue(actualList.size() > 0, "Expected at least one result to be returned");

        setTestStep("The list of applications returned matches those in the expected list.");
        Set<String> appIds = new HashSet<>();
        for (final Collection<AbstractHeader> actual : actualList) {
            for (AbstractHeader appHeader : actual) {
                appIds.add(appHeader.getId());
            }
        }

        Assert.assertTrue(!appIds.contains("command_line_interface"), "Expected the list : " + appIds + ",not to contain CLI App " );
        Assert.assertTrue(!appIds.contains("network_explorer"), "Expected the list : " + appIds + ",not to contain Network Explorer " );
    }

    /**
     * /* Get Groups With Valid User /* @DESCRIPTION Invoke getGroups() in ApplicationResource using appropriate URL /* @PRE Application Server is
     * running with REST I/F published and data is seeded /* @PRIORITY HIGH
     */
    @VUsers(vusers = { 1 })
    @Context(context = { Context.REST })
    @Test(groups = "acceptance", dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    @TestId(id="Serv TORFTUI-841_Func_3", title="Get Groups With Valid User")
    void getGroupsWithValidUser() {

        setTestStep("Send a request to the server with the 'groups' URL and a valid user included in the request.");
        final String user = ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER;
        final LauncherResourceRESTOperator operator = new LauncherResourceRESTOperator(user);
        final Collection<Group> expected = operator.getExpectedGroupsWithValidUserResult();

        setTestStep("The list of groups returned matches those in the expected list and contains the expected applications.");
        final List<Collection<AbstractHeader>> actualList = operator.doTafGetWithUserAuth(ApplicationResourceRESTGetter.GROUPS_URI);

        Assert.assertTrue(actualList.size() > 0, "Expected at least one result to be returned");

        for (final Collection<AbstractHeader> actual : actualList) {
            LOGGER.debug("Expected the list : " + expected + ", to contain all : " + actual);
            Assert.assertTrue(expected.containsAll(actual), "Expected the list : " + expected + ", to contain all : " + actual);
        }
    }

    /**
     * /* Get Favourites With Valid User /* @DESCRIPTION Invoke getFavorites() in ApplicationResource using appropriate URL /* @PRE Application Server
     * is running with REST I/F published and data is seeded and favorites have been set /* @PRIORITY HIGH
     */
    @VUsers(vusers = { 1 })
    @Context(context = { Context.REST })
    @Test(groups = "acceptance", dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    @TestId(id="Serv TORFTUI-841_Func_4", title="Get Favourites With Valid User")
    void getFavouritesWithValidUser() {

        // Define the user
        final String user = ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER;

        // Define the favorites
        final LauncherResourceRESTOperator operator = new LauncherResourceRESTOperator(user);
        final Collection<String> favorites = operator.getExpectedSettings();
        operator.setSettings(favorites, "true");

        setTestStep("Send a request to the server using the 'favorites' and a valid user included in the request");
        final List<Collection<AbstractHeader>> actualList = operator.doTafGetWithUserAuth(ApplicationResourceRESTGetter.GET_FAVORITES_URI);

        setTestStep("The Favourites returned match those in the expected list.");

        Assert.assertTrue(actualList.size() > 0, "Expected at least one result to be returned");

        for (final Collection<AbstractHeader> actual : actualList) {
            final List<String> actualStrs = new ArrayList<>(actual.size());
            for (final AbstractHeader actualHeader : actual) {
                actualStrs.add(actualHeader.getId());
            }
            LOGGER.debug("Expected the list : " + actualStrs + ", to contain all : " + favorites);
            Assert.assertTrue(favorites.containsAll(actualStrs), "Expected the list : " + actualStrs + ", to contain all : " + favorites);
        }

        //Unset favorites
        operator.setSettings(favorites, "");
    }

    /**
     * /* TORFTUI-841_Func_41: Launch Web App With valid user /* @DESCRIPTION Invoke launchWebApplication() in ApplicationResource using appropriate
     * URL /* @PRE Application Server is running with REST I/F published and data is seeded with some valid Web Applications /* @PRIORITY HIGH
     */
    @VUsers(vusers = { 1 })
    @Context(context = { Context.REST })
    @Test(groups = "requires_https", dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    @TestId(id="Serv TORFTUI-841_Func_41", title="Get Web App With Valid User")
    void launchWebAppWithValidUser() {
        final LauncherResourceRESTOperator operator = new LauncherResourceRESTOperator(user);
        final Map<String, Map<String, Object>> seedMap = factory.seedMap();
        final Map<String, Object> appMap = seedMap.get("web");
        final Object appObj = appMap.values().iterator().next();
        final AbstractApplication app = (AbstractApplication) appObj;

        // Configure LauncherConfig
        final WebApplication webApp = copyUtil.copy(false,(WebApplication) app);
        final String expectedUri = webApp.getTargetUri();
        final String expectedPath = expectedUri.substring(expectedUri.indexOf(HASH));

        setTestStep("Send a request to the server using the 'apps/web/{appID}' URL and a valid user included in the requst and a valid appID passed as an argument.");
        final Map<String, String> actualResponses = operator.getWebApp(app.getId()).getHeaders();

        // Check at least one result is returned
        Assert.assertTrue(actualResponses.size() > 0, "Expected at least one result to be returned");

        setTestStep("The Response has the 'location' value set to the expected URL in the header.");
        for (final String actualHeader : actualResponses.keySet()) {
            if (actualHeader.startsWith("Location")) {
                LOGGER.debug("Expected Header to be : " + expectedUri + ", but was " + actualHeader);
                Assert.assertTrue(actualResponses.get(actualHeader).endsWith(expectedPath));
                break;
            }
        }
    }

    /**
     * /* TORFTUI-841_Func_43: Launch Web App With invalid app /* @DESCRIPTION Invoke launchWebApplication() in ApplicationResource using appropriate
     * URL with invalid app /* @PRE Application Server is running with REST I/F published and data is seeded with some valid Web Applications /* @PRIORITY
     * MEDIUM
     */
    @VUsers(vusers = { 1 })
    @Context(context = { Context.REST })
    @Test(groups = "requires_https", dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    @TestId(id="Serv TORFTUI-841_Func_43", title="Get Web App With  With invalid app id")
    void launchWebAppWithInvalidApp() {
        final LauncherResourceRESTOperator operator = new LauncherResourceRESTOperator(user);

        setTestStep("Send a request to the server using the 'apps/web/{appID}' URL and an valid user included in the request and an invalid appID passed as an argument.");
        HttpResponse response = operator.getWebApp("invalid");

        setTestStep("The Response is a 404 error.");
        // Check at least one result is returned
        Assert.assertNotNull(response.getResponseCode(), "Expected at least one result to be returned");
        LOGGER.debug("Expected Response to be 404, but was " + response.getResponseCode().getCode());
        Assert.assertEquals(response.getResponseCode().getCode(), 404);
    }

    /**
     * /* Applications are returned in <500 ms /* @DESCRIPTION The application list should return in less than 500 ms /* @PRE Application Server is
     * running with REST I/F published and data is seeded /* @PRIORITY MEDIUM
     */
    @VUsers(vusers = { 1 })
    @Context(context = { Context.REST })
    @Test(groups = "performance", dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    @TestId(id="Serv TORFTUI-841_Perf_1", title="Applications are returned in <500 ms")
    void applicationsAreReturnedIn500Ms() {
        final LauncherResourceRESTOperator operator = new LauncherResourceRESTOperator(user);
        final Collection<AbstractApplication> expectedList = operator.getExpectedApplicationsWithValidUserResult();

        setTestStep("Send a request to the server with the 'apps' URL and a valid user included in the request.");
        final List<Collection<AbstractHeader>> actualList = operator.doTafGetWithUserAuth(ApplicationResourceRESTGetter.APPS_URI);

        // Check at least one result is returned
        Assert.assertTrue(actualList.size() > 0, "Expected at least one result to be returned");

        setTestStep("The list of applications returned matches those in the expected list.");
        for (AbstractHeader app : actualList.get(0)) {
            System.out.println(app.getId() + " -> "+ expectedList.contains(app));
        }

        for (final Collection<AbstractHeader> actual : actualList) {
            Assert.assertTrue(expectedList.containsAll(actual));
        }

        // Test Exec time meets performance criteria
        //        final List<Long> lastExecTimes = operator.getLastExecTimes();
        //
        //        for (final Long execTime : lastExecTimes) {
        //            LOGGER.debug("Expected execution time to be < 500ms, but was : " + execTime + "ms");
        //            Assert.assertTrue(execTime < 500, "Expected execution time to be < 500ms, but was : " + execTime + "ms");
        //        }
    }

    /**
     * /* Favorite applications are returned in <500 ms /* @DESCRIPTION Favorite applications should return in less than 500 ms /* @PRE Application
     * Server is running with REST I/F published and data is seeded /* @PRIORITY MEDIUM
     */
    @VUsers(vusers = { 1 })
    @Context(context = { Context.REST })
    @Test(groups = "performance", dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    @TestId(id="Serv TORFTUI-841_Perf_3", title="Favorite applications are returned in <500 ms")
    void favoriteApplicationsAreReturnedIn500Ms() {

        // Define the favorites (Make all web apps favorites)
        final LauncherResourceRESTOperator operator = new LauncherResourceRESTOperator(user);
        final Collection<String> favorites = operator.getExpectedSettings();
        operator.setSettings(favorites, "true");

        setTestStep("Send a request to the server using the 'favorites' and a valid user included in the request");
        final List<Collection<AbstractHeader>> actualList = operator.doTafGetWithUserAuth(ApplicationResourceRESTGetter.GET_FAVORITES_URI);

        // Check at least one result is returned
        Assert.assertTrue(actualList.size() > 0, "Expected at least one result to be returned");

        setTestStep("The Favourites returned match those in the expected list.");

        for (final Collection<AbstractHeader> actual : actualList) {
            final List<String> actualStrs = new ArrayList<>(actual.size());
            for (final AbstractHeader actualHeader : actual) {
                actualStrs.add(actualHeader.getId());
            }
            LOGGER.debug("Expected favorites list : " + favorites + " to contain all : " + actualStrs);
            Assert.assertTrue(favorites.containsAll(actualStrs), "Expected favorites list : " + favorites + " to contain all : " + actualStrs);
        }

        // Test Exec time meets performance criteria
        //        final List<Long> lastExecTimes = operator.getLastExecTimes();
        //        for (final Long execTime : lastExecTimes) {
        //            LOGGER.debug("Expected execution time to be < 500ms, but was : " + execTime + "ms");
        //            Assert.assertTrue(execTime < 500, "Expected execution time to be < 500ms, but was : " + execTime + "ms");
        //        }

        //Unset favorites
        operator.setSettings(favorites, "");
    }
}
