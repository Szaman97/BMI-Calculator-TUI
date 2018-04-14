package app;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Separator;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * BMI Calculator
 * @author Micha³ G³ogowski, Alicja Badower
 * @version 0.2.1
 * License: MIT
 * Program requires additional libraries - lanterna 3.0.0
 */

public class BMI_calc_TUI_MichalGlogowski_AlicjaBadower {
	
	private static String version = "0.2.1";
	
	//Private static fields containing necessary components of the window
	private static Screen screen = null;
	private static WindowBasedTextGUI textGUI = null;
	private static Window window = null;
	private static Panel contentPanel = null;
	
	//Private static fields containing weight, height and nick typed by the user (with '_s' there are Strings, with '_d' double numbers)
	//BMI - field for result of the calculations
	private static String weight_s = "";
	private static String height_s = "";
	private static String BMI_s = "";
	private static String nick_s = "";
	private static double weight_d = 0;
	private static double height_d = 0;
	private static double BMI = 0;
	
	//String fields used for correctly formatting text sending to file
	//'\t' characters are adding, depending on size of texts
	private static String tabNick = "";
	private static String tabBMI = "";
	private static String tabWeight = "";
	
	
	private static File file = new File("data.txt");
	
	/**
	 * Method responsible for parsing strings to doubles, calculating BMI, rounding double variables to two digits after the comma
	 * and saving it to strings to further file operations 
	 * @param weight string should contain weight that will be parsed to double
	 * @param height string should contain height that will be parsed to double
	 */
	private static void calculate()
	{
		//Change potential commas to dots
		weight_s = commaToDot(weight_s);
		height_s = commaToDot(height_s);
				
		//Parsed strings to double
    	weight_d = Double.parseDouble(weight_s);
    	height_d = Double.parseDouble(height_s);
    	
    	//Calculated BMI
    	BMI = weight_d / height_d / height_d;
    	
    	//Rounding to two digits after comma
    	BMI = round(BMI);
    	weight_d = round(weight_d);
    	height_d = round(height_d);
    	
    	//Saving rounded numbers to strings
    	BMI_s = String.valueOf(BMI);
    	weight_s = String.valueOf(weight_d);
    	height_s = String.valueOf(height_d);
	}
	
	/**
	 * Method responsible for rounding double numbers up to 2 digits after comma
	 * @param arg number to round
	 * @return number rounded up to two digits after comma
	 */
	private static double round(double arg)
	{
		arg = arg * 100;
		arg = Math.round(arg);
		arg = arg / 100;
		return arg;
	}
	
