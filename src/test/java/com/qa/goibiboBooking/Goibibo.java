package com.qa.goibiboBooking;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.qa.utility.ExcelFileReader;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Goibibo {
	
	public static WebDriver driver;
	public static ExtentReports extentReports;
	public static ExtentTest extentTest;
	public static int departDay;
	public static int departMonth;
	public static int departYear;
	public static int returnDay;
	public static int returnMonth;
	public static int returnYear;

	
	@BeforeTest
	public void setExtentReports() {
		extentReports=new ExtentReports(System.getProperty("user.dir")+"/target-output/ExtentReport.html",true);
	}
	
	@AfterTest
	public void endExtentReports() {
		extentReports.flush();
		extentReports.close();
	}
	
	public static String getScreenshot(WebDriver driver, String screenshotName) throws IOException
	{
		String dateName=new  SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(new Date());
		
		File source= ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		
		String destination=System.getProperty("user.dir")+"/FailedScreenshot/"+screenshotName+dateName+".png";
		FileUtils.copyFile(source, new File(destination));
		return destination;

	}
	
	@BeforeMethod
	@Parameters({"Driver","site"})
	public void setUp(String Driver, String site) {
		if(Driver.equals("Chrome"))
		{
			WebDriverManager.chromedriver().setup();
			ChromeOptions opt=new ChromeOptions();
			opt.addArguments("--disable-notifications");
		    driver=new ChromeDriver(opt);		
		}
		
		else if(Driver.equals("Firefox"))
		{
			WebDriverManager.firefoxdriver().setup();
			FirefoxOptions opt=new FirefoxOptions();
			opt.addArguments("--disable-notifications");
		    driver=new FirefoxDriver(opt);		
		}
		
		else if(Driver.equals("Edge"))
		{
			WebDriverManager.edgedriver().setup();
			EdgeOptions opt=new EdgeOptions();
			opt.addArguments("--disable-notifications");
		    driver=new EdgeDriver(opt);		
		}
		
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().pageLoadTimeout(20,TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(20,TimeUnit.SECONDS);
		driver.get(site);	
	}
	
	@Test(priority=0)
	public void goibiboTitleTest() {
		SoftAssert softAssert=new SoftAssert();
		extentTest=extentReports.startTest("goibiboTitleTest");
		String tit=driver.getTitle();
		System.out.println(tit);
		softAssert.assertEquals(tit,"Goibibo - Best Travel Website. Book Hotels, Flights, Trains, Bus and Cabs with upto 50% off","Title is different");
		softAssert.assertAll();
		extentTest.log(LogStatus.INFO,"Title test is done successfully");
	}
	
	@Test(priority=1)
	public void goibiboLogoTest() {
		SoftAssert softAssert=new SoftAssert();
		extentTest=extentReports.startTest("goibiboLogoTest");
		boolean logo=driver.findElement(By.xpath("//span[@class='header-sprite logo']")).isDisplayed();
		softAssert.assertTrue(logo);
		softAssert.assertAll();
	}
	
	@DataProvider
	public Object[][] getBookingData() throws IOException{
		Object data[][]=ExcelFileReader.bookingData("BookingDetails");
		return data;
	}
	
	@Test(priority=2, dataProvider="getBookingData")
	public void goibiboBookingTest(String FromCity, String FromAirport, String ToCity, String ToAirport, String DepartDate, String DepartDateFormat,String ReturnDate, String ReturnDateFormat, String TravelClass, String Adult, String Children, String Infants ) throws Exception {
		SoftAssert softAssert=new SoftAssert();
		extentTest=extentReports.startTest("goibiboBookingTest");
		driver.findElement(By.xpath("//span[text()='Round-trip']")).click();
		driver.findElement(By.xpath("//span[text()='From']/following-sibling::p[@class='sc-bBHxTw hqJqrJ fswWidgetPlaceholder']")).click();
		driver.findElement(By.xpath("//span[text()='From']/following-sibling::input[@type='text']")).sendKeys(FromCity);
		driver.findElement(By.xpath("//li[@role='presentation']/descendant::p[@class='sc-efQSVx diVtEz' and text()='"+FromAirport+"']")).click();
	    extentTest.log(LogStatus.INFO,"FromCity is selected successfully");
	    
		driver.findElement(By.xpath("//span[text()='To']/following-sibling::input[@type='text']")).sendKeys(ToCity);
		driver.findElement(By.xpath("//li[@role='presentation']/descendant::p[@class='sc-efQSVx diVtEz' and text()='"+ToAirport+"']")).click();
	    extentTest.log(LogStatus.INFO,"ToCity is selected successfully");
	    
	    Calendar calendar=Calendar.getInstance();
	    SimpleDateFormat departDateFormate=new SimpleDateFormat(DepartDateFormat);
	    Date formattedDepartdate;
	    try
	    {
	    	departDateFormate.setLenient(false);
	    	formattedDepartdate=departDateFormate.parse(DepartDate);
	    	calendar.setTime(formattedDepartdate);
	    	
	        departDay= calendar.get(Calendar.DAY_OF_MONTH);
	        departMonth=calendar.get(Calendar.MONTH);
	    	departYear=calendar.get(Calendar.YEAR);
	    	
	    	String actualDate=driver.findElement(By.xpath("//div[@class='DayPicker-Caption']")).getText();
	        calendar.setTime(new SimpleDateFormat("MMM yyyy").parse(actualDate));
	        
	        int actualDay=calendar.get(Calendar.DAY_OF_MONTH);
	        int actualMonth=calendar.get(Calendar.MONTH);
	    	int actualYear=calendar.get(Calendar.YEAR);
	    	
	    	while(departMonth > actualMonth || departYear > actualYear)
	    	{
	    		driver.findElement(By.xpath("//span[@aria-label='Next Month']")).click();
	    		actualDate=driver.findElement(By.xpath("//div[@class='DayPicker-Caption']")).getText();
	    	    calendar.setTime(new SimpleDateFormat("MMM yyyy").parse(actualDate));
	    	    
	    	    actualDay=calendar.get(Calendar.DAY_OF_MONTH);
	    	    actualMonth=calendar.get(Calendar.MONTH);
		        actualYear=calendar.get(Calendar.YEAR);
	    	}
	    	WebElement day=driver.findElement(By.xpath("//div[contains(@class,'DayPicker-Day')]/p[text()='"+departDay+"']"));
	    	day.click();
	    	List<WebElement> l=driver.findElements(By.xpath("//div[contains(@class,'DayPicker-Day')]/p[text()='"+departDay+"']/following-sibling::p"));
	        if(l.size()==0)
	        {
	        	System.out.println("Price is not displayed on date "+DepartDate);
	        	
	        }
	        else{
	        	 String price=driver.findElement(By.xpath("//div[contains(@class,'DayPicker-Day')]/p[text()='"+departDay+"']/following-sibling::p")).getText();
	        	 System.out.println("Price of ticket on "+DepartDate+" is "+price);
	        }
	    
	    }
	    catch(ParseException x)
	    {
	    	throw new Exception("Invalid date is provided, please check input date");
	    }
	    

	    SimpleDateFormat returnDateFormate=new SimpleDateFormat(ReturnDateFormat);
	    Date formattedReturndate;
	    try
	    {
	    	returnDateFormate.setLenient(false);
	    	formattedReturndate=returnDateFormate.parse(ReturnDate);
	    	calendar.setTime(formattedReturndate);
	    	
	    	int returnDay= calendar.get(Calendar.DAY_OF_MONTH);
	    	int returnMonth=calendar.get(Calendar.MONTH);
	    	int returnYear=calendar.get(Calendar.YEAR);
	    	
	    	if(returnDay >= departDay && returnMonth >= departMonth && returnYear >= departYear) 
	    	{
	    		String actualDate=driver.findElement(By.xpath("//div[@class='DayPicker-Caption']")).getText();
		        calendar.setTime(new SimpleDateFormat("MMM yyyy").parse(actualDate));
		        
		        int actualDay=calendar.get(Calendar.DAY_OF_MONTH);
		        int actualMonth=calendar.get(Calendar.MONTH);
		    	int actualYear=calendar.get(Calendar.YEAR);
		    	
		    	while(returnMonth > actualMonth || returnYear > actualYear)
		    	{
		    		driver.findElement(By.xpath("//span[@aria-label='Next Month']")).click();
		    		actualDate=driver.findElement(By.xpath("//div[@class='DayPicker-Caption']")).getText();
		    	    calendar.setTime(new SimpleDateFormat("MMM yyyy").parse(actualDate));
		    	    
		    	    actualDay=calendar.get(Calendar.DAY_OF_MONTH);
		    	    actualMonth=calendar.get(Calendar.MONTH);
			        actualYear=calendar.get(Calendar.YEAR);
		    	}
		    	WebElement day=driver.findElement(By.xpath("//div[contains(@class,'DayPicker-Day')]/p[text()='"+returnDay+"']"));
		    	day.click();
		    	List<WebElement> l=driver.findElements(By.xpath("//div[contains(@class,'DayPicker-Day')]/p[text()='"+returnDay+"']/following-sibling::p"));
		        if(l.size()==0)
		        {
		        	System.out.println("Price is not displayed on date "+ReturnDate);
		        	
		        }
		        else{
		        	 String price=driver.findElement(By.xpath("//div[contains(@class,'DayPicker-Day')]/p[text()='"+returnDay+"']/following-sibling::p")).getText();
		        	 System.out.println("Price of ticket on "+ReturnDate+" is "+price);
		        }
		     driver.findElement(By.xpath("//span[text()='Done']")).click();
		    }
	    	else
	    	{
	    		System.out.println("Please check your return date");
	    	}
	    	}
	    catch(ParseException x)
	    {
	    	throw new Exception("Invalid date is provided, please check input date");
	    }
	    
	    driver.findElement(By.xpath("//li[text()='"+TravelClass+"']")).click();
	  
	   String ActualadultNo=driver.findElement(By.xpath("//p[text()='Adults']/parent::div[@class='sc-jeraig lkvSvZ']/descendant::span[@class='sc-lcepkR hqqbLZ']")).getText();
	   int ActualadultNo1=Integer.parseInt(ActualadultNo);
	//   System.out.println(ActualadultNo1);
	   int Adult1=Integer.parseInt(Adult);
	//   System.out.println(Adult1);
	   
	   String ActualchildrenNo=driver.findElement(By.xpath("//p[text()='Children']/parent::div[@class='sc-jeraig lkvSvZ']/descendant::span[@class='sc-lcepkR hqqbLZ']")).getText();
	   int ActualchildrenNo1=Integer.parseInt(ActualchildrenNo);
	//   System.out.println(ActualchildrenNo1);
	   int Children1=Integer.parseInt(Children);
	//   System.out.println(Children1);
	   
	   String ActualinfantsNo=driver.findElement(By.xpath("//p[text()='Infants']/parent::div[@class='sc-jeraig lkvSvZ']/descendant::span[@class='sc-lcepkR hqqbLZ']")).getText();
	   int ActualinfantsNo1=Integer.parseInt(ActualinfantsNo);
	 //  System.out.println(ActualinfantsNo1);
	   int Infants1=Integer.parseInt(Infants);
	 //  System.out.println(Infants1);
	   
	   if(Adult1 >=1 && 9>= Adult1)
  	   {
		   while(ActualadultNo1 < Adult1)
		      {
		    	
			   driver.findElement(By.xpath("//p[text()='Adults']/parent::div[@class='sc-jeraig lkvSvZ']/descendant::span[@class='sc-lcepkR hqqbLZ']/following-sibling::span[@class='sc-ehCJOs kujlZU']")).click();
			   
			   ActualadultNo=driver.findElement(By.xpath("//p[text()='Adults']/parent::div[@class='sc-jeraig lkvSvZ']/descendant::span[@class='sc-lcepkR hqqbLZ']")).getText();
			   ActualadultNo1=Integer.parseInt(ActualadultNo); 
		       }
		   if(8>= Children1 &&  9>=Children1+Adult1) 
		   {
			   while(ActualchildrenNo1 < Children1)
			   {
				   
				   driver.findElement(By.xpath("//p[text()='Children']/parent::div[@class='sc-jeraig lkvSvZ']/descendant::span[@class='sc-lcepkR hqqbLZ']/following-sibling::span[@class='sc-ehCJOs kujlZU']")).click();
				   
				   ActualchildrenNo=driver.findElement(By.xpath("//p[text()='Children']/parent::div[@class='sc-jeraig lkvSvZ']/descendant::span[@class='sc-lcepkR hqqbLZ']")).getText();
				   ActualchildrenNo1=Integer.parseInt(ActualchildrenNo); 
			   }
			   if(Infants1<=Adult1 && 9>=Adult1+Infants1+Children1)
			   {
				   while(ActualinfantsNo1 < Infants1)
				   {
					  
					   driver.findElement(By.xpath("//p[text()='Infants']/parent::div[@class='sc-jeraig lkvSvZ']/descendant::span[@class='sc-lcepkR hqqbLZ']/following-sibling::span[@class='sc-ehCJOs kujlZU']")).click();
					   
					   ActualinfantsNo=driver.findElement(By.xpath("//p[text()='Infants']/parent::div[@class='sc-jeraig lkvSvZ']/descendant::span[@class='sc-lcepkR hqqbLZ']")).getText();
					   ActualinfantsNo1=Integer.parseInt(ActualinfantsNo); 
				   }  
				   driver.findElement(By.xpath("//a[@class='sc-eLwHnm hHxEGr']")).click();
				   
				   driver.findElement(By.xpath("//span[@class='sc-dFtzxp hwZghA']")).click();
			   }
			   else
			   {
				   System.out.println("Please check the Infants number or you exceed the maximum seats");
				  
			   }
		   }
		   else
	      	{
			   System.out.println("Please check the Children number or you exceed the maximum seats");
			 
	     	} 
  	   }
		   else
	  	   {
	  		   System.out.println("Please check the Adult number or you exceed the maximum seats");
	  	
	  	   }
	}
	

	@AfterMethod
	public void tearDown(ITestResult result) throws IOException {
		if(result.getStatus()==ITestResult.SUCCESS)
		{
			extentTest.log(LogStatus.PASS,"Testcase PASSED is " +result.getName());
		}
		
		else if(result.getStatus()==ITestResult.SKIP)
		{
			extentTest.log(LogStatus.SKIP,"Testcase SKIPPED is " +result.getName());	
		}
		
		else if(result.getStatus()==ITestResult.FAILURE)
		{
			extentTest.log(LogStatus.FAIL,"Testcase FAILLED is " +result.getName());
			
			extentTest.log(LogStatus.FAIL,"Testcase FAILLED is " +result.getThrowable());
			
			String screenshotPath=Goibibo.getScreenshot(driver, result.getName());
			extentTest.log(LogStatus.FAIL, extentTest.addScreenCapture(screenshotPath));
		}
		extentReports.endTest(extentTest);
		//driver.quit();
	}

}
