/* This Class represents a big number and mathematical functions */

import java.util.ArrayList;

public class BigInt implements Comparable<BigInt> {

    private ArrayList<Integer> bigInt; // represents the big Integer
    private enum Sign {POSITIVE, NEGATIVE} // represents the sign of the integer
    private Sign bigIntSign;

    public BigInt(String num) {
        bigInt = new ArrayList<>();
        int i = 0; //checking that the input is legal
        if (num.charAt(i) == '-') {
            bigIntSign = Sign.NEGATIVE; // number is negative
            i++;
        } else if (num.charAt(i) == '+') {
            bigIntSign = Sign.POSITIVE; // number is positive
            i++;
        } else if (Character.isDigit(num.charAt(i))) {
            bigIntSign = Sign.POSITIVE; // if a number has no sign in the beginning its positive
        } else {
            throw new IllegalArgumentException("Illegal characters in BigInt: " + num.charAt(i));
        }
        for (int j = i; j < num.length(); j++) { // checking that there are only digits in the string
            if (Character.isDigit(num.charAt(j)) && num.charAt(j) != '0') {
                break;
            } else if (!Character.isDigit(num.charAt(j))) {
                throw new IllegalArgumentException("Illegal characters in BigInt: " + num.charAt(j));
            }
            i++;
        }
        if (i == num.length()) // number is zero
        {
            bigIntSign = Sign.POSITIVE; // there is only one digit in the string and its zero
            bigInt.add(0);
        }

        for (int j = i; j < num.length(); j++) { // placing the digits in the arraylist
            if (Character.isDigit(num.charAt(j)))
                bigInt.add(Character.getNumericValue(num.charAt(j)));
            else throw new IllegalArgumentException("Illegal characters in BigInt: " + num.charAt(j));
        }
    }

    public BigInt(ArrayList<Integer> num, Sign sign) { // a constructor that gets an arraylist and the sign of the number
        bigInt = num;
        bigIntSign = sign;
    }

    public BigInt(BigInt other) { // a copy constructor
        bigInt = new ArrayList<>(other.bigInt);
        bigIntSign = other.bigIntSign;
    }

    /* A method to add two big integers
    * @param other the other integer to add to this
    * @return BigInt that represents the sum of the numbers
    * */
    public BigInt plus(BigInt other) {
        BigInt sum; // the value returned
        Sign sumSign; // sign of the sum
        int size = Math.max(bigInt.size() + 1, other.bigInt.size() + 1); // the maximum size for the result's array list
        ArrayList<Integer> sumArr = zerosList(size); // generates an array list with the maximum size, only zeros
        int ones; // keeping track for the plus method for units
        int tens = 0; // keeping track for the plus method for tens
        BigInt zero = new BigInt("0"); // will be equaled to number if needed to check if number is zero
        BigInt temp; // will restore a temporary number to send to the minus method
        int i = bigInt.size() - 1; // index for this number
        int j = other.bigInt.size() - 1; // index for other number
        int n = size - 1; // index for sum number
        int tempSum; // temporary saves the sum of the last digits

        if (other.equals(zero)) // if one of the numbers equals zero, return the other
            return this;
        if (this.equals(zero))
            return other;

        if (other.bigIntSign == bigIntSign) // same signs
        {
            sumSign = bigIntSign; // sum will have the same sign
            while (i >= 0 && j >= 0) {
                tempSum = bigInt.get(i) + other.bigInt.get(j); // adding the last digit of the other number to this
                if (tempSum < 10) { // there is no carry
                    ones = tempSum;
                    if (ones + tens >= 10) { // there is a carry from the previous loop
                        ones = (ones + tens) % 10; // will restore the units on ones
                        sumArr.set(n, ones); // store the value
                        tens = 1; // add carry
                    } else {
                        sumArr.set(n, ones + tens); // store the value
                        tens = 0; // no carry
                    }
                } else { // there is a carry from previous loop
                    ones = tempSum % 10;
                    sumArr.set(n, ones + tens);
                    tens = 1;
                }
                j--;
                i--;
                n--;
            }
            while (i >= 0) // this number didn't reach it's end but other did
            {
                ones = bigInt.get(i); // will add only this number's digits
                if (ones + tens >= 10) {
                    ones = (ones + tens) % 10;
                    sumArr.set(n, ones);
                    tens = 1;
                } else {
                    sumArr.set(n, ones + tens);
                    tens = 0;
                }
                i--;
                n--;
            }
            while (j >= 0) // this number didn't reach it's end but other did
            {
                ones = other.bigInt.get(j);
                if (ones + tens >= 10) {
                    ones = (ones + tens) % 10;
                    tens = 1;
                } else
                    tens = 0;
                sumArr.set(n, ones + tens);
                j--;
                n--;
            }
            if (tens > 0) { // will add additional carry
                sumArr.set(n, tens);
                n--;
            }
            while (n >= 0) { // will remove extra zeros from the beginning of the array list
                sumArr.remove(n);
                n--;
            }
            sum = new BigInt(sumArr, sumSign); // constructs a BigInt
        } else // numbers have different signs
        {
            if (bigIntSign == Sign.POSITIVE) // this number is positive and other negative
            {
                if (this.isLarger(other)) {
                    temp = new BigInt(other.bigInt, Sign.POSITIVE);
                    sum = this.minus(temp);
                    sum.bigIntSign = Sign.POSITIVE;
                } else {
                    temp = new BigInt(other.bigInt, Sign.POSITIVE);
                    sum = this.minus(temp);
                    sum.bigIntSign = Sign.NEGATIVE;
                }
            } else // this number is negative and other is positive
            {
                if (this.isLarger(other)) {
                    temp = new BigInt(this.bigInt, Sign.POSITIVE);
                    sum = other.minus(temp);
                    sum.bigIntSign = Sign.NEGATIVE;
                } else {
                    temp = new BigInt(this.bigInt, Sign.POSITIVE);
                    sum = other.minus(temp);
                    sum.bigIntSign = Sign.POSITIVE;
                }
            }
        }
        return sum;
    }

