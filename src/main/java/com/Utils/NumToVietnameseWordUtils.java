package com.Utils;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class NumToVietnameseWordUtils {
    private static final List<String> digitsName = Arrays.asList(
            "không",
            "một",
            "hai",
            "ba",
            "bốn",
            "năm",
            "sáu",
            "bảy",
            "tám",
            "chín"
    );

    private static final List<String> thousandsName = Arrays.asList(
            "",
            "nghìn",
            "triệu",
            "tỷ",
            "nghìn tỷ",
            "triệu tỷ",
            "tỷ tỷ"
    );

    // Algorithm section

    /**
     * turn triplet digits into vietnamese words
     *
     * @param triplet         a string of 3 digit integer
     * @param showZeroHundred whether to show Zero hundred
     * @return vietnamese string represent the input number
     */
    private static String readTriple(String triplet, boolean showZeroHundred) {
        List<Integer> digitList = stringToInt(triplet);

        int a = digitList.get(0);
        int b = digitList.get(1);
        int c = digitList.get(2);

        if (a == 0) {
            if (b == 0 && c == 0) {
                return "";
            }

            if (showZeroHundred) {
                return "không trăm " + readPair(b, c);
            }

            if (b == 0) {
                return digitsName.get(c);
            } else {
                return readPair(b, c);
            }

        }

        return digitsName.get(a) + " trăm " + readPair(b, c);
    }


    private static String readPair(int b, int c) {
        String temp;

        switch (b) {
            case 0:
                return c == 0 ? "" : "lẻ " + digitsName.get(c);
            case 1:
                switch (c) {
                    case 0:
                        temp = " ";
                        break;
                    case 5:
                        temp = "lăm ";
                        break;
                    default:
                        temp = digitsName.get(c);
                        break;
                }

                return "mười " + temp;
            default:
                switch (c) {
                    case 0:
                        temp = "";
                        break;
                    case 1:
                        temp = "mốt ";
                        break;
                    case 4:
                        temp = "tư ";
                        break;
                    case 5:
                        temp = "lăm ";
                        break;
                    default:
                        temp = digitsName.get(c);
                        break;
                }

                return digitsName.get(b) + " mươi " + temp;
        }
    }

    private static List<Integer> stringToInt(String triplet) {
        return triplet.chars()
                .map(NumToVietnameseWordUtils::charToInt)
                .boxed()
                .collect(Collectors.toList());
    }

    private static int charToInt(int c) {
        return c - '0';
    }

    public static String convertToCommas(String input) {
    	if(ObjectUtils.isEmpty(input)) {
    		return "";
    	}
        double amount = Double.parseDouble(input);
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount).replaceAll(",", ".");
    }

    public static String num2String(Long num) {

        if (num == 0L) {
            return "Không";
        }

        if (num < 0L) {
            return "âm " + num2String(-num);
        }

        String str = num.toString();


        // zero padding in front of string to prepare for splitting
        switch (str.length() % 3) {
            case 1:
                str = "00" + str;
                break;
            case 2:
                str = "0" + str;
                break;
            default:
                break;
        }

        // Split into chunks of 3 digits each
        List<String> groupOfThousand = Arrays.asList(str.split("(?<=\\G.{3})"));

        boolean showZeroHundred = doShowZeroHundred(groupOfThousand);

        AtomicInteger index = new AtomicInteger();

        return capitalize(groupOfThousand.stream()
                .map(triplet -> readTriple(triplet, showZeroHundred && index.get() > 0))
                .map(vnString -> vnString.trim().isEmpty()
                        ? ""
                        : vnString + " " + thousandsName.get(groupOfThousand.size() - 1 - index.get())
                )
                .peek(s -> index.getAndIncrement())
                .collect(Collectors.joining(" "))
                .replaceAll("\\s+", " ")
                .trim());
    }

    public static String num2String1(Double num, String type) {
        String check = num.toString();
        String[] s = StringUtils.split(check, ".");
        if (s[1].equals("0")) {
            return num2String(num.longValue()) + " " + convertNameMoney(type);
        } else {
            Long start = Long.valueOf(s[0]);
            Long end = Long.valueOf(s[1]);
            if (s[1].length() == 1) {
                end *= 10;
            }
            String start1 = num2String(start.longValue()) + " " + convertNameMoney(type);
            String end1 = " và " + num2String(end.longValue()) + " cent";
            return start1 + end1.toLowerCase(Locale.ROOT);
        }
    }


    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * determine whether to show zero-hundred text
     * <p>
     * Explain: count the amount of consecutive "000" at the end of the number and
     * compare it with number length
     *
     * @param groupOfThousand number represented in group of 3 digits of each 1000^n
     * @return a boolean
     */
    private static boolean doShowZeroHundred(List<String> groupOfThousand) {
        int count = 0;
        int i = groupOfThousand.size() - 1;
        while (i >= 0 && groupOfThousand.get(i).equals("000")) {
            count++;
            i--;
        }

        return count < groupOfThousand.size() - 1;
    }
    public static String convertToCommasUSD(String num) {
    	if(ObjectUtils.isEmpty(num)) {
    		return "";
    	}
    	String [] buildArr = num.split("\\.");
    	if(buildArr.length > 1) {
    		return convertToCommas(buildArr[0]) +","+buildArr[1];
    	}else if(buildArr.length == 1){
    		return convertToCommas(buildArr[0]);
    	}else {
    		return "";
    	}
    }

    public static String num2StringEURO(String num, String cur) {
    	if(ObjectUtils.isEmpty(num)) {
    		return "";
    	}
    	String [] buildArr = num.split("\\.");
    	if("USD".equals(cur)) {
    		if(buildArr.length > 1) {
    			long count = buildArr[1].length();
    			if(count == 1) {
    				return num2String(Long.valueOf(buildArr[0]))+" đô la Mỹ và "+num2String(Long.valueOf(buildArr[1])*10).toLowerCase() +" cent";
    			}else if(count == 2){
    				return num2String(Long.valueOf(buildArr[0]))+" đô la Mỹ và "+num2String(Long.valueOf(buildArr[1])).toLowerCase() +" cent";
    			}else {
    				return "";
    			}
        		
        	}else if(buildArr.length == 1){
        		return num2String(Long.valueOf(buildArr[0]))+" đô la Mỹ";
        	}else {
        		return "";
        	}
    	}else {
    		if(buildArr.length > 1) {
    			long count = buildArr[1].length();
    			if(count == 1) {
    				return num2String(Long.valueOf(buildArr[0]))+" EURO và "+num2String(Long.valueOf(buildArr[1])*10).toLowerCase() +" cent";
    			}else if(count == 2){
    				return num2String(Long.valueOf(buildArr[0]))+" EURO và "+num2String(Long.valueOf(buildArr[1])).toLowerCase() +" cent";
    			}else {
    				return "";
    			}
        		
        	}else if(buildArr.length == 1){
        		return num2String(Long.valueOf(buildArr[0]))+" EURO";
        	}else {
        		return "";
        	}
    	}
    }

    private static String convertNameMoney(String name) {
        if (name.equals("USD")) return " đô la Mỹ";
        else if (name.equals("VND")) return " đồng";
        else return " euro";
    }
}
