import java.util.Scanner;

public class BigIntRun {
    public static void main (String[] args)
    {
        Scanner scan = new Scanner(System.in);
        BigInt num1 = null;
        BigInt num2 = null;
        BigInt temp;
        int comp;
        final int QUANTITY = 2;

        for(int i=0; i<QUANTITY ; i++)
        {
            temp = null;
            System.out.println("Please enter number #" +(i+1) +" :");
            while (temp == null)
            {
                String num = scan.nextLine();
                try{
                    temp = new BigInt(num);
                } catch (IllegalArgumentException e)
                {
                    System.out.println("The Input is illegal, must enter an integer number\nPlease try again");
                }
            }
            if(i==0)
                num1 = new BigInt(temp);
            else
                num2 = new BigInt(temp);
        }

        System.out.println("The first number is: " + num1);
        System.out.println("The second number is: " + num2);
        System.out.println(num1 + " + " + num2 + " = " + num1.plus(num2));
        System.out.println(num1 + " - " + num2 + " = " + num1.minus(num2));
        System.out.println(num1 + " * " + num2 + " = " + num1.multiply(num2));
        try {
            System.out.println(num1 + " / " + num2 + " = " + num1.divide(num2));
        } catch (ArithmeticException e) {
            System.out.println("It's illegal to divide an number with Zero");
        }
        System.out.println("Does " + num1 + " equals " + num2 + " ? " + num1.equals(num2));
        comp = num1.compareTo(num2);
        System.out.println("If we compare the numbers we will get: " +comp);
        if(comp<0)
        {
            System.out.println("Which means that the first number is *smaller* than the second");
        }
        else if (comp==0)
        {
            System.out.println("Which means that the first *equals* the second");

        }
        else if(comp>0)
        {
            System.out.println("Which means that the first number is *greater* than the second");
        }
    }
}
