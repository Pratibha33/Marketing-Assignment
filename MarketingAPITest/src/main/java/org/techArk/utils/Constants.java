package org.techArk.utils;

import java.io.File;

public class Constants {
    public final static String PROJECT_HOME = System.getProperty("user.dir");
    public final static String MAIN_RESOURCES = PROJECT_HOME + File.separator + "src" + File.separator + "main" + File.separator + "resources";
    public final static String ENVIRONMENT_DETAILS_PROPERTIES_FILE = MAIN_RESOURCES + File.separator + "EnvironmentDetails" + File.separator + "TechArk.properties";
    public final static String TESTDATA_PROPERTIES_FILE = MAIN_RESOURCES + File.separator + "Testdata" + File.separator + "Testdata.properties";

}
