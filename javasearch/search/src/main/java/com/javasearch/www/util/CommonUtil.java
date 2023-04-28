package com.javasearch.www.util;

import com.javasearch.www.pojo.Search.IdContextWordCountVO;

import java.util.ArrayList;

public class CommonUtil {

    public static void main(String[] args) {

    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }


    //雪花算法
    static public class SnowFlake {
        /**
         * 起始的时间戳:这个时间戳自己随意获取，比如自己代码的时间戳
         */
        private final static long START_STAMP = 1677857428729L;
        private static final long datacenterId = 1L;  //数据中心
        private static long sequence = 1L; //序列号

        private static long lastStamp = -1L;//上一次时间戳

        /**
         * 每一部分占用的位数
         */
        private static final long SEQUENCE_BIT = 12; //序列号占用的位数
        private static final long MACHINE_BIT = 5;  //方法类型占用的位数
        private static final long DATACENTER_BIT = 5;//数据中心占用的位数


        /**
         * 每一部分的最大值：先进行左移运算，再同-1进行异或运算；异或：相同位置相同结果为0，不同结果为1
         */

        /**
         * 用位运算计算出最大支持的数据中心数量：31  (应该用不到)
         */
        private static final long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);

        /**
         * 用位运算计算出最大支持的机器数量：31    (应该用不到)
         */
        private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);

        /**
         * 用位运算计算出12位能存储的最大正整数：4095
         */
        private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

        /**
         * 每一部分向左的位移
         */

        /**
         * 机器标志较序列号的偏移量
         */
        private static final long MACHINE_LEFT = SEQUENCE_BIT;

        /**
         * 数据中心较机器标志的偏移量
         */
        private static final long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;

        /**
         * 时间戳较数据中心的偏移量
         */
        private static final long TIMESTAMP_LEFT = SEQUENCE_BIT + MACHINE_BIT + DATACENTER_BIT;

        /**
         * 此处无参构造私有，同时没有给出有参构造，在于避免以下两点问题：
         * 1、私有化避免了通过new的方式进行调用，主要是解决了在for循环中通过new的方式调用产生的id不一定唯一问题问题，因为用于记录上一次时间戳的lastStmp永远无法得到比对；
         * 2、没有给出有参构造在第一点的基础上考虑了一套分布式系统产生的唯一序列号应该是基于相同的参数(意思是单例对象)
         */
        private SnowFlake() {
        }

        /**
         * 产生下一个ID
         */
        public static synchronized long getSnowFlakeId(Long type) {
            //方法类型    1——文章ID       2——正排索引       3——倒排索引        位运算  >> 12 & 31

            /** 获取当前时间戳 */
            long currStamp = getNewStamp();

            /** 如果当前时间戳小于上次时间戳则抛出异常 */
            if (currStamp < lastStamp) {
                System.err.printf("clock is moving backwards.  Rejecting requests until %d.", lastStamp);
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                        lastStamp - currStamp));
            }
            /** 相同毫秒内 */
            if (currStamp == lastStamp) {
                //相同毫秒内，序列号自增
                //保证不超过最大值
                sequence = (sequence + 1) & MAX_SEQUENCE;
                //同一毫秒的序列数已经达到最大,则重新获取时间戳
                if (sequence == 0L) {
                    /** 获取下一时间的时间戳并赋值给当前时间戳 */
                    currStamp = getNextMill();
                }
            } else {
                //不同毫秒内，序列号置为0
                sequence = 0L;
            }
            /** 当前时间戳存档记录，用于下次产生id时对比是否为相同时间戳 */
            lastStamp = currStamp;


            return (currStamp - START_STAMP) << TIMESTAMP_LEFT //时间戳部分
                    | datacenterId << DATACENTER_LEFT      //数据中心部分
                    | type << MACHINE_LEFT            //方法类型
                    | sequence;                            //序列号部分
        }


        private static long getNextMill() {
            long newStamp = getNewStamp();
            while (newStamp <= lastStamp) {
                newStamp = getNewStamp();
            }
            return newStamp;
        }

        private static long getNewStamp() {
            return System.currentTimeMillis();
        }

    }

    //kmp算法进行强匹配
    public static int kmp(String source, String target) {
        if (source == null || target == null) {
            return -1;
        } else if (source.length() < target.length()) {
            return -1;
        }
        int[] next = getNextArray(target.toCharArray());
        int run1 = 0, run2 = 0;
        int l1 = source.length(), l2 = target.length();
        while (run1 < l1 && run2 < l2) {
            if (source.charAt(run1) == target.charAt(run2)) {
                run1++;
                run2++;
            } else if (next[run2] == -1) {
                run1++;
            } else {
                run2 = next[run2];
            }
        }
        return run2 == l2 ? run1 - run2 : -1;
    }

    private static int[] getNextArray(char[] strs) {
        if (strs.length == 1) {
            return new int[]{-1};
        }
        int[] next = new int[strs.length];
        next[0] = -1;
        next[1] = 0;
        int i = 2;
        int cn = 0;
        while (i < next.length) {
            if (strs[i - 1] == strs[cn]) {
                next[i++] = ++cn;
            } else if (cn > 0) {
                cn = next[cn];
            } else {
                next[i++] = 0;
            }
        }
        return next; 
    }

    /**
     * 快速排序
     */
    public static void quickSort(ArrayList<IdContextWordCountVO> list, int startIndex, int endIndex) {
        if (startIndex < endIndex) {
            //找出基准
            int partition = partition(list, startIndex, endIndex);
            //分成两边递归进行
            quickSort(list, startIndex, partition - 1);
            quickSort(list, partition + 1, endIndex);
        }
    }

    //找基准
    private static int partition(ArrayList<IdContextWordCountVO> list, int startIndex, int endIndex) {
        int pivot = list.get(startIndex).getWordCount();
        int left = startIndex;
        int right = endIndex;
        while (left != right) {
            while (left < right && list.get(right).getWordCount() > pivot) {
                right--;
            }
            while (left < right && list.get(left).getWordCount() <= pivot) {
                left++;
            }
            //找到left比基准大，right比基准小，进行交换
            if (left < right) {
                swap(list, left, right);
            }
        }
        //第一轮完成，让left和right重合的位置和基准交换，返回基准的位置
        swap(list, startIndex, left);
        return left;
    }

    //两数交换
    private static void swap(ArrayList<IdContextWordCountVO> list, int i, int j) {
        int temp = list.get(i).wordCount;
        list.get(i).wordCount = list.get(j).wordCount;
        list.get(j).wordCount = temp;
    }
}
