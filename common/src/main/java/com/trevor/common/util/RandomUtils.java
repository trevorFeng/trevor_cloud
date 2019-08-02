package com.trevor.common.util;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;

import java.util.List;
import java.util.Random;

/**
 * @author trevor
 * @date 03/20/19 16:18
 */
public class RandomUtils {

    /**
     * 生成任意长度的字符串
     * @param length 字符串长度
     * @return
     */
    public static String getRandomChars(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i=0;i<length;i++){
            //获取一个随机数，范围：0——base.length
            int randomInt = random.nextInt(base.length());
            builder.append(base.charAt(randomInt));
        }
        return builder.toString();
    }

    /**
     * 生成随机6位数
     * @return
     */
    public static String getRandNum() {
        String randNum = new Random().nextInt(1000000)+"";
        //如果生成的不是6位数随机数则返回该方法继续生成
        if (randNum.length()!=6) {
            return getRandNum();
        }
        return randNum;
    }

    /**
     * 生成小于几的随机数
     * @return
     */
    public static Integer getRandNumMax(Integer maxNum){
        Random random = new Random();
        return random.nextInt(maxNum);
    }

    /**
     * 生成随机数，每5个放在一起
     * @param maxNum 随机数的范围[0 ,maxNum)
     * @param geShu 随机数的个数
     * @return
     */
    public static List<List<Integer>> getSplitListByMax(Integer maxNum ,Integer geShu){
        int[] array = randomArray(0 ,maxNum-1 ,geShu);
        List<Integer> nums = Ints.asList(array);
        List<List<Integer>> lists = Lists.newArrayList();
        List<Integer> list = Lists.newArrayList();
        for (int i = 0;i<nums.size();i++) {
            list.add(nums.get(i));
            if (list.size()==5) {
                lists.add(list);
                list = Lists.newArrayList();
            }
        }
        return lists;
    }



    /**
     * 随机指定范围内N个不重复的数
     * 在初始化的无重复待选数组中随机产生一个数放入结果中，
     * 将待选数组被随机到的数，用待选数组(len-1)下标对应的数替换
     * 然后从len-2里随机产生下一个随机数，如此类推
     * @param max  指定范围最大值
     * @param min  指定范围最小值
     * @param n  随机数个数
     * @return int[] 随机数结果集
     */
    public static int[] randomArray(int min,int max,int n){
        int len = max-min+1;

        if(max < min || n > len){
            return null;
        }

        //初始化给定范围的待选数组
        int[] source = new int[len];
        for (int i = min; i < min+len; i++){
            source[i-min] = i;
        }

        int[] result = new int[n];
        Random rd = new Random();
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            //待选数组0到(len-2)随机一个下标
            index = Math.abs(rd.nextInt() % len--);
            //将随机到的数放入结果集
            result[i] = source[index];
            //将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换
            source[index] = source[len];
        }
        return result;
    }

//    public static void main(String[] args) {
//        List<List<Integer>> integers = getSplitListByMax(60 ,25);
//        System.out.println(integers);
//    }
}
