package com.ericsson.nms.pres.test.operators.REST;

import com.ericsson.cifwk.taf.GenericOperator;
import com.ericsson.cifwk.taf.RestOperator;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.cifwk.taf.tools.http.HttpToolBuilder;
import com.ericsson.cifwk.taf.tools.http.constants.HttpStatus;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.pres.test.cases.util.PresentationServerContstants;
import com.ericsson.nms.pres.test.getters.ApplicationResourceRESTGetter;
import com.ericsson.nms.pres.test.operators.HttpToolHelper;
import com.ericsson.nms.pres.test.operators.LauncherResourceOperator;
import com.ericsson.nms.pres.test.operators.PresentationServerRestHelper;
import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;
import com.ericsson.nms.presentation.service.api.dto.AbstractHeader;
import com.ericsson.nms.presentation.service.api.dto.Group;
import com.ericsson.nms.presentation.service.api.dto.WebApplication;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * REST Context Operator for executing Test Cases for ApplicationResource
 */
public class LauncherResourceRESTOperator extends LauncherResourceOperator implements RestOperator, GenericOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(LauncherResourceRESTOperator.class);

    private final PresentationServerRestHelper helper;

    private final Host uiHost;

    public LauncherResourceRESTOperator(final String user) {
        super(user);
        uiHost = HostConfigurator.getApache();
        // try {
        // uiHost.stopTunnel();
        //        } catch (final IncorrectHostConfigurationException e) {
        //            e.printStackTrace();
        //        }
        helper = new PresentationServerRestHelper(user);
    }

    public HttpStatus setSettings(final Collection<String> settings, final String value) {
        for (final String setting : settings) {
            try {
                final HttpTool tool = HttpToolHelper.buildHttpToolWithAuthenticatedUser(uiHost, user);
                final String jsonString = "{\"id\":\"" + setting + "\", \"value\":\"" + value + "\"}";
                final HttpResponse response = tool.request().header(PresentationServerContstants.X_TOR_USER_ID, user)
                        .header("Content-Type", "application/json").header("Accept", "application/json")
                        .body(jsonString).put("rest/ui/settings/launcher/favorites");
                return response.getResponseCode();

            } catch (final Exception e) {
                LOGGER.error("Error when executing getFavorites REST call : " + e);
            }
        }
        return null;
    }

    public List<String> getSettings(final String application, final String settingType) {
        List<String> results = null;
        try {
            final HttpTool tool = HttpToolHelper.buildHttpToolWithAuthenticatedUser(uiHost, user);
            final HttpResponse response = tool.request().header(PresentationServerContstants.X_TOR_USER_ID, user)
                    .header("Accept", "application/json").get("rest/ui/settings/" + application + "/" + settingType);

            results = new ArrayList<>();
            results.add(response.getBody());

        } catch (final Exception e) {
            LOGGER.error("Error when executing getFavorites REST call : " + e);
        }
        return results;
    }

    public WebApplication[] getApplicationsWithDatatype(String mocName, String targetType, boolean multipleSelection) {
        try {

            String json = "{\"multipleSelection\":\""+ multipleSelection+"\",\"dataTypes\":[{\"name\":\"ManagedObject\"}]}";

            System.out.println("consumes -> "+ json);
            String encodedJson = URLEncoder.encode(json, "UTF-8");

            final HttpTool tool = HttpToolHelper.buildHttpToolWithAuthenticatedUser(uiHost, user);
            final HttpResponse response = tool.request()
                .header(PresentationServerContstants.X_TOR_USER_ID, user)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json;version=2.0.0")
                .queryParam("consumes", encodedJson)
                .get("rest/apps");

            final String jsonResponse = response.getBody();
            System.out.println("jsonResponse: \n"+ jsonResponse);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            WebApplication[] apps = mapper.readValue(jsonResponse, WebApplication[].class);

            return apps;

        } catch (final Exception e) {
            LOGGER.error("Error when executing getFavorites REST call : " + e);
            return null;
        }
    }


    /**
     * Performs a call out to the rest/apps/web resource
     *
     * @param appId
     *            The app to launch
     * @return Collection of String responses
     */
    public HttpResponse getWebApp(final String appId) {
        try {
            final String uri = ApplicationResourceRESTGetter.APPS_URI_WEB + "/" + appId;
            final HttpTool tool = HttpToolHelper.buildHttpToolWithAuthenticatedUser(uiHost, user);
            final HttpResponse response = tool.request().header(PresentationServerContstants.X_TOR_USER_ID, user).get(uri);
            return response;
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Do a Http GET request that puts a user in the session and therefore should be a valid call if all other attributes are valid
     *
     * @param uri
     *            to call
     * @return Return null
     */
    public List<Collection<AbstractHeader>> doTafGetWithUserAuth(final String uri) {

        final HttpTool tool = HttpToolHelper.buildHttpToolWithAuthenticatedUser(uiHost, user);
        final HttpResponse response = tool.request()
                .header(PresentationServerContstants.X_TOR_USER_ID, user)
                .header("Accept", "application/json")
                .get(uri);
        final String actual = response.getBody();

        final List<Collection<AbstractHeader>> resultList = new ArrayList<>();
        final Collection<AbstractHeader> headerList = new ArrayList<>();
        if (!actual.equals("\n")) {
            // Check if a HTML error was reported
            if (actual.trim().startsWith("<html>")) {
                LOGGER.error("HTML Error Returned from Response : \n" + actual);
                Assert.assertEquals(actual, "A JSON Response");
            } else if (uri.equals(ApplicationResourceRESTGetter.GROUPS_URI)) {
                final Collection<Group> groups = helper.transformJsonGroups(actual);
                headerList.addAll(groups);
            } else {
                final Collection<AbstractApplication> apps = helper.transformJsonApps(actual);
                headerList.addAll(apps);
            }
        }
        resultList.add(headerList);

        return resultList;
    }

    /**
     * Do a Http GET request that does not put a user in the session and therefore nothing is returned and an error is thrown.
     *
     * @param uri
     *            to call
     * @return Return null
     */
    public List<String> doTafGetWithoutUserAuth(final String uri) {
        final HttpTool httpTool = HttpToolBuilder.newBuilder(uiHost).useHttpsIfProvided(true).trustSslCertificates(true).followRedirect(false).build();
        final String responseBody = httpTool.get(uri).getBody();
        final List<String> results = new ArrayList<>();
        if ((responseBody != null) && (!responseBody.equals("\n"))) {
            results.add(responseBody.replaceAll("\n", "").trim());
        }

        return results;
    }

//    public List<Long> getLastExecTimes() {
//        return tool.getLastExecutionTimes();
//    }
//
//    public Long getAvgExecTime() {
//        return tool.getAverageExecutionTime();
//    }
//
//    public List<RestResponseCode> getLastResponseCodes() {
//        return tool.getLastResponseCodes();
//    }
}
