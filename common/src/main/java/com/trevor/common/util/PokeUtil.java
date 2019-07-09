package com.trevor.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-05 22:21
 **/

public class PokeUtil {

    /**
     * 5个花色,5-黑桃，4-桃心，3-樱花，2-方片，1-星星
     */
    private final static List<String> poke5 = new ArrayList<>(2<<7);

    /**
     * 4个花色
     */
    private final static List<String> poke4 = new ArrayList<>(2<<6);

    static {
        int tmp;
        for (int i = 1; i <6 ; i++) {
            for (int j = 1; j <14 ; j++) {
                tmp = ((byte)i) << 4 | (byte)j;
                poke5.add(Integer.toHexString(tmp));
            }
        }
        for (int i = 1; i <5 ; i++) {
            for (int j = 1; j <14 ; j++) {
                tmp = ((byte)i) << 4 | (byte)j;
                poke4.add(Integer.toHexString(tmp));
            }
        }

    }

    public static void main(String[] a) {
        System.out.println(generatePoke5());
    }

    /**
     * 生成一副随机牌，5个花色
     * @return list
     */
    public static List<String> generatePoke5(){
        List<String> tmpList = new ArrayList<>(2<<7);
        tmpList.addAll(poke5);
        Collections.shuffle(tmpList);
        Collections.shuffle(tmpList);
        Collections.shuffle(tmpList);
        return tmpList;
    }

    /**
     * 生成一副随机牌，4个花色
     * @return list
     */
    public static List<String> generatePoke4(){
        List<String> tmpList = new ArrayList<>(2<<6);
        tmpList.addAll(poke4);
        Collections.shuffle(tmpList);
        return tmpList;
    }

}
