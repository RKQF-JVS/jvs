package cn.bctools.dingding;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 钉钉，通知@
 *
 * @author: GuoZi
 */
@Data
public class At {

    /**
     * 需要通知@的成员
     */
    private List<String> atMobiles;
    /**
     * 是否通知@全体成员
     */
    private boolean isAtAll;

    public At(List<String> atMobiles) {
        if (atMobiles != null && atMobiles.size() > 0) {
            this.atMobiles = atMobiles;
            this.isAtAll = false;
        } else {
            this.atMobiles = Collections.emptyList();
            this.isAtAll = true;
        }
    }

}