    /* A method to deduct two big integers
     * @param other the other integer to deduct from this
     * @return BigInt that represents the result
     * */
    public BigInt minus(BigInt other) {

        BigInt min;
        BigInt temp1, temp2;
        Sign minSign;
        BigInt zero = new BigInt("0");
        int size = Math.max(bigInt.size(), other.bigInt.size());
        ArrayList<Integer> minArr = zerosList(size);
        int tempNum;
        int carry = 0;
        int i = bigInt.size() - 1; // index for this number
        int j = other.bigInt.size() - 1; // index for other number
        int n = size - 1; // sum's index = largest size + 1

        // -x-y = -(x+y) so we will send the numbers to plus method and change results
        if (bigIntSign == Sign.NEGATIVE && other.bigIntSign == Sign.POSITIVE) {
            temp1 = new BigInt(this.bigInt, Sign.POSITIVE);
            min = temp1.plus(other);
            min.bigIntSign = Sign.NEGATIVE;
            return min;
        }
        // x--y = x+y so we will send the numbers to plus method
        if (bigIntSign == Sign.POSITIVE && other.bigIntSign == Sign.NEGATIVE) {
            temp1 = new BigInt(other.bigInt, Sign.POSITIVE);
            min = this.plus(temp1);
            min.bigIntSign = Sign.POSITIVE;
            return min;
        }
        // -x--y = -x+y = y-x so we will send the numbers to minus but replace their places
        if (bigIntSign == Sign.NEGATIVE && other.bigIntSign == Sign.NEGATIVE) {
            temp1 = new BigInt(this.bigInt, Sign.POSITIVE);
            temp2 = new BigInt(other.bigInt, Sign.POSITIVE);
            if (temp1.equals(temp2))
                return zero;
            if (temp1.isLarger(temp2)) {
                min = temp2.minus(temp1);
                min.bigIntSign = Sign.NEGATIVE;
            } else {
                min = temp2.minus(temp1);
                min.bigIntSign = Sign.POSITIVE;
            }
            return min;
        } else // both positive
        {
            if (this.equals(other)) // numbers are equal
            {
                min = new BigInt("0");
            } else if (!this.isLarger(other)) {
                min = other.minus(this);
                min.bigIntSign = Sign.NEGATIVE;
            } // we want to deduct the smaller from the greater (absolute)

            else {
                minSign = Sign.POSITIVE;
                while (i >= 0 && j >= 0) {
                    tempNum = bigInt.get(i) - other.bigInt.get(j) + carry;
                    if (tempNum < 0) {
                        carry = -1;
                        tempNum += 10;
                    } else {
                        carry = 0;
                    }
                    minArr.set(n, tempNum);
                    i--;
                    j--;
                    n--;
                }
                if (j < 0) {
                    while (i >= 0) {
                        tempNum = bigInt.get(i) + carry;
                        if (tempNum < 0) {
                            carry = -1;
                            tempNum += 10;
                        } else {
                            carry = 0;
                        }
                        minArr.set(n, tempNum);
                        i--;
                        n--;
                    }

                    while (minArr.get(0) == 0 && minArr.size() > 1) {
                        minArr.remove(0);
                    }
                }
                min = new BigInt(minArr, minSign);
            }
        }
        return min;
    }

