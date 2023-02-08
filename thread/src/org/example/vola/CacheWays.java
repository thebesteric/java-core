package org.example.vola;

import fr.ujm.tse.lt2c.satin.cache.size.CacheInfo;
import fr.ujm.tse.lt2c.satin.cache.size.CacheLevel;
import fr.ujm.tse.lt2c.satin.cache.size.CacheLevelInfo;
import fr.ujm.tse.lt2c.satin.cache.size.CacheType;

public class CacheWays {
    public static void main(String[] args) {
        CacheInfo instance = CacheInfo.getInstance();
        CacheLevelInfo l1 = instance.getCacheInformation(CacheLevel.L1, CacheType.DATA_CACHE);
        System.out.println("一级缓存 sets: " + l1.getCacheSets());
        System.out.println("一级缓存 ways: " + l1.getCacheWaysOfAssociativity());

        CacheLevelInfo l2 = instance.getCacheInformation(CacheLevel.L1, CacheType.DATA_CACHE);
        System.out.println("二级缓存 sets: " + l2.getCacheSets());
        System.out.println("二级缓存 ways: " + l2.getCacheWaysOfAssociativity());

        CacheLevelInfo l3 = instance.getCacheInformation(CacheLevel.L1, CacheType.DATA_CACHE);
        System.out.println("三级缓存 sets: " + l3.getCacheSets());
        System.out.println("三级缓存 ways: " + l3.getCacheWaysOfAssociativity());
    }
}
