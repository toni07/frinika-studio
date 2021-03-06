/*
 * Copyright (c) 2011 Peter Johan Salomonsen (http://www.petersalomonsen.com) - Licensed under GNU GPL
 */
package com.frinika.jvstsynth;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Peter Johan Salomonsen
 */
public class NativeLibLoader {

    private static final Logger logger = Logger.getLogger(NativeLibLoader.class.getName());
    static boolean loadedNativeLib = false;

    public static void loadNativeLibs() throws Exception {
        if (loadedNativeLib) {
            return;
        }

        try
        {
            String arch = System.getProperty("sun.arch.data.model");
        
            File tmpDir = new File(System.getProperty("java.io.tmpdir"));
            String osname = System.getProperty("os.name").toLowerCase();
            String nativeLibName = null;
            // these files seem to come from https://github.com/mhroth/jvsthost (11 years ago...)
            if (osname.startsWith("win") && "x86".equals(System.getProperty("os.arch")))
            {
                nativeLibName = "jvsthost2_" + arch + ".dll";
            }
            else if (osname.startsWith("linux"))
            {
                nativeLibName = "libjvsthost2_" + arch + ".so";
            }
            else if (osname.startsWith("mac"))
            {
                //nativeLibName = "libjvsthost2_" + arch + ".jnilib"; The x64 file does not exist, so no use to searh for it
                nativeLibName = "libjvsthost2_32.jnilib";
            }
            else
            {
                System.out.println("Found no JVSTHost binary for " + osname + " / " + System.getProperty("os.arch"));
                return;
            }

            File tmpLibFile = new File(tmpDir, nativeLibName);
            System.out.println("Extracting native library to " + tmpLibFile.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(tmpLibFile);
            byte[] buf = new byte[1024];

            String fileName = "/com/frinika/jvstsynth/" + nativeLibName;
            InputStream is = NativeLibLoader.class.getResourceAsStream(fileName);
            logger.info("File name is: " + fileName);
            int len = is.read(buf);
            while (len > -1) {
                fos.write(buf, 0, len);
                len = is.read(buf);
            }
            fos.close();
            System.load(tmpLibFile.getAbsolutePath());
            System.out.println("Loaded native library: " + tmpLibFile.getAbsolutePath());
            loadedNativeLib = true;
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Error when managing native sound librairies", t);
            throw new Exception(t);
        }
    }

    public static boolean isLoadedNativeLib() {
        return loadedNativeLib;
    }
}
