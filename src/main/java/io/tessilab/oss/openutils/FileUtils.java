/*
 * Copyright 2017 Tessi lab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.tessilab.oss.openutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The class containing many usefull functions related with IO files.
 * @author Andres BEL ALONSO
 */
public class FileUtils {

    private static final Logger LOGGER = LogManager.getLogger(FileUtils.class);

    private static final String ENCODING = "UTF-8";

    private FileUtils() {

    }

    public static String fileToString(String filePath) throws IOException {
        return fileToString(filePath, false);
    }

    /**
     * @param filePath
     *            Can be a resource path or a local file path.
     * @param resourcesOnly true if the file must be read from the java ressource folder
     * @return The string of the file content.
     * @throws java.io.IOException if there was a problem reading the file
     */
    public static String fileToString(String filePath, boolean resourcesOnly) throws IOException {
        try {
            StringWriter writer = new StringWriter();
            InputStream is = FileUtils.class.getResourceAsStream(filePath);
            IOUtils.copy(is, writer, ENCODING);
            is.close();
            return writer.toString();
        } catch (Exception e) {
            if (!resourcesOnly) {
                return org.apache.commons.io.FileUtils.readFileToString(new File(filePath));
            } else {
                throw new IOException("Error while reading the file: " + filePath);
            }
        }
    }

    /**
     * 
     * @param filePath The path to the file
     * @return A list with all the lines on the file
     * @throws IOException If there was a problem reading the file
     */
    public static List<String> getFileLines(String filePath) throws IOException {
        return getFileLines(filePath, false);
    }

    /**
     * @param filePath
     *            Can be a resource path or a local file path.
     * @param resourcesOnly true if the file must be read from the java ressource folder
     * @return The file content, cut into lines.
     * @throws java.io.IOException if there was a problem reading the file
     */
    public static List<String> getFileLines(String filePath, boolean resourcesOnly) throws IOException {
        String content = fileToString(filePath, resourcesOnly);
        String[] linesTemp = content.split("\\r?\\n");

        ArrayList<String> lines = new ArrayList<>();
        for (String line : linesTemp) {
            line = line.trim();
            if (!"".equals(line))
                lines.add(line);
        }
        return lines;
    }

    /**
     * List directory contents for a resource folder. Not recursive. This is
     * basically a brute-force implementation. Works for regular files and also
     * JARs.
     * 
     * @author Greg Briggs
     * @param clazz
     *            Any java class that lives in the same place as the resources
     *            you want.
     * @param path
     *            Should end with "/", but not start with one.
     * @return Just the name of each member item, not the full paths.
     * @throws URISyntaxException A problem related to paths
     * @throws IOException A problem related to file reading
     */
    public static String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
        URL dirURL = clazz.getClassLoader().getResource(path);
        if (dirURL != null && "file".equals(dirURL.getProtocol())) {
            /* A file path: easy enough */
            return new File(dirURL.toURI()).list();
        }

        if (dirURL == null) {
            /*
             * In case of a jar file, we can't actually find a directory. Have
             * to assume the same jar as clazz.
             */
            String me = clazz.getName().replace(".", "/") + ".class";
            dirURL = clazz.getClassLoader().getResource(me);
        }

        if ("jar".equals(dirURL.getProtocol())) {
            /* A JAR path */
            // strip out only the JAR file
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));

            JarFile jar = new JarFile(URLDecoder.decode(jarPath, ENCODING));
            // gives ALL entries in jar
            Enumeration<JarEntry> entries = jar.entries();
            // avoid duplicates in case it is a subdirectory
            Set<String> result = new HashSet<>();

            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.startsWith(path)) { // filter according to the path
                    String entry = name.substring(path.length());
                    int checkSubdir = entry.indexOf("/");
                    if (checkSubdir >= 0) {
                        // if it is a subdirectory, we just return the directory
                        // name
                        entry = entry.substring(0, checkSubdir);
                    }
                    result.add(entry);
                }
            }
            jar.close();
            return result.toArray(new String[result.size()]);
        }

        throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
    }

    /**
     * @param obj
     *            The object to serialize.
     * @param path
     *            must be local
     * @throws java.io.IOException A problem when writing the file
     */
    public static void serialize(Serializable obj, String path) throws IOException {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.close();
            fos.close();
        } catch (Exception e) {
            LOGGER.debug("Exception while serializing", e);
            throw new IOException(e.getMessage());
        }
    }

    /**
     * @param path
     *            A resource path.
     * @return The serialized object.
     * @throws java.io.IOException A problem when writing the file
     */
    public static Object deserialize(String path) throws IOException {
        Object obj = null;
        try {
            File file = new File(path);
            FileInputStream inputFileStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(inputFileStream);
            obj = objectInputStream.readObject();
            objectInputStream.close();
            inputFileStream.close();
        } catch (Exception e) {
            LOGGER.debug("Exception while deserializing", e);
            throw new IOException(e.getMessage());
        }
        return obj;
    }

}
