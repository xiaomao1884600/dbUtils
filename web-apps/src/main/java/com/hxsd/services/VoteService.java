package com.hxsd.services;


import com.doubeye.commons.utils.request.RequestHelper;
import com.hxsd.vote.Vote;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Map;

import static com.doubeye.commons.router.GeneralRouter.REQUEST_PARAMETER_REMOTE_HOST;


/**
 * Created by doubeye(doubeye@sina.com) on 2017/1/3.
 */
public class VoteService {
    {
        if (Vote.getOptions().size() == 0) {
            Vote.init();
        }
    }

    public JSONArray getAllOptions(Map<String, String[]> parameters) {
        return JSONArray.fromObject(Vote.getOptions());
    }

    public JSONObject vote(Map<String, String[]> parameters) {
        int id = RequestHelper.getInt(parameters, "id");
        String ip = RequestHelper.getString(parameters, REQUEST_PARAMETER_REMOTE_HOST);
        if (Vote.getVotedIps().contains(ip)) {
            throw new RuntimeException("你已经投过票了");
        }
        Vote.vote(id);
        Vote.getVotedIps().add(ip);
        JSONObject obj = new JSONObject();
        obj.put("SUCCESS", true);
        return obj;
    }

    public JSONObject addVoteOption(Map<String, String[]> parameters) {
        String optionName = RequestHelper.getString(parameters, "optionName");
        Vote.addOption(optionName);
        JSONObject obj = new JSONObject();
        obj.put("SUCCESS", true);
        return obj;
    }
}
