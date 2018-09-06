package com.arcln.myrpc.zk.loadbanace;

import java.util.List;

public interface LoadBanalce {
    String selectHost(List<String> repos);
}
