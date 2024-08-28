package com.ericsson.nms.pres.test.operators;

import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.http.*;
import com.ericsson.nms.pres.test.cases.util.PresentationServerContstants;

public final class HttpToolHelper {

    public static final String APACHE_LOGIN_URI = "/login";

    public static final HttpTool buildHttpToolWithAuthenticatedUser(final Host restServer, final String user) {

        final String password = ((String) DataHandler.getAttribute(user));
        final String localEnvironment = ((String) DataHandler.getAttribute("environment.local"));
        final boolean local = Boolean.valueOf(localEnvironment);
        final HttpTool httpTool = HttpToolBuilder.newBuilder(restServer).useHttpsIfProvided(!local).trustSslCertificates(!local).followRedirect(false).build();

        final HttpResponse authenticationResponse = httpTool.request().body("IDToken1", user).body("IDToken2", password).post(APACHE_LOGIN_URI);

        final String authErrorCode = authenticationResponse.getHeaders().get("X-AuthErrorCode");

        if (local || PresentationServerContstants.VALID_LOGIN.equals(authErrorCode)) {

            httpTool.addCookie(PresentationServerContstants.X_TOR_USER_ID, user);
            return httpTool;

        } else {
            throw new SecurityException("Cannot login over Apache with user[" + user + "]");

        }
    }
}
