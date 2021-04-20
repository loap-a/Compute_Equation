
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Math.*;

class ExpressionException extends Exception {

    int variable_type;
    boolean expression;
    boolean type_wrong = false;

    public ExpressionException(String message) {
        super(message);
        expression = false;
        if (message.equals("type-wrong"))
            type_wrong = true;
    }

    public ExpressionException(String message, String expression) {
        super(message);
        System.out.println("wrong - expression error " + expression);
    }

    public ExpressionException(String message, String key, int type) {
        super(message);
        switch (type) {
            case 0: {
                System.out.println("wrong - variable undefined and the variable name is '" + key + "'");
                variable_type = 0;
                break;
            }
            case 1: {
                System.out.println("wrong - variable assigned and the variable name is '" + key + "'");
                variable_type = 1;
                break;
            }
            default:
                break;
        }
    }

    public boolean get_undefined() {
        return variable_type == 0;
    }

    public boolean get_unassigned() {
        if (variable_type == 1)
            return true;
        else return false;
    }

    public number correct(HashMap<String, number> mp, String key) {
        if (variable_type == 1) {
            System.out.println("Do you want to assign it? (y/n)");
        } else
            System.out.println("Do you want to define it? (y/n) ");


        number temp = mp.get(key);
        boolean type_err = false;
        if (temp != null && temp.gettype().equals("int"))
            type_err = true;

        Scanner sc = new Scanner(System.in);
        String choice = sc.nextLine();
        if (choice.equals("y")) {
            System.out.println("you need to input the value of the variable ");
            String str = sc.nextLine();
            ArrayList<Integer> fl1 = new ArrayList<Integer>();
            fl1.clear();
            ArrayList<Float> fl2 = new ArrayList<Float>();
            fl2.clear();
            int flag1 = 0;
            int flag2 = 0;
            number mynum = null;
            if(type_err)mynum=mp.get(key);

            for(int l=0;l<str.length();l++)
            {
                try {
                    if (type_err && str.charAt(l) == '.')
                    throw new ExpressionException("type-wrong");
                } catch (ExpressionException e) {
                    if (e.correct_2(key)) {
                        mynum.settype("float");
                    }
                    else return null;
                }
            }
            for (int i = 0; i < str.length(); i++) {

                if (str.charAt(i) >= '0' && str.charAt(i) <= '9') {
                    int temp_i = i;
                    fl1.clear();
                    fl1.add(1);
                    float sum2 = 0;
                    boolean float_int = false;
                    if ((i < str.length()-1 && str.charAt(i + 1) == '.') || (i < str.length() - 1 && str.charAt(i + 1) <= '9' && str.charAt(i + 1) >= '0')) {
                        i++;
                        while ((i <= str.length() - 1 && str.charAt(i) >= '0' && str.charAt(i) <= '9')) {
                            flag1++;
                            fl1.add((int) Math.pow(10, flag1));
                            i++;
                        }
                        int temp_flag1 = flag1;
                        for (int l = temp_i; l <= temp_i + temp_flag1; l++) {
                            sum2 = sum2 + (str.charAt(l) - 48) * fl1.get(flag1);
                            flag1--;
                        }

                        if (i < str.length() && str.charAt(i) == '.') {
                            temp_i = i + 1;
                            i++;
                            fl2.clear();
                            flag2 = 0;
                            float_int = true;
                            while (i < str.length() && str.charAt(i) <= '9' && str.charAt(i) >= '0') {
                                flag2++;
                                fl2.add((float) Math.pow(0.1, flag2));
                                i++;
                            }
                            int temp_flag2 = 0;
                            for (int l = temp_i; l < temp_i + flag2; l++) {
                                sum2 = sum2 + (str.charAt(l) - 48) * fl2.get(temp_flag2);
                                temp_flag2++;
                            }

                        }
                        if (float_int) {
                            mynum = new number((float) sum2);
                        } else {
                            mynum = new number((int) sum2);
                        }
//                    System.out.println(sum2);
                        fl1.clear();
                        fl2.clear();
                        flag1 = 0;
                        flag2 = 0;
                    }
                }
                mp.replace(key, mynum);
                return mynum;
            }

        } else {
            System.out.println("The program has stopped ");
            return null;
        }
        return null;
    }

