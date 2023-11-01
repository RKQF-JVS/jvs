package cn.bctools.email;

import cn.bctools.common.exception.BusinessException;
import cn.bctools.common.utils.BeanCopyUtil;
import cn.bctools.common.utils.ObjectNull;
import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Configuration;

import javax.activation.DataSource;
import java.util.List;

/**
 * 邮箱发送组件
 *
 * @author: GuoZi
 */
@Slf4j
@Configuration
public class EmailUtils {

//    MailProperties emailJvsConfig;

    /**
     * 发送绝给默认配置人员
     *
     * @param title
     * @param content
     * @param files
     * @return
     */
    public boolean sendEmailMessage(MailAccount emailconfig, String title, String content, List<String> emailList, List<DataSource> files) {
        try {
            if (ObjectUtils.isEmpty(emailconfig)) {
                throw new BusinessException("平台未检测到邮件配置");
//                emailconfig = new MailAccount()
//                        .setFrom(emailJvsConfig.getUsername())
//                        .setHost(emailJvsConfig.getHost())
//                        .setPass(emailJvsConfig.getPassword());
            }
            if (ObjectUtils.isEmpty(emailList)) {
                throw new BusinessException("没有收件人");
            }
            MailAccount mailAccount = BeanCopyUtil.copy(emailconfig, MailAccount.class);
            mailAccount.setStarttlsEnable(true);
            mailAccount.setSslEnable(true);
            mailAccount.setPort(465);
            mailAccount.setAuth(true);
            {
                final Mail mail = Mail.create(mailAccount).setUseGlobalSession(true);
                mail.setTos(emailList.toArray(new String[emailList.size()]));
                mail.setTitle(title);
                mail.setContent(content);
                mail.setHtml(true);
                if (ObjectNull.isNotNull(files)) {
                    mail.setAttachments(files.toArray(new DataSource[files.size()]));
                }
                mail.send();
            }
            log.info("邮件发送成功");
            return true;
        } catch (Exception e) {
            log.error("邮件发送异常", e);
            return false;
        }
    }

}