    /* A method to multiply two big integers
     * @param other the other integer to multiply with this
     * @return BigInt that represents the result
     * */
    public BigInt multiply(BigInt other) {
        BigInt tempNum; // temp result
        BigInt mult; // result
        Sign multSign; // sign of the result
        BigInt zero = new BigInt("0");
        int temp; // temp multiply of two digits
        int size = (bigInt.size() + other.bigInt.size()); // largest size possible for the result
        ArrayList<Integer> tempArr = zerosList(size); // used to save temporary results
        int k; // index
        int n = size - 1; // index
        int carry = 0; // carry for multiplying two digits

        if (this.equals(zero) || other.equals(zero)) {
            return zero;
        }

        if (this.bigIntSign == other.bigIntSign) // when both numbers have the same sign, result is positive
        {
            multSign = Sign.POSITIVE;
        } else // when one is negative, result is negative
        {
            multSign = Sign.NEGATIVE;
        }

        if (!this.isLarger(other) && other.isLarger(this)) // would make sure the first number is larger (absolut)
        {
            return other.multiply(this); //absolute
        }

        mult = new BigInt("0");
        tempNum = new BigInt(tempArr, multSign);

        for (int j = other.bigInt.size() - 1; j >= 0; j--, n--) {
            tempNum.bigInt = zerosList(size);
            k = n;
            for (int i = this.bigInt.size() - 1; i >= 0; i--, k--) {
                temp = carry + (this.bigInt.get(i) * other.bigInt.get(j));
                if (temp >= 10) {
                    carry = temp / 10;
                    temp = temp % 10;
                    if (i == 0) {
                        tempNum.bigInt.set(k - 1, carry);
                        carry = 0;
                    }
                } else
                    carry = 0;
                tempNum.bigInt.set(k, temp);
            }
            mult = new BigInt(mult.plus(tempNum));
        }

        while (mult.bigInt.get(0) == 0 && mult.bigInt.size() > 1) // remove extra zeros in beginning of the result
        {
            mult.bigInt.remove(0); // removes extra zeros
        }
        return mult;
    }

    /* A method to divide two big integers, using grade school method (long division)
     * @param other the other integer to divide from this
     * @return BigInt that represents the result
     * */
    public BigInt divide(BigInt other) {
        Sign divSign;
        BigInt res = new BigInt("0");
        BigInt thisCopy, otherCopy; // a copy of the absolute value of both
        ArrayList<Integer> thisArr = new ArrayList<>(this.bigInt);
        ArrayList<Integer> otherArr = new ArrayList<>(other.bigInt);
        thisCopy = new BigInt(thisArr, Sign.POSITIVE); // copies of the parameters but positive
        otherCopy = new BigInt(otherArr, Sign.POSITIVE);

        if (other.bigInt.get(0) == 0) {
            throw new ArithmeticException("Illegal division in Zero");
        }

        if (!thisCopy.isLarger(otherCopy)) {
            if (thisCopy.equals(otherCopy) && this.bigIntSign == other.bigIntSign) // numbers are equals
            {
                return new BigInt("1");
            } else if (otherCopy.isLarger(thisCopy))
                return new BigInt("0"); // this number is smaller than other
            else if (thisCopy.equals(otherCopy) && this.bigIntSign != other.bigIntSign)
                return new BigInt("-1"); // numbers are equals with different sign
        }

        if (this.bigIntSign == other.bigIntSign) // when both numbers have the same sign, result is positive
        {
            divSign = Sign.POSITIVE;
        } else // when one is negative, result is negative
        {
            divSign = Sign.NEGATIVE;
        }

        res.bigInt = thisCopy.div(otherCopy, otherCopy.bigInt.size() - 1, new BigInt("0"));
        res.bigIntSign = divSign;
        return res;

    }

    /* A recursive method
     * @param number, other - represent the numbers we want to divide
     * @return BigInt that represents the number of times other number can fill in this number
     * */
    private BigInt divideRec(BigInt number, BigInt other) {
        BigInt temp;

        if (number.compareTo(other) < 0) // this number is smaller than divider
        {
            temp = new BigInt("0");
            return temp;
        }
        temp = new BigInt("1");
        return (temp.plus(divideRec(number.minus(other), other))); // return 1 + divide for this-other with other
    }

