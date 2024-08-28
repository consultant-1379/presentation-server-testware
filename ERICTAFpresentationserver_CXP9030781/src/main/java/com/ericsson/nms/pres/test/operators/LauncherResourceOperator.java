package com.ericsson.nms.pres.test.operators;

import com.ericsson.cifwk.taf.GenericOperator;
import com.ericsson.nms.pres.test.data.ApplicationResourceDataProvider;
import com.ericsson.nms.presentation.service.api.dto.AbstractApplication;
import com.ericsson.nms.presentation.service.api.dto.Group;

import java.util.Collection;

public class LauncherResourceOperator implements GenericOperator {

    protected String user;

    public LauncherResourceOperator(final String user) {
    	this.user = user;
    }

    public Collection<AbstractApplication> getExpectedApplicationsWithValidUserResult() {
    	return ApplicationResourceDataProvider.getExpectedApplications();
    }

    public String getExpectedInvalidUserResult() {
    	return ApplicationResourceDataProvider.getInvalidUserResult();
    }

    public Collection<Group> getExpectedGroupsWithValidUserResult() {
    	return ApplicationResourceDataProvider.getExpectedGroupsWithApps();
    }

    public Collection<String> getExpectedSettings() {
    	return ApplicationResourceDataProvider.getExpectedFavorites();
    }
}
