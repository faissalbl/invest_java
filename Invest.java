
import java.lang.Math; 
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Invest
{

	private static final String USAGE = "Usage: Invest [--initialAmount ?] --monthlyAmount ? --months ? --annualRate ? [--tax true/false]";
  
    
	/**
	args: [--initialAmount ?] --monthlyAmount ? --months ? --annualRate ? [--tax true/false]
	*/
	public static void main(String[] args)
	{
		Map<String, Double> mArgs = createArgsMap(args);
		String result = new InvestmentCalculator(mArgs).calculate();
		System.out.println(result);
	}
  
	private static Map<String, Double> createArgsMap(String[] args) {

		final Set<String> ARG_NAMES = unmodifiableSet("--initialAmount", "--monthlyAmount", "--months", "--annualRate", "--tax");
		final Set<String> REQUIRED_ARG_NAMES = unmodifiableSet("--monthlyAmount", "--months", "--annualRate");

		if (args.length == 0 || args.length % 2 != 0) {
			throw new IllegalArgumentException(USAGE); 
		}
		
		Map<String, Double> result = new HashMap<>();
	   
		for (int i = 0; i < args.length; i += 2) {
			String name = args[i];
			Double value = Double.valueOf(args[i + 1]);
			result.put(name, value);
		}
		
		if (!ARG_NAMES.containsAll(result.keySet())) 
			throw new IllegalArgumentException(USAGE);
		
		if (!result.keySet().containsAll(REQUIRED_ARG_NAMES))
			throw new IllegalArgumentException("Missing required arguments. " + USAGE);
		
		return result;
	}
  
	private static Set<String> unmodifiableSet(String... items) {
		Set<String> set = new HashSet<>();
		set.addAll(Arrays.asList(items));
		return Collections.unmodifiableSet(set);
	}
  
}

class Application {
	private int monthApplied;
	private double amount;

	public Application(int monthApplied, double amount) {
		this.monthApplied = monthApplied;
		this.amount = amount;
	}

	public int getMonthApplied() {
		return monthApplied;
	}

	public double getAmount() {
		return amount;
	}
}

  
class InvestmentCalculator {

	private Map<String, Double> mArgs = new HashMap<>();
  
	public InvestmentCalculator(Map<String, Double> mArgs) {
		this.mArgs = mArgs;
	}
  
	public String calculate() {
		final int MONTHS_IN_YEAR = 12;
		final int MONTH_ZERO = 0;
		final int ONE = 1;

	  	double initialAmount = nvl(mArgs.get("--initialAmount"), 0.0);
		double monthlyAmount = nvl(mArgs.get("--monthlyAmount"), 0.0);
		int months = mArgs.get("--months").intValue();
		double annualRate = mArgs.get("--annualRate");

		List<Application> applications = new ArrayList<>();

		applications.add(new Application(MONTH_ZERO, initialAmount));

		if (monthlyAmount > 0)
			for (int month = 1; month <= months; ++month) {
				applications.add(new Application(month, monthlyAmount));
			}

		double totalAmount = 0.0;
		double totalCorrectedAmount = 0.0;
		double totalTaxAmount = 0.0;
		for(Application application : applications) {

			int applicationMonths = months - application.getMonthApplied();
			int applicationYears = applicationMonths / MONTHS_IN_YEAR;
			int remainingMonths = applicationMonths % MONTHS_IN_YEAR;

			System.out.println("applicationMonths " + applicationMonths);
			double tax = calculateTax(applicationMonths);
			System.out.println("tax " + tax);

			double amount = application.getAmount();
			totalAmount += application.getAmount();

			if (applicationYears > 0) 
				amount = application.getAmount() * Math.pow((1 + annualRate), applicationYears);

			System.out.println("annualRate " + (1 + annualRate));
			System.out.println("years " + applicationYears);
			System.out.println("amountYears " + amount);

			System.out.println("remainingMonths " + remainingMonths);
			if (remainingMonths > 0) {
				double amountYear = amount * Math.pow((1 + annualRate), ONE);
				double amountMonths = amountYear / MONTHS_IN_YEAR;
				System.out.println("amountMonths " + amountMonths);
				amount += amountMonths;
			}

			double grossProfit = amount - application.getAmount();
			System.out.println("grossProfit " + grossProfit);

			totalCorrectedAmount += amount;
			double taxableAmount = amount - application.getAmount();

			System.out.println("taxableAmount " + taxableAmount);
			double taxAmount = taxableAmount * tax;
			System.out.println("taxAmount " + taxAmount);
			System.out.println("------------------------------------------");
			totalTaxAmount += taxAmount;
		}
		double netAmount = totalCorrectedAmount - totalTaxAmount;

		StringBuilder sb = new StringBuilder();
		sb.append("Initial Amount:\t" + initialAmount);    
		sb.append("\nMonths:\t").append(months);
		sb.append("\nInvested Amount:\t").append(totalAmount);
		sb.append("\nCorrected Amount:\t").append(totalCorrectedAmount);
		sb.append("\nTax Amount:\t").append(totalTaxAmount);
		sb.append("\nNet Amount:\t").append(netAmount);	

		return sb.toString();
	}

//TODO verify real taxes for periods
	private double calculateTax(int month) {
		final double INITIAL_TAX = 0.225;

		final int LIMIT_1 = 6;
		final int LIMIT_2 = 12;
		final int LIMIT_3 = 24;
		
		final double TAX_1 = 0.2;
		final double TAX_2 = 0.185;
		final double TAX_3 = 0.15;

		if (month < LIMIT_1) return INITIAL_TAX;
		if (month < LIMIT_2) return TAX_1;
		if (month < LIMIT_3) return TAX_2;
		return TAX_3; 		
	}
  
	private <T> T nvl(T arg, T defaultArg) {
		if (arg == null)
			return defaultArg;
	
		return arg;
	}

}
  


