package hotelDeal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DealSystem {

	public static final String REBATE_3PLUS = "rebate_3plus";
	public static final String REBATE = "rebate";
	public static final String PCT = "pct";
	public static final String OFF_YOUR_STAY = " off your stay";
	public static final String THREE_NIGHT_OR_MORE = " 3 nights or more";
	public static final String cvsSplitBy = ",";
	public static final String NO_DEAL_AVAILABLE = "no deal available ";
	public static String csvFile = "";
	static Map<String, List<Deal>> map = new HashMap();

	public static void main(String[] args) throws ParseException {

		while (true) {
			Scanner sc = new Scanner(System.in);
			String str = sc.nextLine();
			String[] inputs = getInput(str);
			if (str.equals("quit")) {
				break;
			}
			if (!validateInput(inputs)) {
				// not valid
				System.out.println(" Input error, try again ");
				continue;
			}
			if (!csvFile.equals(inputs[0])) {
				// only when the user use new csv file, system will need to init
				csvFile = inputs[0];
				DealSystem obj = new DealSystem();
				obj.run();
			}
			
			// you may want to check this, but not nessasary 
//			if(!map.containsKey(inputs[1])){
//				System.out.println(" hotel name does not exists, you may want to try other name ");
//				continue;
//			}
			
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date checkInDate = format.parse(inputs[2]);
			int daysStay = Integer.parseInt(inputs[3]);
			Deal beastDeal = findBestDeal(inputs[1], checkInDate, daysStay);

			pintPromo(beastDeal);
		}
	}
	
	public static String[] getInput(String str){
		// 
		String[] strs = new String[3];
		if(str==null){
			return strs;
		}
		strs = str.split("\"");
		if(strs.length!=3){
			return strs;
		}
		String[] result = new String[4];
		strs[2] = strs[2].trim();
		
		result[0] = strs[0].trim();
		result[1] = strs[1].trim();
		result[2] = strs[2].split("\\s+")[0].trim();
		result[3] = strs[2].split("\\s+")[1].trim();
		return result;
	}

	public static void pintPromo(Deal deal) {
		if (deal == null) {
			System.out.println(NO_DEAL_AVAILABLE);
			return;
		}
		int val = deal.getDealValue();
		if (deal.getDealType().equals(REBATE_3PLUS)) {
			System.out.println("$"+deal.getDealValue()+OFF_YOUR_STAY+THREE_NIGHT_OR_MORE);
		}else if(deal.getDealType().equals(REBATE)){
			System.out.println("$"+deal.getDealValue()+OFF_YOUR_STAY);
		}else{
			System.out.println(deal.getDealValue()+"%"+OFF_YOUR_STAY);
		}
	}

	public static Deal findBestDeal(String hotelName, Date date, int days) {
		List<Deal> deals = map.get(hotelName);
		if (deals == null) {
			return null;
		}
		long minTotalPrice = Long.MAX_VALUE;
		Deal result = null;
		for (Deal deal : deals) {
			if (!deal.validaByDate(date, days)) {
				continue;
			}
			long price = deal.getPrice(days);
			if (price < minTotalPrice) {
				minTotalPrice = price;
				result = deal;
			}
		}
		return result;
	}

	// i copy this from
	// http://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
	public void run() {

		BufferedReader br = null;
		String line = "";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] info = line.split(cvsSplitBy);
				info[0] = info[0].trim();
				info[1] = info[1].trim();
				info[2] = info[2].trim();
				info[3] = info[3].trim();
				info[4] = info[4].trim();
				info[5] = info[5].trim();
				info[6] = info[6].trim();
				if (!validateCSV(info)) {
					// just ignore the not valid input
					continue;
				}
				Deal deal = new Deal(info[1], info[3], info[4], info[5],
						info[6]);

				if (!map.containsKey(info[0])) {
					map.put(info[0], new LinkedList());
				}
				map.get(info[0]).add(deal);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static boolean validateInput(String[] info) {
		try {
			if (info == null || info.length != 4) {
				return false;
			}
			String fileName = info[0];
			File f = new File(fileName);
			if (!f.exists() || f.isDirectory()) {
				System.out.println("path : " + fileName + " not exists ");
				return false;
			}
			String hotelName = info[1];
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = format.parse(info[2]);
			int days = Integer.parseInt(info[3]);

			if (days < 1) {
				System.out.println(" days stay must be bigger than 0  ");
				return false;
			}

		} catch (Exception exception) {
			System.out.println(" input invalid ");
			return false;
		}
		return true;
	}

	public static boolean validateCSV(String[] info) {
		// all the invalid deal info will be ignore, client will not know these, and they do not need to
		if (info == null || info.length != 7) {
			return false;
		}
		String hotelName;
		int nightlyRate;
		int dealValue;
		String dealType = null;
		Date start;
		Date end;
		try {
			hotelName = info[0];
			nightlyRate = Integer.parseInt(info[1]);
			dealValue = Integer.parseInt(info[3]);
			dealType = info[4];
			if (!dealType.equals(REBATE_3PLUS) && !dealType.equals(PCT)
					&& !dealType.equals(REBATE)) {
				// make sure deal type is valid
			}
			// found this online
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			start = format.parse(info[5]);
			end = format.parse(info[6]);
		} catch (Exception e) {
			return false;
		}
		if (end.before(start)) {
			// make sure start date is before end date
			return false;
		}

		// check the percent is good when type is pct
		if (dealType.equals("pct")) {
			// only valid for -100 to 0
			if (dealValue < -100 || dealValue > 0) {
				return false;
			}
		}
		if (dealValue > 0) {
			return false;
		}

		return true;
	}

}
