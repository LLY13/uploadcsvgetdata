import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Frame {
    public static void main(String[] args) {
        // 获取屏幕大小
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screensize.width;
        int screenHeight = screensize.height;
        // 创建 JFrame 实例
        JFrame frame = new JFrame("最大磁感应变化率");
        // Setting the width and height of frame
        frame.setBounds(screenWidth / 2 - 300, screenHeight / 2 - 200, 600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* 创建面板，这个类似于 HTML 的 div 标签
         * 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        JPanel panel = new JPanel();
        // 添加面板
        frame.add(panel);
        /*
         * 调用用户定义的方法并添加组件到面板
         */
        placeComponents(panel);

        // 设置界面可见
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);
        // 创建上传按钮
        JButton uploadButton = new JButton("upload");
        uploadButton.setBounds(10, 20, 80, 25);
        panel.add(uploadButton);

        JLabel displayLabel = new JLabel();
        displayLabel.setBounds(100, 20, 80, 25);
        panel.add(displayLabel);

        uploadButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String result = "错误";
                try {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setCurrentDirectory(new File("C:\\Users\\Administrator\\Desktop"));
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("csv", "csv");
                    chooser.setFileFilter(filter);
                    chooser.showOpenDialog(uploadButton);
                    List<String[]> data = FileUtils.readCsv(uploadFile(chooser.getSelectedFile()));
                    result = String.valueOf(getMagRateValue(data));
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    displayLabel.setText(result);
                }
            }
        });
    }

    private static String uploadFile(File file) {
        String destPath = "D:/magfiles";
        FileUtils.createFile(destPath);
        String fileName = file.getName();
        String destFullPath = destPath + "/" + new SimpleDateFormat("MM-dd HHmmss").format(new Date()) + fileName;
        File dest = new File(destFullPath);
        FileUtils.copyFile(dest, file);
        return dest.getAbsolutePath();
    }

    /**
     * 返回磁场最大变化率
     *
     * @param csvList
     */
    private static double getMagRateValue(List<String[]> csvList) {
        float min = 0;
        float max = 0;
        int minI = 0;
        int maxI = 0;
        final int interval = 5;
        for (int i = 0; i < csvList.size(); i++) {
            float cur = Float.parseFloat(csvList.get(i)[1]);
            if (cur < min) {
                min = cur;
                minI = i;
            }
            if (cur > max) {
                max = cur;
                maxI = i;
            }
        }
        int delta = Math.abs(maxI - minI) / 2;
        int x1 = Math.min(minI, maxI) - delta;
        int x2 = Math.max(minI, maxI) + delta;
        double maxK = 0;
        for (int i = x1; i < x2; i = i + 1) {
            double k = Math.abs((Float.parseFloat(csvList.get(i + interval)[1]) - Float.parseFloat(csvList.get(i)[1]))
                    / (Float.parseFloat(csvList.get(i + interval)[0]) - Float.parseFloat(csvList.get(i)[0])));
            if (k > maxK) {
                maxK = k;
            }
        }
        return maxK / 1000;
    }
}
