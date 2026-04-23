package com.fragmenterworks.ffxivextract;

import com.fragmenterworks.ffxivextract.gui.FileManagerWindow;
import com.fragmenterworks.ffxivextract.gui.components.Update_Dialog;
import com.fragmenterworks.ffxivextract.helpers.PathSearcher;
import com.fragmenterworks.ffxivextract.helpers.Utils;
import com.fragmenterworks.ffxivextract.helpers.VersionUpdater;
import com.fragmenterworks.ffxivextract.helpers.VersionUpdater.VersionCheckObject;
import com.fragmenterworks.ffxivextract.paths.database.HashDatabase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Utils.getGlobalLogger().info("Starting FFXIV Explorer...");
        boolean akurun = false;
        File[] files = null;
        if (args.length == 0) {
            args = new String[] { "-aku" };
        } else {
            String[] newArgs = new String[args.length + 2];
            System.arraycopy(args, 0, newArgs, 0, args.length);
            newArgs[args.length] = "-aku";
            newArgs[args.length+1] = "-basepath 'G:\\FINAL FANTASY XIV - A Realm Reborn\\game\\sqpack'";
            args = newArgs;
        }

        // Set to windows UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Init the hash database
        File dbFile = new File("./" + Constants.DBFILE_NAME);
        boolean dbInit = false;

        if (dbFile.exists()) {
            try {
                HashDatabase.init();
                dbInit = true;
            } catch (Exception e) {
                Utils.getGlobalLogger().error("Error loading hash database.", e);
            }
        }

        if (!dbInit) {
            JOptionPane.showMessageDialog(null,
                    Constants.DBFILE_NAME + " is missing. No file or folder names will be shown... instead the file's hashes will be displayed.",
                    "Hash DB Load Error", JOptionPane.ERROR_MESSAGE);
        }

        Level currentLevel = LogManager.getRootLogger().getLevel();

        // Arguments
        if (args.length > 0) {

            // Info
            if (args.length == 1) {
                if (args[0].equals("-help"))
                    System.out.println("Commands: -help, -debug, -pathsearch");
                else if (args[0].equals("-pathsearch"))
                    System.out.println("Searches an archive for strings that start with <str>\n-pathsearch <path to index> <str>");
            }

//            if (args[0].equals("-debug") && currentLevel.intLevel() < Level.DEBUG.intLevel())
//                Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);
                Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.TRACE);

            // PATHSEARCH
            if (args[0].equals("-pathsearch")) {
                if (args.length < 3) {
                    Utils.getGlobalLogger().info("Too few args for pathsearch!");
                    return;
                }

                Utils.getGlobalLogger().info("Starting Path Searcher (this will take a while)");

                try {
                    PathSearcher.doPathSearch(args[1], args[2]);
                } catch (IOException e) {
                    Utils.getGlobalLogger().error("Encountered an error while path searching.", e);
                }
                return;
            }

// Arguments (order-independent)
            if (args.length > 0) {

                // parse flags (order-independent)
                String basepath = "C:\\Program Files (x86)\\SquareEnix\\FINAL FANTASY XIV - A Realm Reborn\\game\\sqpack\\";
                boolean doHelp = false;
                boolean doDebug = false;
                boolean doPathSearch = false;
                boolean doAku = false;
                boolean doAku6 = false;

                String pathsearchIndex = null;
                String pathsearchPrefix = null;

                for (int i = 0; i < args.length; i++) {
                    String a = args[i];

                    if ("-help".equals(a)) {
                        doHelp = true;
                        continue;
                    }

                    if ("-debug".equals(a)) {
                        doDebug = true;
                        continue;
                    }

                    if ("-basepath".equals(a)) {
                        if (i + 1 >= args.length) {
                            Utils.getGlobalLogger().info("Missing value for -basepath");
                            return;
                        }
                        basepath = args[++i];
                        if (!basepath.endsWith("\\") && !basepath.endsWith("/")) basepath += File.separator;
                        continue;
                    }

                    if ("-pathsearch".equals(a)) {
                        if (i + 2 >= args.length) {
                            Utils.getGlobalLogger().info("Too few args for pathsearch!");
                            return;
                        }
                        doPathSearch = true;
                        pathsearchIndex = args[++i];
                        pathsearchPrefix = args[++i];
                        continue;
                    }

                    if ("-aku".equals(a)) {
                        doAku = true;
                        continue;
                    }

                    if ("-aku6".equals(a)) {
                        doAku6 = true;
                        continue;
                    }
                }

                // help (prints and exits)
                if (doHelp) {
                    System.out.println("Commands: -help, -debug, -pathsearch, -aku, -aku6, -basepath");
                    System.out.println("-pathsearch <path to index> <str>   Searches an archive for strings that start with <str>");
                    System.out.println("-basepath <path>                   Override sqpack base path (ending with sqpack\\)");
                    return;
                }

                // debug / logging
                if (doDebug) {
                    Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.TRACE);
                }

                // PATHSEARCH
                if (doPathSearch) {
                    Utils.getGlobalLogger().info("Starting Path Searcher (this will take a while)");
                    try {
                        PathSearcher.doPathSearch(pathsearchIndex, pathsearchPrefix);
                    } catch (IOException e) {
                        Utils.getGlobalLogger().error("Encountered an error while path searching.", e);
                    }
                    return;
                }

                // preset file lists
