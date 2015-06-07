package hotelDeal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Deal {
	private int nightlyRate;
	// always positive
	private int dealValue;
	private String dealType;
	private Date start;
	private Date end;

	public Deal(String nightlyRate, String dealValue, String dealType,
			String start, String end) {
		// TODO Auto-generated constructor stub
		try {
			this.nightlyRate = Integer.parseInt(nightlyRate);
			this.dealValue = Integer.parseInt(dealValue);
			if (this.dealValue < 0) {
				this.dealValue = -this.dealValue;
			}
			this.dealType = dealType;
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			this.start = format.parse(start);
			this.end = format.parse(end);
		} catch (Exception e) {
			System.out.println(" Error ready csv file");
			return;
		}
	}
	
	public boolean validaByDate(Date date,int days){
		if(date.before(start) || date.after(end)){
			return false;
		}
		if(days<3 && dealType.equals("rebate_3plus")){
			return false;
		}
		return true;
	}
	
	public long getPrice(int days) {
		
		if(days<1){
			System.out.println(" you should at least stay for one day ");
		}
			// this situation we make sure numday > 3
		if (dealType.equals("pct")) {
			return (long)nightlyRate*days*(100-dealValue)/100;
		} else {
			// same for rebate_3plus and rebate
			return (long)nightlyRate*days-dealValue;
		}
	}

	public int getNightlyRate() {
		return nightlyRate;
	}

	public void setNightlyRate(int nightlyRate) {
		this.nightlyRate = nightlyRate;
	}

	public int getDealValue() {
		return dealValue;
	}

	public void setDealValue(int dealValue) {
		this.dealValue = dealValue;
	}

	public String getDealType() {
		return dealType;
	}

	public void setDealType(String dealType) {
		this.dealType = dealType;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}
}
