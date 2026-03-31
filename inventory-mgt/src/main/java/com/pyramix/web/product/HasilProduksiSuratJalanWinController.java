package com.pyramix.web.product;

import java.math.BigDecimal;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import com.pyramix.domain.entity.Ent_SuratJalan;
import com.pyramix.domain.entity.Ent_SuratJalanProduct;
import com.pyramix.persistence.suratjalan.dao.SuratJalanDao;
import com.pyramix.web.common.GFCBaseController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HasilProduksiSuratJalanWinController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3200757112641200493L;

	private SuratJalanDao suratjalanDao;
	
	private Window hasilProduksiSuratJalanWin;
	private Label customerNameLabel, suratjalanDateLabel, suratjalanNumberLabel,
		nopolLabel, refdocLabel;
	private Listbox suratjalanProductListbox;
	
	private Ent_SuratJalan currSuratJalan;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		currSuratJalan = (Ent_SuratJalan) arg.get("hasilproduksi_suratjalan_data");
	}

	public void onCreate$hasilProduksiSuratJalanWin(Event event) throws Exception {
		log.info("hasilProduksiSuratJalanWin created");
		
		displaySuratJalan();
	}
	
	private void displaySuratJalan() throws Exception {
		customerNameLabel.setValue(currSuratJalan.getCustomer().getCompanyType()+"."+
				currSuratJalan.getCustomer().getCompanyLegalName());
		suratjalanDateLabel.setValue(dateToStringDisplay(currSuratJalan.getSuratjalanDate(),
				getShortDateFormat(), getLocale()));
		suratjalanNumberLabel.setValue(currSuratJalan.getSuratjalanSerial().getSerialComp());
		nopolLabel.setValue(currSuratJalan.getNoPolisi());
		refdocLabel.setValue(currSuratJalan.getRefDocument());
		// render the product list into the suratjalan listbox
		renderSuratJalanProductList();
	}

	private void renderSuratJalanProductList() throws Exception {
		// use proxy
		currSuratJalan = getSuratjalanDao().getSuratJalanProductByProxy(currSuratJalan.getId());
		
		// setup the modellist
		ListModelList<Ent_SuratJalanProduct> suratjalanProductModelList = 
					new ListModelList<Ent_SuratJalanProduct>(currSuratJalan.getSuratjalanProducts());
		suratjalanProductListbox.setModel(suratjalanProductModelList);
		suratjalanProductListbox.setItemRenderer(getSuratJalanProductListitemRenderer());
		
	}

	private ListitemRenderer<Ent_SuratJalanProduct> getSuratJalanProductListitemRenderer() {
		
		return new ListitemRenderer<Ent_SuratJalanProduct>() {
			
			@Override
			public void render(Listitem item, Ent_SuratJalanProduct product, int index) throws Exception {
				Listcell lc;
				
				// marking
				lc = new Listcell(product.getMarking());
				lc.setParent(item);
				
				// jenis-coil
				lc = new Listcell(product.getInventoryCode().getProductCode());
				lc.setParent(item);
				
				// spek
				lc = new Listcell(
						toDecimalFormat(new BigDecimal(product.getThickness()), getLocale(), "#0,00")+" x "+
						toDecimalFormat(new BigDecimal(product.getWidth()), getLocale(), "###.###")+" x "+
						toDecimalFormat(new BigDecimal(product.getLength()), getLocale(), "###.###"));
				lc.setParent(item);
				
				// qty(kg)
				lc = new Listcell(
						toDecimalFormat(new BigDecimal(product.getQuantityByKg()), getLocale(), "###.###"));					
				lc.setParent(item);
				
				// qty(lbr)
				lc = new Listcell(getFormatedInteger(product.getQuantityBySht()));
				lc.setParent(item);
				
			}
		};
	}

	public void onClick$closeButton(Event event) throws Exception {
		log.info("closeButton click");
		
		hasilProduksiSuratJalanWin.detach();
	}

	public SuratJalanDao getSuratjalanDao() {
		return suratjalanDao;
	}

	public void setSuratjalanDao(SuratJalanDao suratjalanDao) {
		this.suratjalanDao = suratjalanDao;
	}
	
}
