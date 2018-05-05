package com.hxsd.vote;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/1/3.
 */
public class VoteBean {
    private int id;
    private String message;
    private int votingCount = 0;
    public VoteBean(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getVotingCount() {
        return votingCount;
    }

    public void setVotingCount(int count) {
        this.votingCount = count;
    }

    public void vote() {
        votingCount ++;
    }
}
