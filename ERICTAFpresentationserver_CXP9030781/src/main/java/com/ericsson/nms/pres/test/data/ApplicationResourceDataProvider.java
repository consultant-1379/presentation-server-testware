package com.ericsson.nms.pres.test.data;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.User;
import com.ericsson.cifwk.taf.handlers.RemoteFileHandler;
import com.ericsson.nms.host.HostConfigurator;
import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;
import com.ericsson.nms.presentation.service.api.dto.ApplicationConfig;
import com.ericsson.nms.presentation.service.api.dto.Group;
import com.ericsson.nms.presentation.service.factory.ApplicationFactory;
import com.ericsson.nms.presentation.service.factory.ApplicationFactoryNoCdi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * DataProvider for executing Test Cases for ApplicationResource Gets the
 * expected results for the different test cases.
 */
public abstract class ApplicationResourceDataProvider {

    private static ApplicationFactory factory = new ApplicationFactoryNoCdi();

    /**
     * Gets a list of expected apps for a test case
     *
     * @return Collection of Applications
     */
    public static Collection<AbstractApplication> getExpectedApplications() {

        copyFilesToSeed();

        final ApplicationConfig appCfg = factory.seed();
        final Map<String, AbstractApplication> appMap = appCfg.getApps();
        return appMap.values();
    }

    /**
     * Get Expected Groups with Apps
     *
     * @return A Collection of Groups with nested Applications
     */
    public static Collection<Group> getExpectedGroupsWithApps() {

        // Get Groups and Apps from XML.
        // Note, Groups are not populated with actual Apps, but only app ids.
        final ApplicationConfig appCfg = factory.seed();
        final Map<String, Group> groupMap = appCfg.getGrps();
        final Map<String, AbstractApplication> appMap = appCfg.getApps();

        // For each group, populate it with the actual apps
        final List<Group> groupWithAppList = new ArrayList<>();
        final Collection<Group> groupList = groupMap.values();
        for (final Group groupDto : groupList) {
            final Collection<String> appIds = groupDto.getAppIds();
            final List<AbstractApplication> groupAppList = new ArrayList<>();
            for (final String appId : appIds) {
                groupAppList.add(appMap.get(appId));
            }
            groupWithAppList.add(groupDto);
        }
        return groupWithAppList;
        }

    /**
     * Gets a Collection of Expected Favorites
     *
     * @return A Collection of 5 Citrix Applications which can be used as
     *         favorites
     */
    public static Collection<String> getExpectedFavorites() {
        final List<String> favorites = new ArrayList<>();
        final Map<String, Map<String, Object>> seedMap = factory.seedMap();
        final Map<String, Object> appsMap = seedMap.get("web");

        final Iterator<Object> appsIter = appsMap.values().iterator();
        while (appsIter.hasNext()) {
            final AbstractApplication app = (AbstractApplication) appsIter.next();
            favorites.add(app.getId());
        }

        return favorites.subList(0, 5);
        }

    /**
     * Copies xml config files from the server under test, so we can get
     * expected results from the same config files during the test run.
     */
    public static void copyFilesToSeed() {
        final String localAppInfoFile = "/ericsson/tor/data/presentation_server/config/appinfo.xml";
        final String localApplicationsFile = "/ericsson/tor/data/presentation_server/config/applications.xml";
        for (final Host node : HostConfigurator.getSVC1().getNodes()) {
            if (node.getHostname().contains("uiserv")) {
                copyFileToNOde(localAppInfoFile, localApplicationsFile, node);
            }
        }
    }

    private static void copyFileToNOde(final String localAppInfoFile, final String localApplicationsFile, final Host host) {
        final User user = new User();
        user.setUsername("root");
        user.setPassword("litpc0b6lEr");
        final RemoteFileHandler remoteFileHandler = new RemoteFileHandler(host, user);


        final String remoteAppInfoFile = ApplicationResourceTestDataProvider.HOST_SC1_NODE_JBOSS1_APPINFO_XML + ".sample";
        remoteFileHandler.copyRemoteFileToLocal(remoteAppInfoFile, localAppInfoFile);

        final String remoteApplicationsFile = ApplicationResourceTestDataProvider.HOST_SC1_NODE_JBOSS1_APPINFO_XML + ".sample";
        remoteFileHandler.createRemoteFile(localAppInfoFile, 0L, "");
        remoteFileHandler.createRemoteFile(localApplicationsFile, 0L, "");
        remoteFileHandler.copyRemoteFileToLocal(remoteApplicationsFile, localApplicationsFile);
    }

    /**
     * Determine if OS is Windows
     * @return boolean true if running on Windows
     */
    public static boolean isWindows() {
        boolean isWindows = false;
        final String os = System.getProperty("os.name");
        if(os.startsWith("Windows")) {
            isWindows = true;
        }
        return isWindows;
    }

    public static String getInvalidUserResult() {
        return "{\"message\":\"User not in header\"}";
    }
}