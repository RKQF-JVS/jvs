package cn.bctools.database.util;

import com.alibaba.cloud.commons.lang.StringUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 修改hosts文件
 * @author Administrator
 */
public abstract class HostUtils {

    /**
     * 获取host文件路径
     *
     * @return
     */
    protected static String getHostFile() {
        String fileName = null;
        // 判断系统
        if ("linux".equalsIgnoreCase(System.getProperty("os.name"))) {
            fileName = "/etc/hosts";
        } else {
            fileName = System.getenv("windir") + "\\system32\\drivers\\etc\\hosts";
        }
        return fileName;
    }

    /**
     * 根据输入IP和Domain，更新host文件中的某个host配置
     *
     * @param ip
     * @param domain
     * @return
     */
    public synchronized static boolean updateHost(String ip, String domain) {
        if (ip == null || ip.trim().isEmpty() || domain == null || domain.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR： ip & domain must be specified");
        }
        String splitter = " ";
        /**
         * Step1: 获取host文件
         */
        String fileName = getHostFile();
        List<?> hostFileDataLines = null;
        try {
            hostFileDataLines = FileUtils.readLines(new File(fileName));
        } catch (IOException e) {
            System.out.println("Reading host file occurs error: " + e.getMessage());
            return false;
        }
        /**
         * Step2: 解析host文件，如果指定域名不存在，则追加，如果已经存在，则修改IP进行保存
         */
        List<String> newLinesList = new ArrayList<String>();
        // 指定domain是否存在，如果存在，则不追加
        boolean findFlag = false;
        // 标识本次文件是否有更新，比如如果指定的IP和域名已经在host文件中存在，则不用再写文件
        boolean updateFlag = false;
        for (Object line : hostFileDataLines) {
            String strLine = line.toString();
            // 将host文件中的空行或无效行，直接去掉
            if (StringUtils.isEmpty(strLine) || strLine.trim().equals("#")) {
                continue;
            }
            if (!strLine.startsWith("#")) {
                strLine = strLine.replaceAll("", splitter);
                int index = strLine.toLowerCase().indexOf(domain.toLowerCase());
                // 如果行字符可以匹配上指定域名，则针对该行做操作
                if (index != -1) {
                    // 如果之前已经找到过一条，则说明当前line的域名已重复，
                    // 故删除当前line, 不将该条数据放到newLinesList中去
                    if (findFlag) {
                        updateFlag = true;
                        continue;
                    }
                    // 不然，则继续寻找
                    String[] array = strLine.trim().split(splitter);
                    Boolean isMatch = false;
                    for (int i = 1; i < array.length; i++) {
                        if (domain.equalsIgnoreCase(array[i]) == false) {
                            continue;
                        } else {
                            findFlag = true;
                            isMatch = true;
                            // IP相同，则不更新该条数据，直接将数据放到newLinesList中去
                            if (array[0].equals(ip) == false) {
                                // IP不同，将匹配上的domain的ip 更新成设定好的IP地址
                                StringBuilder sb = new StringBuilder();
                                sb.append(ip);
                                for (int j = 1; i < array.length; i++) {
                                    sb.append(splitter).append(array[j]);
                                }
                                strLine = sb.toString();
                                updateFlag = true;
                            }
                        }
                    }
                }
            }
            // 如果有更新，则会直接更新到strLine中去
            // 故这里直接将strLine赋值给newLinesList
            newLinesList.add(strLine);
        }
        /**
         * Step3: 如果没有任何Host域名匹配上，则追加
         */
        if (!findFlag) {
            newLinesList.add(new StringBuilder(ip).append(splitter).append(domain).toString());
        }
        /**
         * Step4: 不管三七二十一，写设定文件
         */
        if (updateFlag || !findFlag) {
            try {
                FileUtils.writeLines(new File(fileName), newLinesList);
            } catch (IOException e) {
                System.out.println("Updating host file occurs error: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

}