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

public class Main {

    public static void main(String[] args) {

        Utils.getGlobalLogger().info("Starting FFXIV Explorer...");
        boolean akurun = false;
        File[] files = null;

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

            if (args[0].equals("-aku")) {
                String basepath = "C:\\Program Files (x86)\\SquareEnix\\FINAL FANTASY XIV - A Realm Reborn\\game\\sqpack\\";
                files = new File[56];
                files[0] = new File(basepath + "ffxiv\\000000.win32.index");
                files[1] = new File(basepath + "ffxiv\\010000.win32.index");
                files[2] = new File(basepath + "ffxiv\\020000.win32.index");
                files[3] = new File(basepath + "ffxiv\\030000.win32.index");
                files[4] = new File(basepath + "ffxiv\\040000.win32.index");
                files[5] = new File(basepath + "ffxiv\\050000.win32.index");
                files[6] = new File(basepath + "ffxiv\\060000.win32.index");
                files[7] = new File(basepath + "ffxiv\\070000.win32.index");
                files[8] = new File(basepath + "ffxiv\\080000.win32.index");
                files[9] = new File(basepath + "ffxiv\\0a0000.win32.index");
                files[10] = new File(basepath + "ffxiv\\0b0000.win32.index");
                files[11] = new File(basepath + "ffxiv\\0c0000.win32.index");
                files[12] = new File(basepath + "ffxiv\\120000.win32.index");
                files[13] = new File(basepath + "ffxiv\\130000.win32.index");
                files[14] = new File(basepath + "ex1\\0c0100.win32.index");
                files[15] = new File(basepath + "ex1\\020100.win32.index");
                files[16] = new File(basepath + "ex1\\020101.win32.index");
                files[17] = new File(basepath + "ex1\\020102.win32.index");
                files[18] = new File(basepath + "ex1\\020103.win32.index");
                files[19] = new File(basepath + "ex1\\020104.win32.index");
                files[20] = new File(basepath + "ex1\\020105.win32.index");
                files[21] = new File(basepath + "ex1\\030100.win32.index");
                files[22] = new File(basepath + "ex1\\120100.win32.index");
                files[23] = new File(basepath + "ex2\\0c0200.win32.index");
                files[24] = new File(basepath + "ex2\\020200.win32.index");
                files[25] = new File(basepath + "ex2\\020201.win32.index");
                files[26] = new File(basepath + "ex2\\020202.win32.index");
                files[27] = new File(basepath + "ex2\\020203.win32.index");
                files[28] = new File(basepath + "ex2\\020205.win32.index");
                files[29] = new File(basepath + "ex2\\030200.win32.index");
                files[30] = new File(basepath + "ex3\\0c0300.win32.index");
                files[31] = new File(basepath + "ex3\\020300.win32.index");
                files[32] = new File(basepath + "ex3\\020301.win32.index");
                files[33] = new File(basepath + "ex3\\020302.win32.index");
                files[34] = new File(basepath + "ex3\\020304.win32.index");
                files[35] = new File(basepath + "ex3\\020305.win32.index");
                files[36] = new File(basepath + "ex3\\030300.win32.index");
                files[37] = new File(basepath + "ex4\\0c0400.win32.index");
                files[38] = new File(basepath + "ex4\\020400.win32.index");
                files[39] = new File(basepath + "ex4\\020401.win32.index");
                files[40] = new File(basepath + "ex4\\020402.win32.index");
                files[41] = new File(basepath + "ex4\\020403.win32.index");
                files[42] = new File(basepath + "ex4\\020404.win32.index");
                files[43] = new File(basepath + "ex4\\020405.win32.index");
                files[44] = new File(basepath + "ex4\\020406.win32.index");
                files[45] = new File(basepath + "ex4\\020407.win32.index");
                files[46] = new File(basepath + "ex4\\020408.win32.index");
                files[47] = new File(basepath + "ex4\\020409.win32.index");
                files[48] = new File(basepath + "ex4\\030400.win32.index");
                files[49] = new File(basepath + "ex5\\0c0500.win32.index");
                files[50] = new File(basepath + "ex5\\020500.win32.index");
                files[51] = new File(basepath + "ex5\\020501.win32.index");
                files[52] = new File(basepath + "ex5\\020502.win32.index");
                files[53] = new File(basepath + "ex5\\020503.win32.index");
                files[54] = new File(basepath + "ex5\\020505.win32.index");
                files[55] = new File(basepath + "ex5\\030500.win32.index");
                akurun = true;
            }
            if (args[0].equals("-aku6")) {
                akurun = true;
                String basepath = "C:\\Program Files (x86)\\SquareEnix\\FINAL FANTASY XIV - A Realm Reborn\\game\\sqpack\\";
                files = new File[1];
                files[0] = new File(basepath + "ffxiv\\060000.win32.index");
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
