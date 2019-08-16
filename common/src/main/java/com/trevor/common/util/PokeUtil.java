package com.trevor.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trevor.common.bo.PaiXing;
import com.trevor.common.enums.NiuNiuPaiXingEnum;

import java.util.*;
import java.util.stream.Collectors;

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

//    public static void main(String[] a) {
//        System.out.println(generatePoke5());
//    }

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

    /**
     * 判断玩家的是否为牛
     *
     *      * 1---顺子牛，5倍
     *      * 2---五花牛，6倍
     *      * 3---同花牛，6倍
     *      * 4---葫芦牛，7倍
     *      * 5---炸弹牛，8倍
     *      * 6---五小牛，10倍
     *
     *      * 规则
     *      * 1---牛牛x3，牛九x2，牛八x2
     *      * 2---牛牛x4，牛九x3，牛八x2，牛7x2
     *
     * @param pokes
     * @return
     */
    public static PaiXing isNiuNiu(List<String> pokes , List<Integer> paiXingSet , Integer rule){
        PaiXing paiXing;
        if (paiXingSet == null) {
            paiXingSet = new ArrayList<>();
        }
        //是否是五小牛
        paiXing = isNiu_16(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        //是否是炸弹牛
        paiXing = isNiu_15(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        //是否是葫芦牛
        paiXing = isNiu_14(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        //是否是同花牛
        paiXing = isNiu_13(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        //是否是五花牛
        paiXing = isNiu_12(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        //是否是顺子牛
        paiXing = isNiu_11(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        int ii = 0;
        int jj = 0;
        int kk = 0;
        boolean isNiu = Boolean.FALSE;
        Integer pokeSize = pokes.size();
        for (int i = 0; i < pokeSize; i++) {
            if (i >= 3) {
                break;
            }
            for (int j = i+1; j < pokeSize; j++) {
                for (int k = j+1; k < pokeSize; k++) {
                    int num = changePai_10(pokes.get(i).substring(1,2)) +
                            changePai_10(pokes.get(j).substring(1 ,2)) +
                            changePai_10(pokes.get(k).substring(1 ,2));
                    if (num == 10 || num == 20 || num == 30) {
                        ii = i;
                        jj = j;
                        kk = k;
                        isNiu = Boolean.TRUE;
                        break;
                    }
                }
                if (isNiu) {
                    break;
                }
            }
            if (isNiu) {
                break;
            }
        }
        paiXing = new PaiXing();
        //没牛
        if (!isNiu) {
            paiXing.setMultiple(1);
            paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_0.getPaiXingCode());
            return paiXing;
        }else {
            int num = 0;
            for (int i = 0; i < pokes.size(); i++) {
                if (i != ii && i != jj && i != kk) {
                    num += changePai_10(pokes.get(i).substring(1 ,2));
                }
            }
            // 1 - 牛牛x3，牛九x2，牛八x2
            if (Objects.equals(rule ,1)) {
                if (num == 10 || num == 20) {
                    paiXing.setMultiple(3);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_10.getPaiXingCode());
                    return paiXing;
                }else if (num == 9 || num == 19) {
                    paiXing.setMultiple(2);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_9.getPaiXingCode());
                    return paiXing;
                }else if (num == 8 || num == 18) {
                    paiXing.setMultiple(2);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_8.getPaiXingCode());
                    return paiXing;
                }else {
                    paiXing.setMultiple(1);
                    if (num <= 10) {
                        paiXing.setPaixing(num);
                    }else {
                        paiXing.setPaixing(num-10);
                    }
                    return paiXing;
                }
                //2---牛牛x4，牛九x3，牛八x2，牛7x2
            }else {
                if (num == 10 || num == 20) {
                    paiXing.setMultiple(4);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_10.getPaiXingCode());
                    return paiXing;
                }else if (num == 9 || num == 19) {
                    paiXing.setMultiple(3);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_9.getPaiXingCode());
                    return paiXing;
                }else if (num == 8 || num == 18) {
                    paiXing.setMultiple(2);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_8.getPaiXingCode());
                    return paiXing;
                } else if (num == 7 || num == 17) {
                    paiXing.setMultiple(2);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_7.getPaiXingCode());
                    return paiXing;
                }else {
                    paiXing.setMultiple(1);
                    if (num <= 10) {
                        paiXing.setPaixing(num);
                    }else{
                        paiXing.setPaixing(num-10);
                    }
                    return paiXing;
                }
            }

        }
    }

    /**
     * 是否是五小牛 10 倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    public static PaiXing isNiu_16(List<String> pokes , List<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(6)) {
            int num = 0;
            boolean glt_5 = true;
            for (String str : pokes) {
                String pai = str.substring(1 ,2);
                num += changePai(pai);
                if (changePai(pai) < 5) {
                    glt_5 = false;
                    break;
                }
            }
            if (num <= 10 && glt_5) {
                paiXing = new PaiXing();
                paiXing.setMultiple(10);
                paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_16.getPaiXingCode());
                return paiXing;
            }
        }
        return null;
    }

    /**
     * 筹10
     * @param pai
     * @return
     */
    public static Integer changePai_10(String pai){
        if (Objects.equals("a" ,pai)) {
            return 10;
        }else if (Objects.equals("b" ,pai)) {
            return 10;
        }else if (Objects.equals("c" ,pai)) {
            return 10;
        }else if (Objects.equals("d" ,pai)) {
            return 10;
        }else {
            return Integer.valueOf(pai);
        }
    }

    /**
     * 比较两个同花牛大小
     * @param zhuangJiaPokes
     * @param xianJiaPokes
     * @return zhuangJiaPokes > xianJiaPokes返回true
     */
    public static Boolean niu_13_daXiao(List<String> zhuangJiaPokes ,List<String> xianJiaPokes){
        if (Integer.valueOf(zhuangJiaPokes.get(0).substring(0,1)) > Integer.valueOf(xianJiaPokes.get(0).substring(0,1))) {
            return true;
        }else if (Objects.equals(Integer.valueOf(zhuangJiaPokes.get(0).substring(0,1)) ,Integer.valueOf(xianJiaPokes.get(0).substring(0,1))) ) {
            Integer paiZhi = biPaiZhi(zhuangJiaPokes ,xianJiaPokes);
            if (Objects.equals(paiZhi ,1)) {
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    /**
     * 是否是炸弹牛 8倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    public static PaiXing isNiu_15(List<String> pokes , List<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(5)) {
            int num = 0;
            String pai = pokes.get(0).substring(1,2);
            for (String str : pokes) {
                if (Objects.equals(pai ,str.substring(1 ,2))) {
                    num ++;
                }
            }
            if (num == 0 || num == 4 || num == 1 || num == 5) {
                paiXing = new PaiXing();
                paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_15.getPaiXingCode());
                paiXing.setMultiple(8);
                return paiXing;
            }
        }
        return null;
    }

    /**
     * 比较两个葫芦牛大小
     * @param zhuangJiaPokes
     * @param xianJiaPokes
     * @return zhuangJiaPokes > xianJiaPokes返回true
     */
    public static Boolean niu_14_daXiao(List<String> zhuangJiaPokes ,List<String> xianJiaPokes){
        Integer zhuangJiaNum = getDianShuNumberMap(zhuangJiaPokes ,3);
        Integer xianJiaNum = getDianShuNumberMap(xianJiaPokes ,3);
        if (zhuangJiaNum > xianJiaNum) {
            return true;
        }
        return false;
    }

    public static Integer getDianShuNumberMap(List<String> pokes ,Integer ciShu){
        Map<String ,Integer> map = Maps.newHashMap();
        for (String poke : pokes) {
            String dianShu = poke.substring(1 ,2);
            if (!map.keySet().contains(dianShu)) {
                map.put(dianShu ,1);
            }else {
                map.put(dianShu ,map.get(dianShu) + 1);
            }
        }
        for (Map.Entry<String ,Integer> entry : map.entrySet()) {
            if (Objects.equals(ciShu ,entry.getValue())) {
                return changePai(entry.getKey());
            }
        }
        throw new RuntimeException("出现炸弹牛或葫芦牛，但是牌不对");
    }

    /**
     * 是否是五花牛 6倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    public static PaiXing isNiu_12(List<String> pokes , List<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(2)) {
            boolean j_q_k = true;
            List<String> pais = new ArrayList<>();
            pais.add("b");
            pais.add("c");
            pais.add("d");
            for (String str : pokes) {
                if (!pais.contains(str.substring(1 ,2))) {
                    j_q_k = false;
                    break;
                }
            }
            if (j_q_k) {
                paiXing = new PaiXing();
                paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_12.getPaiXingCode());
                paiXing.setMultiple(6);
                return paiXing;
            }
        }
        return null;
    }

    /**
     * 是否是葫芦牛 7倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    public static PaiXing isNiu_14(List<String> pokes , List<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(4)) {
            Set<String> set = new HashSet<>();
            for (String str : pokes) {
                set.add(str.substring(1 ,2));
            }
            if (set.size() <=2) {
                int num = 0;
                String pai = pokes.get(0).substring(1,2);
                for (String str : pokes) {
                    if (Objects.equals(pai ,str.substring(1 ,2))) {
                        num ++;
                    }
                }
                if (num == 2 || num == 3) {
                    paiXing = new PaiXing();
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_14.getPaiXingCode());
                    paiXing.setMultiple(7);
                    return paiXing;
                }
            }
        }
        return null;
    }

    /**
     * 是否是同花牛 6倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    public static PaiXing isNiu_13(List<String> pokes , List<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(3)) {
            Set<String> set = new HashSet<>();
            for (String str : pokes) {
                set.add(str.substring(0,1));
            }
            if (set.size() == 1) {
                paiXing = new PaiXing();
                paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_13.getPaiXingCode());
                paiXing.setMultiple(6);
                return paiXing;
            }
        }
        return null;
    }

    /**
     * 是否是顺子牛 ，5倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    public static PaiXing isNiu_11(List<String> pokes , List<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(1)) {
            List<Integer> paiList = Lists.newArrayList();
            Set<Integer> paiSet = new HashSet<>();
            for (String str : pokes) {
                paiList.add(changePai(str.substring(1 ,2)));
                paiSet.add(changePai(str.substring(1 ,2)));
            }
            paiList.sort(Comparator.reverseOrder());
            if (paiList.get(0) - paiList.get(4) == 4 && paiSet.size() == 5) {
                paiXing = new PaiXing();
                paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_11.getPaiXingCode());
                paiXing.setMultiple(5);
                return paiXing;
            }
        }
        return null;
    }

    /**
     * 比较两个炸弹牛大小
     * @param zhuangJiaPokes
     * @param xianJiaPokes
     * @return zhuangJiaPokes > xianJiaPokes返回true
     */
    public static Boolean niu_15_daXiao(List<String> zhuangJiaPokes ,List<String> xianJiaPokes){
        Integer zhuangJiaNum = getDianShuNumberMap(zhuangJiaPokes ,4);
        Integer xianJiaNum = getDianShuNumberMap(xianJiaPokes ,4);

        if (zhuangJiaNum > xianJiaNum) {
            return true;
        }
        return false;
    }

    /**
     * 比牌值大小
     * @param zhuangJiaPokes
     * @param xianJiaPokes
     * @return zhuangJiaPokes > xianJiaPokes返回1 ,zhuangJiaPokes < xianJiaPokes返回-1,zhuangJiaPokes == xianJiaPokes返回0
     */
    public static Integer biPaiZhi(List<String> zhuangJiaPokes ,List<String> xianJiaPokes){
        List<Integer> zhuangJiaNums = zhuangJiaPokes.stream().map(str -> changePai(str.substring(1 ,2))
        ).collect(Collectors.toList());
        List<Integer> xianJiaNums = xianJiaPokes.stream().map(str -> changePai(str.substring(1 ,2))
        ).collect(Collectors.toList());
        zhuangJiaNums.sort(Comparator.reverseOrder());
        xianJiaNums.sort(Comparator.reverseOrder());
        for (int j = 0; j < xianJiaNums.size(); j++) {
            if (zhuangJiaNums.get(j) > xianJiaNums.get(j)) {
                return 1;
            }else if (Objects.equals(zhuangJiaNums.get(j) ,xianJiaNums.get(j))) {
                continue;
            }else {
                return -1;
            }
        }
        return 0;
    }

    /**
     * 比大小
     * @param pai
     * @return
     */
    public static Integer changePai(String pai){
        if (Objects.equals("a" ,pai)) {
            return 10;
        }else if (Objects.equals("b" ,pai)) {
            return 11;
        }else if (Objects.equals("c" ,pai)) {
            return 12;
        }else if (Objects.equals("d" ,pai)) {
            return 13;
        }else {
            return Integer.valueOf(pai);
        }
    }

}
