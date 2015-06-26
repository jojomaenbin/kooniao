package com.kooniao.travel.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import com.kooniao.travel.KooniaoApplication;
import com.kooniao.travel.R;
import com.kooniao.travel.api.ApiCaller;
import com.kooniao.travel.api.ApiCaller.APICityListResultCallback;
import com.kooniao.travel.constant.Define;
import com.kooniao.travel.model.AreaParent;
import com.kooniao.travel.model.City;
import com.kooniao.travel.model.SubArea;
import com.kooniao.travel.utils.JsonTools;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;
import com.litesuits.orm.db.assit.QueryBuilder;

/**
 * 城市管理
 * 
 * @author ke.wei.quan
 * 
 */
public class CityManager {
	CityManager() {
	}

	private static CityManager instance;

	public static CityManager getInstance() {
		if (instance == null) {
			synchronized (ApiCaller.class) {
				if (instance == null) {
					instance = new CityManager();
				}
			}
		}

		return instance;
	}

	/**************************************************************************************************************************
	 * 
	 * 回调接口
	 * 
	 * *************************************************************************
	 * ************************************************
	 */
	public interface CityListResultCallback {
		void result(String errMsg, List<City> hotCities, List<City> allCities);
	}

	/**************************************************************************************************************************
	 * 
	 * 请求访问处理
	 * 
	 * *************************************************************************
	 * ************************************************
	 */
	
	public void loadHotCities(final CityListResultCallback callback) {
		ApiCaller.getInstance().loadHotCities(new APICityListResultCallback() {
			
			@Override
			public void result(String errMsg, List<City> hotCities) {
				final List<City> allCities = new ArrayList<City>();
				String[] cityArray = KooniaoApplication.getInstance().getResources().getStringArray(R.array.allCities);
				String[] cityStrings = new String[2];
				for (int i = 0; i < cityArray.length; i++) {
					cityStrings = cityArray[i].split(",");
					City city = new City();
					city.setId(Integer.parseInt(cityStrings[0]));
					city.setHotCity(false);
					city.setLocateCity(false);
					city.setName(cityStrings[1]);

					allCities.add(city);
				}
				
				List<City> cities = getAllCities();
				if (cities.isEmpty()) {
					batchInsertCityToDB(allCities, hotCities);
				}
				
				if (hotCities != null) {
					for (City hotCity : hotCities) {
						hotCity.setHotCity(true); 
					}
				}
				callback.result(errMsg, hotCities, allCities);
			}
		});
	} 
	
	/**************************************************************************************************************************
	 * 
	 * 数据库操作
	 * 
	 * *************************************************************************
	 * ************************************************
	 */
	
	/**
	 * 批量插入城市到数据库
	 * @param hotCities
	 * @param cities
	 */
	public void batchInsertCityToDB(List<City> hotCities, List<City> cities) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		City city = db.queryById(3544, City.class);
		if (city == null) {
			db.save(cities);
		}

		db.save(hotCities);
	}

	/**
	 * 保存或更新城市
	 * 
	 * @param city
	 */
	public void saveOrUpdateCity(City city) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		db.save(city);
	}

	/**
	 * 获取所有城市列表
	 * 
	 * @return
	 */
	public List<City> getAllCities() {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		return db.queryAll(City.class);
	}

	/**
	 * 根据id获取城市model
	 * 
	 * @param id
	 * @return
	 */
	public City getCityById(int id) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		City city = db.queryById(id, City.class);
		return city;
	}

	/**
	 * 根据name获取城市model
	 * 
	 * @param name
	 * @return
	 */
	public City getCityByName(String name) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		List<City> cities = db.query(new QueryBuilder(City.class).where("name" + " = ?", new String[]{name}));
		if (cities == null) {
			return null;
		} else {
			return cities.get(0); 
		}
	}
	
	/**
	 * 获取地区列表
	 * @return
	 */
	public List<AreaParent> getAreaList() {
		InputStream inputStream = KooniaoApplication.getInstance().getResources().openRawResource(R.raw.area_list);
		//得到数据的大小  
	    int length = 0;
	    String areaStringList = null;
		try {
			length = inputStream.available();
			byte [] buffer = new byte[length];          
			//读取数据  
			inputStream.read(buffer);
			areaStringList = EncodingUtils.getString(buffer, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}  
		
		if (areaStringList == null) {
			return null;
		} else {
			List<AreaParent> areaList = JsonTools.jsonObjArray(areaStringList, AreaParent.class);
			return areaList;
		}
	} 
	
	/**
	 * 保存全部子地区列表
	 * @param subAreas
	 */
	public void saveSubAreaList(List<SubArea> subAreas) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		db.save(subAreas);
	}
	
	/**
	 * 根据子地区的名字查找父地区的名称
	 */
	public String getAreaParentNameBySubAreaName(String subAreaName) {
		SubArea subArea = getSubAreaByName(subAreaName);
		if (subArea != null) {
			return subArea.getParent_name();
		} else {
			return null;
		}
	}
	
	/**
	 * 根据子地区的名字查找子地区
	 */
	public SubArea getSubAreaByName(String subAreaName) {
		DataBase db = LiteOrm.newInstance(KooniaoApplication.getInstance(), Define.DB_NAME);
		List<SubArea> subAreas = db.query(new QueryBuilder(SubArea.class).where("area_name" + " = ?", new String[] { subAreaName }));
		if (subAreas != null && !subAreas.isEmpty()) {
			return subAreas.get(0);
		} else {
			return null;
		}
	}
	
}
