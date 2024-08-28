package com.ericsson.nms.pres.test.cases;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.Context;
import com.ericsson.cifwk.taf.annotations.TestId;
import com.ericsson.cifwk.taf.annotations.VUsers;
import com.ericsson.cifwk.taf.tools.http.constants.HttpStatus;
import com.ericsson.nms.pres.test.data.ApplicationResourceTestDataProvider;
import com.ericsson.nms.pres.test.getters.ApplicationResourceRESTGetter;
import com.ericsson.nms.pres.test.operators.REST.LauncherResourceRESTOperator;
import com.ericsson.nms.presentation.service.api.dto.AbstractHeader;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import se.ericsson.jcat.fw.annotations.Setup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class to execute tests against UISettingsResource
 **/
public class UISettingsResource extends TorTestCaseHelper {

    private static final Logger LOGGER = Logger.getLogger(UISettingsResource.class);
    final String user = ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER;

    @BeforeTest
    public void beforeTest() {

        final LauncherResourceRESTOperator operator = new LauncherResourceRESTOperator(user);

        final Collection<AbstractHeader> appHeaders = operator.doTafGetWithUserAuth(ApplicationResourceRESTGetter.GET_FAVORITES_URI).get(0);
        final Collection<String> favorites = new ArrayList<String>();

        for (AbstractHeader appHeader : appHeaders) {
            favorites.add(appHeader.getId());
        }

        operator.setSettings(favorites, "");
    }

