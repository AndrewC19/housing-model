package housing;

import org.apache.commons.math3.random.MersenneTwister;

/**************************************************************************************************
 * Class to represent the rental market
 *
 * @author daniel, Adrian Carro
 *
 *************************************************************************************************/
public class HouseRentalMarket extends HousingMarket {

    //-------------------//
    //----- Methods -----//
    //-------------------//

	public HouseRentalMarket(MersenneTwister prng) {
	    super(prng);
    }

    @Override
	public void completeTransaction(HouseBuyerRecord purchase, HouseSaleRecord sale) {
        Model.rentalMarketStats.recordTransaction(sale);
		sale.house.rentalRecord = null;
		purchase.buyer.completeHouseRental(sale);
		sale.house.owner.completeHouseLet(sale);
		Model.rentalMarketStats.recordSale(purchase, sale);
	}

	@Override
	public HouseSaleRecord offer(House house, double price) {
		if(house.isOnMarket()) {
			System.out.println("Got offer on rental market of house already on sale market");			
		}
		HouseSaleRecord hsr = super.offer(house, price);
		house.putForRent(hsr);
		return(hsr);
	}
	
	@Override
	public void removeOffer(HouseSaleRecord hsr) {
		super.removeOffer(hsr);
		hsr.house.resetRentalRecord();
	}
}
