package com.ericsson.nms.pres.test.cases;

import com.ericsson.cifwk.taf.TestCase;
import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.annotations.VUsers;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.UserType;
import com.ericsson.cifwk.taf.utils.FileFinder;
import com.ericsson.cifwk.taf.utils.ssh.J2SshFileCopy;
import com.ericsson.nms.pres.test.data.ApplicationResourceDataProvider;
import com.ericsson.nms.pres.test.data.ApplicationResourceTestDataProvider;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class LauncherFiles extends TorTestCaseHelper implements TestCase {

    final static String USER = ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER;
    private final boolean isWindows = ApplicationResourceDataProvider.isWindows();

    /**
     * Dummy Test Case to show the interworkings of TAF classes
     */
    @VUsers(vusers = { 1, 10, 100 })
    @Test(dataProvider = "dummyTestData", dataProviderClass = ApplicationResourceTestDataProvider.class)
    public void dummyTestCase() {
    }

    private void coppyAppInfoFile(String appinfoFile, Host sc1) {
        final boolean appInfoExistsOnServer = J2SshFileCopy.getFile(ApplicationResourceTestDataProvider.HOST_SC1_NODE_JBOSS1_APPINFO_XML,
                appinfoFile, sc1.getIp(), sc1.getUser(UserType.ADMIN), sc1.getPass(UserType.ADMIN),
                sc1.getPort().get("ssh"));

        if (!appInfoExistsOnServer) {
            J2SshFileCopy.getFile(ApplicationResourceTestDataProvider.HOST_SC1_NODE_JBOSS1_APPINFO_XML + ".sample", appinfoFile,
                    sc1.getIp(), sc1.getUser(UserType.ADMIN), sc1.getPass(UserType.ADMIN),
                    sc1.getPort().get("ssh"));
        }
    }

    private File coppyAmendedFile(Host sc1) throws IOException {
        final String amendedFilePath = FileFinder.findFile("amendedApplications.xml").get(0);
        final File amendedFile = new File(amendedFilePath);
        final File tmpAmendedFile = new File(getPath("/temp/applications.xml"));
        FileUtils.copyFile(amendedFile, tmpAmendedFile);

        J2SshFileCopy.putFile(tmpAmendedFile.getAbsolutePath(), getConfigDir() + "applications.xml",
                sc1.getIp(), sc1.getUser(UserType.ADMIN), sc1.getPass(UserType.ADMIN),
                sc1.getPort().get("ssh"));
        return tmpAmendedFile;
    }

    /**
     * Gets the directory where the configuration files are located
     *
     * @return
     */
    private String getConfigDir() {
        return "/ericsson/tor/data/presentation_server/config/";
    }

    /**
     * If Windows add the C:
     *
     * @param path
     * @return
     */
    private String getPath(String path) {
        if (isWindows) {
            path = "C:" + path;
        }
        return path;
    }
}