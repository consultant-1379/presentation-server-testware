package com.ericsson.nms.pres.test.cases;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.ericsson.cifwk.taf.TorTestCaseHelper;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.nms.pres.test.data.ApplicationResourceTestDataProvider;
import com.ericsson.nms.security.OpenIDMOperatorImpl;
import com.ericsson.nms.security.utility.ENMUserImpl;

public class UserManagementTestCase extends TorTestCaseHelper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ENMUserImpl adminUser_1 = new ENMUserImpl();
    private final ENMUserImpl adminUser_2 = new ENMUserImpl();
    private final ENMUserImpl adminUser_3 = new ENMUserImpl();

    @Inject
    OpenIDMOperatorImpl openIDMOperator;

    @BeforeSuite
    public void createUser() {
        logger.debug("start create user");
        try {
            adminUser_1.setUsername(ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER);
            adminUser_1.setFirstName("John");
            adminUser_1.setLastName("Doe");
            adminUser_1.setEmail("johndoe@ericsson.com");
            adminUser_1.setEnabled(true);
            adminUser_1.setPassword((String) DataHandler.getAttribute(ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER));

            adminUser_2.setUsername(ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER_1);
            adminUser_2.setFirstName("Emma");
            adminUser_2.setLastName("Harris");
            adminUser_2.setEmail("emmaharris@ericsson.com");
            adminUser_2.setEnabled(true);
            adminUser_2.setPassword((String) DataHandler.getAttribute(ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER_1));

            adminUser_3.setUsername(ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER_3);
            adminUser_3.setFirstName("User");
            adminUser_3.setLastName("Amos");
            adminUser_3.setEmail("ps-user-amos@ericsson.com");
            adminUser_3.setEnabled(true);
            adminUser_3.setPassword((String) DataHandler.getAttribute(ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER_3));

            openIDMOperator.connect("administrator", "TestPassw0rd");
            openIDMOperator.createUser(adminUser_1);
            openIDMOperator.createUser(adminUser_2);
            openIDMOperator.createUser(adminUser_3);
            openIDMOperator.assignUsersToRole("ADMINISTRATOR", //role
                    adminUser_1.getUsername(), //1 to n users to assign
                    adminUser_2.getUsername(),
                    adminUser_3.getUsername());

            openIDMOperator.assignUsersToRole("OPERATOR", //role
                    adminUser_1.getUsername());
            openIDMOperator.assignUsersToRole("OPERATOR", //role
                    adminUser_2.getUsername());
            openIDMOperator.assignUsersToRole("OPERATOR", //role
                    adminUser_3.getUsername());

            openIDMOperator.assignUsersToRole("Amos_Administrator", adminUser_3.getUsername());
            openIDMOperator.assignUsersToRole("Amos_Operator", adminUser_3.getUsername());

        } catch (final Exception e) {
            logger.error(e.getMessage());
            logger.debug("setup Error", e);
        }

        logger.debug("end create user");
    }

    @AfterSuite
    public void cleanUsers() {
        logger.debug("start delete user");
        try {
            openIDMOperator.connect("administrator", "TestPassw0rd");
            openIDMOperator.deleteUser(ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER);
            openIDMOperator.deleteUser(ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER_1);
            openIDMOperator.deleteUser(ApplicationResourceTestDataProvider.HOST_MS1_USER_APPLICATION_USER_3);
        } catch (final Exception e) {
            logger.error(e.getMessage());
        }
        logger.debug("end delete user");
    }

}
