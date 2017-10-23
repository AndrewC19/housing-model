package data;

import housing.Model;

import utilities.Pdf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Demographics {

	/**
	 * Target probability density of age of representative householder
	 * at time t=0
	 * Calibrated against (LCFS 2012)
	 */
	public static Pdf pdfAge = new Pdf(Model.config.DATA_AGE_MARGINAL_PDF);

	/**
	 * Probability density by age of the representative householder given that
	 * the household is newly formed.
	 * New households can be formed by, e.g., children leaving home,
	 * divorce, separation, people leaving an HMO.
	 * Roughly calibrated against "The changing living arrangements of young adults in the UK"
	 *  ONS Population Trends winter 2009 
	 */
// --- calibrated version...
//	public static Pdf pdfHouseholdAgeAtBirth = new Pdf(15.0, 29.0, new DoubleUnaryOperator() {
//		public double applyAsDouble(double age) {
//			return(betaDist.density((age-14.5)/15.0));
//		}	
//	});
	// --- version to make correct age distribution at equilibrium demographics
    public static Pdf pdfHouseholdAgeAtBirth = new Pdf(Model.config.DATA_HOUSEHOLD_AGE_AT_BIRTH_PDF, 800);

	/****
	 * Birth rates into the future (roughly calibrated against current individual birth rate)
	 * @param t	time (months) into the future
	 * @return number of births per year per capita
	 * Calibrated against flux of FTBs, Council of Mortgage Lenders Regulated Mortgage Survey (2015)
	 */
//	public static double futureBirthRate(double t) {
//		return(0.0102);
//	}
	public static double futureBirthRate(double t) {
	    // TODO: Contradictory calibration information, check which version holds and replace data for 2011
        // calibrated against average advances to first time buyers, core indicators 1987-2006
		return(Model.config.FUTURE_BIRTH_RATE);
	}
	

	/***
	 * Probability that a household 'dies' per year given age of the representative householder
	 * Death of a household may occur by marriage, death of single occupant, moving together.
	 * As first order approx: we use female death rates, assuming singles live at home until marriage,
	 * there is no divorce and the male always dies first
	 * TODO: Add marriage/co-habitation
	 */

    // TODO: Clarify that the model was so far killing everybody over 105 only with a 50% chance every month
    public static ArrayList<Double[]> probDeathGivenAgeData = readProbDeathGivenAge(Model.config.DATA_DEATH_PROB_GIVEN_AGE);

    /**
     * Method that gives, for a given age in years, its corresponding probability of death
     * @param   ageInYears  age in years (double)
     * @return  probability probability of death for the given age in years (double)
     */
    public static double probDeathGivenAge(double ageInYears) {
        for (Double[] band : probDeathGivenAgeData) {
            if(ageInYears<band[1]) return(band[2]);
        }
        return(Model.config.constants.MONTHS_IN_YEAR);
    }

    /**
     * Method to read bin edges and the corresponding death probabilities from a file
     * @param   fileName    String with name of file (address inside source folder)
     * @return  probDeathGivenAgeData ArrayList of arrays of (3) Doubles (age edge min, age edge max, prob)
     */
    public static ArrayList<Double[]> readProbDeathGivenAge(String fileName) {
        ArrayList<Double[]> probDeathGivenAgeData = new ArrayList<>();
        // Try-with-resources statement
        try (BufferedReader buffReader = new BufferedReader(new FileReader(fileName))) {
            String line = buffReader.readLine();
            while (line != null) {
                if (line.charAt(0) != '#') {
                    try {
                        Double [] band = new Double[3];
                        band[0] = Double.parseDouble(line.split(",")[0]);
                        band[1] = Double.parseDouble(line.split(",")[1]);
                        band[2] = Double.parseDouble(line.split(",")[2]);
                        probDeathGivenAgeData.add(band);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Exception " + nfe + " while trying to parse " +
                                line.split(",")[0] + " for an double");
                        nfe.printStackTrace();
                    }
                }
                line = buffReader.readLine();
            }
        } catch (IOException ioe) {
            System.out.println("Exception " + ioe + " while trying to read file '" + fileName + "'");
            ioe.printStackTrace();
        }
        return probDeathGivenAgeData;
    }
}
