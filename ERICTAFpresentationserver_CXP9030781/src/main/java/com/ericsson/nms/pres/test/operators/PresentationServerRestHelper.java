package com.ericsson.nms.pres.test.operators;

import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;
import com.ericsson.nms.presentation.service.api.dto.Group;
import com.ericsson.nms.presentation.service.api.dto.WebApplication;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PresentationServerRestHelper {

    protected String user;

    private static final Logger LOGGER = LoggerFactory.getLogger(PresentationServerRestHelper.class);

    public PresentationServerRestHelper(final String user) {
        this.user = user;
    }

    /**
     * Transform the JSON String returned from the test to a Collection<Application> in order to test equality
     *
     * @param jsonStr The JSON String as an InputStream as received in the HttpResponse
     * @return Collection<Application>
     */
    public Collection<AbstractApplication> transformJsonApps(final String jsonStr) {

        Collection<AbstractApplication> appList = null;
        final JSONParser parser = new JSONParser();
        try {
            final JSONArray jsonArr = (JSONArray) parser.parse(jsonStr);
            final Iterator<JSONObject> jsonIter = jsonArr.iterator();
            final Collection<JSONObject> jsonAppList = new ArrayList<>();
            while (jsonIter.hasNext()) {
                jsonAppList.add(jsonIter.next());
            }
            appList = getApplications(jsonAppList);
        } catch (ParseException e) {
            LOGGER.error("ParseException whilst parsing JSON : " + e.getMessage());
        }
        return appList;
    }

    /**
     * Transform the JSON String returned from the test to a Collection<Group> in order to test equality
     *
     * @param jsonStr The JSON String as an InputStream as received in the HttpResponse
     * @return Collection<Group>
     */
    public Collection<Group> transformJsonGroups(final String jsonStr) {

        final Collection<Group> groupList = new ArrayList<>();
        final JSONParser parser = new JSONParser();

        try {
            final JSONArray jsonArr = (JSONArray) parser.parse(jsonStr);
            final Iterator<JSONObject> jsonIter = jsonArr.iterator();
            while (jsonIter.hasNext()) {
                final JSONObject obj = jsonIter.next();
                final String id = (String) obj.get("id");
                final String name = (String) obj.get("name");
                final Collection<JSONObject> jsonAppList = (Collection<JSONObject>) obj.get("apps");
                final Collection<AbstractApplication> appList = getApplications(jsonAppList);
                Set<String> appIds = Sets.newConcurrentHashSet(Lists.transform((List)appList, new Function<AbstractApplication, String>() {
                    @Override
                    public String apply(final AbstractApplication application) {
                        return application.getId();
                    }
                }));
                final Group group = new Group(id, name, appIds);
                groupList.add(group);
            }
        } catch (ParseException e) {
            LOGGER.error("ParseException whilst parsing JSON : " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Exception whilst parsing JSON : " + e.getMessage());
        }
        return groupList;
    }

    private Collection<AbstractApplication> getApplications(final Collection<JSONObject> jsonAppList) {
        final Collection<AbstractApplication> appList = new ArrayList<>();
        final Iterator<JSONObject> jsonAppIter = jsonAppList.iterator();
        while (jsonAppIter.hasNext()) {

            final JSONObject jsonApp = jsonAppIter.next();
            final String appId = (String) jsonApp.get("id");
            final String appName = (String) jsonApp.get("name");

            final AbstractApplication application = new WebApplication(appId,
                    appName,
                    (String) jsonApp.get("path"),
                    (String) jsonApp.get("host"),
                    (String) jsonApp.get("port"),
                    (String) jsonApp.get("protocol"),
                    (boolean) jsonApp.get("openInNewWindow"));

            application.setShortInfo((String) jsonApp.get("shortInfo"));
            appList.add(application);
        }
        return appList;
    }
}
