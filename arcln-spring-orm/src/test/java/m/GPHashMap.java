package m;

import oracle.sql.OracleJdbc2SQLInput;

import java.util.HashMap;
import java.util.Map;

public class GPHashMap<K,V> implements GPMAP<K,V> {
    //默认的初始化桶的数量,必须为2的
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    //桶的最大数量
    static final int MAXIMUM_CAPACITY = 1 << 30;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    //实际存储的key-value对个数
    transient int size;
    //阀值，当table={}时,threshold一般为默认的初始化桶的数量，如果table分配数据后，threshold为  capacity*loadFactory
    int threshold;

    //加载因子，一般为默认值 0.75
    final float loadFactor;
    transient int modCount;
    static final int ALTERNATIVE_HASHING_THRESHOLD_DEFAULT = Integer.MAX_VALUE;

    static final Entry<?,?>[] EMPTY_TABLE = {};
    transient Entry<K,V>[] table = (Entry<K, V>[]) EMPTY_TABLE;
    transient int hashSeed = 0;

    public GPHashMap(){
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }
    public GPHashMap(int initialCapacity, float loadFactor){
        if(initialCapacity < 0)
            throw new IllegalArgumentException("illegal initial capacity:" + initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;

        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                    loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = initialCapacity;
        init();
    }

    private void init() {}

    static class Entry<K,V>{
        final K key;
        V value;
        Entry<K,V> next;
        int hash;

        /**
         *
         * @param hash
         * @param key
         * @param value
         * @param next
         */
        public Entry(int hash,K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
            this.hash = hash;
        }
        public final K getKey() {
            return key;
        }
        public final V getValue() {
            return value;
        }
        public final V setValue(V newValue){
            V oldValue = value;
            this.value = newValue;
            return oldValue;
        }
        void recordAccess(HashMap<K,V> m) {
        }
    }

    public V put(K key, V value) {
        //桶为空时，构建桶
        if(table == EMPTY_TABLE){
            inflateTable(threshold);
        }
        //允许桶中存在一个为null的key
        if(key == null){
           return putForNullKey(value);
        }
        int hash = hash(key);
        int i = indexFor(hash, table.length);

        //遍历桶的一个链表位置,看 key是否存在，存在的话，用新值覆盖旧值，并返回旧值
        for (Entry<K,V> e = table[i]; e != null; e = e.next){
            Object k;
            //比较key的hash值相等，并且 引用相等 或者值内容相等
            if (e.hash == hash && (( k = e.key) == key || key.equals(k))){
                V oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }

        modCount ++;
        //这个就是map真正添加key-value的方法呀
        //会需要哪些参数呢 1.key 2.value,3,hash
        addEntry(hash,key,value,i);
        return null;
    }

    /**
     * 新增一个Entry
     * @param hash
     * @param key
     * @param value
     * @param bucketIndex
     */
    private void addEntry(int hash, K key, V value, int bucketIndex) {
        //如果桶长度超过了阀值，则把桶的长度扩大2倍，并且把之前的旧的内容全部copy到新的桶中
        if(size >= threshold && table[bucketIndex] != null){
            resize(2*table.length);
        }
        createEntry(hash, key, value, bucketIndex);
    }

    private void createEntry(int hash, K key, V value, int bucketIndex) {
        Entry<K,V> e =  table[bucketIndex]; //有可能为空，当前桶的下标位置取出来
        //这个地方就是，把新生成的元素话在链头的位置
        table[bucketIndex] = new Entry<K, V>(hash,key,value,e);
        size ++;
    }
    //当size > threshold阀值的时候，桶的长度翻倍
    void resize(int newCapacity) {

    }

    private V putForNullKey(V value) {
        return null;
    }

    /**
     * 为MAP中的主干数组分配内存空间
     * @param toSize
     */
    private void inflateTable(int toSize) {
        int capacity = roundUpToPowerOf2(toSize);//capacity一定是2的次幂
        //设置阀值，阀值一定不会超过 MAXIMUM_CAPACITY
        threshold = (int)Math.min(capacity * loadFactor,MAXIMUM_CAPACITY + 1);
        //创建桶
        table = new Entry[capacity];
        initHashSeedAsNeeded(capacity);

    }
    private boolean initHashSeedAsNeeded(int capacity) {
        boolean currentAltHashing = hashSeed != 0;
        boolean useAltHashing = sun.misc.VM.isBooted() &&
                (capacity >= Holder.ALTERNATIVE_HASHING_THRESHOLD);
        boolean switching = currentAltHashing ^ useAltHashing;
        if (switching) {
            hashSeed = useAltHashing
                    ? sun.misc.Hashing.randomHashSeed(this)
                    : 0;
        }
        return switching;
    }

    private static int roundUpToPowerOf2(int number) {
        // assert number >= 0 : "number must be non-negative";
        return number >= MAXIMUM_CAPACITY
                ? MAXIMUM_CAPACITY
                : (number > 1) ? Integer.highestOneBit((number - 1) << 1) : 1;
    }


    final int hash(Object k) {
        int h = hashSeed;
        if (0 != h && k instanceof String) {
            return sun.misc.Hashing.stringHash32((String) k);
        }

        h ^= k.hashCode();

        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    /**
     * 这个地方也限制了length必须为 2的n次幂，不然 &的时候 不能得到正确的值
     * @param h
     * @param length
     * @return  返回桶的下标
     */
    static int indexFor(int h, int length) {
        return h & (length-1);
    }

    public static void main(String[] args) {

       GPHashMap<Integer,Integer> map = new GPHashMap<Integer,Integer>();
        for (int i = 0;i< 100; i++){
            if(i==12){
                int a = 0;
            }
            map.put(i,i);

        }

        int b = 9;


        /*System.out.println((1<<2)+"\t"+Integer.toBinaryString((1<<2)));
        System.out.println((1<<3)+"\t"+Integer.toBinaryString((1<<3)));
        System.out.println((1<<4)+"\t"+Integer.toBinaryString((1<<4)));*/

/*        Map<String,Integer> map3 = new HashMap<String,Integer>();
        map3.put("a",1);*/
    }



    private static class Holder {

        /**
         * Table capacity above which to switch to use alternative hashing.
         */
        static final int ALTERNATIVE_HASHING_THRESHOLD;

        static {
            String altThreshold = java.security.AccessController.doPrivileged(
                    new sun.security.action.GetPropertyAction(
                            "jdk.map.althashing.threshold"));

            int threshold;
            try {
                threshold = (null != altThreshold)
                        ? Integer.parseInt(altThreshold)
                        : ALTERNATIVE_HASHING_THRESHOLD_DEFAULT;

                // disable alternative hashing if -1
                if (threshold == -1) {
                    threshold = Integer.MAX_VALUE;
                }

                if (threshold < 0) {
                    throw new IllegalArgumentException("value must be positive integer.");
                }
            } catch(IllegalArgumentException failed) {
                throw new Error("Illegal value for 'jdk.map.althashing.threshold'", failed);
            }

            ALTERNATIVE_HASHING_THRESHOLD = threshold;
        }
    }
}