    /* A recursive method use to help with long division
     * */
    private ArrayList<Integer> div(BigInt other, int sizeOther, BigInt res) // send size other minus one
    {

        ArrayList<Integer> tempArr = new ArrayList<>(this.bigInt);
        BigInt temp = new BigInt(tempArr, Sign.POSITIVE);
        BigInt k;
        BigInt toMultiply = new BigInt("1");
        BigInt tens = new BigInt("10");
        BigInt num2 = new BigInt("0");
        sizeOther = other.bigInt.size() - 1;
        if (this.compareTo(num2) < 0 || this.equals(num2)) {
            return res.bigInt;
        }
        temp.bigInt = new ArrayList<>(this.bigInt.subList(0, sizeOther));
        while (temp.compareTo(other) < 0) {
            sizeOther++;
            if (sizeOther > this.bigInt.size()) {
                return res.bigInt;
            } else {
                temp.bigInt = new ArrayList<>(this.bigInt.subList(0, sizeOther));
            }
        }
        k = divideRec(temp, other);
        for (int j = 0; j < (this.bigInt.size() - temp.bigInt.size()); j++) {
            toMultiply = toMultiply.multiply(tens);
        }
        k = k.multiply(toMultiply);
        res = res.plus(k); // multiply 10s
        num2 = other.multiply(k); // multiply with res from what added today maybe K
        temp = this.minus(num2);
        return temp.div(other, other.bigInt.size() - 1, res);
    }

    /* A method generate an array list with zeros, with given size
     * @param size the size of the arraylist
     * @return ArrayList<Integer> with zeros as it's value
     * */
    private ArrayList<Integer> zerosList(int size) // makes a list of zeros in a given size
    {
        ArrayList<Integer> zeros = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            zeros.add(i, 0);
        }
        return zeros;
    }

    /* A method to compare two big integers
     * @param other the other integer to compare to this
     * @return 0 if numbers are equal, -1 if this number is smaller than other, 1 if this number is greater
     * */
    @Override
    public int compareTo(BigInt other) {
        if (other.bigIntSign != bigIntSign) // numbers have different signs
        {
            if (bigIntSign == Sign.NEGATIVE)
                return -1; // smaller than parameter
            else return 1; // larger than parameter
        } else if (bigIntSign == Sign.NEGATIVE) {
            if (other.bigInt.size() > this.bigInt.size())
                return 1;
            else if (other.bigInt.size() < this.bigInt.size())
                return -1;
        }

        if (bigIntSign == Sign.POSITIVE) {
            if (other.bigInt.size() > this.bigInt.size())
                return -1;
            else if (other.bigInt.size() < this.bigInt.size())
                return 1;
        }

        if (this.bigInt.size() == other.bigInt.size()) {
            for (int i = 0; i < this.bigInt.size(); i++) {
                if (this.bigInt.get(i) > other.bigInt.get(i))
                    return 1;
                else if (this.bigInt.get(i) < other.bigInt.get(i))
                    return -1;
            }
        }
        return 0;
    }

    /* A method to check if two numbers are equal
     * @param other number to compare to
     * @return true if number equal, false otherwise
     * */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof BigInt) && (compareTo((BigInt) obj) == 0);
    }

    /* A method to check if this number's absolute value is larger than other's absolute value
     * @param other the other integer to compare to
     * @return true if this one is larger, false otherwise
     * */
    private boolean isLarger(BigInt other) // absolute value
    {
        if (bigInt.size() > other.bigInt.size())
            return true; // larger than parameter
        else if (bigInt.size() < other.bigInt.size())
            return false; // smaller than parameter
        else {
            for (int i = 0; i < bigInt.size(); i++) {
                if (bigInt.get(i) > other.bigInt.get(i))
                    return true; // larger than parameter
                if (bigInt.get(i) < other.bigInt.get(i))
                    return false; // smaller than parameter
            }
            return false; // equals
        }
    }

    /* A method to generate a string from the big integer
     * @return String that represents the number
     * */
    public String toString() {
        String sign = "";
        String num = "";
        char c;
        int temp;

        for (int i = 0; i < bigInt.size(); i++) {
            temp = bigInt.get(i);
            c = Character.forDigit(temp, 10);
            num += c;
        }

        if (bigIntSign == Sign.NEGATIVE)
            sign += '-';
        sign += num;

        return sign;
    }

}