    public String correct(String message) {
        Scanner sc=new Scanner(System.in);
        System.out.println("There is something wrong with the expression "+message+" do you want to re-input it? (y/n)");
        String choice=sc.nextLine();
        if(choice.equals("y"))
        {
            System.out.println("Please input your expression ");
            String temp= sc.nextLine();
            return temp;
        }
        else
        {
            System.out.println("The program has stopped ");
        return "";
        }

    }
    public boolean correct_2(String variable)
    {
        if(type_wrong)
        {
            System.out.println("You are inputting a float value into a int type,variable's name= "+variable+ " ,Do you want to change its type to float? (y/n)");
            Scanner sc=new Scanner(System.in);
            String choice =sc.nextLine();
            if(choice.equals("y"))
            {
                return true;
            }
            else {
                System.out.println("The program has stopped ");
                return false;
            }
        }
        return false;
    }
}


public class Compute_Equation{
    public static void main(String[] args) {

        try{}
        catch(Exception e){}

        Logger l1=Logger.getLogger("expression");l1.log(Level.INFO,"Begin");
        Logger l2=Logger.getLogger("expression.error");l2.log(Level.INFO,"Begin");
        Handler hd=null;Handler hd2=null;
        try {
            hd =new FileHandler("B:\\expression%u.log",true);
            hd2=new FileHandler("B:\\expression1.log",true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        l1.addHandler(hd);l2.addHandler(hd2);
//
        boolean undefined = false;
        boolean unassigned = false;
        boolean errorexp=false;


        HashMap<String, number> mymap = new HashMap<String, number>();
        ArrayList<String> all_str = new ArrayList<String>();
        Scanner sc = new Scanner(System.in);

        ArrayList<Integer> fl1 = new ArrayList<Integer>();
        fl1.clear();
        ArrayList<Float> fl2 = new ArrayList<Float>();
        fl2.clear();
        int flag1 = 0;
        int flag2 = 0;

        while (true) {
            String temp = sc.nextLine();
            all_str.add(temp);
            if (temp.charAt(temp.length() - 1) == '?') {
                break;
            }
            if (temp.charAt(temp.length() - 1) == '=') {
                break;
            }
        }

        for (int i = 0; i < all_str.size() - 1; i++) {
            String str = all_str.get(i);

            String temp = all_str.get(i).substring(0, 3);

            if (temp.equals("int")) {
                String temp_str = "";
                for (int l = 4; (str.charAt(l) >= 'a' && str.charAt(l) <= 'z') || (str.charAt(l) >= 'A' && str.charAt(l) <= 'Z'); l++) {
                    temp_str = temp_str + str.charAt(l);
                }

                if (str.length() <= 5 + temp_str.length()) {
                    number mynum = new number(temp_str);
                    mynum.settype("int");
                    mymap.put(temp_str, mynum);
                    continue;
                }
                /////////////////////////////////////////////////////////////////
                try {
                    for (int t = 0; t < str.length(); t++) {
                        if(str.charAt(t)=='.')
                            throw new ExpressionException("type-wrong");
                    }
                }
                catch(ExpressionException e)
                {
                    for(int t=0;t<all_str.size();t++)
                    {
                        l2.info(all_str.get(t));
                    }
                    l2.info("type -wrong | the variable name is "+temp_str);

                    if(e.correct_2(temp_str))
                    {
                        all_str.set(i,"float"+all_str.get(i).substring(3,all_str.get(i).length()));
                        i--;
                        continue;
                    }
                    else
                    {
                        return;
                    }
                }

                int j = 6 + temp_str.length();
                flag1 = 0;
                fl1.add((int) pow(10, flag1));
                int key = 0;
                while (str.charAt(j) >= '0' && str.charAt(j) <= '9') {
                    flag1++;
                    fl1.add((int) pow(10, flag1));
                    j++;
                }
                for (int l = 5 + temp_str.length(); l <= str.length() - 2; l++) {
                    // System.out.println(flag1);
                    key = key + (str.charAt(l) - 48) * fl1.get(flag1);
                    flag1 = flag1 - 1;
                }
                fl1.clear();
                flag1 = 0;

                number temp_num = new number(key);
                mymap.put(temp_str, temp_num);

            } else if (temp.equals("flo")) {
                fl1.clear();
                fl2.clear();
                String type = "";
                for (int l = 6; (str.charAt(l) >= 'a' && str.charAt(l) <= 'z') || (str.charAt(l) >= 'A' && str.charAt(l) <= 'Z'); l++) {
                    type = type + str.charAt(l);
                }

                if (str.length() <= 7 + type.length()) {
                    number mynum = new number(type);
                    mynum.settype("float");
                    mymap.put(type, mynum);
                    continue;
                }
                int j = 8 + type.length();
                fl1.add(1);
                flag1 = 0;
                float key = 0;
                for (; j <= str.length() - 1; j++) {
                    while (str.charAt(j) >= '0' && str.charAt(j) <= '9') {
                        flag1++;
                        fl1.add((int) pow(10, flag1));
                        j++;
                    }
                    int temp_flag = flag1;
                    for (int l = 7 + type.length(); l <= 7 + type.length() + temp_flag; l++) {
                        key = key + (str.charAt(l) - 48) * fl1.get(flag1);
                        flag1 = flag1 - 1;
                    }
                    flag1 = 0;
                    if (str.charAt(j) == ';') {
                        number mynum = new number((float) key);
                        mynum.settype("float");
                        mymap.put(type, mynum);
                        continue;
                    }

                    if (str.charAt(j) == '.') {

                        int pause = j;
                        j++;
                        while (str.charAt(j) >= '0' && str.charAt(j) <= '9') {
                            flag2++;
                            fl2.add((float) pow(0.1, flag2));
                            j++;
                        }

                        int temp_flag2 = 0;
                        for (int y = pause + 1; y <= str.length() - 2; y++) {
                            key = key + (str.charAt(y) - 48) * fl2.get(temp_flag2);
                            temp_flag2++;
                        }

                    }
                    //      System.out.println(key);
                    fl1.clear();
                    fl2.clear();
                    flag1 = 0;
                    flag2 = 0;
                    number temp_num = new number(key);
                    mymap.put(type, temp_num);

                }
            } else {

                String variable_name = "";
                for (int l = 0; (str.charAt(l) >= 'a' && str.charAt(l) <= 'z') || (str.charAt(l) >= 'A' && str.charAt(l) <= 'Z'); l++) {
                    variable_name = variable_name + str.charAt(l);
                }

                number temp_num = null;

                try {
                    temp_num = mymap.get(variable_name);
                    if (temp_num == null)
                        throw new ExpressionException("wrong - variable undefined ", variable_name, 0);
                } catch (ExpressionException e) {

                    for(int t=0;t<all_str.size();t++)
                    {
                        l2.info(all_str.get(t));
                    }
                    l2.info("wrong - variable undefined | the variable name is "+variable_name);

                    temp_num = e.correct(mymap, variable_name);
                    if (temp_num == null) {

                        return;
                    }
                    continue;
//                    System.out.println(e.toString());

                }

                if (temp_num.gettype().equals("int")) {
                    int j = 3;
                    fl1.add((int) pow(10, flag1));
                    int key = 0;
                    try{
                        for(int t=0;t<str.length();t++)
                            if(str.charAt(t)=='.')
                                throw new ExpressionException("type-wrong");

                    }
                    catch(ExpressionException e)
                    {
                        for(int t=0;t<all_str.size();t++)
                        {
                            l2.info(all_str.get(t));
                        }
                        l2.info("type -wrong | the variable name is "+variable_name);

                        if(e.correct_2(variable_name))
                        {
                            temp_num.settype("float");
                            mymap.replace(variable_name,temp_num);
                            i--;
                            continue;
                        }
                        else
                            return;
                    }
                    while (str.charAt(j) >= '0' && str.charAt(j) <= '9') {
                        flag1++;
                        fl1.add((int) pow(10, flag1));
                        j++;
                    }
                    for (int l = 2; l <= str.length() - 2; l++) {
                        key = key + (str.charAt(l) - 48) * fl1.get(flag1);
                        flag1 = flag1 - 1;
                    }
                    fl1.clear();
                    flag1 = 0;
                    temp_num.setValue_int(key);

                } else if (temp_num.gettype().equals("float")) {
                    int j = 3;fl1.clear();
                    fl1.add(1);
                    float key = 0;flag1=0;
                    while (str.charAt(j) >= '0' && str.charAt(j) <= '9') {
                        flag1++;
                        fl1.add((int) pow(10, flag1));
                        j++;
                    }
                    int temp_flag1 = flag1;
                    for (int l = 2; l <= 2 + flag1; l++) {
                        key = key + (float)(str.charAt(l) - 48) * (float)fl1.get(temp_flag1);
                        temp_flag1 = temp_flag1 - 1;
                    }
                    if (str.charAt(j) == ';') {
                        temp_num.setValue_float(key);
                        flag1 = 0;
                        fl1.clear();
                        continue;
                    }

                    if (str.charAt(j) == '.') {
                        int pause = j;
                        j++;
                        while (str.charAt(j) >= '0' && str.charAt(j) <= '9') {
                            flag2++;
                            fl2.add((float) pow(0.1, flag2));
                            j++;
                        }
                        int temp_flag2 = 0;
                        for (int y = pause + 1; y <= str.length() - 2; y++) {
                            key = key + (str.charAt(y) - 48) * fl2.get(temp_flag2);
                            temp_flag2++;
                        }

                        temp_num.setValue_float(key);
                        fl1.clear();
                        fl2.clear();
                        flag1 = 0;
                        flag2 = 0;
                    }
                }
            }

        }
        boolean normal_exit=false;String str = all_str.get(all_str.size() - 1);
        while(!normal_exit)
        {
        Stack<number> num = new Stack<number>();
        Stack<Character> latter = new Stack<Character>();

        float sum = 0;
        int flag = 0;
        fl1.clear();
        fl2.clear();
        for (int i = 0; i < str.length(); i++) {

            if (str.charAt(i) == '=') {
                break;
            }
            if (str.charAt(i) == ' ') {
                continue;
            }
            if (str.charAt(i) == '?') {
                break;
            }

            if (str.charAt(i) >= '0' && str.charAt(i) <= '9') {
                int temp_i = i;
                fl1.clear();
                fl1.add(1);
                float sum2 = 0;
                boolean float_int = false;
                if (str.charAt(i + 1) == '.' || (i < str.length() - 1 && str.charAt(i + 1) <= '9' && str.charAt(i + 1) >= '0')) {
                    i++;
                    while ((i < str.length() - 1 && str.charAt(i) >= '0' && str.charAt(i) <= '9')) {
                        flag1++;
                        fl1.add((int) Math.pow(10, flag1));
                        i++;
                    }
                    int temp_flag1 = flag1;
                    for (int l = temp_i; l <= temp_i + temp_flag1; l++) {
                        sum2 = sum2 + (str.charAt(l) - 48) * fl1.get(flag1);
                        flag1--;
                    }

                    if (str.charAt(i) == '.') {
                        temp_i = i + 1;
                        i++;
                        fl2.clear();
                        flag2 = 0;
                        float_int = true;
                        while (i < str.length() && str.charAt(i) <= '9' && str.charAt(i) >= '0') {
                            flag2++;
                            fl2.add((float) Math.pow(0.1, flag2));
                            i++;
                        }
                        int temp_flag2 = 0;
                        for (int l = temp_i; l < temp_i + flag2; l++) {
                            sum2 = sum2 + (str.charAt(l) - 48) * fl2.get(temp_flag2);
                            temp_flag2++;
                        }

                    }
//                    System.out.println(sum2);
                    fl1.clear();
                    fl2.clear();
                    flag1 = 0;
                    flag2 = 0;
                    if (float_int) {
                        number mynum = new number((float) sum2);
                        num.push(mynum);
                        i--;
                        continue;
                    } else {
                        number mynum = new number((int) sum2);
                        num.push(mynum);
                        i--;
                        continue;
                    }
                }
                number mynum = new number((int) str.charAt(i) - 48);
                num.push(mynum);
                continue;
            }

            if ((str.charAt(i) >= 'a' && str.charAt(i) <= 'z') || (str.charAt(i) >= 'A' && str.charAt(i) <= 'Z')) {
                String vari_name = "";
                for (; (str.charAt(i) >= 'a' && str.charAt(i) <= 'z') || (str.charAt(i) >= 'A' && str.charAt(i) <= 'Z'); i++) {
                    vari_name += str.charAt(i);
                }
                i--;
                number temp = mymap.get(vari_name);       ////////////////////////////////////第二处异常处理


                try {
                    if (temp == null) throw new ExpressionException("wrong", vari_name, 0);
                    else if (!temp.initialized) throw new ExpressionException("wrong", vari_name, 1);
                } catch (ExpressionException e) {
                    for(int t=0;t<all_str.size();t++)
                    {
                        l2.info(all_str.get(t));
                    }
                    if(e.get_undefined())
                    l2.info("wrong - variable undefined | the variable name is "+vari_name);
                    else
                        l2.info("wrong - variable unassigned | the variable name is "+vari_name);


                    temp = e.correct(mymap, vari_name);
                    if (temp == null) {
                        return;
                    }
                  else {
                      num.push(temp);

                      continue;
                    }
                }

                 num.push(temp);
            }
            else {

                if (str.charAt(i) == '(') {
                    latter.push(str.charAt(i));
                    flag++;
                    continue;
                }
                if (str.charAt((i)) == ')') {
                    try {
                        if (flag == 0) {
                            errorexp = true;
                            throw new ExpressionException("wrong");
                        }
                        compute_six((Stack<number>) num, latter);

                        latter.pop();
                        flag--;
                    } catch (ExpressionException e) {
                        for(int t=0;t<all_str.size();t++)
                        {
                            l2.info(all_str.get(t));
                        }
                        l2.info("wrong - 表达式出错-括号不匹配 ");

                        str = e.correct("括号不匹配");
                        if (str.equals("")) {
                            return;
                        } else {
                            i = -1;num.clear();latter.clear();flag = 0;sum = 0;
                            fl1.clear();
                            fl2.clear();
                            errorexp = false;
                            continue;
                        }
                    }

                }

                if (str.charAt(i) == '+' || str.charAt(i) == '-') {
                    if (num.size() >= 2) {
                        compute_six((Stack<number>) num, latter);

                    }
                    latter.push(str.charAt(i));
                    continue;
                }
                if (str.charAt(i) == '^') {
                    latter.push(str.charAt(i));
                    continue;
                }

                if (str.charAt(i) == '/' || str.charAt(i) == '%' || str.charAt(i) == '*') {
                    if (latter.empty() || latter.peek() == '+' || latter.peek() == '-' || latter.peek() == '(') {
                        latter.push(str.charAt(i));
                        continue;
                    }
                    if (num.size() >= 2) {
                        while ((!latter.empty() && (latter.peek() == '*' || latter.peek() == '^' || latter.peek() == '%' || latter.peek() == '/')) && latter.peek() != '(') {
                            float t1 = 0, t2 = 0;
                            String type1, type2;
                            if (num.peek().gettype().equals("int")) {
                                t1 = (int) t1;
                                t1 = num.peek().getValue_int();
                                type1 = new String("int");
                                num.pop();
                            } else {
                                t1 = num.peek().getValue_float();
                                type1 = new String("float");
                                num.pop();
                            }
                            if (num.peek().gettype().equals("int")) {
                                t2 = (int) t2;
                                t2 = num.peek().getValue_int();
                                type2 = new String("int");
                                num.pop();
                            } else {
                                t2 = num.peek().getValue_float();
                                type2 = new String("float");
                                num.pop();
                            }

                            compute_four((Stack<number>) num, latter, t1, t2, type1, type2);
                        }
                    }
                    latter.push(str.charAt(i));
                }
            }
        }

        while (!latter.empty()) {
            if (latter.peek() == '(') {
                latter.pop();
                break;
            }
            compute_two((Stack<number>) num, latter);
        }

        try {

            if (num.size() != 1) {
                System.out.println("hey "+num.pop().getValue_int()+" "+num.peek().getValue_float());
                throw new ExpressionException("wrong");
            }

            for (String s : all_str) {
                l1.info(s);
            }

            number tempp = num.peek();
            if (tempp.gettype().equals("int")) {
                l1.info("answer is "+tempp.getValue_int());
                System.out.println(tempp.getValue_int());
            } else {
                DecimalFormat df = new DecimalFormat("#0.00");
                float data = tempp.getValue_float();
                l1.info("answer is "+tempp.getValue_float());
                System.out.println(df.format(data));
            }
            normal_exit=true;
        } catch (ExpressionException e) {
            for(int t=0;t<all_str.size();t++)
            {
                l2.info(all_str.get(t));
            }
            l2.info("wrong - 表达式出错-运算符问题 ");
            str = e.correct("运算符出错");
            if (str.equals("")) {
                return;
            }
            else {
                num.clear();latter.clear();flag=0;
                sum = 0;
                fl1.clear();
                fl2.clear();
                errorexp = false;
            }
        }
    }
    }

    public static void compute_two(Stack<number> num, Stack<Character> latter) {
        float t1=0,t2=0;
        String type1,type2;
        if(num.peek().gettype().equals("int"))
        {
            t1=(int)t1;
            t1=num.peek().getValue_int();
            type1=new String("int");
            num.pop();
        }
        else
        {
            t1= num.peek().getValue_float();
            type1=new String("float");
            num.pop();
        }
        if(num.peek().gettype().equals("int"))
        {
            t2=(int)t2;
            t2=num.peek().getValue_int();
            type2=new String("int");
            num.pop();
        }
        else
        {
            t2= num.peek().getValue_float();
            type2=new String("float");
            num.pop();
        }

        if(latter.peek()=='+')
        {

            number my_num;
            if(type1.equals("float")||type2.equals("float"))
            {
                my_num=new number((float)t2+(float)t1);
            }
            else my_num=new number((int)t2+(int)t1);
            num.push(my_num);
            latter.pop();
        }
        else if(latter.peek()=='-')
        {
            number my_num;
            if(type1.equals("float")||type2.equals("float"))
            {
                my_num=new number((float)t2-(float)t1);
            }
            else my_num=new number((int)t2-(int)t1);
            num.push(my_num);
            latter.pop();
        }
        else compute_four(num, latter, t1, t2, type1, type2);
    }

    public static void compute_four(Stack<number> num, Stack<Character> latter, float t1, float t2, String type1, String type2) {
        if (latter.peek() == '*') {
            number my_num;
            if (type1.equals("float") || type2.equals("float")) {
                my_num = new number(t2 * t1);
            } else my_num = new number((int) t2 * (int) t1);
            num.push(my_num);
            latter.pop();
        } else if (latter.peek() == '/') {
            number my_num;
            if (type1.equals("float") || type2.equals("float")) {
                my_num = new number(t2 / t1);
            } else my_num = new number((int) t2 / (int) t1);
            num.push(my_num);
            latter.pop();
        } else if (latter.peek() == '%') {

            number my_num;
            if (type1.equals("float") || type2.equals("float")) {
                my_num = new number(t2 % t1);
            } else my_num = new number((int) t2 % (int) t1);
            num.push(my_num);

            latter.pop();
        } else if (latter.peek() == '^') {
            number my_num;
            if (type1.equals("float") || type2.equals("float")) {
                my_num = new number((float) pow(t2, t1));
            } else my_num = new number((int) pow((int) t2, (int) t1));
            num.push(my_num);
            latter.pop();
        }
    }

    public static void compute_six(Stack<number> num, Stack<Character> latter) {
        while (!latter.empty() && latter.peek() != '(') {
            compute_two((Stack<number>) num, latter);
        }
    }
}


class number {
    String type = "unsigned";
    int value_int;
    float value_float;
    boolean initialized = false;

    int getValue_int() {
        return value_int;
    }

    float getValue_float() {
        return value_float;
    }

    public void setValue_int(int value) {
        value_int = value;
        type = "int";
        initialized = true;
    }

    public void setValue_float(float value) {
        value_float = value;
        type = "float";
        initialized = true;
    }

    public number(String t) {
        type = t;
        initialized = false;

    }

    public number(int v) {
        value_int = v;
        type = new String("int");
        initialized = true;
    }

    public number(float v) {
        value_float = v;
        type = new String("float");
        initialized = true;
    }

    public void settype(String t) {
        type = t;
    }

    String gettype() {
        return type;
    }
}


//class a{
//    public void show()throws IOException
//    {
//        try {
//            throw new ClassNotFoundException();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//}
//
//    public void a()throws SQLException {
//        throw new BatchUpdateException();
//
//    }
//}
//abstract class a{
//    public abstract void show()throws SQLException,IOException;
//    {
//        try {
//            throw new Exception("error");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            throw new RemoteException("error");
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//
//
//            try {
//                throw new BatchUpdateException();
//            } catch (BatchUpdateException throwables) {
//                throwables.printStackTrace();
//            }
//
//    }
//}
//abstract class b implements a
//{
//
//    public void main()throws NullPointerException,IOException {return ;}
//
//}

/*
int i=10;
float j=10.1;
i*1.1+j=?

int i=10;
float j=10.1;
10*i*1.1+j=?


int i=10;
float j=10.1;
2*i+j=?

int temp=10;
int tem=10;
int te=10;
temp+tem+te=?


float j;
float a;
j=10;
a=3;
j/a=?
 *///        int count1=0;int count2=0;
//        for(int i=0;i<str.length();i++)
//        {
//            if(str.charAt(i)=='(')count1++;
//            if(str.charAt(i)==')')count2++;
//        }
//        if(flag!=0)
//        {
//            errorexp=true;
//        }
//        if(count1!=count2)
//        {
//            errorexp=true;
//        }

//        int x=1;
//
//        assert x == 0 : "fuck me"; //

//        Logger lll=Logger.getLogger("less5.loggertest");
//        lll.info("fuck me");
//        lll.log(Level.INFO,"log info");
//        Handler hd = null;
//        try {
//
//            hd=new FileHandler("B:\\Films\\javatest%u.log",true);//累加的格式
//            lll.addHandler(hd);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        lll.info("shit");
//
//        Logger llll=Logger.getLogger("less5.loggertest.temp");
//        llll.log(Level.SEVERE,"log info after");
//        llll.setUseParentHandlers(false);
//        llll.addHandler(hd);
//        llll.info("shit two");