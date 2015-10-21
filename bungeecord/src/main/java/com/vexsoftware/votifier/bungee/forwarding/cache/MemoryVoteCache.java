package com.vexsoftware.votifier.bungee.forwarding.cache;

import com.google.common.collect.ImmutableSet;
import com.vexsoftware.votifier.model.Vote;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Joe Hirschfeld on 10/20/2015.
 */
public class MemoryVoteCache implements VoteCache {

    public MemoryVoteCache(int initialSize){
        voteCache = new HashMap<>(initialSize);
    }

    private final Map<String, Collection<Vote>> voteCache;

    private final ReentrantLock cacheLock = new ReentrantLock();

    @Override
    public Collection<String> getCachedServers() {
        cacheLock.lock();
        Set<String> servers = ImmutableSet.copyOf(voteCache.keySet());
        cacheLock.unlock();
        return servers;
    }

    @Override
    public void addToCache(Vote v, String server) {
        cacheLock.lock();
        Collection<Vote> voteCollection = voteCache.get(server);
        if(voteCollection == null){
            voteCollection = new LinkedHashSet<>();
            voteCache.put(server,voteCollection);
        }
        voteCollection.add(v);
        cacheLock.unlock();
    }

    @Override
    public Collection<Vote> evict(String server) {
        cacheLock.lock();
        Collection<Vote> removedVotes = ImmutableSet.copyOf(voteCache.remove(server));
        cacheLock.unlock();
        return removedVotes;
    }

    @Override
    public boolean hasVotes(String server) {
        cacheLock.lock();
        boolean b = voteCache.containsKey(server);
        cacheLock.unlock();
        return b;
    }
}