package com.example.calculator_app;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    // Variable list

        // handles calculator input as it is inputted
        String calcInput = "";

        // stores the last answer from the calculation
        String lastAnswer;

        //used to store all inputs from the calculator input and splits them into individual strings
        ArrayList<String> calcInputs = new ArrayList<>();



        //final String TAG = "Testing";

        //handles the display screen
        TextView calcDisplay;

        // handles calculation result
        double result;


        //used to know when a sqrt,square,or inverse function is used
        boolean specialArithmeticInUse = false;

        // used to know when there is new for a input check
        boolean nextInputCheck = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //assigns calc textview to a variable
        calcDisplay = findViewById(R.id.calc_display);

    }

    /**
     * Handles the input of most of the buttons on the calculator
     * @param v represents the data of the view object(Button) clicked
     * returns nothing
     */

    public void calcInputHandler(View v){
        // typecasting the v toa an input var of type button
        Button input = (Button) v;

        //used to check the next btn when the = btn is pressed (to know if the screen needs clearing)
        if(nextInputCheck){
            if ( isParsable(input.getText().toString())){
                calcInput = "";
            }
            nextInputCheck = false;
        }


         //used to check the input after the special arithmetic functions
         // have been used ( if used a x symbol is inserted id the next input is a number )

        if ( isParsable(input.getText().toString()) && !calcInput.equals("") && specialArithmeticInUse){
            calcInput += "×" + input.getText().toString();
        }else{
            calcInput += input.getText().toString();

        }
        specialArithmeticInUse = false;
        calcDisplay.setText(calcInput);

    }

    /**
     * Used to handle input after the = btn is pressed and calculate the result
     * @param V not used at all as data from the = btn is not needed(byt necessary for the editor to view the function)
     * return type: VOID
     */
    public void calculateResult(View V){
            calcInputs.clear();
        //splits the input from the calculator into individual strings using the regular expression below
//        calcInputs = calcInput.split("((?<=\\+)|(?=\\+)|(?<=-)|(?=-)|(?<=×)|(?=×)|(?<=÷)|(?=÷)|(?<=%)|(?=%))");
             parseInput(calcInput , calcInputs);
        //Log.d(TAG, Arrays.toString(calcInputs));

        //Dealing with square roots , squares and inverses=
        for (int i = 0; i < calcInputs.size(); i++) {
                if (!isParsable(calcInputs.get(i)) && i==calcInputs.size()-1 && containsNumbers(calcInputs.get(i))){
                    if (calcInputs.get(i).contains("√")){
                        double sqrt = Double.parseDouble(calcInputs.get(i).substring(1,calcInputs.get(i).length()));
                        sqrt =  Math.sqrt(sqrt);
                        calcInputs.set(i , Double.toString(sqrt))  ;
                    } else if (calcInputs.get(i).contains("⁻¹")) {
                        double inverse = Double.parseDouble(calcInputs.get(i).substring(0,calcInputs.get(i).length()-2));
                        inverse =  (1/inverse);
                        calcInputs.set(i,Double.toString(inverse)) ;
                    } else if (calcInputs.get(i).contains("²")) {
                        double squared = Double.parseDouble(calcInputs.get(i).substring(0,calcInputs.get(i).length()-1));
                        squared = Math.pow(squared,2);
                        calcInputs.set(i,Double.toString(squared))  ;
                    }
                }
        }


        //Input test Checks(for  invalid calculator inputs )
         if (!(isParsable(calcInputs.get(calcInputs.size()-1)))){
             calcDisplay.setText(R.string.error_message);
             return;
         }
         if(!(isParsable(calcInputs.get(0)))){
             calcDisplay.setText(R.string.error_message);
             return;
         }
        for (int i = 0; i < calcInputs.size(); i+=2) {
            if ( i < calcInputs.size()-2){
                if (!(isParsable(calcInputs.get(i+2))) && !(calcInputs.get(i+2).equals("+") || calcInputs.get(i+2).equals("-")) ){
                    calcDisplay.setText(R.string.error_message);
                    return;
                }
            }
        }


        result = Double.parseDouble(calcInputs.get(0));

        // Loops through all the calculator inputs and calculates the result/**
        //N:B :- for more info it loops through all inputs and fins the arithmetic
        // operators and calculates them using the next number


        for (int i = 1; i < calcInputs.size(); i++) {

            switch (calcInputs.get(i)){
                case "+":
                    result += Double.parseDouble(calcInputs.get(i+1));
                    break;
                case "-":
                    result -= Double.parseDouble(calcInputs.get(i+1));
                    break;
                case "÷":
                    result /= Double.parseDouble(calcInputs.get(i+1));
                    break;
                case "×":
                    result *= Double.parseDouble(calcInputs.get(i+1));
                    break;
                case "%":
                    result %= Double.parseDouble(calcInputs.get(i+1));
                    break;
            }

        }
        Locale English  = Locale.ENGLISH;

        // formats the result to 3sf
        String formattedResult = String.format(English,"%.3f" , result);
        if(formattedResult.endsWith("000")){
            formattedResult = String.format(English,"%.0f" , result);
        }

        calcDisplay.setText(formattedResult);
        calcInput = formattedResult;
        lastAnswer = formattedResult;
        nextInputCheck = true;
    }

    /**
     *  splits the input from the calculator into individual strings using the regular expression below
     *
     */

    private  void parseInput(String calcInput, ArrayList<String> calcInputs) {
        String value = "";
        ArrayList<String> unsignedOperators = new ArrayList<>(Arrays.asList("×", "÷"));
        ArrayList<String> signedOperators = new ArrayList<>(Arrays.asList("+", "-","√"));
        for (int i = 0; i < calcInput.length(); i++) {


            if (i == 0 && (isParsable(Character.toString(calcInput.charAt(i + 1))) && signedOperators.contains(Character.toString(calcInput.charAt(i))))) {
                value += Character.toString(calcInput.charAt(i));
            } else if (i == 0 && isParsable(Character.toString(calcInput.charAt(i)))) {
                value += Character.toString(calcInput.charAt(i));
            }

            if (i + 1 < calcInput.length()  && i!=0) {
                if (isParsable(Character.toString(calcInput.charAt(i)))) {
                    value += Character.toString(calcInput.charAt(i));
                } else if (!isParsable(Character.toString(calcInput.charAt(i)))) {
                    if (!value.equals("")){
                        calcInputs.add(value);
                        value = "";
                    }

                    if (!isParsable(Character.toString(calcInput.charAt(i + 1))) && isParsable(Character.toString(calcInput.charAt(i - 1)))) {
                        value += Character.toString(calcInput.charAt(i));
                        calcInputs.add(value);
                        value = "";
                    } else if (isParsable(Character.toString(calcInput.charAt(i + 1))) && isParsable(Character.toString(calcInput.charAt(i - 1)))) {
                        value += Character.toString(calcInput.charAt(i));
                        calcInputs.add(value);
                        value = "";
                    } else if (!isParsable(Character.toString(calcInput.charAt(i + 1))) && !isParsable(Character.toString(calcInput.charAt(i - 1)))) {
                        value += Character.toString(calcInput.charAt(i));
                        calcInputs.add(value);
                        value = "";
                    } else if (isParsable(Character.toString(calcInput.charAt(i + 1))) && !isParsable(Character.toString(calcInput.charAt(i - 1)))) {
                        value += Character.toString(calcInput.charAt(i));
                    }
                }


            }else if(i!=0) {
                if (isParsable(Character.toString(calcInput.charAt(i)))) {
                    value += Character.toString(calcInput.charAt(i));
                    calcInputs.add(value);
                } else if (!isParsable(Character.toString(calcInput.charAt(i)))) {
                    calcInputs.add(value);
                    value = "";
                }


            }


        }
    }

    /**
     * Deletes the last char(backspace btn) when pressed
     * @param V not used at all as data from the = btn is not needed(byt necessary for the editor to view the function)
     * return type: VOID
     */
    public void backspace(View V){
        if(calcInput.length() > 0){
            calcInput = calcInput.substring(0,calcInput.length()-1);
            calcDisplay.setText(calcInput);
        }

    }

    /**
     * clears the screen
     * @param V not used at all as data from the = btn is not needed(byt necessary for the editor to view the function)
     * return type: VOID
     */
    public void clearScreen(View V){
        calcInput = "";
        calcDisplay.setText(calcInput);
    }

    /**
     * Assigns the squared symbol to the respective number
     * Handles improper input formats as well
     * return type: VOID
     */
    public void square(View V){
        if (!calcInput.equals("") ){
            if (Character.isDigit(calcInput.charAt(calcInput.length()-1)))
                calcInput += "²" ;
        }else{
            Toast.makeText(this, "Invalid input format", Toast.LENGTH_SHORT).show();
        }
        calcDisplay.setText(calcInput);

        specialArithmeticInUse = true;
    }

    /**
     * Assigns the sqrt symbol
     * handles invalid inout as well
     * return type: VOID
     */
    public void squareRoot(View V){
        nextInputCheck = false;
        if (!calcInput.equals("")){
            if (Character.isDigit(calcInput.charAt(calcInput.length()-1))){
                calcInput+= "×√";
            } else if (calcInput.charAt(calcInput.length() - 1) == '√') {
                Toast.makeText(this, "Invalid input format", Toast.LENGTH_SHORT).show();
            } else{
                calcInput+= "√";
            }
        }else{
            calcInput+= "√";
        }
            calcDisplay.setText(calcInput);
        }

    /**
     * Assigns inverse symbol to the respective number
     * Handles invalid inputs as well
     * return type: VOID
     */
    public void numberInverse(View V){

        if (!calcInput.equals("")){
            if (Character.isDigit(calcInput.charAt(calcInput.length()-1))){
                calcInput += "⁻¹" ;
            }

        }else{
            Toast.makeText(this, "Invalid input format", Toast.LENGTH_SHORT).show();
        }
        calcDisplay.setText(calcInput);

        specialArithmeticInUse = true;
    }

    /**
     * Displays the last calculated result
     * @param V not used
     * return type: void
     */
    public void showLastAnswer(View V){
        if (!calcInput.equals("")){
            if (Character.isDigit(calcInput.charAt(calcInput.length()-1)) && !calcInput.equals(lastAnswer)){
                calcInput+= "+"+lastAnswer;
            }
            else if (calcInput.equals(lastAnswer)){
                return;
            }
            else {
                calcInput+= lastAnswer;
            }
        }
        else {
            calcInput += lastAnswer;
        }
        calcDisplay.setText(calcInput);
    }

    /**
     * Checks if a string can be converted to a double
     * @param input handles the string that needs to be checked
     * returns a boolean
     */
    public Boolean isParsable(String input ){

        try {
            Double.parseDouble(input);
            return true;
        }catch (NumberFormatException e ){
            return false;
        }
    }

    /**
     * Checks if a string contains a number using regular expressions and match patterns
     * @param input handles the string that needs to be checked
     * returns a boolean
     */
    public static boolean containsNumbers(String input) {
        Pattern pattern = Pattern.compile("\\d");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }




}