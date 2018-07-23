package org.gaby;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;

public final class GFiles {

    public static int copyFileFast(String inFilePathAndName, String outFilePathAndName) throws Exception {
        File inFile = new File(inFilePathAndName);
        File outFile = new File(outFilePathAndName);
        return GFiles.copyFileFast(inFile, outFile);
    }

    public static int renameFile(String oldFileName, String newFileName) {
        File f = new File(oldFileName);
        f.renameTo(new File(newFileName));
        return 1;
    }

    public static boolean existsFile(String filename) {
        File f = new File(filename);
        return f.exists();
    }

    // public static int copyFileFast(File inFilePathAndName,
    // File outFilePathAndName) {
    // //有问题
    // try {
    // InputStream i = new FileInputStream(inFilePathAndName);
    // OutputStream o = new FileOutputStream(outFilePathAndName);
    // copyStream(i, o);
    // i.close();
    // o.close();
    // outFilePathAndName
    // .setLastModified(inFilePathAndName.lastModified());
    // } catch (IOException e) {
    // return 0;
    // }
    // return 1;
    // }
    //
    // public static void copyStream(InputStream inputStream,
    // OutputStream outputStream) throws IOException {
    // //有问题
    // String line;
    // BufferedReader reader = new BufferedReader(new InputStreamReader(
    // inputStream));
    // PrintWriter writer = new PrintWriter(new OutputStreamWriter(
    // outputStream));
    // line = reader.readLine();
    // while (line != null) {
    // writer.println(line);
    // line = reader.readLine();
    // }
    // writer.flush();
    // }

    public static int copyFileFast(File inFilePathAndName, File outFilePathAndName) throws Exception {
        // long time=new Date().getTime();
        int length = 2097152;
        FileInputStream in = new FileInputStream(inFilePathAndName);
        FileOutputStream out = new FileOutputStream(outFilePathAndName);
        FileChannel inC = in.getChannel();
        FileChannel outC = out.getChannel();
        int i = 0;
        while (true) {
            if (inC.position() == inC.size()) {
                inC.close();
                outC.close();
                outFilePathAndName.setLastModified(inFilePathAndName.lastModified());
                return 1;
            }
            if ((inC.size() - inC.position()) < 20971520)
                length = (int) (inC.size() - inC.position());
            else
                length = 20971520;
            inC.transferTo(inC.position(), length, outC);
            inC.position(inC.position() + length);
            i++;
        }

    }

    public static int newFolder(String newFolder) {
        File f = new File(newFolder);
        if (!f.exists()) {
            f.mkdirs();
            return 1;
        }
        return 0;
    }

    public static int newFile(String filePathAndName) throws IOException {
        File f = new File(filePathAndName);
        if (!f.exists()) {
            f.createNewFile();
            return 1;
        }
        return 0;
    }

    public static void newFile(String filePathAndName, String fileContent) {
        try {
            File f = new File(filePathAndName);
            if (!f.exists()) {
                f.createNewFile();
            }

            FileWriter newFileWriter = new FileWriter(f);
            PrintWriter newPrintWriter = new PrintWriter(newFileWriter);
            newPrintWriter.println(fileContent);

            newFileWriter.close();

        } catch (Exception e) {
            debug(e);
        }
    }

    private static void debug(Exception e) {

    }


    public static int deleteFile(String deleteFilePathAndName) {
        File deleteFile = new File(deleteFilePathAndName);
        return GFiles.deleteFile(deleteFile);
    }

    public static int deleteFile(File deleteFile) {
        if (!deleteFile.exists())
            return 0;
        try {
            if (deleteFile.delete()) {
                return 1;
            }
        } catch (Exception e) {
            log("DEBUG", "delete file error!", GFiles.class);
            debug(e);
        }
        return 0;
    }

    private static void log(String debug, String s, Class<GFiles> gFilesClass) {

    }

    public static int deleteFolder(String deletePath) {
        File fileDeletePath = new File(deletePath);
        return GFiles.deleteFolder(fileDeletePath);
    }

    public static int deleteFolder(File deletePath) {
        int t = 0;
        File[] deleteFiles = deletePath.listFiles();

        if (deleteFiles != null) {
            for (int i = 0; i < deleteFiles.length; i++) {
                if (deleteFiles[i].isFile()) {
                    if (deleteFiles[i].delete())
                        t += 1;
                } else {
                    t = t + GFiles.deleteFolder(deleteFiles[i]);
                }
            }
        }
        if (deletePath.delete()) {
            t += 1;
        }
        return t;
    }

    public static int deleteFileOrFolder(String deletePathOrFile) {
        File del = new File(deletePathOrFile);
        return GFiles.deleteFileOrFolder(del);
    }

    public static int deleteFileOrFolder(File deletePathOrFile) {
        if (!deletePathOrFile.exists())
            return 0;

        if (deletePathOrFile.isDirectory())
            return GFiles.deleteFolder(deletePathOrFile);
        else
            return GFiles.deleteFile(deletePathOrFile);
    }

    public static String isSameFileOrFolder(String filePathAndName1, String filePathAndName2) {

        File f1 = new File(filePathAndName1);
        File f2 = new File(filePathAndName2);

        return GFiles.isSameFileOrFolder(f1, f2);

    }

    public static String isSameFileOrFolder(File file1, File file2) {

        if (!file1.exists() || !file2.exists()) {
            return "not exist";
        }

        // type same?
        if (file1.isDirectory() != file2.isDirectory()) {
            return "no";
        }
        // dir?
        if (file1.isDirectory()) {
            // name same?
            if (file1.getName().compareTo(file2.getName()) == 0) {
                return "yes";
            } else {
                return "no";
            }
        }

        // length same?
        if (file1.length() != file2.length()) {
            return "no";
        }

        // modified same?
        if (file1.lastModified() != file2.lastModified()) {
            return "no";
        }

        return "yes";

    }

    public static int fileOrFolderCount(String folderName) {
        File fold = new File(folderName);
        return GFiles.fileOrFolderCount(fold);
    }

    public static int fileOrFolderCount(File fold) {
        File[] fs = fold.listFiles();
        int count = fs.length;
        for (int i = 0; i < fs.length; i++) {
            if (fs[i].isDirectory())
                count = count + fileOrFolderCount(fs[i]);
        }

        return count;
    }

    public static void writeObject(Object object, String file) {
        try {
            ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(file));
            o.writeObject(object);
            o.close();
        } catch (Exception e) {
            debug(e);
        }
    }

    public static Object readObject(String file) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            return in.readObject();
        } catch (Exception e) {
            debug(e);
        }
        return null;
    }

}
