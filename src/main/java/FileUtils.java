import com.csvreader.CsvReader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    //创建文件夹
    public static void createFile(File file) {
        if (!(file.exists() && file.isDirectory())) {
            file.mkdirs();
        }
    }

    //创建文件夹
    public static void createFile(String path) {
        File file = new File(path);
        createFile(file);
    }

    public static void copyFile(File target, File original) {
        try {
            FileInputStream input = null;
            FileOutputStream out = null;
            try {
                input = new FileInputStream(original);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            byte[] buffer = new byte[1024];
            File des = new File(target.getAbsolutePath());
            out = new FileOutputStream(des);
            int len = 0;
            while (-1 != (len = input.read(buffer))) {
                out.write(buffer, 0, len);
            }
            out.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取csv中的数据， size为行数， String[] 为2, 且从csv中的某一行第一列包含TIME的下一行开始计算数据
     * @param path
     */
    public static List<String[]> readCsv(String path) {
        List<String[]> csvList = new ArrayList<String[]>();

        try {
            // 创建CSV读对象
            CsvReader csvReader = new CsvReader(path);

            // 读表头
            csvReader.readHeaders();
            boolean start = false;
            while (csvReader.readRecord()) {
                if (start) {
                    csvList.add(csvReader.getValues());
                }
                if ("TIME".equals(csvReader.getValues()[0])) {
                    start = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvList;
    }
}
