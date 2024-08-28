package com.ericsson.nms.pres.test.operators.REST;

import com.ericsson.cifwk.taf.GenericOperator;
import com.ericsson.cifwk.taf.RestOperator;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.pres.test.cases.util.PresentationServerContstants;
import com.ericsson.nms.pres.test.getters.ApplicationResourceRESTGetter;
import com.ericsson.nms.pres.test.operators.HttpToolHelper;
import com.ericsson.nms.pres.test.operators.LauncherResourceOperator;
import com.ericsson.nms.pres.test.operators.PresentationServerRestHelper;
import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;
import com.ericsson.nms.presentation.service.api.dto.AbstractHeader;
import com.ericsson.nms.presentation.service.api.dto.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * REST Context Operator for executing Test Cases for ServerTime
 */
public class ServerTimeRESTOperator extends LauncherResourceOperator implements RestOperator, GenericOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerTimeRESTOperator.class);

    private final PresentationServerRestHelper helper;

    private final Host uiHost;

    public ServerTimeRESTOperator(final String user) {
        super(user);
        uiHost = HostConfigurator.getApache();
        helper = new PresentationServerRestHelper(user);
    }

    /**
     * Performs a call out to the rest/server/time
     *
     * @return HttpResponse responses
     */
    public HttpResponse getServerTime() {
        try {
            final String uri = ApplicationResourceRESTGetter.SERVER_TIME_URI;
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
        final HttpResponse response = tool.request().header(PresentationServerContstants.X_TOR_USER_ID, user).get(uri);
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

}
