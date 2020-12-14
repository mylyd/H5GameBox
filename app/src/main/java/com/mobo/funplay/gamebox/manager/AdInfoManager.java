package com.mobo.funplay.gamebox.manager;

import com.mobo.funplay.gamebox.BuildConfig;
import com.mobo.funplay.gamebox.R;

import ad.mobo.base.bean.PullInfos;
import ad.mobo.base.view.AdViewIdsHolder;
import ad.mobo.common.request.AdRequestHelper;

/**
 * 广告信息管理
 */
public final class AdInfoManager {
    public static final int NATIVE_NUM = 3;
    private AdInfoManager() {
    }

    private static final int BASE = 1001;
    private static final Info[] sInfoes = {
            /*激励广告*/
            new Info("", ""),
            new Info("", ""),
            /*原生广告*/
            new Info("", "", true),
            new Info("", "", true),
            new Info("", "", true),
            /*插页广告*/
            new Info("", "", false),
            new Info("", "", false),
            new Info("", "", false),

    };

    /**
     *
     * @param relatedAdid 相对于 BASE_REAWRD的值
     * @return
     */
    public static PullInfos getRewardInfos(int relatedAdid) {
        if (relatedAdid < sInfoes.length) {
            return sInfoes[relatedAdid].get(relatedAdid + BASE + "", 1, false);
        }
        return null;
    }

    public static PullInfos getNativeInfos(int relatedAdid, int num) {
        if (relatedAdid < sInfoes.length) {
            return sInfoes[relatedAdid].get(relatedAdid + BASE + "",num,  true);
        }
        return null;
    }

    public static PullInfos getInterstitialInfos(int relatedAdid) {
        if (relatedAdid < sInfoes.length) {
            return sInfoes[relatedAdid].get(relatedAdid + BASE + "",1,  false);
        }
        return null;
    }

    public static AdViewIdsHolder getDefaultAdIdsHolder() {
        return getAdIdsHolder(R.id.ad_title, R.id.ad_icon, R.id.ad_desc,
                R.id.ad_banner_container, R.id.ad_tag,
                R.id.ad_choice_container, R.id.ad_btn);
    }

    public static AdViewIdsHolder getAdIdsHolder(int titleId, int iconId, int desId, int contentId, int tagid, int choiceId, int ctaId) {
        AdViewIdsHolder holder = new AdViewIdsHolder();
        holder.setTitleId(titleId);
        holder.setCtaId(ctaId);
        holder.setIconId(iconId);
        holder.setDescriptionId(desId);
        holder.setContentId(contentId);
        holder.setTagId(tagid);
        holder.setChoiceId(choiceId);
        return holder;
    }

    private static class Info {
        private static final String DEBUG = "ca-app-pub-3940256099942544/5224354917";
        private static final String NATIVE_DEBUG = "ca-app-pub-3940256099942544/1044960115";
        private static final String INTERSTITIAL_DEBUG = "ca-app-pub-3940256099942544/8691691433";
        public Info(String admob, String facebook) {
            if (!BuildConfig.DEBUG) {
                this.admob = admob;
            }
            this.facebook = facebook;
        }

        public Info(String admob, String facebook, boolean isNative) {
            if (!BuildConfig.DEBUG) {
                this.admob = admob;
            } else {
                this.admob = isNative ? NATIVE_DEBUG : INTERSTITIAL_DEBUG;
            }
            this.facebook = facebook;
        }

        String admob = DEBUG;
        String facebook;


        public PullInfos get(String adId, int num, boolean hasMobo) {
            return AdRequestHelper.getPullInfos(adId, num,
                    AdRequestHelper.getAdInfos(admob, facebook, "", "", hasMobo ? adId: ""));
        }
    }
}
