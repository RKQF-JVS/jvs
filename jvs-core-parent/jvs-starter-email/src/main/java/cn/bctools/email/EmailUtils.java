package cn.bctools.email;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.http.HttpUtil;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.email.config.EmailConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * 邮箱发送组件
 *
 * @author: GuoZi
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class EmailUtils {

    EmailConfig emailJvsConfig;

    /**
     * 发送邮件给多人
     *
     * @param emailconfig 邮箱配置
     * @param title       标题
     * @param content     正文
     * @return 发送结果
     */
    public boolean sendEmailMessage(EmailConfig emailconfig, String title, String content, List<String> emailList, String url, String fileName) {
        return sendEmailMessage(emailconfig, title, content, emailList, HttpUtil.downloadFileFromUrl(url, fileName));
    }

    public boolean sendEmailMessage(String title, String content) {
        return sendEmailMessage(null, title, content, null, (File) null);
    }

    public boolean sendEmailMessage(String title, String content, List<String> emailList) {
        return sendEmailMessage(null, title, content, emailList, null);
    }

    public boolean sendEmailMessage(EmailConfig emailconfig, String title, String content, List<String> emailList) {
        return sendEmailMessage(emailconfig, title, content, emailList, null);
    }

    public boolean sendEmailMessage(String title, String content, List<String> emailList, String url, String fileName) {
        return sendEmailMessage(null, title, content, emailList, HttpUtil.downloadFileFromUrl(url, fileName));
    }

    public boolean sendEmailMessage(String title, String content, String url, String fileName) {
        return sendEmailMessage(null, title, content, null, HttpUtil.downloadFileFromUrl(url, fileName));

    }

    public boolean sendEmailMessage(EmailConfig emailconfig, String title, String content, String url, String fileName) {
        return sendEmailMessage(emailconfig, title, content, null, HttpUtil.downloadFileFromUrl(url, fileName));
    }

    /**
     * 发送绝给默认配置人员
     *
     * @param title
     * @param content
     * @param file
     * @return
     */
    public boolean sendEmailMessage(String title, String content, File file) {
        return sendEmailMessage(null, title, content, file);
    }

    public boolean sendEmailMessage(EmailConfig emailconfig, String title, String content, File file) {
        return sendEmailMessage(emailconfig, title, content, null, file);
    }

    public boolean sendEmailMessage(EmailConfig emailconfig, String title, String content, List<String> emailList, File file) {
        try {
            if (ObjectUtils.isEmpty(emailconfig)) {
                emailconfig = emailJvsConfig;
            }
            if (ObjectUtils.isEmpty(emailList)) {
                emailList = emailconfig.getTo();
            }
            MailAccount mailAccount = BeanCopyUtil.copy(emailconfig, MailAccount.class);
            mailAccount.setStarttlsEnable(true);
            mailAccount.setSslEnable(true);
            mailAccount.setPort(465);
            if (Optional.ofNullable(file).isPresent()) {
                MailUtil.send(mailAccount, emailList, title, content, true, file);
            } else {
                MailUtil.send(mailAccount, emailList, title, content, true);
            }
            log.info("邮件发送成功");
            return true;
        } catch (Exception e) {
            log.error("邮件发送异常", e);
            return false;
        }
    }

}