// preset file lists
                if (doAku || doAku6) {
                    akurun = true;

                    if (doAku6) {
                        files = new File[1];
                        files[0] = new File(basepath + "ffxiv\\060000.win32.index");
                    } else {
                        // recursively collect all .index files inside basepath
// -aku: recursively collect canonical sqpack indices only (prevents VirtualFolder collisions)
                        File baseDir = new File(basepath);
                        if (!baseDir.exists() || !baseDir.isDirectory()) {
                            Utils.getGlobalLogger().error("Invalid basepath: " + basepath);
                            return;
                        }

                        List<Path> foundPaths = new ArrayList<>();
                        List<Path> ignored = new ArrayList<>();

                        Path base = baseDir.toPath();

                        try {
                            Files.walk(base)
                                    .filter(Files::isRegularFile)
                                    .forEach(p -> {
                                        Path rel = base.relativize(p);

                                        // only accept "<pack>/<name>.win32.index"
                                        if (rel.getNameCount() == 2 && rel.getFileName().toString().toLowerCase().endsWith(".win32.index")) {
                                            foundPaths.add(p);
                                        } else if (p.toString().toLowerCase().endsWith(".index") || p.toString().toLowerCase().contains(".index")) {
                                            // optional: collect suspicious ones for debugging
                                            ignored.add(p);
                                        }
                                    });
                        } catch (IOException e) {
                            Utils.getGlobalLogger().error("Error scanning for .index files", e);
                            return;
                        }

// deterministic order
                        foundPaths.sort((a, b) -> {
                            Path ra = base.relativize(a);
                            Path rb = base.relativize(b);
                            int da = ra.getNameCount();
                            int db = rb.getNameCount();
                            if (da != db) return Integer.compare(da, db);
                            return ra.toString().compareToIgnoreCase(rb.toString());
                        });

                        files = new File[foundPaths.size()];
                        for (int i = 0; i < foundPaths.size(); i++) files[i] = foundPaths.get(i).toFile();

                        Utils.getGlobalLogger().info("Found " + files.length + " .win32.index files under " + basepath);

// optional debug to identify what would've broken the tree
                        if (!ignored.isEmpty()) {
                            Utils.getGlobalLogger().warn("Ignored " + ignored.size() + " non-canonical index-like paths (showing up to 30):");
                            for (int i = 0; i < ignored.size() && i < 30; i++) {
                                Utils.getGlobalLogger().warn("  " + base.relativize(ignored.get(i)));
                            }
                        }
                    }
                }
            }
        }

        Utils.getGlobalLogger().info("Logging set to {}", LogManager.getRootLogger().getLevel());

        // Open up the main window
        FileManagerWindow fileMan = new FileManagerWindow(Constants.APPNAME);
        fileMan.setVisible(true);

        // Load Prefs
        Preferences prefs = Preferences
                .userNodeForPackage(com.fragmenterworks.ffxivextract.Main.class);
        boolean firstRun = prefs.getBoolean(Constants.PREF_FIRSTRUN, true);
        Constants.datPath = prefs.get(Constants.PREF_DAT_PATH, null);

        // First Run
        if (firstRun) {
            prefs.putBoolean(Constants.PREF_FIRSTRUN, false);

            int n = JOptionPane
                    .showConfirmDialog(
                            fileMan,
                            "Would you like FFXIV Extractor to check for a new hash database?",
                            "Hash DB Version Check", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                prefs.putBoolean(Constants.PREF_DO_DB_UPDATE, true);
            } else
                prefs.putBoolean(Constants.PREF_DO_DB_UPDATE, false);
        }
        if (akurun) {
            try {
                fileMan.openFiles(files);
            } catch (Exception e) {
                System.out.println("Aku load error");
            }
        }
        // Version Check (disabled in fork)
//        if (prefs.getBoolean(Constants.PREF_DO_DB_UPDATE, false)) {
//            VersionCheckObject checkObj = VersionUpdater.checkForUpdates();
//
//            if (HashDatabase.getHashDBVersion() < checkObj.currentDbVer
//                    || Constants.APP_VERSION_CODE < checkObj.currentAppVer) {
//                Update_Dialog updateDialog = new Update_Dialog(checkObj);
//                updateDialog.setLocationRelativeTo(fileMan);
//                updateDialog.setVisible(true);
//            }
//        }
    }

}
