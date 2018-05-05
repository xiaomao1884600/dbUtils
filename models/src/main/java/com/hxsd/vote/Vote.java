package com.hxsd.vote;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/1/3.
 */
public class Vote {

    private static List<VoteBean> options = new ArrayList<>();
    private static List<String> votedIps = new ArrayList<>();

    public static void init() {
        /*
        options.add(new VoteBean(1, "新年许心愿，嗮收获赢大奖"));
        options.add(new VoteBean(2, "青春靓丽秀自我，风采展示赢奖品"));
        options.add(new VoteBean(3, "2016,我的收获与骄傲"));
        options.add(new VoteBean(4, "2016火星足迹与成就展示"));
        options.add(new VoteBean(5, "火星作品show，社群抢豪礼"));
        options.add(new VoteBean(6, "新年许心愿，嗮收获赢大奖"));
        options.add(new VoteBean(7, "晒出你的新年怎么过，寻找小时候年的记忆"));
        options.add(new VoteBean(8, "最美的你，如7而至"));
        options.add(new VoteBean(9, "一路（16）有你，一起（17）前行"));
        options.add(new VoteBean(10, "秀自我，许心愿，赢大奖"));
        */

        options.add(new VoteBean(1, "小Z"));
        options.add(new VoteBean(2, "百晓生"));
        options.add(new VoteBean(3, "百事通"));
        options.add(new VoteBean(4, "机器人小Z"));
        options.add(new VoteBean(5, "火星小Z哥"));
        options.add(new VoteBean(6, "火星小Z"));

        options.add(new VoteBean(7, "Mr Zone"));
        options.add(new VoteBean(8, "Miss Zone"));
        options.add(new VoteBean(9, "火星情报员"));



        votedIps.clear();
    }

    public static List<VoteBean> getOptions() {
        return options;
    }
    public static List<String> getVotedIps() { return votedIps; };
    public static void addOption(String optionName) {
        VoteBean option = new VoteBean(options.size() + 1, optionName);
        options.add(option);
    }

    public static void vote(int id) {
        options.get(id - 1).vote();
    }
}

