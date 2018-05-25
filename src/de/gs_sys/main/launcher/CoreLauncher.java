/*
 *  Copyright (c) 2018 Georg Schmidt <gs-develop@gs-sys.de>
 *  All rights reserved
 */


package de.gs_sys.main.launcher;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class CoreLauncher {

    /* ENABLE_FUNCTION */

    public static final boolean hasDeamon = true;
    public static final boolean hasFxSupport = true;
    public static final boolean isPortAvailable = true;
    public static final boolean hasWebFrontend = true;
    public static final boolean castCommandLineParameter = true;
    public static final boolean isLastCommandLineParameterAnInput = true;
    public static final boolean detectOS = true;
    public static final boolean getJarPath = true;

    public static final boolean getIniFile = true;
    public static final boolean getSysEnv = true;
    public static final boolean checkForUpdate = true;
    public static final boolean hasSmartcardSupport = true;
    public static final boolean getJavaVersion = true;
    public static final boolean hasJDK = true;
    public static final boolean isAdminOrRoot = true;
    public static final boolean cliOnlyMode = true;
    public static final boolean hasGlobalLog = true;
    public static final boolean hasGlobalDebugLog = true;
    public static final boolean runDoOnStart = true;
    public static final boolean runDoOnExit = true;
    public static final boolean doSelfCheck = true;
    public static final boolean runTracker = true;
    public static final boolean manageThreads = true;
    public static final boolean manageFxStages = true;
    public static final boolean isFirstStart = true;


    public static final boolean debugOutputEnabled = true;
    public static final boolean testMode = true;

    /* VALUES */

    public static HashMap<String,String> commandlineParameterWithValues = new HashMap<>();
    public static HashSet<String> commandlineParameter = new HashSet<>();
    public static String JAR_PATH = null;



    static {
        if(getJarPath) {
            try {
                JAR_PATH = URLDecoder.decode(new File(CoreLauncher.class.getProtectionDomain().
                        getCodeSource().getLocation().getPath()).getParentFile().getPath(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if(debugOutputEnabled)
            {
                System.out.println("JAR_PATH = " + JAR_PATH);
            }
        }
    }

    public static boolean hasIniFile() {
      return false; // Todo
    }

    /**
     * Not 100% secure: Works with IDEA
     * @return
     */
    public static boolean isInIDE() {
        return System.console() == null;
    }


    /**
     * Call this in the original main function at first method
     * @param args original or modified args
     */
    public static void main(String ... args)
    {
        if(testMode) {
            args = new String[]{
                    "key-gen",
                    "rsa",
                    "-o",
                    "T:\\test.pem",
                    "-e",
                    "demo"
            };
        }
        if(debugOutputEnabled)
        {
            System.out.println("ARGS: " + Arrays.toString(args));
            System.out.println("Is IDE = " + isInIDE());
        }

        if(castCommandLineParameter)
        {
            castCommandLineParameters(args);
        }
    }

    private static void castCommandLineParameters(String[] args) {

        if(args.length == 0)
            return;

        commandlineParameterWithValues = new HashMap<>(args.length / 2);
        commandlineParameter = new HashSet<>(2);

        boolean phaseSubFunctions = true;

        int pointerLimit = args.length;
        int invalidCount = 0;
        if(isLastCommandLineParameterAnInput)
        {
            commandlineParameterWithValues.put("last_parameter", args[args.length - 1]);
            pointerLimit--;
        }

        String cachedParameterKey = null;
        for (int i = 0; i < pointerLimit;) {
            if(phaseSubFunctions)
            {
                if(args[i].charAt(0) == '-')
                {
                    phaseSubFunctions = false;
                }
                else
                {
                    commandlineParameterWithValues.put("subfunction_" + i, args[i++]);
                }
            }
            else
            {
                if(args[i].charAt(0) == '-')
                {
                    if(cachedParameterKey != null)
                    {
                        commandlineParameter.add(cachedParameterKey);
                        cachedParameterKey = args[i++];
                    }
                    else {
                        cachedParameterKey = args[i++];
                    }
                }
                else {
                    if(cachedParameterKey != null)
                    {
                        commandlineParameterWithValues.put(cachedParameterKey, args[i++]);
                        cachedParameterKey = null;
                    }
                    else {
                        commandlineParameterWithValues.put("invalid_" + invalidCount, args[i]);
                    }
                }
            }
        }

        if(cachedParameterKey != null)
        {
            if(cachedParameterKey.charAt(0) == '-')
            {
                commandlineParameter.add(cachedParameterKey);
            }
            else {
                commandlineParameterWithValues.put("invalid_" + invalidCount, cachedParameterKey);
            }
            // cachedParameterKey = null;
        }

        if(debugOutputEnabled)
        {
            System.out.println("Single parameter:");
            commandlineParameter.forEach(System.out::println);
            System.out.println("\nValue parameter");
            commandlineParameterWithValues.forEach((key, value) -> System.out.println(key + " = " + value));
        }
    }




}