	/**
	 * Method responsible for adding empty line in the Panel
	 * @param x is a content panel the line will be added to
	 */
	private static void addEmptyLine(Panel x)
	{
		x.addComponent(new EmptySpace().setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)));
	}
	
	/**
	 * Method responsible for checking BMI and returning correct description
	 * @param bmi is a calculated value of Body Mass Index which decides what should be returned
	 * @return text depends on BMI value
	 */
	private static String selectCategory()
	{
		String text;
		if(BMI<15) text = "Very severely underweight";
		else if(BMI<16) text = "Severely underweight";
		else if(BMI<18.5) text = "Underweight";
		else if(BMI<25) text = "Normal (healthy weight)";
		else if(BMI<30) text = "Overweight";
		else if(BMI<35) text = "Obese Class I (Moderately obese)";
		else if(BMI<40) text = "Obese Class II (Severely obese)";
		else text = "Obese Class III (Very severely obese)";
		
		return text;
	}
	
	/**
	 * Method responsible for checking String if it contains comma instead of dot.
	 * When there is comma, commaToDot changes it to dot allowing parsing that string to double.
	 * @param s string to check
	 * @return string with dot instead of comma
	 */
	private static String commaToDot(String s)
	{
		return s.replace(',','.');
	}
	
	/**
	 * Method responsible for calculating needed amount of '\t' characters for correctly displaying text
	 */
	private static void calcTabs()
	{
		tabNick = "\t";
		tabBMI = "\t";
		tabWeight = "\t";
		
		if(nick_s.length()<8) tabNick += "\t";
		if(nick_s.length()<4) tabNick += "\t";
		if(BMI_s.length()<4) tabBMI += "\t";
		if(weight_s.length()<4) tabWeight += "\t";
	}
	
	/**
	 * Method responsible for saving results to file
	 */
	private static void saveToFile()
	{
		try
		{
    		FileWriter out = new FileWriter(file,true);	//the second argument allows to append text to existing file
    		out.write(nick_s + tabNick);
    		out.write(BMI_s + tabBMI);
    		out.write(weight_s + tabWeight);
    		out.write(height_s + "\r\n");	//add entry to next line
    		out.close();					//flush and close the stream
		}
		catch(IOException e)
		{
			MessageDialog.showMessageDialog(textGUI, "File error", "Save to file was not possible", MessageDialogButton.OK);
		}
	}
	
	/**
	 * Method responsible for loading and displaying data from file
	 */
	private static void loadAndDisplay()
	{
		 try
		 {
			 FileReader in = new FileReader(file);
			 
			 //BufferedReader created to load lines of text instead of simple characters
			 BufferedReader buffReader = new BufferedReader(in);
			 
			 //Local variables to keep read text from file
			 String fileText = "";
			 String line = "";
			 
			 //Do until EOF is not reached (specified by returning null by method readLine)
			 while( (line = buffReader.readLine()) != null)
			 {
				 fileText += line + "\r\n";
			 }
			 
			 buffReader.close();
			 in.close();
			 
			 //Added 7 spaces before \r\n due to lanterna issue - it does not count special characters to Dialog size calculations
   		 MessageDialog.showMessageDialog(textGUI, "Saved data", "Name\t\tBMI     Weight\tHeight       \r\n" + fileText, MessageDialogButton.OK);
		 }
		 catch(IOException e)
		 {
			 MessageDialog.showMessageDialog(textGUI, "File error", "Could not load the file", MessageDialogButton.OK);
		 }
	}
	
	public static void main(String[] args) {
		
		DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();

        try
        {
        	screen = terminalFactory.createScreen();
            screen.startScreen();
            textGUI = new MultiWindowTextGUI(screen);
            window = new BasicWindow("BMI Calculator");
            
            //Panel holding components of the window - it has 2 columns
            contentPanel = new Panel(new GridLayout(2));
            
            //Making space between the columns
            GridLayout gridLayout = (GridLayout)contentPanel.getLayoutManager();
            gridLayout.setHorizontalSpacing(5);
            
            //Adding components to content panel 
            addEmptyLine(contentPanel);
            
            Label title = new Label("Enter values into boxes below and press 'Calculate' button");
            title.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER, false, false, 2, 1));
            contentPanel.addComponent(title);
            		
            addEmptyLine(contentPanel);
            addEmptyLine(contentPanel);
            
            contentPanel.addComponent(new Label("Weight [kg]"));
            TextBox weight = new TextBox();
            weight.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER));
            contentPanel.addComponent(weight);
            
            addEmptyLine(contentPanel);
            
            contentPanel.addComponent(new Label("Height [m]"));
            TextBox height = new TextBox();
            height.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER));
            contentPanel.addComponent(height);
            
            addEmptyLine(contentPanel);
            
            contentPanel.addComponent(new Label("Nickname"));
            TextBox nickname = new TextBox();
            nickname.setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER));
            contentPanel.addComponent(nickname);
            
            addEmptyLine(contentPanel);
            
            contentPanel.addComponent(new Label("Press the button to calculate BMI"));
            contentPanel.addComponent(new Button("Calculate", new Runnable() 
            {
                @Override
                public void run()
                {
                	//Parsing may throw NumberFormatException in case that text in TextBox wont be possible to change to double
                	try
                	{
                		//Update Strings with text typed in the fields
                		weight_s = weight.getText();
                		height_s = height.getText();
                		nick_s = nickname.getText();
                		
                		calculate();
                		
                		//Take first 11 letters of nicknames
                		//It is needed due to way of displaying the text
                		//Without it '\t' character would move 'BMI' field to 'Weight' etc.
                		if(nick_s.length()>11) nick_s = nick_s.substring(0, 11);
                		
                		//Calculate needed amount of '\t' characters to correctly display text
                		calcTabs();
                		
                		//Save to file
                		saveToFile();
                		
                        MessageDialog.showMessageDialog(textGUI, "BMI", "Calculated BMI: " + BMI + "\n" + selectCategory(), MessageDialogButton.OK);
                	}
                	catch(NumberFormatException e)
                	{
                		MessageDialog.showMessageDialog(textGUI, "ERROR", "NumberFormatException:\n" + "Please check if the fields are correctly filled", MessageDialogButton.OK);
                	}
                }
            }).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER)));
            
            contentPanel.addComponent(new Label("Press the button to view saved data"));
            contentPanel.addComponent(new Button("Load data", new Runnable()
            {
            	 @Override
                 public void run()
                 {
            		 loadAndDisplay();
                 }
            }).setLayoutData(GridLayout.createLayoutData(GridLayout.Alignment.CENTER, GridLayout.Alignment.CENTER)));
            
            addEmptyLine(contentPanel);
            
            //Separator
            contentPanel.addComponent(new Separator(Direction.HORIZONTAL).setLayoutData(GridLayout.createHorizontallyFilledLayoutData(2)));
            
            contentPanel.addComponent(
                    new Button("About", new Runnable() 
                    {
                        @Override
                        public void run() 
                        {
                        	MessageDialog.showMessageDialog(textGUI, "BMI Calculator v" + version, "Created by:\nMicha³ G³ogowski\nAlicja Badower", MessageDialogButton.OK);
                        }
                    }).setLayoutData(GridLayout.createHorizontallyEndAlignedLayoutData(2)));
            
            
            contentPanel.addComponent(
                    new Button("Close", new Runnable() 
                    {
                        @Override
                        public void run() 
                        {
                            window.close();
                        }
                    }).setLayoutData(GridLayout.createHorizontallyEndAlignedLayoutData(2)));
            
            
            //Attach contentPanel to the window
            window.setComponent(contentPanel);
            
            textGUI.addWindowAndWait(window);  
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        finally 
        {
            if(screen != null)
            {
                try 
                {
                    screen.stopScreen();
                }
                catch(IOException e) 
                {
                    e.printStackTrace();
                }
            }
        }
	}
}