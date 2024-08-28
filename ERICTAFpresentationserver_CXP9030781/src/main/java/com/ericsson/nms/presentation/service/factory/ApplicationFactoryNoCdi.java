package com.ericsson.nms.presentation.service.factory;

import com.ericsson.nms.presentation.service.util.ApplicationMetadataFinderNoCdi;
import org.slf4j.LoggerFactory;

/**
 * Created by ejgemro on 12/1/15.
 */
public class ApplicationFactoryNoCdi extends  ApplicationFactory {

    // Just overrides the injections
    public ApplicationFactoryNoCdi() {
        logger = LoggerFactory.getLogger(ApplicationFactory.class);
        finder = new ApplicationMetadataFinderNoCdi();
    }

}
