package com.arcln.myrpc.zk.loadbanace;

import java.util.List;
import java.util.Random;

public class RandomLoadBanance extends AbstractLoadBanance{

    @Override
    protected String doSelect(List<String> repos) {
        Random a = new Random();
        int i = a.nextInt(repos.size());
        return repos.get(i);
    }
}