    /**
     * Dummy Test Case to show the interworkings of TAF classes
     */
    @VUsers(vusers = {1})
    @Test(dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    public void dummyTestCase() {
        setTestcase("TC-DUMMY", "Dummy Test Case");
        setTestInfo("");
    }

    @Setup
    public void prepareTestCaseForTORFTUI842_Func_5() {
        //TODO Application Server is running with REST I/F published and data is seeded
    }

    /**
     * /* Set Favourite With Valid User And App
     * /* @DESCRIPTION Invoke setFavorite() in UISettingsResource using appropriate URL
     * /* @PRE Application Server is running with REST I/F published and data is seeded
     * /* @PRIORITY HIGH
     */
    @VUsers(vusers = {1})
    @Context(context = {Context.REST})
    @Test(groups = "acceptance", dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    @TestId(id = "Serv TORFTUI-842_Func_5", title = "Set Favourite With Valid User And App")
    void setFavouriteWithValidUserAndApp() {

        // define favorites app
        final LauncherResourceRESTOperator operator = new LauncherResourceRESTOperator(user);
        final Collection<String> favorites = operator.getExpectedSettings();
        operator.setSettings(favorites, "true");

        setTestStep("Send a request to the server using the 'rest/ui/settings/favorites' URL with a valid user included in the request and setting the app to a favourite");

        setTestStep("The application is set to a favourite");

        final List<Collection<AbstractHeader>> actualList = operator.doTafGetWithUserAuth(ApplicationResourceRESTGetter.GET_FAVORITES_URI);

        Assert.assertTrue(actualList.size() > 0, "Expected at least one result to be returned");

        // compare the actual and expected results
        for (Collection<AbstractHeader> actual : actualList) {
            final List<String> actualStrs = new ArrayList<>(actual.size());
            for (AbstractHeader actualHeader : actual) {
                actualStrs.add(actualHeader.getId());
            }
            LOGGER.debug("Expected the list of favourites : " + favorites + " to contain all " + actualStrs);
            Assert.assertTrue(favorites.containsAll(actualStrs), "Expected the list of favourites : " + favorites + " to contain all " + actualStrs);
        }

        // reset the favorites...
        operator.setSettings(favorites, "");
    }

    /**
     * /* Unset Favourite With Valid User And App
     * /* @DESCRIPTION Invoke setFavorite() in UISettingsResource using appropriate URL
     * /* @PRE Application Server is running with REST I/F published and data is seeded
     * /* @PRIORITY HIGH
     */
    @VUsers(vusers = {1})
    @Context(context = {Context.REST})
    @Test(groups = "acceptance", dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    @TestId(id = "Serv TORFTUI-842_Func_6", title = "Unset Favourite With Valid User And App")
    void unsetFavouriteWithValidUserAndApp() {

        // define favorites app
        final LauncherResourceRESTOperator operator = new LauncherResourceRESTOperator(user);
        final Collection<String> favorites = operator.getExpectedSettings();
        operator.setSettings(favorites, "");

        setTestStep("Send a reqeust to the server using the '/rest/ui/settings/favorites' URL with a valid user included in the request and setting the app to NOT a favourite");

        final List<Collection<AbstractHeader>> actualList = operator.doTafGetWithUserAuth(ApplicationResourceRESTGetter.GET_FAVORITES_URI);
        final Collection<AbstractHeader> expected = new ArrayList<>();

        Assert.assertTrue(actualList.size() > 0, "Expected at least one result to be returned");

        // compare the actual and expected results
        for (Collection<AbstractHeader> actual : actualList) {
            LOGGER.debug("Expected " + expected + " to be the same as " + actual);
            Assert.assertEquals(expected, actual, "Expected " + expected + " to be the same as " + actual);
        }

        // Check the settings are gone from the cache
        final List<String> settings = operator.getSettings("launcher", "favorites");

        Assert.assertFalse(favorites.contains(settings));

    }

    /**
     * /* Set Favourite With Invalid App
     * /* @DESCRIPTION Invoke setFavorite() in UISettingsResource using appropriate URL
     * /* @PRE Application Server is running with REST I/F published and data is seeded
     * /* @PRIORITY HIGH
     */
    @VUsers(vusers = {1})
    @Context(context = {Context.REST})
    @Test(groups = "acceptance", dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    @TestId(id = "Serv TORFTUI-842_Func_8", title = "Set Favourite With Invalid App")
    void setFavouriteWithInvalidApp() {

        // define favorites app
        final Collection<String> favoriteApps = new ArrayList<>();

        favoriteApps.add("invalidApp");

        setTestStep("Send a request to the server using the following '/rest/ui/settings/favorites' URL with an invalid application ID included in the request");

        final LauncherResourceRESTOperator operator = new LauncherResourceRESTOperator(user);
        HttpStatus responseCodes = operator.setSettings(favoriteApps, "true");

        setTestStep("There are no favourites set and an error is given.");

        Assert.assertNotNull(responseCodes);

        LOGGER.debug("Expected the response code to equal 204, but was : " + responseCodes.getCode());
        Assert.assertEquals(responseCodes.getCode(), 204, "Expected the response code to equal 204, but was : " + responseCodes.getCode());
    }

    /**
     * /* Favorite applications are set in <500 ms
     * /* @DESCRIPTION Favorite applications should be set in less than 500 ms
     * /* @PRE Application Server is running with REST I/F published and data is seeded
     * /* @PRIORITY MEDIUM
     */
    @VUsers(vusers = {1})
    @Context(context = {Context.REST})
    @Test(groups = "performance", dataProvider = "ApplicationResourceTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    @TestId(id = "Serv TORFTUI-842_Perf_1", title = "Favorite applications are set in <500 ms")
    void favoriteApplicationsAreSetIn500Ms() {

        // define favorites app
        final LauncherResourceRESTOperator operator = new LauncherResourceRESTOperator(user);
        final Collection<String> favorites = operator.getExpectedSettings();
        operator.setSettings(favorites, "true");

        setTestStep("Send a request to the server using the 'rest/ui/settings/favorites' URL with a valid user included in the request and setting the app to a favourite");

        setTestStep("The application is set to a favourite");

        final List<Collection<AbstractHeader>> actualList = operator.doTafGetWithUserAuth(ApplicationResourceRESTGetter.GET_FAVORITES_URI);

        Assert.assertTrue(actualList.size() > 0, "Expected at least one result to be returned");

        // compare the actual and expected results
        for (Collection<AbstractHeader> actual : actualList) {
            final List<String> actualStrs = new ArrayList<>(actual.size());
            for (AbstractHeader actualHeader : actual) {
                actualStrs.add(actualHeader.getId());
            }
            LOGGER.debug("Expected the list : " + favorites + " to contain all the following : " + actualStrs);
            Assert.assertTrue(favorites.containsAll(actualStrs), "Expected the list : " + favorites + " to contain all the following : " + actualStrs);
        }

        // Test Exec time meets performance criteria
//		final List<Long> lastExecTimes = operator.getLastExecTimes();
//		for(Long execTime : lastExecTimes) {
//			LOGGER.debug("Expected the execution to return in less than 500ms, but was " + execTime + "ms");
//			Assert.assertTrue(execTime < 500, "Expected the execution to return in less than 500ms, but was " + execTime + "ms");
//		}
//
        // reset the favorites...
        operator.setSettings(favorites, "");
    }
}