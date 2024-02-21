package org.ajoberstar.grgit.samples;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println(
            String.format("Executing %s#main...", Main.class.getCanonicalName()));
        try (InputStream stream = Manifests.openStream(Main.class)) {
            Manifest manifest = new Manifest(stream);
            Attributes attributes = manifest.getMainAttributes();
            String date = attributes.getValue("Build-Date");
            String branch = attributes.getValue("Git-Branch");
            String commit = attributes.getValue("Git-Commit");
            System.out.println(
                String.format("Class: %s\nBuilt: %s\nBranch: %s\nCommit: %s",
                    Main.class.getCanonicalName(), date, branch, commit));
        } catch (UnsupportedOperationException x) {
            System.out.println(
                "Unable to open jar manifest to retrieve git commit and branch.");
            System.out.println(
                "Make sure you're running the generated jar, not from Gradle or an IDE.");
        }
    }
}

class Manifests {

    /**
     * <p>Opens an input stream for the manifest resource of the jar the
     * parameter class belongs to.</p>
     *
     * <p>Throws <code>UnsupportedOperationException</code> if the class
     * does not belong to a jar.</p>
     *
     * @param clss The class to open the jar manifest strea for
     * @return A jar manifest stream for the parameter class
     * @throws IOException                   If unable to open the stream
     * @throws UnsupportedOperationException If the class is not from a jar
     */
    public static InputStream openStream(Class<?> clss) throws IOException {
        String manifestPath = getResourcePath(clss);
        if (null == manifestPath)
            throw new UnsupportedOperationException();
        return new URL(manifestPath).openStream();
    }

    /**
     * Returns the manifest resource path for the jar the parameter class
     * belongs to, or null if it is not part of a jar.
     *
     * @param clss The class to locate a jar manifest for
     * @return The manifest resource path for the parameter class
     */
    public static String getResourcePath(Class<?> clss) {
        String resourcePath = Classes.getResourcePath(clss);
        if (!resourcePath.startsWith("jar"))
            return null;
        int offset = resourcePath.lastIndexOf("!") + 1;
        return resourcePath.substring(0, offset) + "/META-INF/MANIFEST.MF";
    }
}

class Classes {

    public static String getResourcePath(Class<?> clss) {
        return clss.getResource(getResourceName(clss)).toString();
    }

    public static String getResourceName(Class<?> clss) {
        return clss.getSimpleName() + ".class";
    }
}

